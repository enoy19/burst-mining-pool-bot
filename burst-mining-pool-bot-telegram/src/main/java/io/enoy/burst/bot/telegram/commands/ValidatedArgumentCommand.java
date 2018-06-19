package io.enoy.burst.bot.telegram.commands;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class ValidatedArgumentCommand extends ArgumentCommand {

	@Override
	protected boolean processArgument(int argumentIndex, Message message) {
		final ValidationResult validationResult = isArgumentValid(argumentIndex, message);

		if (Objects.isNull(validationResult)) {
			return true;
		}

		final ValidationResultType resultType = validationResult.getType();

		switch (resultType) {
			case OK:
				return validArgument(argumentIndex, message);
			case ABORTED:
			case INVALID_ARGUMENT:
			default:
				invalidArgument(argumentIndex, validationResult, message);
				return true;
		}
	}

	protected abstract boolean validArgument(int argumentIndex, Message message);

	protected abstract void invalidArgument(int argumentIndex, ValidationResult validationResult, Message message);

	protected abstract ValidationResult isArgumentValid(int argumentIndex, Message message);

	@Data
	@Builder
	public static class ValidationResult {
		final ValidationResultType type;
		String message;

		public static Optional<ValidationResult> okWhen(Supplier<Boolean> condition) {
			if (condition.get()) {
				return Optional.of(ok());
			} else {
				return Optional.empty();
			}
		}

		public static ValidationResult invalidArgument(String message) {
			return new ValidationResult(ValidationResultType.INVALID_ARGUMENT, message);
		}

		public static ValidationResult aborted() {
			return new ValidationResult(ValidationResultType.ABORTED, null);
		}

		public static ValidationResult ok() {
			return new ValidationResult(ValidationResultType.OK, null);
		}

	}

	public enum ValidationResultType {
		OK("Ok"), INVALID_ARGUMENT("Invalid argument"), ABORTED("Aborted");

		@Getter
		private final String messagePrefix;

		ValidationResultType(String messagePrefix) {
			this.messagePrefix = messagePrefix;
		}
	}

}
