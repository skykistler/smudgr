package io.smudgr.extensions.cef;

import java.util.HashMap;
import java.util.Set;

import io.smudgr.extensions.cef.commands.CefCommand;
import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.reflect.Reflect;

public class CommandInvoker {

	private HashMap<String, CefCommand> commands = new HashMap<String, CefCommand>();

	public CommandInvoker() {
		Reflect commandReflect = new Reflect(CefCommand.class);

		Set<Class<?>> commandClasses = commandReflect.get();
		for (Class<?> c : commandClasses) {
			try {
				CefCommand command = (CefCommand) c.newInstance();
				commands.put(command.getCommand(), command);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public CefMessage invoke(String request) {
		CefMessage message = CefMessage.deserialize(request);

		String command = message.get("command");
		CefMessage response = new CefMessage();

		if (command == null || !commands.containsKey(command)) {
			response.put("message", "No command could be found for: " + request);
			return CefMessage.command("response", "failure", response);
		}

		CefCommand strategy = commands.get(command);

		try {
			CefMessage data = null;
			if (message.hasKey("data"))
				data = CefMessage.deserialize(message.get("data"));

			response = strategy.execute(data);
		} catch (Exception e) {
			e.printStackTrace();

			response.put("message", "Failed to execute command: " + command);
			return CefMessage.command("response", "failure", response);
		}

		return response != null ? response : CefMessage.command("response", "success", response);
	}

}