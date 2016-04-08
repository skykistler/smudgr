package io.smudgr.view;

import io.smudgr.source.Source;

public interface View {
	public void start();

	public void update();

	public void stop();

	public Source getSource();

	public void setSource(Source s);

}
