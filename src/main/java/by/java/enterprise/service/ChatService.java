package by.java.enterprise.service;

import by.java.enterprise.interfaces.Content;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.Message;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class ChatService {
    /**
    * Хранит id чата в качестве ключа и список его сообщений
     */
    private ConcurrentHashMap<Long, List<Message>> messages;
    private final AtomicLong chatIdCounter = new AtomicLong(0);
    private final AtomicLong messageIdCounter = new AtomicLong(0);

    public HashMap<Long, List<Message>> getMessages() {
        return new HashMap<>(messages);
    }

    public List<Message> getMessagesByChatId(long chatId) {
        return messages.getOrDefault(chatId, new ArrayList<>());
    }

    public Chat createChat(String title) {
        return new Chat(
                chatIdCounter.getAndIncrement(),
                title
        );
    }

    public Message createMessage(long chatId, long senderId, Content content) {
        return new Message(
               messageIdCounter.getAndIncrement(),
                chatId,
                senderId,
                content,
                Instant.now()
        );
    }

    public Optional<Message> getLastMessage(long chatId) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        if (snapshot.isEmpty()) {
            return Optional.empty();
        }

        return snapshot.stream().max(Comparator.comparing(Message::sentAt));
    }

    public static String preview(Message m) {
        return switch (m.content()) {
            case Content.TextContent t -> t.text().length() > 50 ? t.text().substring(0, 50) : t.text();
            case Content.ImageContent i -> "[изображение]";
            case Content.FileContent f -> "[файл]" + f.name();
            case Content.VideoContent v -> "[видео]" + v.url();
        };
    }

    public List<Message> search(long chatId, String query, int limit) {
        HashMap<Long, List<Message>> snapshot = getMessages();

        return snapshot.entrySet().stream()
                .filter(entry -> entry.getKey() == chatId)
                .flatMap(entry -> entry.getValue().stream())
                .filter(
                        messages -> messages.content() instanceof Content.TextContent(String text) &&
                                text.toLowerCase().contains(query.toLowerCase()))
                .limit(limit)
                .toList();
    }

    public Map<Long, Long> countByAuthor(long chatId) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        return snapshot.stream().collect(Collectors.groupingBy(Message::senderId, Collectors.counting()));
    }

    public Optional<Message> firstMentioning(long chatId, String query) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        return snapshot.stream().filter(
                message -> message.content() instanceof Content.TextContent(String text) &&
                        text.contains(query)).findFirst();
    }

    public List<Message> lastMessages(long chatId, int n) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        return snapshot.stream().sorted(Comparator.comparing(Message::sentAt)).skip(n).toList();
    }

    public Map<LocalDate, Long> countByDay(long chatId) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        return snapshot.stream().collect(Collectors.groupingBy(
                m -> m.sentAt().atZone(ZoneOffset.UTC).toLocalDate(), Collectors.counting()));
    }

    public Optional<Long> mostActiveAuthor(long chatId) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        Map<Long, Long> countByAuthor = snapshot.stream().collect(Collectors.groupingBy(Message::senderId, Collectors.counting()));

        return countByAuthor.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    public Set<String> allWords(long chatId) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        List<Message> textMessages = snapshot.stream().filter(message -> message.content() instanceof Content.TextContent).toList();

        return textMessages.stream()
                .map(message ->  ((Content.TextContent) message.content()).text().split(" "))
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }

    public Map<Long, Long> topChatsByActivity(int n) {
        HashMap<Long, List<Message>> snapshot = getMessages();

        Map<Long, Long> messagesCount = snapshot.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Message::chatId, Collectors.counting()));


        return messagesCount.entrySet().stream().sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public long totalFileSize() {
        HashMap<Long, List<Message>> snapshot = getMessages();

        return snapshot.values().stream().flatMap(Collection::stream)
                .filter(message -> message.content() instanceof Content.FileContent)
                .mapToLong(message -> ((Content.FileContent) message.content()).sizeBytes())
                .sum();
    }

    public Map<String, Long> countByContentType() {
        HashMap<Long, List<Message>> snapshot = getMessages();

         return snapshot.values().stream().flatMap(Collection::stream)
                 .collect(Collectors.groupingBy(m -> switch (m.content()) {
                     case Content.TextContent _ -> "TextContent";
                     case Content.ImageContent _ -> "ImageContent";
                     case Content.FileContent _ -> "FileContent";
                     case Content.VideoContent _ -> "VideoContent";
                 }, Collectors.counting()));
    }

    public Map<Boolean, List<Message>> splitByImage() {
        HashMap<Long, List<Message>> snapshot = getMessages();

        return snapshot.values().stream().flatMap(Collection::stream)
                .collect(Collectors.partitioningBy(
                        message -> message.content() instanceof Content.ImageContent));
    }

    public Map<Long, Message> lastMessagePerChat() {
        HashMap<Long, List<Message>> snapshot = getMessages();

        return snapshot.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry ->
                                entry.getValue().stream().max(Comparator.comparing(Message::sentAt)).orElseThrow()
                ));
    }

    Optional<Message> getLastMessageReduce(long chatId) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        return snapshot.stream().reduce((a, b) -> a.sentAt().isAfter(b.sentAt()) ? a : b);
    }
}
