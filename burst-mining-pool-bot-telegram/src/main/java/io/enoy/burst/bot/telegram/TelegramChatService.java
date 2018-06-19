package io.enoy.burst.bot.telegram;

import io.enoy.burst.bot.telegram.commands.ValidatedArgumentCommand.ValidationResult;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.scope.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;

@Service
@RequiredArgsConstructor
@TelegramChatScope
public class TelegramChatService {

	private final TelegramChatServiceContextless chatServiceContextless;

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

	private long getCurrentContextChatIdLong() {
		return Long.parseLong(TelegramContextHolder.currentContext().getChatId());
	}

}

