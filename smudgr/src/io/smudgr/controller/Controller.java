package io.smudgr.controller;

import java.util.ArrayList;

import io.smudgr.controller.controls.Controllable;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.view.View;

public interface Controller {

	public void start();

	public void pause();

	public void stop();

	public void update();

	public void add(Object o);

	public void add(Object o, int id);

	public ArrayList<Controllable> getControls();

	public ArrayList<ControllerExtension> getExtensions();

	public Smudge getSmudge();

	public void setSmudge(Smudge smudge);

	public View getView();

	public void setView(View view);

}
