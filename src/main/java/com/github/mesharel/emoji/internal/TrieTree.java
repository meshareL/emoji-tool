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

import com.github.mesharel.emoji.Emoji;
import com.github.mesharel.emoji.SkinTone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class TrieTree {
    private final Node root;

    public TrieTree(Collection<Emoji> emojis) {
        this.root = new Node();
        emojis.forEach(this::insertNode);
    }

    /**
     * 在给定的代码点中尝试匹配 emoji
     *
     * @param codePoints 代码点数组
     * @param start 初始索引
     * @return 匹配到的最终索引
     */
    public int tryMatch(int[] codePoints, int start) {
        int end = -1;
        Node node = this.root;

        for (int i = start; i < codePoints.length; i++) {
            int cp = codePoints[i];
            if (SkinTone.isSkinTone(cp)) {
                end = i;
                continue;
            }

            if (node.hasNode(cp)) {
                node = node.getNode(cp);
                end = i;
                continue;
            }

            return end;
        }

        return end;
    }

    /**
     * 向字典树中插入给定 emoji
     *
     * @param emoji emoji
     */
    private void insertNode(Emoji emoji) {
        if (emoji == null || !StringUtils.hasText(emoji.getEmoji())) {
            return;
        }

        Node trie = this.root;
        int[] cps = emoji.getEmoji().codePoints().toArray();
        for (int cp : cps) {
            if (!trie.hasNode(cp)) {
                Node child = new Node();
                trie.insertNode(cp, child);
                trie = child;
            } else {
                trie = trie.getNode(cp);
            }
        }

        trie.setEmoji(emoji);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TrieTree.class.getSimpleName() + "[", "]")
            .add("root=" + root)
            .toString();
    }

    /**
     * 字典树节点
     */
    private static class Node {
        private Emoji emoji;
        private Map<Integer, Node> children;

        public Emoji getEmoji() {
            return emoji;
        }

        public void setEmoji(Emoji emoji) {
            this.emoji = emoji;
        }

        public void insertNode(int codePoint, Node node) {
            if (this.children == null) {
                this.children = new HashMap<>();
            }

            this.children.put(codePoint, node);
        }

        public boolean hasNode(int codePoint) {
            if (this.children == null) {
                return false;
            }

            return this.children.containsKey(codePoint);
        }

        public Node getNode(int codePoint) {
            if (this.children == null) {
                return null;
            }

            return this.children.get(codePoint);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Node.class.getSimpleName() + "[", "]")
                .add("emoji=" + emoji)
                .add("children=" + children)
                .toString();
        }
    }
}
