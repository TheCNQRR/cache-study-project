package by.java.enterprise.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Content.TextContent.class, name = "text"),
        @JsonSubTypes.Type(value = Content.ImageContent.class, name = "image"),
        @JsonSubTypes.Type(value = Content.FileContent.class, name = "file"),
        @JsonSubTypes.Type(value = Content.VideoContent.class, name = "video")
})

public sealed interface Content {
    record TextContent(String text) implements Content {}
    record ImageContent(String url, int width, int height) implements Content {}
    record FileContent(String name, long sizeBytes) implements Content {
        public FileContent {
            if (sizeBytes < 0) {
                throw new IllegalArgumentException("Size bytes cant be negative");
            }
        }
    }
    record VideoContent(String url, int width, int height, long durationSec) implements Content {}
}
