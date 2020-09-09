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

import java.util.Objects;
import java.util.StringJoiner;

/**
 * 在给定字符串中提取出的 emoji<br>
 *
 * 该类所有属性都不可更改
 */
public class ExtractedEmoji {
    private final String emoji;
    private final int start;
    private final int end;
    private final Emoji detail;

    public ExtractedEmoji(String emoji, int start, int end, Emoji detail) {
        this.emoji = emoji;
        this.start = start;
        this.end = end;
        this.detail = detail;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Emoji getDetail() {
        return detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtractedEmoji that = (ExtractedEmoji) o;
        return start == that.start &&
            end == that.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExtractedEmoji.class.getSimpleName() + "[", "]")
            .add("emoji='" + emoji + "'")
            .add("start=" + start)
            .add("end=" + end)
            .add("detail=" + detail)
            .toString();
    }
}
