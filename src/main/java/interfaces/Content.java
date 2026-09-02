package interfaces;

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
