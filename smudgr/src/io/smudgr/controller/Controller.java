package io.smudgr.controller;

import java.util.ArrayList;
import java.util.Collection;

import io.smudgr.controller.controls.Controllable;
import io.smudgr.smudge.Smudge;

public interface Controller {

	public void start();

	public void pause();

	public void stop();

	public void update();

	public void add(Object o);

	public ProjectIdManager getIdManager();

	public ArrayList<Controllable> getControls();

	public ControllerExtension getExtension(String name);

	public Collection<ControllerExtension> getExtensions();

	public Smudge getSmudge();

	public void setSmudge(Smudge smudge);

}
