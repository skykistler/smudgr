package io.smudgr.view.cef;

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

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;

import io.smudgr.Smudge;
import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.view.View;

public class CefView implements View {
	private int WINDOW_WIDTH = 800;
	private int WINDOW_HEIGHT = 600;

	private Controller controller;
	private Frame frame;
	private int canvasWidth;
	private int canvasHeight;

	private JFrame window;
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

		cefBrowser = cefClient.createBrowser("smudgr://handler.html", false, false);
		cefBrowserUI = cefBrowser.getUIComponent();
	}

	private void makeWindow() {
		window = new JFrame("smudgr");

		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setBackground(Color.black);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
		window.setLocation(x, y);

		canvas = new Canvas();
		canvas.setSize(400, 300);
		//		canvas.createBufferStrategy(2);
		//		strategy = canvas.getBufferStrategy();

		window.add(cefBrowserUI);

		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				getController().stop();
			}
		});

		window.setVisible(true);
	}

	public void draw() {
		if (true)
			return;
		Smudge smudge = controller.getSmudge();

		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, window.getWidth(), window.getHeight());

		frame = smudge.render();
		if (frame != null)
			drawFittedImage(g, frame.getBufferedImage());

		g.dispose();

		strategy.show();

	}

	public void dispose() {
		CefApp.getInstance().dispose();
		window.dispose();
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller c) {
		controller = c;

		if (controller.getView() != this)
			controller.setView(this);
	}

	private void drawFittedImage(Graphics g, BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();

		boolean largerThanCanvas = height > canvasHeight || width > canvasWidth;
		boolean smallerThanCanvas = height < canvasHeight && width < canvasWidth;
		if (largerThanCanvas || smallerThanCanvas) {
			width = (int) (width * ((double) canvasHeight / height));
			height = canvasHeight;
		}

		int x = canvasWidth / 2 - width / 2;
		int y = canvasHeight / 2 - height / 2;

		g.drawImage(image, x, y, width, height, null);
	}

}
