package io.smudgr.api;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;

/**
 * Interface for defining the behavior of a unique command.
 * <p>
 * {@code execute()} is called when the API is invoked with a packet referencing
 * the string returned by {@code getCommand()}
 * <p>
 * Every class that implements ApiCommand is instantiated once at runtime using
 * the default constructor.
 * 
 * @see ApiInvoker
 * @see ApiMessage
 */
public interface ApiCommand {

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
		getController().sendMessage(message);
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

}
