package io.enoy.burst.bot.telegram;

import io.enoy.burst.bot.commons.PendBurstNotificationEvent;
import io.enoy.burst.bot.commons.PendBurstNotificationEventHandler;
import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TelegramPendBurstNotificationEventHandler implements PendBurstNotificationEventHandler {

	private final TelegramChatServiceContextless chatServiceContextless;
	private final WalletService walletService;

	@Override
	public void accept(PendBurstNotificationEvent event) {
		final String chatIdString = event.getChatId();
		final long chatId = Long.valueOf(chatIdString);

		final Set<Wallet> chatWallets = walletService.getWalletsOfChat(chatIdString);

		final String message;
		if (chatWallets.size() == 1) {
			message = String.format(Locale.US, "Pend. changed: %.2f BURST (+%.2f)", event.getPending(), event.getGrowth());
		} else {
			message = String.format(Locale.US, "%s: %.2f BURST (+%.2f)", event.getWalletId(), event.getPending(), event.getGrowth());
		}

		chatServiceContextless.sendMessage(chatId, message);
	}

}
