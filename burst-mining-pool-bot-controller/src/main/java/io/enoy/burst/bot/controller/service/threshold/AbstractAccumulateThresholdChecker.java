package io.enoy.burst.bot.controller.service.threshold;

import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.ThresholdMode;
import io.enoy.burst.bot.model.Wallet;

import java.util.Date;
import java.util.Objects;

public abstract class AbstractAccumulateThresholdChecker implements ThresholdChecker {

	@Override
	public ThresholdCheckResult check(ChatWallet chatWallet) {
		final double pendingGrowth = getPendingChange(chatWallet);
		final boolean thresholdMet = pendingGrowth > chatWallet.getNotificationThreshold();

		return new ThresholdCheckResult(thresholdMet, pendingGrowth);
	}

	private double getPendingChange(ChatWallet chatWallet) {
		final Date lastThresholdReached = chatWallet.getLastThresholdReached();
		final Wallet wallet = chatWallet.getWallet();

		if (Objects.isNull(lastThresholdReached)) {
			return getPendingChangeInternal(wallet);
		} else {
			return getPendingChangeInternal(wallet, lastThresholdReached);
		}
	}

	protected abstract double getPendingChangeInternal(Wallet wallet);

	protected abstract double getPendingChangeInternal(Wallet wallet, Date lastThresholdReached);

	@Override
	public ThresholdMode getThresholdMode() {
		return ThresholdMode.ACCUMULATE;
	}

}
