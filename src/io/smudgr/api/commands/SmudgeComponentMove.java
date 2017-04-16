package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.SmudgeComponent;

/**
 * Take the {@link SmudgeComponent} at the {@code fromIndex} and insert it at
 * the
 * {@code toIndex}
 */
public class SmudgeComponentMove implements ApiCommand {

	@Override
	public String getCommand() {
		return "smudge.component.move";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		int id = (int) data.getNumber("smudge");
		Smudge smudge = (Smudge) getProject().getItem(id);

		int fromIndex = Integer.parseInt(data.get("fromIndex"));
		int toIndex = Integer.parseInt(data.get("toIndex"));

		smudge.move(fromIndex, toIndex);

		return ApiMessage.ok(getCommand(), data);
	}

}
