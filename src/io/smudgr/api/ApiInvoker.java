package io.smudgr.api;

import java.util.HashMap;
import java.util.Set;

import io.smudgr.reflect.Reflect;

public class ApiInvoker {

	private HashMap<String, ApiCommand> commands = new HashMap<String, ApiCommand>();

	public ApiInvoker() {
		Reflect commandReflect = new Reflect(ApiCommand.class);

		Set<Class<?>> commandClasses = commandReflect.get();
		for (Class<?> c : commandClasses) {
			try {
				ApiCommand command = (ApiCommand) c.newInstance();
				commands.put(command.getCommand(), command);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public ApiMessage invoke(String request) {
		ApiMessage message = ApiMessage.deserialize(request);

		String command = message.get("command");
		ApiMessage response = new ApiMessage();

		if (command == null || !commands.containsKey(command)) {
			response.put("message", "No command could be found for: " + request);
			return ApiMessage.command("response", "failure", response);
		}

		ApiCommand strategy = commands.get(command);

		try {
			ApiMessage data = null;
			if (message.hasKey("data"))
				data = ApiMessage.deserialize(message.get("data"));

			response = strategy.execute(data);
		} catch (Exception e) {
			e.printStackTrace();

			response.put("message", "Failed to execute command: " + command);
			return ApiMessage.command("response", "failure", response);
		}

		return response != null ? response : ApiMessage.command("response", "success", response);
	}

}