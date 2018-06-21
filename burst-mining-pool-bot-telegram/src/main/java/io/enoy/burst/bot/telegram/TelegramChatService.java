package io.enoy.burst.bot.telegram;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.Chat;
import io.enoy.burst.bot.telegram.commands.ValidatedArgumentCommand.ValidationResult;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.scope.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@TelegramChatScope
public class TelegramChatService {

	private final TelegramChatServiceContextless chatServiceContextless;
	private final WalletService walletService;

	public Message sendMessage(String message) {
		long chatId = getCurrentContextChatIdLong();
		return chatServiceContextless.sendMessage(chatId, message);
	}

	public Message sendKeyboard(String message, boolean oneTimeKeyboard, String... buttons) {
		long chatId = getCurrentContextChatIdLong();
		return chatServiceContextless.sendKeyboard(chatId, message, oneTimeKeyboard, buttons);
	}

	public void sendValidationResult(ValidationResult validationResult) {
		long chatId = getCurrentContextChatIdLong();
		chatServiceContextless.sendValidationResult(chatId, validationResult);
	}

	public void removeKeyboard() {
		long chatId = getCurrentContextChatIdLong();
		chatServiceContextless.removeKeyboard(chatId);
	}

	public boolean isAdmin(String username) {
		return chatServiceContextless.isAdmin(username);
	}

	private long getCurrentContextChatIdLong() {
		return Long.parseLong(TelegramContextHolder.currentContext().getChatId());
	}

	public void broadcast(String messageText) {
		List<Chat> chats = walletService.getChats();

		for (Chat chat : chats) {
			long chatId = Long.parseLong(chat.getId());

			try {
				chatServiceContextless.sendMessage(chatId, String.format("Broadcast:%n%s", messageText));
			} catch (IllegalStateException e) {
				log.debug("User \"{}\" blocked the bot", chatId);
				log.trace(e.getMessage(), e);
			}
		}
	}
}

