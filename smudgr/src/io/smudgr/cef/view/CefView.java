package io.smudgr.cef.view;

import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.source.Source;
import io.smudgr.view.View;

public class CefView implements View {

	private Controller controller;
	private Source source;

	private CefWindow window;

	public CefView(Controller c, boolean debug) {
		this.controller = c;
		controller.setView(this);

		window = new CefWindow(this, debug);
	}

	public void start() {
		window.init();
	}

	public void update() {
		Frame frame = source.getFrame();

		window.getRenderFrame().draw(frame);
	}

	public void stop() {
		window.dispose();
	}

	public CefWindow getWindow() {
		return window;
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

}
