package io.smudgr.extensions.cef.commands;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class HelloSmudgr implements CefCommand {

	public String getCommand() {
		return "smudge.hello";
	}

	public boolean request(String content) {
		(new Thread(new Runnable() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(null, "Hello, smudgr!");
					}
				});
			}
		})).start();

		return true;

	}

	public String onSuccess() {
		return "Successfully greeted the backend";
	}

	public String onFailure() {
		return "Could not greet the backend";
	}

}
