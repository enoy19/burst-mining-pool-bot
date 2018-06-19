package io.enoy.burst.bot.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class BurstPoolBot extends TelegramLongPollingBot {

	private final TelegramUpdateHandler updateHandler;

	@Value("${bot.token}")
	private String botToken;

	@Value("${bot.username}")
	private String botUsername;

	@Override
	public void onUpdateReceived(Update update) {
		updateHandler.handle(update);
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}
}
