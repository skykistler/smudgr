// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package io.smudgr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefAppHandlerAdapter;

/**
 * This is a simple example application using JCEF. It displays a JFrame with a JTextField at its top and a CefBrowser in its center. The JTextField is used to enter and assign an URL to the browser UI. No additional handlers or callbacks are used in this example.
 *
 * The number of used JCEF classes is reduced (nearly) to its minimum and should assist you to get familiar with JCEF.
 *
 * For a more feature complete example have also a look onto the example code within the package "tests.detailed".
 */
public class smudgr extends JFrame {
	private static final long serialVersionUID = -5570653778104813836L;
	private final CefApp cefApp_;
	private final CefClient client_;
	private final CefBrowser browser_;
	private final Component browserUI_;

	/**
	 * To display a simple browser window, it suffices completely to create an instance of the class CefBrowser and to assign its UI component to your application (e.g. to your content pane). But to be more verbose, this CTOR keeps an instance of each object on the way to the browser UI.
	 */
	private smudgr(String startURL, boolean useOSR, boolean isTransparent) {
		CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
			@Override
			public void stateHasChanged(org.cef.CefApp.CefAppState state) {
				// Shutdown the app if the native CEF part is terminated
				if (state == CefAppState.TERMINATED)
					System.exit(0);
			}
		});
		CefSettings settings = new CefSettings();
		settings.windowless_rendering_enabled = useOSR;
		cefApp_ = CefApp.getInstance(settings);

		client_ = cefApp_.createClient();

		browser_ = client_.createBrowser(startURL, useOSR, isTransparent);
		browserUI_ = browser_.getUIComponent();

		add(browserUI_, BorderLayout.CENTER);
		setSize(801, 600);
		setVisible(true);

		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setSize(800, 600);

		// (6) To take care of shutting down CEF accordingly, it's important to call
		//     the method "dispose()" of the CefApp instance if the Java
		//     application will be closed. Otherwise you'll get asserts from CEF.
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				CefApp.getInstance().dispose();
				dispose();
			}
		});
	}

	public static void main(String[] args) {
		new smudgr("http://smudgr.io", OS.isLinux(), false);
	}
}
