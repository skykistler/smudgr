package io.smudgr.view;

import io.smudgr.controller.SmudgeController;
import io.smudgr.source.Source;

public interface View {
	public void init();

	public void draw();

	public Source getSource();

	public void setSource(Source s);

	public SmudgeController getController();

	public void setController(SmudgeController c);

	public void dispose();

}
