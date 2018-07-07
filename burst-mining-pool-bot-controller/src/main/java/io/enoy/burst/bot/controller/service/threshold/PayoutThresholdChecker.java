package io.enoy.burst.bot.controller.service.threshold;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.ThresholdMode;
import io.enoy.burst.bot.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PayoutThresholdChecker extends AbstractAccumulateThresholdChecker {

	private final WalletService walletService;

	@Override
	protected double getPendingChangeInternal(Wallet wallet) {
		return walletService.getPayouts(wallet);
	}

	@Override
	protected double getPendingChangeInternal(Wallet wallet, Date lastThresholdReached) {
		return walletService.getPayouts(wallet, lastThresholdReached);
	}

	@Override
	public ThresholdMode getThresholdMode() {
		return ThresholdMode.PAYOUT;
	}

}
