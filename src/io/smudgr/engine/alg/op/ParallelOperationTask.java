/**
 *
 */
package io.smudgr.engine.alg.op;

import java.util.concurrent.Callable;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.util.Frame;

/**
 *
 */
public abstract class ParallelOperationTask implements Callable<Boolean> {

	private Frame toImg;
	private PixelIndexList forList;

	/**
	 * Updates the data to work on for the next
	 * {@link ParallelOperationTask#executeParallel(Frame, PixelIndexList)} call
	 *
	 * @param img
	 *            {@link Frame}
	 * @param coords
	 *            {@link PixelIndexList}
	 */
	public void setData(Frame img, PixelIndexList coords) {
		toImg = img;
		forList = coords;
	}

	/**
	 * Calls the parallel execution behavior
	 */
	@Override
	public Boolean call() {
		executeParallel(toImg, forList);
		return true;
	}

	/**
	 * Execute this task on the given image and coordinate list
	 *
	 * @param img
	 *            {@link Frame}
	 * @param coords
	 *            {@link PixelIndexList}
	 */
	public abstract void executeParallel(Frame img, PixelIndexList coords);

}
