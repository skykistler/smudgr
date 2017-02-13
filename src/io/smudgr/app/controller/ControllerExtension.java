package io.smudgr.app.controller;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;
import io.smudgr.app.project.reflect.ReflectableType;
import io.smudgr.app.project.util.PropertyMap;

/**
 * The {@link ControllerExtension} interface provides methods for hooking into
 * the application life cycle. This allows implementations to add functionality
 * to the application, such as controlling parameters with external devices or
 * providing user interfaces.
 */
public interface ControllerExtension extends ReflectableType {

	@Override
	public default String getTypeIdentifier() {
		return "extension";
	}

	@Override
	public default String getTypeName() {
		return "Controller Extension";
	}

	/**
	 * Gets the name of this {@link ControllerExtension}
	 *
	 * @return {@link String}
	 */
	@Override
	public String getElementName();

	/**
	 * This method is called on application startup.
	 */
	public void init();

	/**
	 * This method is called every application update loop.
	 */
	public void update();

	/**
	 * This method is called when the application is shutting down.
	 */
	public void stop();

	/**
	 * This method is called when an {@link ApiMessage} is broadcasted to the
	 * entire application.
	 *
	 * @param message
	 *            {@link ApiMessage}
	 * @see Controller#broadcastMessage(ApiMessage)
	 */
	public void onMessage(ApiMessage message);

	/**
	 * This method is called to save the current state of this
	 * {@link ControllerExtension} to the given {@link PropertyMap}
	 *
	 * @param pm
	 *            {@link PropertyMap}
	 * @see Project#save(PropertyMap)
	 */
	public void save(PropertyMap pm);

	/**
	 * This method is called to load the saved state of this
	 * {@link ControllerExtension} from the given {@link PropertyMap}
	 *
	 * @param pm
	 *            {@link PropertyMap}
	 * @see Project#load(PropertyMap)
	 */
	public void load(PropertyMap pm);

	/**
	 * Gets the {@link Project} loaded by the current application instance.
	 *
	 * @return {@link Project}
	 */
	public default Project getProject() {
		return Controller.getInstance().getProject();
	}

}
