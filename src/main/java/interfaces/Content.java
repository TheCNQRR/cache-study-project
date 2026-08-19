package interfaces;

public sealed interface Content {
    record TextContent(String text) implements Content {}
    record ImageContent(String url, int width, int height) implements Content {}
    record FileContent(String name, long sizeBytes) implements Content {}
}
