/*
 * Copyright 2020 MengYao Lu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mesharel.emoji;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmojiToolTest {
    private static EmojiProcessor EMOJI_PROCESSOR;

    @BeforeAll
    public static void before() throws URISyntaxException, FileNotFoundException {
        Jsonb jsonb = JsonbBuilder.create();
        File file = new File(EmojiToolTest.class.getResource("/emoji.json").toURI());
        List<Map<String, Object>> all = jsonb.fromJson(
            new FileInputStream(file),
            new ArrayList<Map<String, Object>>() {}.getClass().getGenericSuperclass());

        List<Emoji> emojis = new ArrayList<>(all.size());
        for (Map<String, Object> map : all) {
            @SuppressWarnings("unchecked")
            Emoji emoji = new Emoji(
                (String) map.get("emoji"),
                (List<String>) map.get("aliases"),
                (List<String>) map.get("tags"),
                (Boolean) map.getOrDefault("skin_tones", false)
            );

            emojis.add(emoji);
        }

        EMOJI_PROCESSOR = new EmojiProcessor(emojis);
    }

    @Test
    public void checkEmoji() {
        Assertions.assertTrue(() -> EMOJI_PROCESSOR.isEmoji("\uD83D\uDC4B"));
        // emoji with skin tone
        Assertions.assertTrue(() -> EMOJI_PROCESSOR.isEmoji("\uD83D\uDC4B\uD83C\uDFFB"));
        Assertions.assertTrue(() -> EMOJI_PROCESSOR.isEmoji("\uD83D\uDC4B\uD83C\uDFFC"));
    }

    @Test
    public void addEmojiSkinTone() {
        Assertions.assertEquals(
            "\uD83D\uDC4B\uD83C\uDFFB",
            EMOJI_PROCESSOR.applySkinTone("\uD83D\uDC4B", SkinTone.LIGHT)
        );

        // man: light, woman: light, girl: dark, boy: dark
        Assertions.assertEquals(
            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDC67\uD83C\uDFFF\u200D\uD83D\uDC66\uD83C\uDFFF",
            EMOJI_PROCESSOR.applySkinTone("\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66",
                SkinTone.LIGHT, SkinTone.LIGHT, SkinTone.DARK)
        );

        // man: light, woman: dark, girl: dark, boy: dark
        Assertions.assertEquals(
            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDC69\uD83C\uDFFF\u200D\uD83D\uDC67\uD83C\uDFFF\u200D\uD83D\uDC66\uD83C\uDFFF",
            // main: light, woman: light, girl: light, body: dark
            EMOJI_PROCESSOR.applySkinTone("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDC69\uD83C\uDFFB\u200D" +
                    "\uD83D\uDC67\uD83C\uDFFB\u200D\uD83D\uDC66\uD83C\uDFFF",
                SkinTone.LIGHT, SkinTone.DARK)
        );
    }

    @Test
    public void removeEmojiSkinTone() {
        Assertions.assertEquals("\uD83D\uDC4B", EMOJI_PROCESSOR.removeSkinTone("\uD83D\uDC4B\uD83C\uDFFB"));
        Assertions.assertEquals(
            "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66",
            // main: light, woman: light, girl: light, boy: light
            EMOJI_PROCESSOR.removeSkinTone("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDC69\uD83C\uDFFB\u200D" +
                "\uD83D\uDC66\uD83C\uDFFB\u200D\uD83D\uDC66\uD83C\uDFFB"));
    }

    @Test
    public void findEmojiByAlias() {
        Assertions.assertEquals(
            "\uD83D\uDC4B",
            EMOJI_PROCESSOR.findByAlias("wave").map(Emoji::getEmoji).orElseThrow());
        Assertions.assertEquals(
            "\uD83D\uDE00",
            EMOJI_PROCESSOR.findByAlias("grinning").map(Emoji::getEmoji).orElseThrow());
        Assertions.assertFalse(EMOJI_PROCESSOR.findByAlias(UUID.randomUUID().toString()).isPresent());
    }

    @Test
    public void findEmojiByUnicode() {
        Assertions.assertEquals(
            "\uD83D\uDC4B",
            EMOJI_PROCESSOR.findByUnicode("\uD83D\uDC4B").map(Emoji::getEmoji).orElseThrow());

        // emoji with skin tone
        Assertions.assertEquals(
            "\uD83D\uDC4B",
            EMOJI_PROCESSOR.findByUnicode("\uD83D\uDC4B\uD83C\uDFFB").map(Emoji::getEmoji).orElseThrow());
    }

    @Test
    public void extractEmoji() {
        Assertions.assertArrayEquals(
            new String[]{"\u2702\uFE0F", "\uD83D\uDCCB", "\uD83D\uDC4D"},
            EMOJI_PROCESSOR.extract("\u2702\uFE0F Copy and \uD83D\uDCCB Paste Emoji \uD83D\uDC4D").stream()
                .map(ExtractedEmoji::getEmoji)
                .toArray());

        List<String> expected = List.of("\uD83D\uDC4B", "\uD83D\uDC4B\uD83C\uDFFB", "\uD83D\uDC4B\uD83C\uDFFC");
        List<ExtractedEmoji> extracts = EMOJI_PROCESSOR.extract("\uD83D\uDC4B, \uD83D\uDC4B\uD83C\uDFFB and \uD83D\uDC4B\uD83C\uDFFC");
        Assertions.assertEquals(3, extracts.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), extracts.get(i).getEmoji());
            Assertions.assertEquals("\uD83D\uDC4B", extracts.get(i).getDetail().getEmoji());
        }

        extracts = EMOJI_PROCESSOR.extract("family: \uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66");
        Assertions.assertEquals(1, extracts.size());
        Assertions.assertEquals("\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66", extracts.get(0).getEmoji());

        // man: light, woman: light, girl: dark, boy: dark
        extracts = EMOJI_PROCESSOR.extract("family: \uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDC69\uD83C\uDFFB\u200D" +
            "\uD83D\uDC67\uD83C\uDFFF\u200D\uD83D\uDC66\uD83C\uDFFF");
        Assertions.assertEquals(1, extracts.size());
        Assertions.assertEquals(
            "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDC67\uD83C\uDFFF\u200D\uD83D\uDC66\uD83C\uDFFF",
            extracts.get(0).getEmoji());
        Assertions.assertEquals(
            "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66",
            extracts.get(0).getDetail().getEmoji());

    }

    @Test
    public void replaceAllByAlias() {
        Assertions.assertEquals(
            "Emoji \uD83D\uDE00 has a cat variant, \uD83D\uDE3A Grinning Cat Face.",
            EMOJI_PROCESSOR.replaceByAlias("Emoji :grinning: has a cat variant, :smiley_cat: Grinning Cat Face."));

        Assertions.assertEquals(
            "\uD83D\uDE00",
            EMOJI_PROCESSOR.replaceByAlias(":grinning:")
        );

        Assertions.assertEquals(
            "\uD83D\uDE00 \uD83D\uDE3A",
            EMOJI_PROCESSOR.replaceByAlias(":grinning: :smiley_cat:"));

        Assertions.assertEquals(
            "\uD83D\uDE00 has a cat variant \uD83D\uDE3A",
            EMOJI_PROCESSOR.replaceByAlias(":grinning: has a cat variant :smiley_cat:"));

        Assertions.assertEquals(
            "Emoji \uD83D\uDE00 has a cat variant, \uD83D\uDE3A Grinning Cat Face.",
            EMOJI_PROCESSOR.replaceByAlias("Emoji \uD83D\uDE00 has a cat variant, \uD83D\uDE3A Grinning Cat Face."));

        Assertions.assertEquals(
            "Emoji \uD83D\uDE00 has a cat variant, :smiley_cats: Grinning Cat Face.",
            EMOJI_PROCESSOR.replaceByAlias("Emoji :grinning: has a cat variant, :smiley_cats: Grinning Cat Face.")
        );

        Assertions.assertEquals(
            "\uD83D\uDE00 \uD83D\uDE3A \uD83D\uDE00",
            EMOJI_PROCESSOR.replaceByAlias(":grinning: :smiley_cat: :grinning:")
        );
    }
}
