package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.service.WalletKeyboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class NotificationThresholdCommand extends ValidatedArgumentCommand {

	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

	private final TelegramChatService chatService;
	private final WalletKeyboardService walletKeyboardService;
	private final WalletService walletService;

	private String burstAddress;

	@Override
	protected boolean initialResponse(Message message) {
		final boolean walletsAvailable = walletKeyboardService.sendLinkedWalletsKeyboard("Select a wallet.");
		return !walletsAvailable; // done only if no wallets are available
	}


	@Override
	protected boolean validArgument(int argumentIndex, Message message) {
		if (argumentIndex == 1) {
			burstAddress = message.getText().trim();
			chatService.sendMessage("Please enter your notification threshold. It must be positive!");
			return false;
		} else if (argumentIndex == 2) {
			try {
				final double threshold = getThreshold(message);
				final String chatId = String.valueOf(message.getChatId());

				walletService.setChatNotificationThreshold(chatId, burstAddress, threshold);

				final String response = String.format(Locale.US, "Threshold set to: %f", threshold);
				chatService.sendMessage(response);
			} catch (ParseException e) {
				chatService.sendMessage("Something went wrong. Please try again");
			}

		}
		return true;
	}

	@Override
	protected void invalidArgument(int argumentIndex, ValidationResult validationResult, Message message) {
		chatService.sendValidationResult(validationResult);
	}

	@Override
	protected ValidationResult isArgumentValid(int argumentIndex, Message message) {
		if (argumentIndex == 1) {
			if(walletKeyboardService.isAbortMessage(message)) {
				return ValidationResult.aborted();
			}

			String chatId = String.valueOf(message.getChatId());

			return ValidationResult.okWhen(() ->
					message.hasText() &&
							walletService.getChatWallet(chatId, message.getText().trim()).isPresent())

					.orElseGet(() -> ValidationResult.invalidArgument("Wallet was not found."));

		} else if (argumentIndex == 2 && message.hasText()) {
			try {
				final double threshold = getThreshold(message);
				return ValidationResult.okWhen(() -> threshold >= 0)
						.orElseGet(() -> ValidationResult.invalidArgument("Threshold must be positive."));
			} catch (ParseException e) {
				return ValidationResult.invalidArgument("Could not parse number.");
			}
		}
		return null;
	}

	@Override
	public void shutdown() {
		chatService.removeKeyboard();
	}

	@Override
	public boolean accepts(String message) {
		return message.trim().equalsIgnoreCase("/notification_threshold");
	}

	private double getThreshold(Message message) throws ParseException {
		final String numberString = message.getText();
		return NUMBER_FORMAT.parse(numberString).doubleValue();
	}

}
