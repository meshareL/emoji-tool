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

package com.github.mesharel.emoji.internal;

/**
 * String util
 */
public abstract class StringUtils {
    /**
     * 检查给定的字符串是否具有有效文本<br>
     *
     * 字符串不为 {@code null}, 且长度大于0, 并且至少包含一个非空白字符
     *
     * <pre>
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("123") = true
     * StringUtils.hasText(" 123 ") = true
     * </pre>
     *
     * @param s 需要检查的字符串
     * @return 如果字符串符合检查条件返回 {@code true}, 否则返回 {@code false}
     * @see Character#isWhitespace(char)
     */
    public static boolean hasText(String s) {
        if (s.isEmpty()) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 修剪给定字符串中的前导和尾随空格
     *
     * @param str 需要修建的字符串
     * @return 修剪后的字符串
     */
    public static String trimWhitespace(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int beginIndex = 0;
        int endIndex = str.length() - 1;

        while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
            beginIndex++;
        }

        while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
            endIndex--;
        }

        return str.substring(beginIndex, endIndex + 1);
    }

    /**
     * 根据代码点计算字符索引
     *
     * @param codePoints 代码点数组
     * @param from 初始索引
     * @param to 最终索引
     * @return 计算出的索引
     */
    public static int computeIndex(int[] codePoints, int from, int to) {
        int index = 0;
        for (int i = from; i < to; i++) {
            index += Character.isSupplementaryCodePoint(codePoints[i]) ? 2 : 1;
        }

        return index;
    }
}
