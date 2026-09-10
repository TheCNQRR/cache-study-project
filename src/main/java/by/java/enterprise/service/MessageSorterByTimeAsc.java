package by.java.enterprise.service;

import by.java.enterprise.interfaces.MessageSorter;
import by.java.enterprise.model.Message;
import org.springframework.stereotype.Component;

@Component("byTimeAsc")
public class MessageSorterByTimeAsc implements MessageSorter {
    @Override
    public int compare(Message message1, Message message2) {
        return message1.sentAt().compareTo(message2.sentAt());
    }
}
