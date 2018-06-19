package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.service.WalletKeyboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class NotificationToggleCommand extends ValidatedArgumentCommand {

	private final WalletService walletService;
	private final TelegramChatService chatService;
	private final WalletKeyboardService walletKeyboardService;

	@Override
	protected boolean initialResponse(Message message) {
		final boolean walletsAvailable = walletKeyboardService.sendLinkedWalletsKeyboard("Select a wallet.");
		return !walletsAvailable; // done if no wallets found
	}

	@Override
	protected boolean validArgument(int argumentIndex, Message message) {
		final String chatId = String.valueOf(message.getChatId());
		String burstAddress = message.getText().trim();
		final boolean newValue = walletService.toggleChatNotificationsActive(chatId, burstAddress);
		final String stateString = newValue ? "Activated" : "Deactivated";

		chatService.sendMessage(String.format("Notifications: %s", stateString));
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
	public boolean accepts(String message) {
		return message.trim().equalsIgnoreCase("/notification");
	}

}
