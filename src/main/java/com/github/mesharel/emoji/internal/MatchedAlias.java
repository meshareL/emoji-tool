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

import java.util.StringJoiner;

public class MatchedAlias {
    private final String alias;
    private final int start;
    private final int end;
    private final String emoji;

    public MatchedAlias(String alias, int start, int end, String emoji) {
        this.alias = alias;
        this.start = start;
        this.end = end;
        this.emoji = emoji;
    }

    public String getAlias() {
        return alias;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MatchedAlias.class.getSimpleName() + "[", "]")
            .add("alias='" + alias + "'")
            .add("start=" + start)
            .add("end=" + end)
            .add("emoji='" + emoji + "'")
            .toString();
    }
}
