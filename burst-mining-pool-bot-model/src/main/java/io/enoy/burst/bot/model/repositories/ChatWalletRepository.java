package io.enoy.burst.bot.model.repositories;

import io.enoy.burst.bot.model.Chat;
import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatWalletRepository extends JpaRepository<ChatWallet, Long> {

	List<ChatWallet> findAllByChat(Chat chat);
	List<ChatWallet> findAllByNotificationActive(boolean notificationActive);
	Optional<ChatWallet> findByChatAndWallet(Chat chat, Wallet wallet);

}
