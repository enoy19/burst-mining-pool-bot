package io.enoy.burst.bot.telegram.commands;

import io.enoy.burst.bot.telegram.TelegramChatService;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

@Component
@TelegramChatScope
@RequiredArgsConstructor
public class BroadcastCommand extends ValidatedArgumentCommand {

	public static final String POSITIVE_ANSWER = "Yes";
	private final TelegramChatService chatService;

	private String broadcast;

	@Override
	public void shutdown() {
		broadcast = null;
	}

	@Override
	protected boolean initialResponse(Message message) {
		final String username = message.getFrom().getUserName();
		if (!chatService.isAdmin(username)) {
			chatService.sendMessage("Insufficient permissions.");
			return true;
		}
		chatService.sendMessage("Please enter your broadcast");
		return false;
	}

	@Override
	protected boolean validArgument(int argumentIndex, Message message) {
		final String messageText = message.getText().trim();

		if (argumentIndex == 1) {
			broadcast = messageText;
			chatService.sendKeyboard("Are you sure that you want to broadcast that message?", true, POSITIVE_ANSWER, "No, abort");
			return false;
		} else {
			if(messageText.equalsIgnoreCase(POSITIVE_ANSWER)) {
				sendBroadcast(broadcast);
			} else {
				chatService.sendMessage("Aborted.");
			}
			return true;
		}
	}

	private void sendBroadcast(String messageText) {
		chatService.broadcast(messageText);
	}

	@Override
	protected void invalidArgument(int argumentIndex, ValidationResult validationResult, Message message) {
		chatService.sendValidationResult(validationResult);
	}

	@Override
	protected ValidationResult isArgumentValid(int argumentIndex, Message message) {
		return ValidationResult.okWhen(message::hasText)
				.orElse(ValidationResult.invalidArgument("Invalid argument."));
	}

	@Override
	public boolean accepts(String message) {
		return message.trim().equals("/broadcast");
	}
}
