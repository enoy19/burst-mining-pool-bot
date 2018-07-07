package io.enoy.burst.bot.controller.service;

import io.enoy.burst.bot.commons.WalletDataGatherer;
import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.model.WalletData;
import io.enoy.burst.bot.model.repositories.WalletDataRepository;
import io.enoy.burst.bot.model.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletDataGatherService {

	private final WalletRepository walletRepository;
	private final WalletDataGatherer walletDataGatherer;
	private final WalletDataRepository walletDataRepository;
	private final NotificationService notificationService;

	public void trigger() {
		List<WalletData> walletData = gatherDataOfAllRegisteredWallets();
		persistWalletDataWithCurrentTimestamp(walletData);
	}

	private List<WalletData> gatherDataOfAllRegisteredWallets() {
		final List<Wallet> wallets = walletRepository.findAll();
		return walletDataGatherer.gatherDataOf(wallets);
	}

	private void persistWalletDataWithCurrentTimestamp(List<WalletData> walletData) {
		final Date now = new Date();
		walletData.forEach(w -> w.setTimestamp(now));
		walletDataRepository.saveAll(walletData);
		notificationService.checkNotifications();
	}

}
