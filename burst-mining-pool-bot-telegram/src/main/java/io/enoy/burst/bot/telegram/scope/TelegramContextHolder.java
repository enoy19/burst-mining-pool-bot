package io.enoy.burst.bot.telegram.scope;

import io.enoy.burst.bot.model.Chat;
import lombok.Data;

public class TelegramContextHolder {

	private static final ThreadLocal<TelegramContext> telegramContextThreadLocal = new ThreadLocal<>();

	public static TelegramContext currentContext() {
		return telegramContextThreadLocal.get();
	}

	public static void setupContext(final Chat chat) {
		telegramContextThreadLocal.set(new TelegramContext(chat.getId()));
	}

	@Data
	public static class TelegramContext {
		private final String chatId;
	}

}
