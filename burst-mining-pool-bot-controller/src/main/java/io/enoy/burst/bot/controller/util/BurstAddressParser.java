package io.enoy.burst.bot.controller.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BurstAddressParser {

	private static final Pattern BURST_ADDRESS_PATTERN = Pattern.compile("^(?:BURST-)?([ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{4})-([ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{4})-([ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{4})-([ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{5})$");
	private static final int BURST_ADDRESS_MAX_LENGTH = 26;
	private static final int BURST_ADDRESS_MIN_LENGTH = 20;

	public static BurstAddress parse(String burstAddressString) throws IllegalBurstAddressException {
		return new BurstAddress(burstAddressString);
	}

	public static class BurstAddress {

		private final String[] segments;

		private BurstAddress(String burstAddressString) throws IllegalBurstAddressException {
			final int length = burstAddressString.length();
			if (length > BURST_ADDRESS_MAX_LENGTH || length < BURST_ADDRESS_MIN_LENGTH) {
				throw new IllegalBurstAddressException();
			}

			Matcher matcher = BURST_ADDRESS_PATTERN.matcher(burstAddressString);

			if (matcher.find()) {
				int segmentOffset = matcher.groupCount() - 4;
				segments = new String[4];
				for (int i = 0; i < 4; i++) {
					segments[i] = matcher.group(i + 1 + segmentOffset);
				}
			} else {
				throw new IllegalBurstAddressException();
			}
		}

		public String[] getSegments() {
			return segments;
		}

		@Override
		public String toString() {
			return String.format("%s-%s-%s-%s", segments[0], segments[1], segments[2], segments[3]);
		}
	}

	public static class IllegalBurstAddressException extends Exception {

	}

}