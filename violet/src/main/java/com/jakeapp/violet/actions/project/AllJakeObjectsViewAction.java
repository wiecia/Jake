package com.jakeapp.violet.actions.project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFileModificationListener;
import com.jakeapp.violet.model.ILogModificationListener;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Gets all JakeObject that ever were in the log (including remote-only,
 * deleted, ...).
 * 
 * @author johannes
 */
public class AllJakeObjectsViewAction extends
		AvailableLaterObject<AllJakeObjectsView> implements
		IFileModificationListener, ILogModificationListener {

	private static Logger log = Logger
			.getLogger(AllJakeObjectsViewAction.class);

	private ProjectModel model;

	public AllJakeObjectsViewAction(ProjectModel model) {
		this.model = model;
		this.model.getFss().addModificationListener(this);
		this.model.getLog().addModificationListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AllJakeObjectsView calculate() {
		Set<JakeObject> objects = new HashSet<JakeObject>();
		try {
			objects.addAll(this.model.getLog().getExistingFileObjects(true));
		} catch (SQLException e) {
			log.error(e);
		}
		try {
			List<String> files = this.model.getFss().recursiveListFiles();
			for (String s : files) {
				objects.add(new JakeObject(s));
			}
		} catch (IOException e) {
			log.error(e);
		}
		return new AllJakeObjectsView(model, objects);
	}

	@Override
	public void logModified(JakeObject jo, ModifyActions action) {
		this.get().onModification(jo);
	}

	@Override
	public void fileModified(String relpath,
			com.jakeapp.jake.fss.IModificationListener.ModifyActions action) {
		this.get().onModification(new JakeObject(relpath));
	}

}