package io.enoy.burst.bot.controller.service;

import io.enoy.burst.bot.model.WalletData;
import io.enoy.burst.bot.model.repositories.WalletDataRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class WalletServiceTest {

	@Test
	public void testPendingGrowth() {
		WalletDataRepository walletDataRepository = Mockito.mock(WalletDataRepository.class);
		WalletService walletService = new WalletService(null, null, walletDataRepository, null);

		List<WalletData> walletData = new ArrayList<>();

		WalletData a = new WalletData();
		WalletData b = new WalletData();
		WalletData c = new WalletData();
		WalletData d = new WalletData();
		WalletData e = new WalletData();

		a.setPending(0);    // 0    +50
		b.setPending(50);   // 50   +50
		c.setPending(100);  // 100  -100
		d.setPending(0);    // 100  +25
		e.setPending(25);   // 125

		walletData.add(a);
		walletData.add(b);
		walletData.add(c);
		walletData.add(d);
		walletData.add(e);

		Mockito.when(walletDataRepository.findAllByWallet(Mockito.any())).thenReturn(walletData);

		final double pendingGrowth = walletService.getPendingGrowth(null);

		Assert.assertEquals(125, pendingGrowth, 0);
	}

	@Test
	public void testPayouts() {
		WalletDataRepository walletDataRepository = Mockito.mock(WalletDataRepository.class);
		WalletService walletService = new WalletService(null, null, walletDataRepository, null);

		List<WalletData> walletData = new ArrayList<>();

		WalletData a = new WalletData();
		WalletData b = new WalletData();
		WalletData c = new WalletData();
		WalletData d = new WalletData();
		WalletData e = new WalletData();

		a.setPending(0);    // 0    +50
		b.setPending(50);   // 50   +50
		c.setPending(100);  // 100  -100 <-- payout
		d.setPending(0);    // 100  +25
		e.setPending(25);   // 125

		walletData.add(a);
		walletData.add(b);
		walletData.add(c);
		walletData.add(d);
		walletData.add(e);

		Mockito.when(walletDataRepository.findAllByWallet(Mockito.any())).thenReturn(walletData);

		final double pendingGrowth = walletService.getPayouts(null);

		Assert.assertEquals(100, pendingGrowth, 0);
	}

}
