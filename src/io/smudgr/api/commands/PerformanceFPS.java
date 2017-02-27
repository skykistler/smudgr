/**
 *
 */
package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;

/**
 * Gets the current rate the {@link Project#getRack()} is rendering at.
 */
public class PerformanceFPS implements ApiCommand {

	@Override
	public String getCommand() {
		return "performance.fps";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		return ApiMessage.ok(getCommand(), new ApiMessage("fps", Controller.getInstance().getActualFPS()));
	}

}
