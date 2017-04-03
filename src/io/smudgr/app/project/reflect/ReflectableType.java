package io.smudgr.app.project.reflect;

/**
 * A {@link ReflectableType} class allows implementations of itself to be
 * automatically enumerated by smudgr.
 * <p>
 * This means a developer can specify an 'Operation' {@link ReflectableType},
 * which allows other developers to create 'Operation' mods.
 */
public interface ReflectableType {

	/**
	 * This should return a unique identifier of the category of this type.
	 * <p>
	 * The name that this type category is identified to the user with may
	 * differ from this identifier. This concept is similar to a 'slug'.
	 * <p>
	 * Implementations of a type should not override this method.
	 * <p>
	 * The {@link String} returned by this method should not be changed between
	 * versions.
	 *
	 * @return String that will enumerate this type category across versions of
	 *         the application
	 *
	 * @see #getTypeIdentifier()
	 * @see #getTypeCategoryName()
	 */
	public String getTypeCategoryIdentifier();

	/**
	 * The user identifiable name of the category of this type.
	 * <p>
	 * This name can safely be changed between versions of the application, and
	 * may be used for translations.
	 * <p>
	 * Implementations of this type should not override this method.
	 *
	 * @return String that will identify this type category to the user.
	 *
	 * @see #getTypeName()
	 * @see #getTypeCategoryIdentifier()
	 */
	public String getTypeCategoryName();

	/**
	 * This should return a unique identifier of this type implementation.
	 * <p>
	 * The name that this type implementation is identified to the user with may
	 * differ from this identifier. This concept is similar to a 'slug'.
	 * <p>
	 * Implementations of a type should use this method to distinguish
	 * themselves.
	 * <p>
	 * The {@link String} returned by this method should be changed between
	 * versions.
	 *
	 * @return String that will enumerate this implementation across versions of
	 *         the application
	 *
	 * @see #getTypeCategoryIdentifier()
	 * @see #getTypeName()
	 */
	public String getTypeIdentifier();

	/**
	 * The user identifiable name of this implementation.
	 * <p>
	 * This name can safely be changed between versions of the application, and
	 * may be used for translations.
	 * <p>
	 * Implementations of this implementation should not override this method.
	 *
	 * @return String that will identify this implementation to the user.
	 *
	 * @see #getTypeCategoryName()
	 * @see #getTypeIdentifier()
	 */
	public String getTypeName();

}
