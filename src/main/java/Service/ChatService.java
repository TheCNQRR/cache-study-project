package Service;

import interfaces.Content;
import records.Message;
import records.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {
    private ConcurrentHashMap<Long, User> users;
    /**
    * Хранит id чата в качестве ключа и список его сообщений
     */
    private ConcurrentHashMap<Long, List<Message>> messages;

    Optional<User> findUser(long id) {
        User user = users.getOrDefault(id, null);

        return user == null ? Optional.empty() : Optional.of(user);
    }

    List<Message> getMessages(long chatId) {
        return messages.getOrDefault(chatId, new ArrayList<>());
    }

    Optional<Message> getLastMessage(long chatId) {
        List<Message> messages = this.messages.getOrDefault(chatId, new ArrayList<>());

        if (messages.isEmpty()) {
            return Optional.empty();
        }

        return messages.stream().max(Comparator.comparing(Message::sentAt));
    }

    String preview(Message m) {
        return switch (m.content()) {
            case Content.TextContent t -> t.text().length() > 50 ? t.text().substring(0, 50) : t.text();
            case Content.ImageContent i -> "[изображение]";
            case Content.FileContent f -> "[файл]" + f.name();
        };
    }
}
