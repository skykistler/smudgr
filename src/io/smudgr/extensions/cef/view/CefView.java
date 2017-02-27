package io.smudgr.extensions.cef.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Vector;

import javax.swing.JFrame;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.network.CefCookieManager;

import io.smudgr.app.smudgr;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.view.View;
import io.smudgr.extensions.cef.util.CefAppHandler;
import io.smudgr.extensions.cef.util.CefQueryHandler;
import io.smudgr.extensions.cef.util.DialogHandler;
import io.smudgr.util.Frame;

/**
 * The {@link CefView} is a {@link View} that represents a CEF instance. Adding
 * this view to the current application instance will attempt to start a CEF
 * instance.
 */
public class CefView extends JFrame implements View {

	@Override
	public String getName() {
		return "CEF";
	}

	private boolean cefLogging = false;
	private boolean debug;

	private CefApp cefApp;
	private CefClient cefClient;
	private CefBrowser cefBrowser;
	private Component cefBrowserUI;

	/**
	 * Start a new CefView
	 *
	 * @param debug
	 *            if {@code true}, host a DevTools instance on port 54321
	 */
	public CefView(boolean debug) {
		super("smudgr");
		this.debug = debug;
	}

	@Override
	public void start() {
		startCef();

		DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		int width = display.getWidth() - 200;
		int height = display.getHeight() - 200;
		setSize(width, height);

		setBackground(new Color(0x212121));

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - width) / 2);
		int y = (int) ((dimension.getHeight() - height) / 2);
		setLocation(x, y);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Controller.getInstance().stop();
			}
		});

		// addComponentListener(new ComponentListener() {
		// @Override
		// public void componentResized(ComponentEvent e) {
		// Insets inset = getInsets();
		// cefBrowserUI.setSize(getWidth() - inset.left - inset.right,
		// getHeight() - inset.top - inset.bottom);
		//
		// cefBrowserUI.setIgnoreRepaint(false);
		// cefBrowserUI.repaint();
		// }
		//
		// @Override
		// public void componentMoved(ComponentEvent e) {
		// }
		//
		// @Override
		// public void componentShown(ComponentEvent e) {
		// }
		//
		// @Override
		// public void componentHidden(ComponentEvent e) {
		// }
		// });

		add(cefBrowserUI);

		setVisible(true);

		// setSize(width - 100, height);
		setSize(width, height);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		repaint();

		// if (OS.isWindows())
		// setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
	}

	@Override
	public void update(Frame frame) {
	}

	private void startCef() {
		CefApp.addAppHandler(new CefAppHandler());

		CefSettings settings = new CefSettings();
		settings.background_color = settings.new ColorType(255, 0, 0, 0);

		if (debug) {
			settings.remote_debugging_port = 54321;

			if (cefLogging) {
				settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_VERBOSE;

				String parent_path = "";
				try {
					String this_path = URLDecoder
							.decode(smudgr.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");

					parent_path = (new File(this_path)).getParentFile().getAbsolutePath() + "/";
				} catch (UnsupportedEncodingException e) {
				}

				settings.log_file = parent_path + "cef_debug.log";
			}
		}

		cefApp = CefApp.getInstance(settings);

		cefClient = cefApp.createClient();

		CefMessageRouter msgRouter = CefMessageRouter.create(new CefQueryHandler(Controller.getInstance().getApiInvoker()));
		cefClient.addMessageRouter(msgRouter);

		cefClient.addDialogHandler(new DialogHandler());

		CefCookieManager cookieManager = CefCookieManager.getGlobalManager();

		Vector<String> cookieSchemas = new Vector<String>();
		cookieSchemas.add("http");
		cookieSchemas.add("https");
		cookieSchemas.add("smudgr");
		cookieManager.setSupportedSchemes(cookieSchemas);

		cefBrowser = cefClient.createBrowser("smudgr://ui", false, false);

		cefBrowserUI = cefBrowser.getUIComponent();
	}

	@Override
	public void stop() {
		setVisible(false);
		CefApp.getInstance().dispose();
	}

	private static final long serialVersionUID = 6622012878252406208L;

}
