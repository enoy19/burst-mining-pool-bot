package io.enoy.burst.bot.controller;

import io.enoy.burst.bot.commons.WalletDataGatherer;
import io.enoy.burst.bot.controller.service.WalletDataGatherService;
import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.WalletData;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class WalletDataGathererScheduler {

	private final WalletDataGatherService walletDataGatherService;

	/**
	 * gathers {@link WalletData} from the {@link WalletDataGatherer}
	 * using the {@link WalletService} every 4 minutes.
	 * TODO: make rate configurable
	 */
	@Scheduled(fixedRate = 1000L * 60L * 4)
	private void startWalletGatherer() {
		walletDataGatherService.trigger();
	}

}
