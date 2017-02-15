package io.smudgr.api;

import java.util.HashMap;

import io.smudgr.app.project.reflect.TypeLibrary;

/**
 * API controller for loading and invoking API commands. This class does not
 * listen for commands on its own, and should be invoked by any API listening
 * service.
 *
 * @see ApiCommand
 * @see ApiMessage
 */
public class ApiInvoker {

	private static HashMap<String, ApiCommand> commands;

	/**
	 * Initialize the ApiInvoker by enumerating all classes implementing
	 * ApiCommand.
	 *
	 * @see ApiCommand
	 */
	public void init() {
		if (commands != null)
			return;

		commands = new HashMap<String, ApiCommand>();

		TypeLibrary<ApiCommand> commandLibrary = new TypeLibrary<ApiCommand>(ApiCommand.class);

		for (String id : commandLibrary.getIdList()) {
			ApiCommand command = commandLibrary.getNewInstance(id);
			commands.put(id, command);
			command.onInit();
		}
	}

	/**
	 * Invoke an API command given a serialized request packet.
	 *
	 * @param request
	 *            serialized JSON request packet
	 * @return API response
	 * @see ApiMessage
	 */
	public ApiMessage invoke(String request) {
		// Deserialize JSON string into ApiMessage instance
		ApiMessage message = ApiMessage.deserialize(request);

		if (message == null) {
			return ApiMessage.failed("response", new ApiMessage("message", "Failed to deserialize packet."));
		}

		// command this request is trying to call
		String command = message.get("command");

		/*
		 * If there's no loaded ApiCommand associate with given command, return
		 * a failure response
		 */
		if (command == null || !commands.containsKey(command)) {
			return ApiMessage.failed("response", new ApiMessage("message", "No command could be found for: " + request));
		}

		// Find associated command strategy
		ApiCommand strategy = commands.get(command);

		/*
		 * Attempt to execute the associated ApiCommand, possibly with given
		 * data, return a failure response if an exception is thrown.
		 */
		try {
			ApiMessage data = null;
			if (message.hasKey("data"))
				data = ApiMessage.deserialize(message.get("data"));

			ApiMessage response = strategy.execute(data);

			/*
			 * Assume generic success response if none given.
			 */
			if (response == null)
				return ApiMessage.success(command, null);

			/*
			 * If response already wrapped as command, return as-is
			 */
			if (response.get("command") != null)
				return response;

			/*
			 * If response is not wrapped as command, return success response
			 * with given response as data.
			 */
			return ApiMessage.success(command, response);
		} catch (Exception e) {
			e.printStackTrace();

			return ApiMessage.failed("response", new ApiMessage("message", "Failed to execute command: " + command));
		}

	}

}