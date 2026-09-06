package by.java.enterprise.service;

import by.java.enterprise.config.ChatProperties;
import by.java.enterprise.controller.UserController;
import by.java.enterprise.dto.request.ChatStoryRequest;
import by.java.enterprise.dto.request.CreateChatRequest;
import by.java.enterprise.dto.request.CreateMessageRequest;
import by.java.enterprise.dto.request.ViewMessageRequest;
import by.java.enterprise.dto.response.ChatStoryResponse;
import by.java.enterprise.dto.response.CreateChatResponse;
import by.java.enterprise.dto.response.CreateMessageResponse;
import by.java.enterprise.exception.MessageAlreadyViewedException;
import by.java.enterprise.interfaces.Content;
import by.java.enterprise.model.Chat;
import by.java.enterprise.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ChatService {
    /**
    * Хранит id чата в качестве ключа и список его сообщений
     */
    private ConcurrentHashMap<Long, List<Message>> messages = new ConcurrentHashMap<>();;
    private final AtomicLong chatIdCounter = new AtomicLong(0);
    private final AtomicLong messageIdCounter = new AtomicLong(0);
    private final CommonService commonService;
    private final ChatProperties chatProperties;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Value("${chat.default-page-limit}")
    private int defaultPageLimit;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.features.search-enabled:false}")
    private boolean searchEnabled;

    private UserService userService;

    public ChatService(UserService userService, CommonService commonService, ChatProperties chatProperties) {
        this.userService = userService;
        this.commonService = commonService;
        this.chatProperties = chatProperties;
        log.info("UserService hash: {}", System.identityHashCode(this.userService));
    }

    public HashMap<Long, List<Message>> getMessages() {
        return new HashMap<>(messages);
    }

    public List<Message> getMessagesByChatId(long chatId) {
        return messages.getOrDefault(chatId, new ArrayList<>());
    }

    public CreateChatResponse createChat(CreateChatRequest request) {
        long chatId = chatIdCounter.getAndIncrement();

        Chat chat = new Chat(
                chatId,
                request.title()
        );

        messages.put(chatId, new CopyOnWriteArrayList<>());

        return new CreateChatResponse(
                chat.id(),
                chat.title()
        );
    }

    public CreateMessageResponse createMessage(CreateMessageRequest request) {
        Message message = new Message(
                messageIdCounter.getAndIncrement(),
                request.chatId(),
                request.senderId(),
                request.content(),
                Instant.now(),
                false
        );

        List<Message> list = messages.computeIfAbsent(request.chatId(), k -> new CopyOnWriteArrayList<>());
        list.add(message);

        return new CreateMessageResponse(
                message.id(),
                message.chatId(),
                message.senderId(),
                message.content(),
                message.sentAt()
        );
    }

    public ChatStoryResponse getChatStory(ChatStoryRequest request) {
        List<Message> snapshot = getMessagesByChatId(request.chatId());

        List<Message> paginationResponse = snapshot.stream()
                .sorted(Comparator.comparing(Message::sentAt).reversed())
                .limit(request.quantity())
                .toList();

        return new ChatStoryResponse(paginationResponse);
    }

    public void viewMessage(ViewMessageRequest request) {
        List<Message> messages = getMessagesByChatId(request.chatId());

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id() == request.messageId()) {
                Message oldMessage = messages.get(i);
                if (oldMessage.isViewed()) {
                    throw new MessageAlreadyViewedException("Сообщение уже просмотрено");
                }

                Message newMessage = new Message(
                        oldMessage.id(),
                        oldMessage.chatId(),
                        oldMessage.senderId(),
                        oldMessage.content(),
                        oldMessage.sentAt(),
                        true
                );

                messages.set(i, newMessage);
                break;
            }
        }
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
                     case Content.TextContent t -> "TextContent";
                     case Content.ImageContent i -> "ImageContent";
                     case Content.FileContent f -> "FileContent";
                     case Content.VideoContent v -> "VideoContent";
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
