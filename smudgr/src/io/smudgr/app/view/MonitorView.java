package io.smudgr.app.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.util.Frame;

public class MonitorView implements View, KeyListener {

	private JFrame monitor;

	private BufferedImage nativeImage = null;

	public void start() {
		monitor = new JFrame("smudgr");
		monitor.setBounds(new Rectangle(800, 600));
		monitor.setVisible(true);
		monitor.createBufferStrategy(2);

		monitor.addKeyListener(this);
		monitor.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Controller.getInstance().stop();
			}
		});
	}

	public void update(Frame frame) {
		if (nativeImage == null || monitor.getWidth() != nativeImage.getWidth() || monitor.getHeight() != nativeImage.getHeight())
			nativeImage = getNewNativeImage(monitor.getWidth(), monitor.getHeight());

		frame.drawTo(nativeImage);

		BufferStrategy st = monitor.getBufferStrategy();

		Graphics g = st.getDrawGraphics();

		if (nativeImage != null)
			g.drawImage(nativeImage, 0, 0, monitor.getWidth(), monitor.getHeight(), null);
		else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, monitor.getWidth(), monitor.getHeight());
		}

		g.dispose();
		st.show();
	}

	public void stop() {
		monitor.setVisible(false);
		monitor.dispose();
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			Controller.getInstance().stop();
	}

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

}
