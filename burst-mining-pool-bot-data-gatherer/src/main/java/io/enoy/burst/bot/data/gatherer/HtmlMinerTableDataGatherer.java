package io.enoy.burst.bot.data.gatherer;

import io.enoy.burst.bot.commons.WalletDataGatherer;
import io.enoy.burst.bot.controller.service.WalletService;
import io.enoy.burst.bot.model.Wallet;
import io.enoy.burst.bot.model.WalletData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"crypto-guru", "goburstpool", "nogrod", "burstcoinro", "minertable"})
public class HtmlMinerTableDataGatherer implements WalletDataGatherer {

	private static final Pattern SHARE_PATTERN = Pattern.compile("([\\d.]+) %");
	private static final Pattern EFFECTIVE_CAPACITY_PATTERN = Pattern.compile("([\\d.]+) TB");

	private final WalletService walletService;

	@Value("${pool.url.base}")
	private String poolBaseUrl;

	@Value("${pool.url.minersPath:/miners}")
	private String minersPath;

	@Value("${pool.minerTable.id:miner-table}")
	private String minerTableId;

	@Value("${pool.minerTable.walletId.column:1}")
	private int walletIdColumn;

	@Value("${pool.minerTable.pending.column:2}")
	private int pendingColumn;

	@Value("${pool.minerTable.share.column:3}")
	private int shareColumn;

	@Value("${pool.minerTable.effectiveCapacity.column:4}")
	private int effectiveCapacityColumn;

	@Value("${pool.minerTable.confirmedDeadlines.column:5}")
	private int confirmedDeadlinesColumn;

	@Override
	public List<WalletData> gatherDataOf(Collection<Wallet> wallets) {
		final Document document;
		try {
			document = Jsoup.connect(getMinersUrl()).get();
		} catch (IOException e) {
			log.error("Could not retrieve html table wallet data");
			log.debug(e.getMessage(), e);
			return null;
		}

		final Elements miners = getMiners(document);

		Map<String, Elements> walletIdMinerMap = new HashMap<>(miners.size());
		List<String> walletIds = new ArrayList<>(miners.size());

		putWalletIds(miners, walletIdMinerMap, walletIds);

		final List<Wallet> onlyRegisteredWallets = walletService.getOnlyRegisteredWallets(walletIds);

		return getWalletData(walletIdMinerMap, onlyRegisteredWallets);
	}

	private String getMinersUrl() {
		// just for people like me who tend to forget the leading slash
		final String minersPathWithSlash = minersPath.startsWith("/") ? minersPath : "/" + minersPath;
		return poolBaseUrl + minersPathWithSlash;
	}

	private List<WalletData> getWalletData(Map<String, Elements> walletIdMinerMap, List<Wallet> wallets) {

		final List<WalletData> walletDataSet = new ArrayList<>(wallets.size());

		for (Wallet wallet : wallets) {
			final String walletId = wallet.getId();
			final Elements miner = walletIdMinerMap.get(walletId);

			WalletData walletData = new WalletData();
			walletData.setWallet(wallet);
			walletData.setConfirmedDeadlines(getConfirmedDeadlines(miner));
			walletData.setEffectiveCapacity(getEffectiveCapacity(miner));
			walletData.setPending(getPending(miner));
			walletData.setShare(getShare(miner));

			walletDataSet.add(walletData);
		}
		return walletDataSet;
	}

	private void putWalletIds(Elements miners, Map<String, Elements> walletIdMinerMap, List<String> walletIds) {
		for (Element miner : miners) {
			final Elements cells = miner.getElementsByTag("td");
			final String walletId = getWalletId(cells);
			walletIds.add(walletId);
			walletIdMinerMap.put(walletId, cells);
		}
	}

	private Elements getMiners(Document document) {
		final Element minerTable = document.getElementById(minerTableId);
		final Elements minerTableBodyElements = minerTable.getElementsByTag("tbody");
		final Element minerTableBody = minerTableBodyElements.get(0);
		return minerTableBody.getElementsByTag("tr");
	}

	private double getShare(Elements miner) {
		final Element shareElement = miner.get(shareColumn);
		final String shareText = shareElement.text().trim();
		final Matcher matcher = SHARE_PATTERN.matcher(shareText);

		if (matcher.find()) {
			final String shareValueString = matcher.group(1);
			return Double.parseDouble(shareValueString);
		}

		return 0;
	}

	private double getPending(Elements miner) {
		final String pendingString = miner.get(pendingColumn).text().trim();
		return Double.parseDouble(pendingString);
	}

	// (remember: copy & paste isn't good!)
	private double getEffectiveCapacity(Elements miner) {
		final Element effectiveCapacityElement = miner.get(effectiveCapacityColumn);
		final String effectiveCapacityText = effectiveCapacityElement.text().trim();
		final Matcher matcher = EFFECTIVE_CAPACITY_PATTERN.matcher(effectiveCapacityText);

		if (matcher.find()) {
			final String effectiveCapacityValueString = matcher.group(1);
			return Double.parseDouble(effectiveCapacityValueString);
		}

		return 0;
	}

	private long getConfirmedDeadlines(Elements miner) {
		final String confirmedDeadlinesString = miner.get(confirmedDeadlinesColumn).text().trim();
		return Long.parseLong(confirmedDeadlinesString);
	}

	/**
	 * @param cells the table columns (td)
	 * @return the miner id/address: BURST-AAAA-BBBB-CCCC-DDDDD
	 */
	private String getWalletId(Elements cells) {
		return cells.get(walletIdColumn).child(0).text().trim();
	}
}
