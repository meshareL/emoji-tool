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

import java.util.StringJoiner;

/**
 * emoji 肤色
 *
 * @see <a href="https://emojipedia.org/emoji-modifier-sequence">skin tone</a>
 */
public enum SkinTone {
    LIGHT("\uD83C\uDFFB", 0x1f3fb),
    MEDIUM_LIGHT("\uD83C\uDFFC", 0x1f3fc),
    MEDIUM("\uD83C\uDFFD", 0x1f3fd),
    MEDIUM_DARK("\uD83C\uDFFE", 0x1f3fe),
    DARK("\uD83C\uDFFF", 0x1f3ff);

    private final String unicode;
    private final int codePoint;

    SkinTone(String unicode, int codePoint) {
        this.unicode = unicode;
        this.codePoint = codePoint;
    }

    public String getUnicode() {
        return unicode;
    }

    public int getCodePoint() {
        return codePoint;
    }

    public static boolean isSkinTone(String s) {
        if (s.codePointCount(0, s.length()) > 1) {
            return false;
        }

        return isSkinTone(s.codePointAt(0));
    }

    public static boolean isSkinTone(int codePoint) {
        return codePoint >= 0x1f3fb && codePoint <= 0x1f3ff;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SkinTone.class.getSimpleName() + "[", "]")
            .add("unicode='" + unicode + "'")
            .add("codePoint=" + codePoint)
            .toString();
    }
}
