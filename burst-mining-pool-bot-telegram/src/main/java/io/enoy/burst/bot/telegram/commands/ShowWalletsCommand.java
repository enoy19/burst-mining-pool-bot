package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.scope.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class ShowWalletsCommand implements Command {

	private final TelegramChatService chatService;
	private final WalletService walletService;

	@Override
	public boolean execute(Message message) {
		final String chatId = TelegramContextHolder.currentContext().getChatId();
		final Set<Wallet> wallets = walletService.getWalletsOfChat(chatId);

		if (wallets.isEmpty()) {
			chatService.sendMessage("You added no wallets to this chat yet. Use /add_wallet");
		} else {
			final String walletsString =
					wallets.stream()
							.map(Wallet::getId)
							.sorted()
							.collect(Collectors.joining("\n"));

			chatService.sendMessage(walletsString);
		}

		return true;
	}

	@Override
	public boolean accepts(String message) {
		return message.trim().equalsIgnoreCase("/show_wallets");
	}
}
