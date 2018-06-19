package io.enoy.burst.bot.telegram.commands;

import org.telegram.telegrambots.api.objects.Message;

/**
 * A convenient abstract class that splits messages to command response and argument responses.
 * The argument index starts at 1.
 */
public abstract class ArgumentCommand implements Command {

	private int argumentIndex;

	/**
	 * the first response a user gets when calling a command
	 */
	protected abstract boolean initialResponse(Message message);

	@Override
	public void init() {
		argumentIndex = 0;
	}

	@Override
	public final boolean execute(Message message) {
		if(argumentIndex == 0) {
			argumentIndex++;
			return initialResponse(message);
		}

		boolean result = processArgument(argumentIndex, message);
		argumentIndex++;
		return result;
	}

	protected abstract boolean processArgument(int argumentIndex, Message message);

}
