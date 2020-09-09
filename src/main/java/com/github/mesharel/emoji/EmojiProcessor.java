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

import com.github.mesharel.emoji.internal.MatchedAlias;
import com.github.mesharel.emoji.internal.StringUtils;
import com.github.mesharel.emoji.internal.TrieTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理 emoji 的工具类
 */
public class EmojiProcessor {
    /** https://emojipedia.org/emoji-zwj-sequence */
    private static final String ZWJ = "\u200D";
    private static final int ZWJ_HEX = 0x200d;

    /** https://emojipedia.org/variation-selector-16 */
    private static final String VARIATION_16 = "\uFE0F";
    private static final int VARIATION_16_HEX = 0xfe0f;

    private static final Pattern ALIAS_PATTERN = Pattern.compile(":(\\w+):");

    private final List<Emoji> originals;
    private final Map<String, Emoji> emojiMap;
    private Map<String, Emoji> aliasMap;
    private TrieTree trieTree;

    public EmojiProcessor(List<Emoji> emojis) {
        this.originals = emojis;
        this.emojiMap = new HashMap<>(emojis.size());

        emojis.forEach(emoji -> emojiMap.put(emoji.getEmoji(), emoji));
    }

    /**
     * 检查给定的字符串是否为 emoji<br>
     *
     * 该方法可以检查带有肤色的 emoji
     *
     * @param s 需要检查的字符串
     * @return 如果该字符串是 emoji(包括肤色) 则返回 {@code true}, 否则返回 {@code false}
     */
    public boolean isEmoji(String s) {
        if (s == null || !StringUtils.hasText(s)) {
            return false;
        }

        if (SkinTone.isSkinTone(s)) {
            return true;
        }

        return emojiMap.containsKey(s.codePointCount(0, s.length()) == 1 ? s : removeSkinTone(s));
    }

    /**
     * 查找具有给定 {@code alias} 别名的 emoji
     *
     * @param alias emoji 别名
     * @return 具有给定 {@code alias} 的 emoji
     */
    public Optional<Emoji> findByAlias(String alias) {
        if (alias == null || !StringUtils.hasText(alias)) {
            return Optional.empty();
        }

        return Optional.ofNullable(getAliasMap().get(StringUtils.trimWhitespace(alias)));
    }

    /**
     * 查找具有给定 unicode 编码的 emoji<br>
     *
     * 该函数会忽略 emoji 中的肤色<br>
     *
     * @param unicode unicode 编码
     * @return 查找到的 emoji
     */
    public Optional<Emoji> findByUnicode(String unicode) {
        if (unicode == null || !StringUtils.hasText(unicode)) {
            return Optional.empty();
        }

        Emoji emoji = this.emojiMap.get(unicode);
        if (emoji != null) {
            return Optional.of(emoji);
        }

        String removed = removeSkinTone(unicode);

        if (!StringUtils.hasText(removed)) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.emojiMap.get(removed));
    }

    /**
     * 修改 emoji 中的肤色<br>
     *
     * 如果 {@code skinTone} 未传入参数, 则直接返回该 emoji<br>
     *
     * 如果出入的肤色数量少于 emoji 可更改的数量, 则使用最有一个肤色修改剩余的所有 emoji<br>
     *
     * @param emoji 不能为 {@literal null}
     * @param skinTones 肤色
     * @return 应用肤色后的 emoji
     */
    public String applySkinTone(String emoji, SkinTone... skinTones) {
        Objects.requireNonNull(emoji, "emoji must not be null");

        if (skinTones.length == 0) {
            return emoji;
        }

        String removed = removeSkinTone(emoji);
        String[] splits = removed.split(ZWJ);
        StringBuilder sb = new StringBuilder((splits.length * 3) - 1);

        int index = 0;
        for (int i = 0; i < splits.length; i++) {
            String section = splits[i];

            if (i != 0) {
                sb.append(ZWJ);
            }

            if (!this.emojiMap.containsKey(section) || !this.emojiMap.get(section).isSkinnable()) {
                sb.append(section);
                continue;
            }

            SkinTone tone = skinTones[Math.min(index, skinTones.length - 1)];
            tint(sb, section, tone);
            index++;
        }

        return sb.toString();
    }

    /**
     * 删除 emoji 中的肤色<br>
     *
     * 如果传入肤色则会返回空字符串
     *
     * @param emoji emoji, 不能为 {@literal null}
     * @return 删除肤色后的 emoji
     */
    public String removeSkinTone(String emoji) {
        Objects.requireNonNull(emoji, "emoji must not be null");
        if (!StringUtils.hasText(emoji) || SkinTone.isSkinTone(emoji)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < emoji.length();) {
            int codePoint = emoji.codePointAt(i);

            if (SkinTone.isSkinTone(codePoint)) {
                i += 2;
                continue;
            }

            sb.appendCodePoint(codePoint);
            i += Character.isSupplementaryCodePoint(codePoint) ? 2 : 1;
        }

        return sb.toString();
    }

    /**
     * 提取给定字符串中的所有 emoji
     *
     * @param s 包含 emoji 的字符串
     * @return 不可更改列表
     */
    public List<ExtractedEmoji> extract(String s) {
        if (s == null || !StringUtils.hasText(s)) {
            return Collections.emptyList();
        }

        int start = 0;
        int[] codePoints = s.codePoints().toArray();
        List<ExtractedEmoji> extracts = new ArrayList<>();

        while (start < codePoints.length) {
            int end = getTrieTree().tryMatch(codePoints, start);

            if (end == -1) {
                start++;
                continue;
            }

            end += 1;
            String origin = toString(codePoints, start, end);
            Emoji emoji = this.emojiMap.get(removeSkinTone(origin));

            if (emoji == null) {
                start++;
            } else {
                int si = StringUtils.computeIndex(codePoints, 0, start);
                int ei = StringUtils.computeIndex(codePoints, 0, end);
                extracts.add(new ExtractedEmoji(origin, si, ei, emoji));
                start = end;
            }
        }

        return extracts;
    }

    /**
     * 将字符串中的所有 emoji 别名替换为 emoji<br>
     *
     * Example:
     * <pre>
     * Emoji :grinning: has a cat variant, :smiley_cat: Grinning Cat Face
     * Emoji 😀 has a cat variant, 😺 Grinning Cat Face
     * </pre>
     *
     * @param s 包含 emoji 的字符串, 不能为 {@literal null}
     * @return 替换为 emoji 的字符串
     */
    public String replaceByAlias(String s) {
        Objects.requireNonNull(s, "The string to be replaced cannot be null");

        if (!StringUtils.hasText(s)) {
            return "";
        }

        Matcher matcher = ALIAS_PATTERN.matcher(s);
        if (!matcher.find()) {
            return s;
        }

        List<MatchedAlias> matchedAliases = new ArrayList<>();
        do {
            String alias = matcher.group(1);
            if (!getAliasMap().containsKey(alias)) {
                continue;
            }

            matchedAliases.add(new MatchedAlias(
                alias,
                matcher.start(),
                matcher.end(),
                getAliasMap().get(alias).getEmoji()));
        } while (matcher.find());

        StringBuilder sb = new StringBuilder((matchedAliases.size() << 1) + 1);
        for (int i = 0; i < matchedAliases.size(); i++) {
            MatchedAlias matched = matchedAliases.get(i);

            String sub;
            if (i == 0) {
                sub = s.substring(0, matched.getStart());
            } else {
                sub = s.substring(matchedAliases.get(i - 1).getEnd(), matched.getStart());
            }

            sb.append(sub);
            sb.append(matched.getEmoji());
        }

        MatchedAlias last = matchedAliases.get(matchedAliases.size() - 1);
        if (last.getEnd() != s.length()) {
            sb.append(s.substring(last.getEnd()));
        }

        return sb.toString();
    }

    private void tint(StringBuilder sb, String emoji, SkinTone tone) {
        int[] codePoints = emoji.codePoints().toArray();
        for (int i = 0; i < codePoints.length; i++) {
            int point = codePoints[i];

            if (i != codePoints.length - 1) {
                sb.appendCodePoint(point);
            } else {
                if (point != VARIATION_16_HEX) {
                    sb.appendCodePoint(point);
                }
                sb.append(tone.getUnicode());
            }
        }
    }

    private String toString(int[] codePoints, int from, int to) {
        StringBuilder sb = new StringBuilder(to - from);

        for (int i = from; i < to; i++) {
            sb.appendCodePoint(codePoints[i]);
        }

        return sb.toString();
    }

    private Map<String, Emoji> getAliasMap() {
        if (this.aliasMap != null) {
            return this.aliasMap;
        }

        this.aliasMap = new HashMap<>(this.originals.size() << 1);
        this.originals.forEach(emoji -> emoji.getAliases().forEach(alias -> this.aliasMap.put(alias, emoji)));
        return this.aliasMap;
    }

    private TrieTree getTrieTree() {
        if (this.trieTree != null) {
            return this.trieTree;
        }

        this.trieTree = new TrieTree(this.originals);
        return this.trieTree;
    }
}
