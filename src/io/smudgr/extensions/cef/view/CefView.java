package io.smudgr.extensions.cef.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.view.View;
import io.smudgr.extensions.cef.CefExtension;
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

	private boolean debug;

	private RenderFrame renderFrame;

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

		renderFrame = new RenderFrame(this);
		Controller.getInstance().add(renderFrame);
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
			public void windowActivated(WindowEvent e) {
				if (e.getOppositeWindow() != renderFrame)
					renderFrame.updateIsVisible();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				if (e.getOppositeWindow() != renderFrame)
					renderFrame.updateIsVisible();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				renderFrame.updateIsVisible();
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				renderFrame.updateIsVisible();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Controller.getInstance().stop();
			}
		});

		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				Insets inset = getInsets();
				cefBrowserUI.setSize(getWidth() - inset.left - inset.right, getHeight() - inset.top - inset.bottom);
				renderFrame.updateDimensions();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				renderFrame.updateDimensions();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				renderFrame.updateIsVisible();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				renderFrame.updateIsVisible();
			}
		});

		add(cefBrowserUI);

		renderFrame.setVisible(true);
		setVisible(true);

		setSize(width - 100, height);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		setSize(width, height);

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

		if (debug)
			settings.remote_debugging_port = 54321;

		cefApp = CefApp.getInstance(settings);

		cefClient = cefApp.createClient();

		CefExtension extension = (CefExtension) Controller.getInstance().getExtension("CEF");
		CefMessageRouter msgRouter = CefMessageRouter.create(new CefQueryHandler(extension.getInvoker()));
		cefClient.addMessageRouter(msgRouter);

		cefClient.addDialogHandler(new DialogHandler());

		cefBrowser = cefClient.createBrowser("smudgr://ui/index.html", false, false);

		cefBrowserUI = cefBrowser.getUIComponent();
	}

	@Override
	public void stop() {
		renderFrame.setVisible(false);
		setVisible(false);

		super.dispose();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CefApp.getInstance().dispose();
			}
		});
	}

	private static final long serialVersionUID = 6622012878252406208L;

}
