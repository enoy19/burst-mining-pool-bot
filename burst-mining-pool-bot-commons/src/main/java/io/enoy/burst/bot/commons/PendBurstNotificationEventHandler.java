package io.enoy.burst.bot.commons;

import java.util.function.Consumer;

@FunctionalInterface
public interface PendBurstNotificationEventHandler extends Consumer<PendBurstNotificationEvent> {

}
