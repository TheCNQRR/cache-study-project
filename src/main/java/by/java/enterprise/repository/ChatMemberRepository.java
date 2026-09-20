package by.java.enterprise.repository;

import by.java.enterprise.model.ChatMember;
import by.java.enterprise.model.ChatMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {
    List<ChatMember> findByChatId(Long chatId);
}
