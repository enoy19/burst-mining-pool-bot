package io.enoy.burst.bot.telegram.commands;

import org.telegram.telegrambots.api.objects.Message;

public interface Command {

	boolean accepts(String message);
	default void init(){}
	boolean execute(Message message);
	default void shutdown(){}

}
