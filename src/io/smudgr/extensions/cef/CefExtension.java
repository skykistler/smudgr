package io.smudgr.extensions.cef;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.controller.ControllerExtension;
import io.smudgr.app.project.util.PropertyMap;

/**
 * The {@link CefExtension} provides integration with the Chromium Extendable
 * Framework.
 * <p>
 * This allows smudgr to communicate with a front-end built on web
 * frameworks, via an internal stand-alone stripped down build of Chromium.
 */
public class CefExtension implements ControllerExtension {

	@Override
	public String getTypeName() {
		return "CEF";
	}

	@Override
	public String getTypeIdentifier() {
		return "cef";
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onUpdate() {
	}

	@Override
	public void onStop() {
	}

	@Override
	public void onMessage(ApiMessage message) {
	}

	@Override
	public void save(PropertyMap pm) {
	}

	@Override
	public void load(PropertyMap pm) {
	}

}
