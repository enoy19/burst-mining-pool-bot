package io.enoy.burst.bot.controller.service.threshold;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.ThresholdMode;
import io.enoy.burst.bot.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccumulateThresholdChecker implements ThresholdChecker {

	private final WalletService walletService;

	@Override
	public ThresholdCheckResult check(ChatWallet chatWallet) {
		final double pendingGrowth = getPendingGrowth(chatWallet);
		final boolean thresholdMet = pendingGrowth > chatWallet.getNotificationThreshold();

		return new ThresholdCheckResult(thresholdMet, pendingGrowth);
	}

	private double getPendingGrowth(ChatWallet chatWallet) {
		final Date lastThresholdReached = chatWallet.getLastThresholdReached();
		final Wallet wallet = chatWallet.getWallet();

		if (Objects.isNull(lastThresholdReached)) {
			return walletService.getPendingGrowth(wallet);
		} else {
			return walletService.getPendingGrowth(wallet, lastThresholdReached);
		}
	}

	@Override
	public ThresholdMode getThresholdMode() {
		return ThresholdMode.ACCUMULATE;
	}

}
