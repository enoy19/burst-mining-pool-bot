package io.enoy.burst.bot.boot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RequiredArgsConstructor
@ComponentScan("io.enoy.burst.bot")
public class BurstMiningPoolBotApplication {

	private final TelegramLongPollingBot bot;

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(BurstMiningPoolBotApplication.class, args);
	}

	@PostConstruct
	private void init() throws TelegramApiRequestException {
		telegramBotsApi().registerBot(bot);
	}

	@Bean
	public TelegramBotsApi telegramBotsApi() {
		return new TelegramBotsApi();
	}

}
