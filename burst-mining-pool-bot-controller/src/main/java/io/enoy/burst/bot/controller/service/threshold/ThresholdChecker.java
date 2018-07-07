package io.enoy.burst.bot.controller.service.threshold;

import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.ThresholdMode;

public interface ThresholdChecker {
	boolean isThresholdMet(ChatWallet chatWallet);
	ThresholdMode getThresholdMode();
}
