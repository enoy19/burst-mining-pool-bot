package io.enoy.burst.bot.telegram.scope;

import org.springframework.context.annotation.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Scope(ActualTelegramChatScope.SCOPE_NAME)
public @interface TelegramChatScope {
}
