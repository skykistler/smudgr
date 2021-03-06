package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.engine.Smudge;

/**
 * Take the {@link Smudge} at the {@code fromIndex} and insert it at the
 * {@code toIndex}
 */
public class RackSmudgeMove implements ApiCommand {

	@Override
	public String getCommand() {
		return "rack.smudge.move";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		int fromIndex = Integer.parseInt(data.get("fromIndex"));
		int toIndex = Integer.parseInt(data.get("toIndex"));

		getProject().getRack().move(fromIndex, toIndex);

		return ApiMessage.ok(getCommand(), data);
	}

}
