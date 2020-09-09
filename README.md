# emoji-tool
emoji-tool 是一个处理 emoji 的 java 库

## 安装
Maven
```xml
<dependency>
    <groupId>com.github.mesharel</groupId>
    <artifactId>emoji-tool</artifactId>
    <version>0.0.1</version>
</dependency>
```

Gradle
```groovy
implementation 'com.github.mesharel:emoji-tool:0.0.1'
```

## 使用
#### 提取字符串中的emoji
```java
EmojiProcessor processor = new EmojiProcessor(/* emoji list */);
processor.extract("👋, 👋🏻, 👋🏼");
```

#### 替换字符串中的 emoji 别名
```java
processor.replaceByAlias(":grinning: :smiley_cat:");
// 😀 😺
```

## License
[Apache-2.0](https://github.com/meshareL/emoji-tool/blob/master/LICENSE)
