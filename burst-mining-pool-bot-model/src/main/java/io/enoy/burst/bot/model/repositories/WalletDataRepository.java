package io.enoy.burst.bot.model.repositories;

import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.model.WalletData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WalletDataRepository extends JpaRepository<WalletData, Long> {

	WalletData findFirstByWallet_IdOrderByTimestampDesc(String walletId);
	List<WalletData> findFirst2ByWallet_IdOrderByTimestampDesc(String walletId);

	List<WalletData> findAllByWallet(Wallet wallet);
	List<WalletData> findAllByWalletAndTimestampAfter(Wallet wallet, Date afterThisDate);

	void deleteByTimestampBefore(Date date);
}
