package io.smudgr.out;

import io.smudgr.source.Source;

public interface Output {
	public void init();

	public void setSource(Source s);
}
