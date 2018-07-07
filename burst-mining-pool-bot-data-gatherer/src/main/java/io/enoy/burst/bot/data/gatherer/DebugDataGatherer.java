package io.enoy.burst.bot.data.gatherer;

import io.enoy.burst.bot.commons.WalletDataGatherer;
import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.model.WalletData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Profile("debug")
public class DebugDataGatherer implements WalletDataGatherer {

	private static final Random RANDOM = new Random();
	private final WalletService walletService;

	@Value("${debug.random.increase:10}")
	private double randomIncrease;

	@Override
	public List<WalletData> gatherDataOf(Collection<Wallet> wallets) {
		List<WalletData> walletDataList = new ArrayList<>(wallets.size());

		for (Wallet wallet : wallets) {
			final Optional<WalletData> latestWalletDataOpt = walletService.getLatestWalletData(wallet);
			final double previousPending = latestWalletDataOpt.map(WalletData::getPending).orElse(0d);

			double newPending = previousPending + RANDOM.nextDouble() * randomIncrease;
			if(newPending > 100) { // payout at 100
				newPending = 0;
			}

			WalletData walletData = new WalletData();
			walletData.setWallet(wallet);
			walletData.setPending(newPending);
			// ... ignore rest

			walletDataList.add(walletData);
		}

		return walletDataList;
	}

}
