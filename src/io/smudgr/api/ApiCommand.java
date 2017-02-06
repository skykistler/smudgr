package io.smudgr.api;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;
import io.smudgr.app.project.reflect.ReflectableType;

/**
 * Interface for defining the behavior of a unique command.
 * <p>
 * {@link ApiCommand#execute(ApiMessage)} is called when the API is invoked with
 * a packet referencing he string returned by {@link ApiCommand#getCommand()}
 * <p>
 * Every class that implements {@link ApiCommand} is instantiated only once at
 * runtime using the default constructor.
 *
 * @see ApiInvoker
 * @see ApiMessage
 */
public interface ApiCommand extends ReflectableType {

	/**
	 * Used to enumerate this command when a packet is received.
	 *
	 * @return Unique string associated with this command.
	 */
	public String getCommand();

	/**
	 * Behavior of command when a packet is received.
	 *
	 * @param data
	 *            the received packet
	 * @return the response, or null if successful and no response needed.
	 * @see ApiMessage
	 */
	public ApiMessage execute(ApiMessage data);

	/**
	 * Helper method for sending non-response messages. Used for when a command
	 * is triggered elsewhere in the application.
	 *
	 * @param message
	 *            the message to send to controllers
	 */
	public default void sendMessage(ApiMessage message) {
		getController().broadcastMessage(message);
	}

	/**
	 * Gets the currently running application controller.
	 *
	 * @return the current application controller instance
	 */
	public default Controller getController() {
		return Controller.getInstance();
	}

	/**
	 * Gets the currently loaded project.
	 *
	 * @return the current project
	 */
	public default Project getProject() {
		return getController().getProject();
	}

	@Override
	public default String getTypeIdentifier() {
		return "api-command";
	}

	@Override
	public default String getTypeName() {
		return "API Command";
	};

	@Override
	public default String getIdentifier() {
		return getCommand();
	}

	@Override
	public default String getName() {
		return getCommand();
	}

}
