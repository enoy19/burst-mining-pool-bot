package io.enoy.burst.bot.controller.service.threshold;

import lombok.Data;

@Data
public class ThresholdCheckResult {

	private final boolean thresholdMet;
	private final double change;

}
