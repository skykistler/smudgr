package io.smudgr.view.cef;

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

	public void init() {
		window.init();
	}

	public void draw() {
		Frame frame = source.getFrame();

		window.drawToRenderFrame(frame);
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

	public void dispose() {
		window.dispose();
	}

}
