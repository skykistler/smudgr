package io.smudgr.alg.math;

import java.util.ArrayList;

import io.smudgr.alg.bound.Bound;
import processing.core.PImage;

public abstract class CoordFunction {
	ArrayList<ArrayList<Integer>> coordSet = null;
	private PImage image;
	private Bound bound;

	protected abstract void generate();

	public ArrayList<ArrayList<Integer>> getCoordSet() {
		return coordSet;
	}

	public Bound getBound() {
		return bound;
	}

	public void setBound(Bound bound) {
		this.bound = bound;
		coordSet = new ArrayList<ArrayList<Integer>>();
		generate();
	}

	public PImage getImage() {
		return image;
	}

	public void setImage(PImage image) {
		this.image = image;
		coordSet = new ArrayList<ArrayList<Integer>>();
		generate();
	}

}
