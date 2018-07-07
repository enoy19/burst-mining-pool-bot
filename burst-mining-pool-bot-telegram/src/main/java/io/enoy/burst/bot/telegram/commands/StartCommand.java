package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class StartCommand implements Command {

	private final TelegramChatService chatService;

	@Override
	public boolean execute(Message message) {
		chatService.sendMessage("Welcome!\nUse /add_wallet and enter your burst address to add your wallet.\nEnter / to see all available commands.");
		return true;
	}

	@Override
	public boolean accepts(String message) {
		return message.equalsIgnoreCase("/start");
	}

}
