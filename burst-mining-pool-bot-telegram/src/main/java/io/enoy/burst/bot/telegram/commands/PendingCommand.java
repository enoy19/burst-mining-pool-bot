package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.WalletData;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@TelegramChatScope
public class PendingCommand extends WalletDataCommand {

	@Autowired
	public PendingCommand(WalletService walletService, TelegramChatService chatService) {
		super(walletService, chatService);
	}

	@Override
	protected String dataToString(WalletData walletData) {
		return String.format(Locale.US, "%.2f", walletData.getPending());
	}

	@Override
	public boolean accepts(String message) {
		return message.trim().equalsIgnoreCase("/pending");
	}
}
