package io.enoy.burst.bot.telegram;

import io.enoy.burst.bot.telegram.commands.ValidatedArgumentCommand.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TelegramChatServiceContextless {

	private final TelegramLongPollingBot bot;

	public Message sendMessage(long chatId, String message) {
		return execute(new SendMessage(chatId, message));
	}

	public Message sendKeyboard(long chatId, String message, boolean oneTimeKeyboard, String... buttons) {
		ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
		keyboard.setOneTimeKeyboard(oneTimeKeyboard);

		List<KeyboardRow> keyboardRows = new ArrayList<>(buttons.length);
		for (String button : buttons) {
			KeyboardRow row = new KeyboardRow();
			row.add(button);
			keyboardRows.add(row);
		}
		keyboard.setKeyboard(keyboardRows);

		SendMessage sendMessage = new SendMessage(chatId, message);
		sendMessage.setReplyMarkup(keyboard);

		return execute(sendMessage);
	}

	public void sendValidationResult(long chatId, ValidationResult validationResult) {
		final String message = validationResult.getMessage();

		if (Objects.nonNull(message) && !message.trim().isEmpty()) {
			sendMessage(chatId, String.format("%s: %s", validationResult.getType().getMessagePrefix(), message));
		} else {
			sendMessage(chatId, validationResult.getType().getMessagePrefix() + ".");
		}
	}

	public void removeKeyboard(long chatId) {
		ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove();
		SendMessage sendMessage = new SendMessage(chatId, "Removing keyboard...");
		sendMessage.setReplyMarkup(removeMarkup);
		Message message = execute(sendMessage);

		DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
		execute(deleteMessage);
	}

	private <R extends Serializable, T extends BotApiMethod<R>> R execute(T method) {
		try {
			return executeWithException(method);
		} catch (TelegramApiException e) {
			throw new IllegalStateException(e);
		}
	}

	private <R extends Serializable, T extends BotApiMethod<R>> R executeWithException(T method) throws TelegramApiException {
		return bot.execute(method);
	}
}

