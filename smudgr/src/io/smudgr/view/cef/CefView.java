package io.smudgr.view.cef;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;

import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.source.Source;
import io.smudgr.view.View;

public class CefView implements View {
	private int WINDOW_WIDTH = 800;
	private int WINDOW_HEIGHT = 600;

	private Controller controller;
	private Source source;

	private JFrame window;
	private JLayeredPane layeredPane;
	private Canvas canvas;
	private BufferStrategy strategy;

	private CefApp cefApp;
	private CefClient cefClient;
	private CefBrowser cefBrowser;
	private Component cefBrowserUI;

	public CefView(Controller c) {
		this.controller = c;
		controller.setView(this);
	}

	public void init() {
		startCef();
		makeWindow();
	}

	private void startCef() {
		CefApp.addAppHandler(new CefHandler(this));

		CefSettings settings = new CefSettings();
		settings.background_color = settings.new ColorType(255, 0, 0, 0);
		cefApp = CefApp.getInstance(settings);

		cefClient = cefApp.createClient();

		CefMessageRouter msgRouter = CefMessageRouter.create(new SmudgrQueryRouter(this));
		cefClient.addMessageRouter(msgRouter);

		cefBrowser = cefClient.createBrowser("smudgr://ui/index.html", false, false);

		cefBrowserUI = cefBrowser.getUIComponent();
	}

	private void makeWindow() {
		window = new JFrame("smudgr");
		window.getContentPane().setLayout(new BorderLayout());

		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setBackground(Color.black);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
		window.setLocation(x, y);

		layeredPane = new JLayeredPane();
		layeredPane.setSize(window.getSize());

		canvas = new Canvas();
		canvas.setLocation(200, 200);
		canvas.setSize(400, 400);

		cefBrowserUI.setSize(window.getSize());

		layeredPane.add(cefBrowserUI, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(canvas, JLayeredPane.PALETTE_LAYER);

		window.add(layeredPane);

		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				getController().stop();
			}
		});

		window.setVisible(true);
		window.createBufferStrategy(2);

		canvas.createBufferStrategy(2);
		strategy = canvas.getBufferStrategy();
	}

	public void draw() {
		Frame frame = source.getFrame();

		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, window.getWidth(), window.getHeight());

		if (frame != null)
			drawFittedImage(g, frame.getBufferedImage());

		g.dispose();

		strategy.show();

	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source s) {
		source = s;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller c) {
		controller = c;

		if (controller.getView() != this)
			controller.setView(this);
	}

	public void dispose() {
		window.dispose();

		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			CefApp.getInstance().dispose();
		}
	}

	private void drawFittedImage(Graphics g, BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();

		boolean largerThanCanvas = height > canvas.getHeight() || width > canvas.getWidth();
		boolean smallerThanCanvas = height < canvas.getHeight() && width < canvas.getWidth();
		if (largerThanCanvas || smallerThanCanvas) {
			width = (int) (width * ((double) canvas.getHeight() / height));
			height = canvas.getHeight();
		}

		int x = canvas.getWidth() / 2 - width / 2;
		int y = canvas.getHeight() / 2 - height / 2;

		g.drawImage(image, x, y, width, height, null);
	}

}
