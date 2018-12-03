package io.enoy.burst.bot.controller;

import io.enoy.burst.bot.controller.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public final class DatabaseJanitor {

    private final WalletService walletService;

    @Value("${wallet.data.keep.hours}")
    private long keepWalletDataTime;

    /**
     * cleanup method that runs every 61 minutes and once at startup (thank you Jon Hanna https://english.stackexchange.com/a/154469)
     */
    @Scheduled(fixedRate = 1000L * 60L )
    private void cleanUp() {
        long walletDataCount = walletService.countAllWalletData();
        log.debug("Starting cleanup. Current walletDataCount: {}", walletDataCount);

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime deleteBefore = now.minusHours(keepWalletDataTime);
        walletService.deleteWalletDataBefore(deleteBefore);

        walletDataCount = walletService.countAllWalletData();
        log.debug("Cleanup finished. New walletDataCount: {}", walletDataCount);
    }

}
