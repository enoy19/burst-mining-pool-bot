package io.enoy.burst.bot.controller.service.threshold;

import io.enoy.burst.bot.model.ChatWallet;
import io.enoy.burst.bot.model.ThresholdMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ThresholdCheckerService {

	private Set<ThresholdChecker> thresholdCheckers;
	private Map<ThresholdMode, ThresholdChecker> mappedThresholdCheckers;

	@PostConstruct
	private void init() {
		mappedThresholdCheckers = new HashMap<>(thresholdCheckers.size());
		for (ThresholdChecker thresholdChecker : thresholdCheckers) {
			mappedThresholdCheckers.put(thresholdChecker.getThresholdMode(), thresholdChecker);
		}
	}

	public ThresholdCheckResult check(ChatWallet chatWallet) {
		ThresholdMode thresholdMode = chatWallet.getThresholdMode();

		if (Objects.isNull(thresholdMode)) {
			thresholdMode = ThresholdMode.DEFAULT_THRESHOLD_MODE;
		}

		final ThresholdChecker thresholdChecker = getMatchingThresholdChecker(thresholdMode);
		return thresholdChecker.check(chatWallet);
	}

	private ThresholdChecker getMatchingThresholdChecker(ThresholdMode thresholdMode) {
		return mappedThresholdCheckers.get(thresholdMode);
	}

}
