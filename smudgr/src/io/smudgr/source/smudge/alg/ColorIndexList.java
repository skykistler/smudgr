package io.smudgr.source.smudge.alg;

import gnu.trove.list.array.TIntArrayList;

public class ColorIndexList extends TIntArrayList {
	public void add(int index, int value) {
		insert(index, value);
	}
}
