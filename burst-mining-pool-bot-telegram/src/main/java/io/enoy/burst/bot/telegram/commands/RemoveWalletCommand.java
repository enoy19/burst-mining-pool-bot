package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.service.WalletKeyboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.NoSuchElementException;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class RemoveWalletCommand extends ValidatedArgumentCommand {

	private final TelegramChatService chatService;
	private final WalletKeyboardService walletKeyboardService;
	private final WalletService walletService;

	@Override
	protected boolean initialResponse(Message message) {
		final boolean walletsAvailable = walletKeyboardService.sendLinkedWalletsKeyboard("Select a wallet you want to remove");
		return !walletsAvailable; // done when no wallets are found
	}

	@Override
	protected boolean validArgument(int argumentIndex, Message message) {
		final String burstAddress = message.getText().trim();

		final Long chatId = message.getChatId();
		final String chatIdString = String.valueOf(chatId);

		try {
			walletService.unlinkWallet(chatIdString, burstAddress);
			chatService.sendMessage("Wallet removed");
		} catch (NoSuchElementException e) {
			chatService.sendMessage("Wallet does not exist.");
		}
		return true;
	}

	@Override
	protected void invalidArgument(int argumentIndex, ValidationResult validationResult, Message message) {
		chatService.sendValidationResult(validationResult);
	}

	@Override
	protected ValidationResult isArgumentValid(int argumentIndex, Message message) {
		if(walletKeyboardService.isAbortMessage(message)) {
			return ValidationResult.aborted();
		}

		String chatId = String.valueOf(message.getChatId());

		return ValidationResult.okWhen(() ->
				message.hasText() &&
						walletService.getChatWallet(chatId, message.getText().trim()).isPresent())

				.orElseGet(() -> ValidationResult.invalidArgument("Wallet was not found."));
	}

	@Override
	public void shutdown() {
		chatService.removeKeyboard();
	}

	@Override
	public boolean accepts(String message) {
		return message.trim().equalsIgnoreCase("/remove_wallet");
	}

}
