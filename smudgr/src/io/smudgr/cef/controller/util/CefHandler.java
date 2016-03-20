package io.smudgr.cef.controller.util;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

import io.smudgr.view.View;

public class CefHandler extends CefAppHandlerAdapter {

	private View view;

	public CefHandler(View view) {
		super(null);
		this.view = view;
	}

	public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
		registrar.addCustomScheme(SmudgrScheme.scheme, true, false, false);
	}

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
			view.getController().stop();
	}
}
