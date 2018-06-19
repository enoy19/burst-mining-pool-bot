package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.controller.util.BurstAddressParser;
import io.enoy.burst.bot.controller.util.BurstAddressParser.BurstAddress;
import io.enoy.burst.bot.controller.util.BurstAddressParser.IllegalBurstAddressException;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class AddWalletCommand extends ValidatedArgumentCommand {

	private static final String ABORT_MESSAGE = "Abort";

	private final TelegramChatService chatService;
	private final WalletService walletService;

	private BurstAddress burstAddress;

	@Override
	public void init() {
		super.init();
		burstAddress = null;
	}

	@Override
	protected boolean initialResponse(Message message) {
		chatService.sendMessage("Please enter Burst address.\nExample: BURST-AAAA-BBBB-CCCC-DDDDD\n\nEnter \"" + ABORT_MESSAGE + "\" to stop this command.");
		return false;
	}

	@Override
	protected boolean validArgument(int argumentIndex, Message message) {
		final String burstAddressString = burstAddress.toString();
		final Long chatId = message.getChatId();
		final String chatIdString = String.valueOf(chatId);

		walletService.registerWallet(burstAddressString);
		walletService.linkWallet(burstAddressString, chatIdString);

		chatService.sendMessage("Wallet was added. It might take a while until you can view your data.");

		return true;
	}

	@Override
	protected void invalidArgument(int argumentIndex, ValidationResult validationResult, Message message) {
		chatService.sendValidationResult(validationResult);
	}

	@Override
	protected ValidationResult isArgumentValid(int argumentIndex, Message message) {
		if (!message.hasText()) {
			return ValidationResult.invalidArgument("Invalid message.");
		}

		final String burstAddressInput = message.getText().trim();

		if (burstAddressInput.equalsIgnoreCase(ABORT_MESSAGE)) {
			chatService.sendMessage("Aborted.");
			return ValidationResult.aborted();
		}

		try {
			burstAddress = BurstAddressParser.parse(burstAddressInput);
			return ValidationResult.ok();
		} catch (IllegalBurstAddressException e) {
			return ValidationResult.invalidArgument("Invalid address.");
		}
	}

	@Override
	public boolean accepts(String message) {
		return message.trim().equalsIgnoreCase("/add_wallet");
	}

}
