package io.enoy.burst.bot.controller.service.threshold;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.ThresholdMode;
import io.enoy.burst.bot.model.WalletData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SingleThresholdChecker implements ThresholdChecker {

	private final WalletService walletService;

	@Override
	public ThresholdCheckResult check(ChatWallet chatWallet) {
		final double threshold = chatWallet.getNotificationThreshold();
		final Optional<List<WalletData>> latestWalletData = walletService.getLatestTwoWalletData(chatWallet.getWallet());

		if (latestWalletData.isPresent()) {
			final List<WalletData> walletData = latestWalletData.get();
			final WalletData latest = walletData.get(0);
			final WalletData previous = walletData.get(1);

			final double latestPending = latest.getPending();               // 10   | 0
			final double previousPending = previous.getPending();           // 2    | 99
			final double deltaPending = latestPending - previousPending;    // 8    | -99
			// ^ Since negative thresholds are not allowed and we use greater than, we can ignore the fact that delta may be negative

			return new ThresholdCheckResult(deltaPending > threshold, deltaPending);
		} else {
			return new ThresholdCheckResult(false, 0);
		}
	}

	@Override
	public ThresholdMode getThresholdMode() {
		return ThresholdMode.SINGLE;
	}
}
