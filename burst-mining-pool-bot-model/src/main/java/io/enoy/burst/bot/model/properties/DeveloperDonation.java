package io.enoy.burst.bot.model.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@Component
@ConfigurationProperties("developer")
public class DeveloperDonation {

	private Map<String, String> donation = new TreeMap<>();

}
