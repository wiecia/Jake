package com.jakeapp.gui.swing.actions;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

/**
 * @author: studpete
 */
public class StartStopOrJoinProjectAction extends CompoundProjectAction {
	private final StartStopProjectAction startStopAction = new StartStopProjectAction();
	private final JoinProjectAction joinAction = new JoinProjectAction();
	private static final Logger log = Logger.getLogger(StartStopOrJoinProjectAction.class);


	public StartStopOrJoinProjectAction() {
		super();

		// link updates
		startStopAction.addPropertyChangeListener(up);
		joinAction.addPropertyChangeListener(up);

		updateAction();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug("action performed.");
		// use either on of the two actions within
		if (isNormalAction()) {
			log.debug("isNormalAction");
			startStopAction.actionPerformed(e);
		} else {
			log.debug("joinAction");
			joinAction.actionPerformed(e);
		}
	}

	@Override
	public void updateAction() {
		log.debug("updating combined action with proj " + getProject());
		// wait for full initialize
		if (startStopAction == null) return;
		log.debug("updating combined action: do it");

		// use either on of the two actions within
		if (isNormalAction()) {
			internalActivateAction(startStopAction);
		} else {
			internalActivateAction(joinAction);
		}
	}

	private boolean isNormalAction() {
		return getProject() == null || !getProject().isInvitation();
	}
}
