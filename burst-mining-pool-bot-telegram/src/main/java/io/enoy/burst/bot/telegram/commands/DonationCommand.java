package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.model.properties.DeveloperDonation;
import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class DonationCommand implements Command {

	private final TelegramChatService chatService;
	private final DeveloperDonation developerDonation;

	@Value("${host.donation.address:}")
	private String hostDonationAddress;

	@Value("${host.donation.name:}")
	private String hostDonationName;

	@Override
	public boolean execute(Message message) {
		StringBuilder sb = new StringBuilder();
		sb.append("Feel free to donate burst:\n\n");

		if (!hostDonationAddress.isEmpty()) {
			sb.append("Host: ");
			sb.append(hostDonationAddress);

			if (!hostDonationName.isEmpty()) {
				sb.append(" (");
				sb.append("@");
				sb.append(hostDonationName);
				sb.append(")");
			}

			sb.append("\n\n");
		}

		sb.append("Developer/s:\n");
		developerDonation.getDonation().forEach((name, address) -> {
			sb.append(address);
			sb.append(" (");
			sb.append("@");
			sb.append(name);
			sb.append(")");
		});

		chatService.sendMessage(sb.toString());

		return true;
	}

	@Override
	public boolean accepts(String message) {
		return message.equalsIgnoreCase("/donate");
	}
}
