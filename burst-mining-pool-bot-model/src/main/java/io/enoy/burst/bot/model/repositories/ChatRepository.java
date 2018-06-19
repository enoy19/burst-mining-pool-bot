package io.enoy.burst.bot.model.repositories;

import io.enoy.burst.bot.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

}
