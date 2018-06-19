package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.model.WalletData;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import io.enoy.burst.bot.telegram.scope.TelegramContextHolder;
import io.enoy.burst.bot.telegram.scope.TelegramContextHolder.TelegramContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public abstract class WalletDataCommand implements Command {

	private final WalletService walletService;
	private final TelegramChatService chatService;

	@Override
	public boolean execute(Message message) {
		final TelegramContext telegramContext = TelegramContextHolder.currentContext();
		final Set<Wallet> wallets = walletService.getWalletsOfChat(telegramContext.getChatId());
		final Set<WalletData> latestWalletData = walletService.getLatestWalletData(wallets);
		final boolean singleWallet = wallets.size() == 1;

		if (latestWalletData.isEmpty()) {
			chatService.sendMessage("No data found!");
		} else {
			final String dataMessage =
					latestWalletData.stream()
							.sorted(Comparator.comparing(o -> o.getWallet().getId()))
							.map(data -> getWalletDataFormatted(data, singleWallet))
							.collect(Collectors.joining("\n"));

			chatService.sendMessage(dataMessage);
		}

		return true;
	}

	private String getWalletDataFormatted(WalletData data, boolean singleWallet) {
		if (singleWallet) {
			return dataToString(data);
		} else {
			return String.format("%s:%n%s", data.getWallet().getId(), dataToString(data));
		}
	}

	protected abstract String dataToString(WalletData walletData);

}
