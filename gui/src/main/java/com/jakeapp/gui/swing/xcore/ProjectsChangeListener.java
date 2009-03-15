package com.jakeapp.gui.swing.xcore;

import java.util.EnumSet;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback.DataReason;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import org.apache.log4j.Logger;

public class ProjectsChangeListener implements ChangeListener {
	private static final Logger log = Logger.getLogger(ProjectsChangeListener.class);

	public ProjectsChangeListener() {
	}

	@Override public INegotiationSuccessListener beganRequest(JakeObject jo) {
		log.debug("beganRequest for " + jo);
		return null;
	}

	@Override public void pullNegotiationDone(JakeObject jo) {
		log.debug("pullNegitiationDone: " + jo);
	}

	@Override public void pullDone(JakeObject jo) {
		log.debug("pullDone: " + jo);

		EventCore.get().fireDataChanged(EnumSet.of(DataReason.Files, DataReason.Notes), jo.getProject());
	}

	@Override public void pullProgressUpdate(JakeObject jo, Status status,
					double progress) {
		log.debug("pullProgressUpdate: " + jo + ", status: " + status + ", progress: " + progress);
	}

	@Override public void onlineStatusChanged(Project p) {
		log.debug("GUI received online status changed... updating");

		EventCore.get().fireUserChanged(p);
	}

	@Override public void syncStateChanged(Project p, SyncState state) {
		log.debug("Sync State Changed for " + p.getName() + " to " + state);

		// fixme: only update on success
		EventCore.get().fireLogChanged(p);
	}

	@Override
	public void pullFailed(JakeObject jo, Exception reason) {
		log.debug("pullFailed: " + jo, reason);
	}
}