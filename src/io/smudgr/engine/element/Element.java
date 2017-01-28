package io.smudgr.engine.element;

/**
 * TODO: Refactor soon
 */
public interface Element {

	/**
	 * Default project map identifier
	 */
	public static final String PROJECT_MAP_TAG = "element";

	/**
	 * Type category of this element
	 * 
	 * @return type
	 */
	public String getType();

	/**
	 * Name of this element implementation
	 * 
	 * @return name
	 */
	public String getName();

}
