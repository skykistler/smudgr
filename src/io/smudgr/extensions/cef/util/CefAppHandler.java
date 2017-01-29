package io.smudgr.extensions.cef.util;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

import io.smudgr.app.controller.Controller;

/**
 * The {@link CefAppHandler} registers the custom 'smudgr://' scheme on the CEF
 * instance, and stops the current application instance when CEF terminates.
 */
public class CefAppHandler extends CefAppHandlerAdapter {

	/**
	 * Create a new {@link CefAppHandler}
	 */
	public CefAppHandler() {
		super(null);
	}

	@Override
	public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
		registrar.addCustomScheme(SmudgrScheme.scheme, true, false, false);
	}

	@Override
	public void onContextInitialized() {
		CefApp.getInstance().registerSchemeHandlerFactory(SmudgrScheme.scheme, "", new SchemeHandlerFactory());
	}

	private class SchemeHandlerFactory implements CefSchemeHandlerFactory {
		@Override
		public CefResourceHandler create(CefBrowser browser, String schemeName, CefRequest request) {
			if (schemeName.equals(SmudgrScheme.scheme))
				return new SmudgrScheme();

			return null;
		}
	}

	@Override
	public void stateHasChanged(CefAppState state) {
		if (state == CefAppState.TERMINATED)
			Controller.getInstance().stop();
	}
}
