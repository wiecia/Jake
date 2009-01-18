package com.jakeapp.core.services;

import com.jakeapp.core.dao.IProjectMemberDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.jake.fss.IFSService;

import java.util.List;
import java.io.File;
import java.util.Date;
import java.io.FileNotFoundException;

/**
 * This handles the list of projects and their states
 * 
 * TODO: add table here what kind of states a project can have.
 * 
 * @author dominik, ..., johannes
 * 
 */
public interface IProjectsManagingService {
	/**
	 * Get a list of all Projects known to jake
	 * 
	 * @return a list of all known jake projects
	 */
	List<Project> getProjectList();


	/**
	 * Get a list of all Projects known to Jake matching the given
	 * InvitationState
	 * 
	 * @param state
	 *            The invitationState requested
	 * @return a list of all known jake projects matching the given
	 *         InvitationState
	 */
	List<Project> getProjectList(InvitationState state);

	/**
	 * Creates a new <code>Project</code> given the supplied name and rootPath
	 * 
	 * @param name
	 *            the name the new <code>Project</code> should have
	 * @param rootPath
	 *            the Path to the rootFolder of this <code>Project</code>. If it
	 *            does not yet exist, it is created.
	 * @param msgService
	 *            The MessageService this project should be assigned to. <b>THIS
	 *            CAN BE NULL!</b>
	 * @return the loaded instance of this <code>Project</code>
	 * @throws FileNotFoundException
	 *             if the rootPath is invalid
	 * @throws IllegalArgumentException
	 *             if the supplied <code>name</code> is invalid
	 */
	Project createProject(String name, String rootPath,
			MsgService msgService) throws FileNotFoundException,
			IllegalArgumentException;


	/**
	 * Start the given project (load database)
	 * 
	 * @param project
	 *            the <code>Project</code> to be loaded
	 * @param cl
	 *            get notified for changes
	 * @return true on success, false on error
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the <code>Project</code> does not exist
	 *             anymore
	 * @throws ProjectException
	 *             couldn't start the project for another reason (algorithms
	 *             missing, desktop not supported by java, etc.)
	 */
	boolean startProject(Project project, ChangeListener cl)
			throws IllegalArgumentException, FileNotFoundException,
			ProjectException;


	/**
	 * Stops the given project (unloads the database, eventually disconnects
	 * from the network)
	 * 
	 * @param project
	 *            the <code>Project</code> to be stopped.
	 * @return true on success, false on error
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the <code>Project</code> does not exist
	 *             anymore
	 */
	boolean stopProject(Project project)
			throws IllegalArgumentException, FileNotFoundException;

	/**
	 * Loads the given project (load database)
	 * 
	 * @param rootPath
	 *            The location where the project is. It must be a folder and it
	 *            must contain a database file describing the Jake-Project.
	 * @param name
	 *            Name of the Project
	 * @throws IllegalArgumentException
	 *             if the supplied name is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the loaded <code>Project</code> does not
	 *             exist anymore or it is not a readable directory or the
	 *             database- file cannot be opened.
	 * @return the opened, but not yet started, <code>Project</code>
	 */
	Project openProject(File rootPath, String name)
			throws IllegalArgumentException, FileNotFoundException;


	/**
	 * Stops the given project and removes it from the list of projects.
	 * 
	 * @param project
	 *            the <code>Project</code> to be closed.
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the <code>Project</code> does not exist
	 *             anymore
	 */
	void closeProject(Project project) throws IllegalArgumentException,
			FileNotFoundException;


	/**
	 * @param project
	 *            the <code>Project</code> to be deleted
	 * @return true on success, false on error
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws SecurityException
	 *             if the supplied <code>Project</code> could not be deleted due
	 *             to filesystem permissons
	 * @throws FileNotFoundException
	 *             if the rootFolder of the <code>Project</code> already got
	 *             deleted. The project is removed from within jake, but the
	 *             user should be informed that he should not manually delete
	 *             projects.
	 */
	boolean deleteProject(Project project)
			throws IllegalArgumentException, SecurityException,
			FileNotFoundException;

	/**
	 * Get all LogEntrys from the supplied project
	 * 
	 * @param project
	 *            the <code>Project</code> to get the <code>LogEntry</code>s of
	 * @return a List of <code>LogEntry</code>s corresponding to this
	 *         <code>Project</code>
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null or does not
	 *             exist.
	 */
	List<LogEntry<? extends ILogable>> getLog(Project project)
			throws IllegalArgumentException;

	/**
	 * Gets all LogEntrys from the supplied <code>JakeObject</code>
	 * 
	 * @param jakeObject
	 *            the JakeObject to get the LogEntrys for
	 * @return a List of LogEntrys
	 * @throws IllegalArgumentException
	 *             if the supplied JakeObject is null
	 */
	List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
			throws IllegalArgumentException;


	/**
	 * Assigns a UserId to a project if this project has no UserId set yet.
	 * 
	 * @param project
	 *            the Project to set the UserId
	 * @param userId
	 *            the UserId to be set
	 * @throws IllegalArgumentException
	 *             if project or userId are null
	 * @throws IllegalAccessException
	 *             if the project already has a userId set
	 */
	void assignUserToProject(Project project, UserId userId)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * Sets the level of trust we have to the specified user. If the user does
	 * not exist yet in the <code>Project</code> the user is invited.
	 * 
	 * @param project
	 *            The <code>Project</code> to apply the new level of trust to.
	 * @param userId
	 *            The user whose trustlevel gets changed.
	 * @param trust
	 *            The new level of trust for the specified user.
	 * @throws IllegalArgumentException
	 *             if project or userId are null
	 * @throws IllegalAccessException
	 *             if the project has no userId set yet.
	 */
	void setTrust(Project project, UserId userId, TrustState trust)
			throws IllegalArgumentException, IllegalAccessException;
	
	/**
	 * @return a service for file-operations
	 */
	IFSService getFileServices(Project p);
	
	/**
	 * @return a service for note operations
	 * @throws ProjectNotLoadedException
	 *         if the project is not open.
	 * @throws IllegalArgumentException
	 *         if <code>project</code> is null.
	 */
	INoteManagingService getNoteManagingService(Project p)
		throws ProjectNotLoadedException, IllegalArgumentException;

	/**
	 * @return The number of files in a Project
	 * @see #getAllProjectFiles(Project, AvailabiltyListener)
	 */
	AvailableLaterObject<Integer> getProjectFileCount(Project project, AvailabilityListener listener)
		throws NoSuchProjectException, FileNotFoundException,IllegalArgumentException;

	/**
	 * @param project
	 * @return The amount of bytes all files in a project take up.
	 * @see #getAllProjectFiles(Project, AvailabiltyListener)
	 */
	AvailableLaterObject<Long> getProjectSizeTotal(Project project, AvailabilityListener listener)
		throws NoSuchProjectException, FileNotFoundException,IllegalArgumentException;

	/**
	 * Retrieves all Files that exist in a <code>Project</code>
	 * 
	 * @param project
	 * @param listener A listener for reporting status
	 * @return A List of all files.
	 * @throws NoSuchProjectException If the specified Project does not exist.
	 * @throws FileNotFoundException If the root path of the specified Project is not found.
	 * @throws IllegalArgumentException If project is null.
	 */
	AvailableLaterObject<List<FileObject>> getAllProjectFiles(Project project, AvailabilityListener listener)
		throws NoSuchProjectException, FileNotFoundException,IllegalArgumentException;;
	
	/**
	 * Joins the Project and notifies the inviter.
	 * @param project The project to join.
	 * @param inviter The user that invited us to join the project.
	 * 	She is notified that we accepted joining the project.
	 * @throws IllegalStateException if <code>project</code> is not an invitation. 
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void joinProject(Project project,UserId inviter)
		throws IllegalStateException,NoSuchProjectException;


	/**
	 * Rejects joining of a invited project and notifies the inviter.
	 * @param project The project not to join.
	 * @param inviter The user that invited us to join the project.
	 * 	She is notified that we rejected joining the project.
	 * @throws IllegalStateException if <code>project</code> is not an invitation. 
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void rejectProject(Project project, UserId inviter)
		throws IllegalStateException,NoSuchProjectException;
	
	/**
	 * Sets the project a new name and persists it.
	 * @param project The {@link Project}t to rename.
	 * @param newName the new name of the project.
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void updateProjectName(Project project, String newName)
		throws NoSuchProjectException;
	
	/**
	 * @return The date and time when a {@link JakeObject} was last modified. If the JakeObject
	 * has never been modified, the current Date is returned.
	 * @throws NoSuchProjectException if the JakeObject's Project does not exist.
	 * @throws IllegalArgumentException If the JakeObject does not exist/is null.
	 */
	Date getLastEdit(JakeObject jo)
		throws NoSuchProjectException, IllegalArgumentException;


	/**
	 * @return The ProjectMember who last modified the JakeObject. If the JakeObject
	 * has never been modified, null is returned.
	 * @throws NoSuchProjectException if the JakeObject's Project does not exist.
	 * @throws IllegalArgumentException If the JakeObject does not exist/is null.
	 */
	ProjectMember getLastEditor(JakeObject jo) throws NoSuchProjectException,
			IllegalArgumentException;


	ProjectMember getProjectMember(Project project,MsgService msg);


	String getProjectMemberID(Project project,ProjectMember pm);


	/**
	 * 
	 * @param project The Project that Fileobject should be located in.
	 * @param relpath The path where to look for a FileObject
	 * @return A FileObject at the given relative path
	 * @throws NoSuchJakeObjectException If no such FileObject exists.
	 */
	FileObject getFileObjectByRelPath(Project project, String relpath)
			throws NoSuchJakeObjectException;
}