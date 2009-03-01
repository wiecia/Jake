package com.jakeapp.core.services;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.CreateAccountFuture;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO this 'factory' stores the elements it constructed, but they are never removed.

/**
 * Factory Class to create MsgServices by giving ServiceCredentials
 */
@Transactional
public class MsgServiceFactory {

	private static Logger log = Logger.getLogger(MsgServiceFactory.class);

	@Injected
	private IServiceCredentialsDao serviceCredentialsDao;

	private Map<ServiceCredentials,MsgService<UserId>> msgServices =
			new HashMap<ServiceCredentials,MsgService<UserId>>();
	
	private Map<ServiceCredentials,MsgService<UserId>> getMsgServices() {
		return msgServices;
	}

	@Injected
	private ICSManager icsManager;

	
	public ICSManager getICSManager() {
		return icsManager;
	}

	
	public void setICSManager(ICSManager icsManager) {
		this.icsManager = icsManager;
	}

	public MsgServiceFactory() {
		log.debug("calling empty Constructor");
	}

	public MsgServiceFactory(IServiceCredentialsDao serviceCredentialsDao) {
		log.debug("calling constructor with serviceCredentialsDao");
		this.serviceCredentialsDao = serviceCredentialsDao;
	}

	private IServiceCredentialsDao getServiceCredentialsDao() {
		return serviceCredentialsDao;
	}

	/**
	 * Creates a new MessageService if one for the specified credentials does not exist
	 * yet.
	 * @param credentials The ServiceCredentials to create a MsgService for.
	 * @throws ProtocolNotSupportedException if the protocol specified in the
	 * 	credentials is not supported.
	 */
	public MsgService<UserId> createMsgService(ServiceCredentials credentials)
					throws ProtocolNotSupportedException {
		MsgService<UserId> msgService;
		boolean needsInitialization=false;
		
		if(credentials == null) {
			throw new InvalidCredentialsException("Credentials cannot be null!");
		}
		
		MsgService.setServiceCredentialsDao(getServiceCredentialsDao());

		log.debug("calling createMsgService with crendentials: " + credentials);
		log.debug("credentials.UserId="+((credentials==null)?"null":credentials.getUserId()));
		log.debug("ptpwlength="+((credentials==null||credentials.getPlainTextPassword()==null)?"null":""+credentials.getPlainTextPassword().length()));

		if (this.getMsgServices().containsKey(credentials) && (msgService=(MsgService<UserId>) (this.getMsgServices().get(credentials)))!=null  )
			log.debug("retrieving a STORED MessageService.");
		else if (credentials.getProtocol() != null && credentials.getProtocol()
						.equals(ProtocolType.XMPP)) {
			log.debug("Creating new XMPPMsgService for userId " + credentials.getUserId());
			msgService = new XMPPMsgService();
			msgService.setIcsManager(icsManager);
			msgService.setServiceCredentials(credentials);
			this.getMsgServices().put(credentials, msgService);
			needsInitialization = true;
		} else {
			log.warn("Currently unsupported protocol given");
			throw new ProtocolNotSupportedException();
		}

		// create the UserId from the credentials.
		if (needsInitialization)
			msgService.setUserId(createUserforMsgService(credentials));

		log.debug("resulting MsgService is " + msgService);

		return msgService;
	}

	/**
	 * Every MsgService has a UserId connected.
	 * This creates the UserId from the ServiceCredentals.
	 *
	 * @param credentials ServiceCredentials that are used to create the UserId
	 * @return
	 * @throws ProtocolNotSupportedException
	 */
	private UserId createUserforMsgService(ServiceCredentials credentials)
					throws ProtocolNotSupportedException {

		// switch through the supported protocols and create the user
		switch (credentials.getProtocol()) {
			case XMPP:
				UUID res = UUID.randomUUID();

				if (credentials.getUuid() != null)
					res = UUID.fromString(credentials.getUuid());

				credentials.setUuid(res);

				return new UserId(ProtocolType.XMPP, credentials.getUserId());

			default:
				throw new ProtocolNotSupportedException("Backend not yet implemented");
		}
	}

	@Transactional
	public List<MsgService<UserId>> getAll() {
		log.debug("calling getAll");
		// ensureInitialised();

		List<ServiceCredentials> credentialsList = this.serviceCredentialsDao.getAll();
		log.debug("Found " + credentialsList.size() + " Credentials in the DB");

		List<MsgService<UserId>> result = new ArrayList<MsgService<UserId>>();

		for (ServiceCredentials credentials : credentialsList) {
			try {
				MsgService<UserId> service = this.createMsgService(credentials);
				if (!result.contains(service)) {
					result.add(service);
				}
			} catch (ProtocolNotSupportedException e) {
				log.warn("Protocol not supported: ", e);
				log.info("ignoring unsupported entry " + credentials);
			}
		}
		log.debug("Made " + result.size() + " messageservices");

		return result;
	}

	/**
	 * creates and adds a msgservice for the right protocol
	 * This adds the ServiceCrenentials from the MsgService into the database.
	 *
	 * @param credentials
	 * @return the service
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 */
	public MsgService addMsgService(ServiceCredentials credentials)
					throws InvalidCredentialsException, ProtocolNotSupportedException {
		log.debug("calling addMsgService");

		MsgService<UserId> msgService = this.createMsgService(credentials);

		// add to global array
		this.getMsgServices().put(credentials,msgService);

		// persist the ServicCredentials!
		this.getServiceCredentialsDao().persist(credentials);

		return msgService;
	}

	/**
	 * Tries to return the MsgService that is associated with ServiceCredentials.
	 * ServiceCredentials is saved in the DB, MsgServiccreateMsgServicee not.
	 * If no MsgService was created until now, we try to created it.
	 *
	 * @param credentials
	 * @return the MsgService or null.
	 */
	public MsgService getByCredentials(ServiceCredentials credentials) {
		log.debug("Get MsgService by credentials: " + credentials);

		for (MsgService msg : getAll()) {
			if (msg.getServiceCredentials().equals(credentials)) {
				return msg;
			}
		}
		try {
			return this.addMsgService(credentials);
		} catch (Exception e) {
			log.error("Unable to create MessageService:", e);
			return null;
		}
	}
}