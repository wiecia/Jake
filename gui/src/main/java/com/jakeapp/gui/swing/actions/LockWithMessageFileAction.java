package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import javax.swing.*;

import org.jdesktop.application.ResourceMap;

import java.awt.event.ActionEvent;
/**
 * Action for locking files with a message. No batch processing.
 * @author Simon
 *
 */
public class LockWithMessageFileAction extends FileAction {

	private static final long serialVersionUID = -7898650898881238796L;
	private final ResourceMap resourceMap;

	public LockWithMessageFileAction() {
		super();

		this.resourceMap = JakeMainView.getResouceMap();
		
		String actionStr = this.resourceMap.getString("lockWithMessageMenuItem.text");
		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		// only enable if exact one element is selected AND that element is NOT a folder.
		setEnabled(this.isSingleFileSelected());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String promptStr = this.resourceMap.getString("promptLockWithComment");
		String comment = JOptionPane.showInputDialog(promptStr, null);
		
		//TODO lock with comment 
		
	}
}