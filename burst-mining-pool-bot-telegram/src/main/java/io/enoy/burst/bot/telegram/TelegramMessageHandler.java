package io.enoy.burst.bot.telegram;

import io.enoy.burst.bot.telegram.commands.Command;
import io.enoy.burst.bot.telegram.scope.TelegramChatScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@TelegramChatScope
public class TelegramMessageHandler {

	private final TelegramChatService chatService;
	private final Set<Command> commands;
	private Command currentCommand;

	@Value("${bot.username}")
	private String botUsername;

	public synchronized void handleMessage(Message message) {
		setupCommand(message);

		if (Objects.isNull(currentCommand)) {
			// command not found / no command executed
			return;
		}

		final boolean commandDone = currentCommand.execute(message);

		if (commandDone) {
			try {
				currentCommand.shutdown();
			} finally {
				currentCommand = null;
			}
		}
	}

	private synchronized void setupCommand(Message message) {
		final Boolean userChat = message.getChat().isUserChat();

		if (Objects.isNull(currentCommand) && message.hasText()) {
			final String messageText = message.getText();

			if(userChat && messageText.startsWith("/")
					|| !userChat && messageText.matches("/\\w*@"+botUsername)) {
				final Optional<Command> matchingCommandOpt = findMatchingCommand(message);
				matchingCommandOpt.ifPresent(command -> {
					this.currentCommand = command;
					this.currentCommand.init();
				});

				if (!matchingCommandOpt.isPresent()) {
					chatService.sendMessage("Command not found.");
				}
			}
		}
	}

	private Optional<Command> findMatchingCommand(Message message) {
		if (message.hasText()) {
			return
					commands.stream()
							.sorted(Comparator.comparing(Object::toString))
							.filter(command -> {
								String messageText = message.getText().trim();
								messageText = messageText.replaceAll("@"+botUsername, "");
								return command.accepts(messageText);
							})
							.findFirst();
		}

		return Optional.empty();
	}

}
