package io.smudgr.view;

import io.smudgr.controller.Controller;
import io.smudgr.source.Source;

public interface View {
	public void init();

	public void draw();

	public Source getSource();

	public void setSource(Source s);

	public Controller getController();

	public void setController(Controller c);

	public void dispose();

}
