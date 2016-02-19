package io.smudgr.view;

import io.smudgr.controller.Controller;

public interface View {
	public void init();

	public void draw();

	public void dispose();

	public Controller getController();

	public void setController(Controller c);

}
