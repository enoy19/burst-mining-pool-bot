package io.enoy.burst.bot.controller.service.threshold;

import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.WalletData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class SingleThresholdCheckerTest {

	@Mock
	private ChatWallet chatWallet;

	@Mock
	private WalletService walletService;

	private SingleThresholdChecker singleThresholdChecker;

	@Before
	public void before() {
		Mockito.when(chatWallet.getNotificationThreshold()).thenReturn(10d);
		singleThresholdChecker = new SingleThresholdChecker(walletService);
	}

	@Test
	public void thresholdMetTest() {
		Optional<List<WalletData>> walletDataListOpt = getFakeData(11, 0);
		Mockito.when(walletService.getLatestTwoWalletData(Mockito.any())).thenReturn(walletDataListOpt);

		final ThresholdCheckResult result = singleThresholdChecker.check(chatWallet);

		Assert.assertEquals(11, result.getChange(), 0);
		Assert.assertEquals(true, result.isThresholdMet());
	}

	@Test
	public void thresholdNotMetTest() {
		Optional<List<WalletData>> walletDataListOpt = getFakeData(5, 0);
		Mockito.when(walletService.getLatestTwoWalletData(Mockito.any())).thenReturn(walletDataListOpt);

		final ThresholdCheckResult result = singleThresholdChecker.check(chatWallet);

		Assert.assertEquals(5, result.getChange(), 0);
		Assert.assertEquals(false, result.isThresholdMet());
	}

	@Test
	public void payoutThresholdNotMet() {
		Optional<List<WalletData>> walletDataListOpt = getFakeData(0, 100);
		Mockito.when(walletService.getLatestTwoWalletData(Mockito.any())).thenReturn(walletDataListOpt);

		final ThresholdCheckResult result = singleThresholdChecker.check(chatWallet);

		Assert.assertEquals(-100, result.getChange(), 0);
		Assert.assertEquals(false, result.isThresholdMet());
	}

	private Optional<List<WalletData>> getFakeData(double pendingLatest, double pendingPrevious) {
		List<WalletData> walletDataList = new ArrayList<>(2);

		WalletData walletDataLatest = new WalletData();
		WalletData walletDataPrevious = new WalletData();
		walletDataLatest.setPending(pendingLatest);
		walletDataPrevious.setPending(pendingPrevious);

		walletDataList.add(walletDataLatest);
		walletDataList.add(walletDataPrevious);

		return Optional.of(walletDataList);
	}

}
