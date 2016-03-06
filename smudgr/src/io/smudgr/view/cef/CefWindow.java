package io.smudgr.view.cef;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;

import io.smudgr.source.Frame;
import io.smudgr.view.cef.util.CefHandler;
import io.smudgr.view.cef.util.DialogHandler;
import io.smudgr.view.cef.util.SmudgrQueryRouter;

public class CefWindow extends JFrame {

	private int width, height;
	private boolean debug;

	private CefView view;
	private RenderFrame renderFrame;
	private CefApp cefApp;
	private CefClient cefClient;
	private CefBrowser cefBrowser;
	private Component cefBrowserUI;

	public CefWindow(CefView v, boolean debug) {
		super("smudgr");
		view = v;
		this.debug = debug;

		renderFrame = new RenderFrame(this);
	}

	public void init() {
		startCef();

		DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		width = display.getWidth();
		height = display.getHeight();
		setSize(width, height);

		setBackground(Color.black);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - width) / 2);
		int y = (int) ((dimension.getHeight() - height) / 2);
		setLocation(x, y);

		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				if (e.getOppositeWindow() != renderFrame)
					renderFrame.setVisible(true);
			}

			public void windowDeactivated(WindowEvent e) {
				if (e.getOppositeWindow() != renderFrame)
					renderFrame.setVisible(false);
			}

			public void windowIconified(WindowEvent e) {
				renderFrame.setVisible(false);
			}

			public void windowDeiconified(WindowEvent e) {
				renderFrame.setVisible(true);
			}

			public void windowClosing(WindowEvent e) {
				view.getController().stop();
			}
		});

		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				width = getWidth();
				height = getHeight();
				cefBrowserUI.setSize(width, height);
				renderFrame.updateDimensions();
			}

			public void componentMoved(ComponentEvent e) {
				renderFrame.updateDimensions();
			}

			public void componentShown(ComponentEvent e) {
				renderFrame.setVisible(true);
			}

			public void componentHidden(ComponentEvent e) {
				renderFrame.setVisible(false);
			}
		});

		add(cefBrowserUI);

		renderFrame.setVisible(false);
		setVisible(true);

		setSize(width - 100, height);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		setSize(width + 100, height);
	}

	private void startCef() {
		CefApp.addAppHandler(new CefHandler(view));

		CefSettings settings = new CefSettings();
		settings.background_color = settings.new ColorType(255, 0, 0, 0);

		if (debug)
			settings.remote_debugging_port = 54321;

		cefApp = CefApp.getInstance(settings);

		cefClient = cefApp.createClient();

		CefMessageRouter msgRouter = CefMessageRouter.create(new SmudgrQueryRouter(view));
		cefClient.addMessageRouter(msgRouter);

		cefClient.addDialogHandler(new DialogHandler());

		cefBrowser = cefClient.createBrowser("smudgr://ui/html/main.html", false, false);

		cefBrowserUI = cefBrowser.getUIComponent();
	}

	public void drawToRenderFrame(Frame image) {
		renderFrame.drawFittedImage(image);
	}

	public void dispose() {
		renderFrame.setVisible(false);
		setVisible(false);

		CefApp.getInstance().dispose();

		super.dispose();
	}

	private static final long serialVersionUID = 6622012878252406208L;

}
