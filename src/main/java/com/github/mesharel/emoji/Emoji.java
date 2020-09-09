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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 该类代表一个 emoji<br>
 *
 * 该类创建后不可修改
 */
public class Emoji {
    private final String emoji;
    private final boolean skinnable;
    private final List<String> aliases;
    private final List<String> tags;

    /**
     * 创建一个 emoji
     *
     * @param emoji emoji 表情符号
     * @param aliases emoji 别名
     * @param tags emoji 标签
     * @param skinnable emoji 是否支持皮肤
     */
    public Emoji(String emoji, List<String> aliases, List<String> tags, boolean skinnable) {
        this.emoji = emoji;
        this.aliases = Collections.unmodifiableList(aliases);
        this.tags = Collections.unmodifiableList(tags);
        this.skinnable = skinnable;
    }

    public String getEmoji() {
        return emoji;
    }

    public boolean isSkinnable() {
        return skinnable;
    }

    /**
     * emoji 的所有别名
     *
     * @return 不可修改列表
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * emoji 所有标签
     *
     * @return 不可修改列表
     */
    public List<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emoji emoji1 = (Emoji) o;
        return emoji.equals(emoji1.emoji);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emoji);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Emoji.class.getSimpleName() + "[", "]")
            .add("emoji='" + emoji + "'")
            .add("skinnable=" + skinnable)
            .add("aliases=" + aliases)
            .add("tags=" + tags)
            .toString();
    }
}
