package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.ThresholdMode;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.service.WalletKeyboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class NotificationThresholdModeCommand extends ValidatedArgumentCommand {

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

			final List<String> modesList = Arrays.stream(ThresholdMode.values())
					.map(ThresholdMode::name)
					.collect(Collectors.toList());

			final String[] modes = modesList.toArray(new String[ThresholdMode.values().length]);

			chatService.sendKeyboard(getThresholdModesMessage(), true, modes);
			return false;
		} else if (argumentIndex == 2) {
			ThresholdMode mode = ThresholdMode.valueOf(message.getText());
			final String chatId = String.valueOf(message.getChatId());

			walletService.setThresholdMode(chatId, burstAddress, mode);

			chatService.sendMessage("Threshold mode set.");
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
			// yes I copied this part... I feel so bad now.
			if (walletKeyboardService.isAbortMessage(message)) {
				return ValidationResult.aborted();
			}

			String chatId = String.valueOf(message.getChatId());

			return ValidationResult.okWhen(() ->
					message.hasText() &&
							walletService.getChatWallet(chatId, message.getText().trim()).isPresent())

					.orElseGet(() -> ValidationResult.invalidArgument("Wallet was not found."));

		} else if (argumentIndex == 2 && message.hasText()) {
			try {
				ThresholdMode.valueOf(message.getText());
				return ValidationResult.ok();
			} catch (IllegalArgumentException e) {
				return ValidationResult.invalidArgument("Invalid threshold mode.");
			}
		}
		chatService.sendMessage("Invalid argument!");
		return null;
	}

	@Override
	public void shutdown() {
		chatService.removeKeyboard();
	}

	private String getThresholdModesMessage() {
		final String thresholdModesHelpTexts =
				Arrays.stream(ThresholdMode.values())
						.map(mode -> String.format("%s - %s", mode.name(), mode.getHelpText()))
						.collect(Collectors.joining("\n\n"));

		return "Please select a threshold mode:\n" + thresholdModesHelpTexts;
	}

	@Override
	public boolean accepts(String message) {
		return message.equalsIgnoreCase("/threshold_mode");
	}
}
