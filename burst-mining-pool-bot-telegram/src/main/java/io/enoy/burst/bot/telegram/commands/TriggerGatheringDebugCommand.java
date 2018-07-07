package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletDataGatherService;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

@Component
@TelegramChatScope
@RequiredArgsConstructor
@Profile("debug")
public class TriggerGatheringDebugCommand implements Command {

	private final WalletDataGatherService walletDataGatherService;
	private final TelegramChatService chatService;

	@Override
	public boolean execute(Message message) {
		walletDataGatherService.trigger();
		chatService.sendMessage("ok");
		return true;
	}

	@Override
	public boolean accepts(String message) {
		return message.equalsIgnoreCase("/trigger_gatherer");
	}
}
