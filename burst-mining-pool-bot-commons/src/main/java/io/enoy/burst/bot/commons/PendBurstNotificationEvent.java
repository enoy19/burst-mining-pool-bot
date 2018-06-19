package io.enoy.burst.bot.commons;

import lombok.Data;

@Data
public class PendBurstNotificationEvent {

	private String chatId;
	private String walletId;
	private double pending;
	private double growth;

}
