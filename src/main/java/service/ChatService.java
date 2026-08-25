package service;

import interfaces.Content;
import model.Message;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ChatService {
    /**
    * Хранит id чата в качестве ключа и список его сообщений
     */
    private ConcurrentHashMap<Long, List<Message>> messages;

    public HashMap<Long, List<Message>> getMessages() {
        return new HashMap<>(messages);
    }

    public List<Message> getMessagesByChatId(long chatId) {
        return messages.getOrDefault(chatId, new ArrayList<>());
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

    public Optional<Long> mostActiveAuthor(long chatId) {
        List<Message> snapshot = getMessagesByChatId(chatId);

        Map<Long, Long> countByAuthor = snapshot.stream().collect(Collectors.groupingBy(Message::senderId, Collectors.counting()));

        return countByAuthor.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }


}
