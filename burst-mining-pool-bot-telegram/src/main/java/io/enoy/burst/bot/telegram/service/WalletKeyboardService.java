package io.enoy.burst.bot.telegram.service;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletKeyboardService {

	public static final String ABORT_MESSAGE = "ABORT \u274c";

	private final TelegramChatService chatService;
	private final WalletService walletService;

	/**
	 * @return true if wallets were found and keyboard sent, false if no wallets are linked
	 */
	public boolean sendLinkedWalletsKeyboard(String message) {
		final String chatIdString = String.valueOf(TelegramContextHolder.currentContext().getChatId());

		final Set<Wallet> linkedWalletsOfChat = walletService.getWalletsOfChat(chatIdString);

		if (linkedWalletsOfChat.isEmpty()) {
			chatService.sendMessage("No wallets are linked to this chat.");
			return false;
		}

		final List<String> wallets = linkedWalletsOfChat.stream()
				.map(Wallet::getId)
				.sorted(String::compareToIgnoreCase)
				.collect(Collectors.toList());

		wallets.add(ABORT_MESSAGE);

		final String[] keyboard = wallets.toArray(new String[0]);

		chatService.sendKeyboard(message, true, keyboard);

		return true;
	}

	public boolean isAbortMessage(Message message) {
		return message.hasText() && message.getText().trim().equals(ABORT_MESSAGE);
	}

}
