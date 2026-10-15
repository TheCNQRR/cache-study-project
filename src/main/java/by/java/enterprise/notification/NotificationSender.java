package by.java.enterprise.notification;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationSender {

    private final Map<ChannelType, NotificationChannel> channels;

    public NotificationSender(List<NotificationChannel> channelList) {
        this.channels = channelList.stream()
                .collect(Collectors.toMap(NotificationChannel::type, Function.identity()));
    }

    public void send(ChannelType type, NotificationMessage message) {
        NotificationChannel channel = channels.get(type);
        if (channel == null) {
            throw new IllegalArgumentException("No channel registered for type " + type);
        }
        channel.send(message);
    }
}
