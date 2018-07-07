package io.enoy.burst.bot.model;

public enum ThresholdMode {
	ACCUMULATE("Adds all incrementations of your pending payment and notifies you when the amount is greater than your threshold."),
	SINGLE("Notifications will be sent when a single pending payment increase is greater than your threshold"),
	PAYOUT("Works like accumulate but only counts payouts (pending payment decreases).");

	public static final ThresholdMode DEFAULT_THRESHOLD_MODE = ACCUMULATE;

	private final String helpText;

	ThresholdMode(String helpText) {
		this.helpText = helpText;
	}

	public String getHelpText() {
		return helpText;
	}

}
