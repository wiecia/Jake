package com.jakeapp.core.services;

import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.ILoginStateListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is the only visible interface to other components accessing the jake
 * core
 */
public interface IFrontendService {

	/**
	 * This method is used to authenticate a client accessing the jake-core. On
	 * a successful authentication a Session-Identifier (sessionId) is returned
	 *
	 * @param credentials a Map of credentials // TODO to be specified. Has to be empty for
	 * 		current implementation
	 * @param changeListener						 the callback or JakeObject-changes
	 * @return a Session-Identifier
	 * @throws IllegalArgumentException	 if the supplied credentials are null or one of the entries is
	 *                                     null
	 * @throws InvalidCredentialsException if the supplied credentials are wrong
	 */
	public String authenticate(Map<String, String> credentials, ChangeListener changeListener)
			  throws IllegalArgumentException, InvalidCredentialsException;

	/**
	 * This method logs a specific client out.
	 *
	 * @param sessionId the session to be terminated
	 * @return true on success, false on failure
	 * @throws IllegalArgumentException if the supplied session is null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no such session existed
	 */
	public boolean logout(String sessionId) throws IllegalArgumentException,
			  FrontendNotLoggedInException;

	/**
	 * Gets an instance of a {@link IProjectsManagingService}
	 *
	 * @param sessionId a Session-Identifier
	 * @return a ProjectService on success
	 * @throws IllegalArgumentException if the supplied session is null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no such session existed
	 * @throws IllegalStateException	 if no ProjectService is available
	 */
	public IProjectsManagingService getProjectsManagingService(String sessionId)
			  throws IllegalArgumentException, FrontendNotLoggedInException, IllegalStateException;


	/**
	 * Gets a list of the currently available MessageServices
	 *
	 * @param sessionId a Session-Identifier
	 * @return a List of MessageServices
	 * @throws IllegalArgumentException if the supplied session is null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no such session existed
	 * @throws IllegalStateException	 if no MessageServices are configured for this component
	 */
	public List<MsgService<UserId>> getMsgServices(String sessionId) throws FrontendNotLoggedInException;

	/**
	 * Gets the SyncService
	 *
	 * @param sessionId a Session-Identifier
	 * @return
	 * @throws IllegalArgumentException if the supplied session is null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no such session existed
	 * @throws IllegalStateException	 if no MessageServices are configured for this component
	 */
	public IFriendlySyncService getSyncService(String sessionId) throws FrontendNotLoggedInException;


	/**
	 * Method that creates an account by the IM-Provider
	 *
	 * @param sessionId
	 * @param credentials
	 * @return
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 * @throws Exception							the creation failed for another reason
	 * @throws com.jakeapp.jake.ics.exceptions.NetworkException
	 */
	public AvailableLaterObject<Void> createAccount(String sessionId, ServiceCredentials credentials)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException, NetworkException;


	/**
	 * Provides and registers a new MsgService ready for login.
	 *
	 * @param sessionId
	 * @param credentials
	 * @return
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 */
	public MsgService addAccount(String sessionId, ServiceCredentials credentials)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException;

	/**
	 * Removes an account from the database registry
	 *
	 * @param sessionId
	 * @param msg
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 * @throws com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException
	 */
	void removeAccount(String sessionId, MsgService msg)  throws FrontendNotLoggedInException,
																															 NoSuchMsgServiceException;

	/**
	 * Logs all Message services that are currently active out.
	 * After calling this method all MessageServices within the specified
	 * Session are logged out.
	 *
	 * @param sessionId
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *          If <code>sessionId</code> is invalid.
	 */
	void signOut(String sessionId) throws FrontendNotLoggedInException;

	/**
	 * Pings the core to prevent session expiry
	 *
	 * @param sessionId a Session-Identifier
	 * @throws IllegalArgumentException if the supplied session is null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no such session existed
	 */
	public void ping(String sessionId) throws IllegalArgumentException,
			  FrontendNotLoggedInException;

	/**
	 * @return All stored Service-credentials that were 'recently' used.
	 *         A definition of recently is pending.
	 */
	Collection<ServiceCredentials> getLastLogins();

	/**
	 * Logs a Messageservice in and sets the password
	 * @param session
	 * @param service
	 * @param password new password to be stored, or null to use the stored one.
	 * @param rememberPassword
	 * @return
	 * @internally throws Exception
	 */
	AvailableLaterObject<Boolean> login(String session, MsgService service, String password,
			boolean rememberPassword, final ILoginStateListener loginListener);


	/**
	 * Logs a Messageservice in with the stored password
	 * @param session
	 * @param service
	 * @param credentials
	 * @return
	 * @internally throws Exception
	 */
	AvailableLaterObject<Boolean> login(String session, MsgService service,
					ServiceCredentials credentials, final ILoginStateListener loginListener);

}
