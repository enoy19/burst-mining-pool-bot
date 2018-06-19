package io.enoy.burst.bot.telegram.scope;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramChatScopeConfiguration {

	@Bean
	public CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();

		scopeConfigurer.addScope(ActualTelegramChatScope.SCOPE_NAME, new ActualTelegramChatScope());

		return scopeConfigurer;
	}

}
