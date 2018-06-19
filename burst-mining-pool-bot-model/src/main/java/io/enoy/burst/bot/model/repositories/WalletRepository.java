package io.enoy.burst.bot.model.repositories;

import io.enoy.burst.bot.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {

	List<Wallet> findAllByIdIn(Collection<String> ids);

}
