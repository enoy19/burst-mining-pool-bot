package io.enoy.burst.bot.commons;

import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.model.WalletData;

import java.util.Collection;
import java.util.List;

public interface WalletDataGatherer {

	List<WalletData> gatherDataOf(Collection<Wallet> wallets);

}
