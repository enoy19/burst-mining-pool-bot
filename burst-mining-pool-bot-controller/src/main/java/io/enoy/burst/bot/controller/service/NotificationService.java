package io.enoy.burst.bot.controller.service;

import io.enoy.burst.bot.commons.PendBurstNotificationEvent;
import io.enoy.burst.bot.commons.PendBurstNotificationEventHandler;
import io.enoy.burst.bot.controller.service.threshold.ThresholdCheckResult;
import io.enoy.burst.bot.controller.service.threshold.ThresholdCheckerService;
import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.WalletData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

	private final WalletService walletService;
	private final Set<PendBurstNotificationEventHandler> eventHandlers;
	private final ThresholdCheckerService thresholdCheckerService;
	private final ExecutorService executorService = Executors.newFixedThreadPool(4);

	public void checkNotifications() {
		final List<ChatWallet> chatWallets = walletService.getNotificationChatWallets();

		for (ChatWallet chatWallet : chatWallets) {
			checkChatWalletNotification(chatWallet);
		}
	}

	private void checkChatWalletNotification(ChatWallet chatWallet) {
		ThresholdCheckResult thresholdMet = thresholdCheckerService.check(chatWallet);

		if (thresholdMet.isThresholdMet()) {
			final double pending = walletService.getLatestWalletData(chatWallet.getWallet())
					.map(WalletData::getPending)
					.orElse(0d);

			sendNotification(chatWallet, pending, thresholdMet.getChange());
			walletService.updateLastThresholdReached(chatWallet.getId(), new Date());
		}
	}

	private void sendNotification(ChatWallet chatWallet, double pending, double pendingGrowth) {
		final PendBurstNotificationEvent event = new PendBurstNotificationEvent();
		event.setChatId(chatWallet.getChat().getId());
		event.setWalletId(chatWallet.getWallet().getId());
		event.setPending(pending);
		event.setGrowth(pendingGrowth);

		eventHandlers.forEach(handler -> submitHandleEvent(handler, event));
	}

	private void submitHandleEvent(final PendBurstNotificationEventHandler handler, final PendBurstNotificationEvent event) {
		executorService.submit(() -> {
			try {
				handler.accept(event);
			} catch (Exception e) {
				log.warn("failed to send notification: {}", e.getMessage());
				log.debug(e.getMessage(), e);
			}
		});
	}

}
