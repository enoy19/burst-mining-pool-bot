package io.enoy.burst.bot.telegram.scope;

import io.enoy.burst.bot.telegram.scope.TelegramContextHolder.TelegramContext;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActualTelegramChatScope implements Scope {

	public static final String SCOPE_NAME = "telegramChat";

	private final Map<TelegramContext, Map<String, Object>> scopedObjects
			= Collections.synchronizedMap(new HashMap<TelegramContext, Map<String, Object>>());

	private final Map<TelegramContext, Map<String, Runnable>> destructionCallbacks
			= Collections.synchronizedMap(new HashMap<TelegramContext, Map<String, Runnable>>());

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Map<String, Object> scopedObjects = getContextScopedObjects();

		if (!scopedObjects.containsKey(name))
			scopedObjects.put(name, objectFactory.getObject());

		return scopedObjects.get(name);
	}

	@Override
	public Object remove(String name) {
		Map<String, Runnable> destructionCallbacks = getContextDestructionCallbacks();
		Map<String, Object> scopedObjects = getContextScopedObjects();

		destructionCallbacks.remove(name);
		return scopedObjects.remove(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable runnable) {
		Map<String, Runnable> destructionCallbacks = getContextDestructionCallbacks();
		destructionCallbacks.put(name, runnable);
	}

	@Override
	public Object resolveContextualObject(String name) {
		return null;
	}

	@Override
	public String getConversationId() {
		return SCOPE_NAME;
	}

	private Map<String, Object> getContextScopedObjects() {
		TelegramContext context = TelegramContextHolder.currentContext();

		if (!scopedObjects.containsKey(context)) {
			scopedObjects.put(context, new HashMap<>());
		}

		return scopedObjects.get(context);
	}

	private Map<String, Runnable> getContextDestructionCallbacks() {
		TelegramContext context = TelegramContextHolder.currentContext();

		if (!destructionCallbacks.containsKey(context)) {
			destructionCallbacks.put(context, new HashMap<>());
		}

		return destructionCallbacks.get(context);
	}

}
