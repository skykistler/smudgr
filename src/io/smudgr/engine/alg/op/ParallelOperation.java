/**
 *
 */
package io.smudgr.engine.alg.op;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.util.Frame;

/**
 * The abstract {@link ParallelOperation} class allows implementors to easily
 * execute an operation in parallel across each pixel index list.
 */
public abstract class ParallelOperation extends Operation {

	private ArrayList<ParallelOperationTask> tasks = new ArrayList<ParallelOperationTask>();
	private ForkJoinPool pool = new ForkJoinPool();

	// Declared for memory reuse
	private int taskIndex;
	private ArrayList<PixelIndexList> selectedPixelLists;

	@Override
	public void execute(Frame img) {
		// Pre-parallel operations
		preParallel(img);

		selectedPixelLists = getSelectedPixels();

		// Add any necessary task instances and set all of their working data
		for (taskIndex = 0; taskIndex < selectedPixelLists.size(); taskIndex++) {

			// If the current selected pixels list is longer than the amount of
			// workers previously allocated, add a new one
			if (taskIndex >= tasks.size()) {
				ParallelOperationTask t = getParallelTask();
				tasks.add(t);
			}

			// Update the working data
			tasks.get(taskIndex).setData(img, selectedPixelLists.get(taskIndex));
		}

		// Don't bother starting a pool if there's only one task to run
		if (tasks.size() == 1)
			tasks.get(0).call();

		// Otherwise, invoke all the tasks
		else {
			try {
				pool.invokeAll(tasks.subList(0, selectedPixelLists.size()), 30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}
		}

		// Post-parallel operations
		postParallel(img);
	}

	/**
	 * Implementations of this method will be run prior to the parallel
	 * operations.
	 *
	 * @param img
	 * @see ParallelOperation#postParallel(Frame)
	 */
	protected void preParallel(Frame img) {
		// Optional implementation
	}

	/**
	 * Returns a new worker task to add to the pool.
	 * <p>
	 * Implementations of {@link ParallelOperation} should return a new
	 * {@link ParallelOperationTask} with this method.
	 *
	 * @see ParallelOperationTask
	 */
	protected abstract ParallelOperationTask getParallelTask();

	/**
	 * Implementations of this method will be run after the parallel operations.
	 *
	 * @param img
	 * @see ParallelOperation#preParallel(Frame)
	 */
	protected void postParallel(Frame img) {
		// Optional implementation
	}

}
