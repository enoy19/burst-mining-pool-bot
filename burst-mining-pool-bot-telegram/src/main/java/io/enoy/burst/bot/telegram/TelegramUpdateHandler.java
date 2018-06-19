package io.enoy.burst.bot.telegram;

import io.enoy.burst.bot.model.Chat;
import io.enoy.burst.bot.model.repositories.ChatRepository;
import io.enoy.burst.bot.telegram.scope.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramUpdateHandler {

	private final ApplicationContext context;
	private final ChatRepository chatRepository;

	public void handle(Update update) {
		if (update.hasMessage()) {
			final Message message = update.getMessage();
			final Thread messageHandleThread = new Thread(() -> handleMessage(message), "telegramBotMessageHandler");
			messageHandleThread.setDaemon(true);
			messageHandleThread.start();
		}
	}

	private void handleMessage(Message message) {
		Long chatIdLong = message.getChatId();
		Chat chat = getChat(chatIdLong);

		TelegramContextHolder.setupContext(chat);

		final TelegramMessageHandler messageHandler = context.getBean(TelegramMessageHandler.class);
		messageHandler.handleMessage(message);
	}

	private synchronized Chat getChat(Long telegramChatId) {
		String chatId = String.valueOf(telegramChatId);

		final Optional<Chat> chatOpt = chatRepository.findById(chatId);

		Chat chat = chatOpt.orElseGet(() -> {
			Chat newChat = new Chat();
			newChat.setId(chatId);
			return newChat;
		});

		if (!chatOpt.isPresent()) {
			chat = chatRepository.save(chat);
		}

		return chat;
	}

}
