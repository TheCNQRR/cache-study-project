package by.java.enterprise.service;

import by.java.enterprise.interfaces.MessageSorter;
import by.java.enterprise.model.Message;
import org.springframework.stereotype.Component;

@Component("byAuthorThenTime")
public class MessageSorterByAuthorThenTime implements MessageSorter {
    @Override
    public int compare(Message message1, Message message2) {
        int byAuthor = Long.compare(message1.senderId(), message2.senderId());
        return byAuthor != 0 ? byAuthor : message1.sentAt().compareTo(message2.sentAt());
    }
}
