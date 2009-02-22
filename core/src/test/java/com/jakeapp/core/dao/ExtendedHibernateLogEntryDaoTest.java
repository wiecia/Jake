package com.jakeapp.core.dao;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.JakeObjectLogEntry;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectLogEntry;
import com.jakeapp.core.domain.ProjectMemberLogEntry;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.TagLogEntry;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.XMPPMsgService;

@ContextConfiguration(locations = { "/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml" })
public class ExtendedHibernateLogEntryDaoTest extends AbstractJUnit4SpringContextTests {

	private static Logger log = Logger.getLogger(ExtendedHibernateLogEntryDaoTest.class);

	private HibernateTemplate hibernateTemplate;

	private ILogEntryDao logEntryDao;

	private FileObject file1;

	private FileObject nofile;

	private NoteObject note1;

	private UserId me = new UserId(ProtocolType.XMPP, "me");

	private Project project;

	public HibernateTemplate getHibernateTemplate() {
		return this.hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public ILogEntryDao getLogEntryDao() {
		return this.logEntryDao;
	}

	public void setLogEntryDao(ILogEntryDao logEntryDao) {
		this.logEntryDao = logEntryDao;
	}

	@Before
	public void setUp() throws Exception {
		this
				.setLogEntryDao((ILogEntryDao) this.applicationContext
						.getBean("logEntryDao"));
		this.setHibernateTemplate((HibernateTemplate) this.applicationContext
				.getBean("hibernateTemplate"));
		this.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.beginTransaction();

		fill();


	}

	private void fill() throws InvalidTagNameException {
		MsgService msgService = new XMPPMsgService();
		File file = new File(System.getProperty("user.dir"));

		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-6766-40a0-82c5-bcc0fe7a67c2"), msgService, file);


		LogEntry<Project> projectLogEntry = new ProjectLogEntry(testProject, me);


		logEntryDao.create(projectLogEntry);

		note1 = new NoteObject(u[3], project, "foo bar");
		LogEntry<JakeObject> note1announce = new JakeObjectLogEntry(
				LogAction.JAKE_OBJECT_NEW_VERSION, note1, me, "initial checkin", null,
				true);
		logEntryDao.create(note1announce);

		LogEntry<JakeObject> note1announce2 = new JakeObjectLogEntry(
				LogAction.JAKE_OBJECT_NEW_VERSION, note1, me, "improved version", null,
				false);
		logEntryDao.create(note1announce2);

		file1 = new FileObject(u[5], project, "foo bar");
		LogEntry<JakeObject> file1announce = new JakeObjectLogEntry(
				LogAction.JAKE_OBJECT_NEW_VERSION, file1, me, "my version", "mychecksum",
				true);
		logEntryDao.create(file1announce);
		LogEntry<JakeObject> file1lock = new JakeObjectLogEntry(
				LogAction.JAKE_OBJECT_LOCK, file1, me, "locking ...", null, true);
		logEntryDao.create(file1lock);
		LogEntry<JakeObject> file1unlock = new JakeObjectLogEntry(
				LogAction.JAKE_OBJECT_UNLOCK, file1, me, "unlocking ...", null, true);
		logEntryDao.create(file1unlock);
		LogEntry<JakeObject> file1lock2 = new JakeObjectLogEntry(
				LogAction.JAKE_OBJECT_LOCK, file1, me, "locking ...", null, true);
		logEntryDao.create(file1lock2);
		LogEntry<JakeObject> file1delete = new JakeObjectLogEntry(
				LogAction.JAKE_OBJECT_DELETE, file1, me, "I hate this file",
				"mychecksum", false);
		logEntryDao.create(file1delete);

		Tag tag = new Tag("tag1");
		tag.setObject(file1);
		Tag tag2 = new Tag("tag2");
		tag2.setObject(file1);

		TagLogEntry file1tag = new TagLogEntry(LogAction.TAG_ADD, tag, me);
		logEntryDao.create(file1tag);
		TagLogEntry file1tag2 = new TagLogEntry(LogAction.TAG_ADD, tag2, me);
		logEntryDao.create(file1tag2);
		TagLogEntry file1untag = new TagLogEntry(LogAction.TAG_REMOVE, tag, me);
		logEntryDao.create(file1untag);

		nofile = new FileObject(u[13], project, "I dont exist");
	}

	private UUID[] u = { new UUID(1, 2), new UUID(1, 3), new UUID(1, 4), new UUID(1, 5),
			new UUID(1, 6), new UUID(1, 7), new UUID(1, 8), new UUID(1, 9),
			new UUID(1, 10), new UUID(1, 11), new UUID(1, 12), new UUID(1, 13),
			new UUID(1, 14), new UUID(1, 15) };

	@After
	public void tearDown() {
		this.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.getTransaction().rollback();
	}

	@Transactional
	@Test
	// @Ignore
	public void testDump() {
		List<LogEntry<? extends ILogable>> results = logEntryDao.getAll(true);
		log.debug("DUMPING LOGENTRIES: ___");
		for (LogEntry<? extends ILogable> r : results) {
			log.debug((r.isProcessed() ? "P " : "UP") + " - " + r);
		}
		log.debug("DUMPING LOGENTRIES DONE");
	}

	@Transactional
	@Test
	public void testGetting() throws Exception {
		Assert.assertEquals(11, logEntryDao.getAll(true).size());
		Assert.assertEquals(9, logEntryDao.getAll(false).size());
		Assert.assertEquals(2, logEntryDao.getAllVersions(false).size());
		Assert.assertEquals(4, logEntryDao.getAllVersions(true).size());
		Assert
				.assertEquals(2, logEntryDao.getAllVersionsOfJakeObject(note1, true)
						.size());
		Assert
				.assertEquals(1, logEntryDao.getAllVersionsOfJakeObject(file1, true)
						.size());
		Assert.assertEquals(4, logEntryDao.getAllVersions(true).size());
	}

	@Transactional
	@Test
	public void testJakeObjects() throws Exception {
		Assert.assertEquals(Boolean.FALSE, logEntryDao.getDeleteState(note1, false));
		Assert.assertEquals(Boolean.FALSE, logEntryDao.getDeleteState(file1, false));
		Assert.assertEquals(Boolean.TRUE, logEntryDao.getDeleteState(file1, true));
		Assert.assertEquals(1, logEntryDao.getExistingFileObjects(false).size());
		Assert.assertEquals(0, logEntryDao.getExistingFileObjects(true).size());
		Assert.assertEquals(LogAction.JAKE_OBJECT_NEW_VERSION, logEntryDao
				.getLastOfJakeObject(note1, false).getLogAction());
		Assert.assertEquals(LogAction.TAG_REMOVE, logEntryDao.getLastOfJakeObject(file1,
				false).getLogAction());
		Assert.assertEquals(null, logEntryDao.getLastVersion(nofile, false));
		Assert.assertEquals("my version", logEntryDao.getLastVersion(file1, false)
				.getComment());
		Assert.assertEquals(null, logEntryDao.getLastVersion(file1, true));
		Assert.assertEquals("initial checkin", logEntryDao.getLastVersion(note1, false)
				.getComment());
		Assert.assertEquals("improved version", logEntryDao.getLastVersion(note1, true)
				.getComment());
	}

	@Transactional
	@Test(expected = NoSuchLogEntryException.class)
	public void testgetLastVersionOfJakeObject_throws() throws Exception {
		logEntryDao.getLastVersionOfJakeObject(nofile, false);
	}

	@Transactional
	@Test
	public void testgetLastVersionOfJakeObject() throws Exception {
		Assert.assertEquals("my version", logEntryDao.getLastVersionOfJakeObject(file1,
				true).getComment());
		Assert.assertEquals("my version", logEntryDao.getLastVersionOfJakeObject(file1,
				false).getComment());
		Assert.assertEquals("initial checkin", logEntryDao.getLastVersionOfJakeObject(
				note1, false).getComment());
		Assert.assertEquals("improved version", logEntryDao.getLastVersionOfJakeObject(
				note1, true).getComment());
	}

	@Transactional
	@Test
	public void testlocks() throws Exception {
		Assert.assertEquals(null, logEntryDao.getLock(note1));
		Assert.assertEquals(null, logEntryDao.getLock(nofile));
		Assert.assertNotNull(logEntryDao.getLock(file1));
		Assert.assertEquals("locking ...", logEntryDao.getLock(file1).getComment());
	}

	@Transactional
	@Test
	public void testUnprocessed() throws Exception {
		Collection<LogEntry<JakeObject>> up = logEntryDao.getUnprocessed();
		Assert.assertEquals(2, up.size());
		Iterator<LogEntry<JakeObject>> it = up.iterator();
		Assert.assertFalse(it.next().isProcessed());
		Assert.assertFalse(it.next().isProcessed());
		LogEntry<JakeObject> next = logEntryDao.getNextUnprocessed();
		Assert.assertFalse(next.isProcessed());
		Assert.assertEquals(u[3], next.getBelongsTo().getUuid());
		logEntryDao.setProcessed(next);
		Assert.assertTrue(next.isProcessed());
		Assert.assertEquals(u[5], logEntryDao.getNextUnprocessed().getBelongsTo()
				.getUuid());
		Assert.assertEquals(1, logEntryDao.getUnprocessed().size());
		Assert.assertFalse(logEntryDao.hasUnprocessed(note1));
		Assert.assertTrue(logEntryDao.hasUnprocessed(file1));
	}

	@Transactional
	@Test
	public void testFileTags() throws Exception {

		Collection<Tag> filetags = logEntryDao.getTags(file1);
		Assert.assertEquals(1, filetags.size());
		Tag tag = filetags.iterator().next();
		Assert.assertEquals("tag2", tag.getName());
		Assert.assertEquals(file1, tag.getObject());
	}

	@Transactional
	@Test
	public void testNoteTags() throws Exception {
		Collection<Tag> notetags = logEntryDao.getTags(note1);
		Assert.assertEquals(0, notetags.size());
	}

	@Transactional
	@Test
	public void testProjectMembers_empty() throws Exception {
		Assert.assertEquals(1, logEntryDao.getCurrentProjectMembers().size());

		Assert.assertEquals(1, logEntryDao.getTrustGraph().size());
	}

	@Transactional
	@Test
	public void testProjectCreator() throws Exception {
		Assert.assertEquals("me", logEntryDao.getProjectCreatedEntry().getMember().getUserId());
	}

	@Transactional
	@Test
	public void testProjectMembers() throws Exception {
		UserId member1 = new UserId(ProtocolType.XMPP, "max");
		UserId member2 = new UserId(ProtocolType.XMPP, "jack");
		UserId member3 = new UserId(ProtocolType.XMPP, "rick");
		logEntryDao.create(new ProjectMemberLogEntry(
				LogAction.START_TRUSTING_PROJECTMEMBER, me, me));
		logEntryDao.create(new ProjectMemberLogEntry(
				LogAction.START_TRUSTING_PROJECTMEMBER, member2, me));
		logEntryDao.create(new ProjectMemberLogEntry(
				LogAction.START_TRUSTING_PROJECTMEMBER, member2, member1));
		logEntryDao.create(new ProjectMemberLogEntry(
				LogAction.START_TRUSTING_PROJECTMEMBER, member3, member1));

		Assert.assertEquals(3, logEntryDao.getCurrentProjectMembers().size());
		/*
		Assert.assertEquals(3, logEntryDao.getTrustGraph().size());
		logEntryDao.create(new ProjectMemberLogEntry(
				LogAction.STOP_TRUSTING_PROJECTMEMBER, member3, member1));
		Assert.assertEquals(2, logEntryDao.getCurrentProjectMembers().size());

		Assert.assertEquals(4, logEntryDao.getTrustGraph().size());
		*/
	}

	@Transactional
	@Test
	public void testGet_TagLogEntry_belongsTo() {
		List<LogEntry<? extends ILogable>> result = logEntryDao.getAll(true);
		Assert.assertEquals(11, result.size());
		int i = 0;
		while (result.get(i).getLogAction() != LogAction.TAG_ADD)
			i++;
		LogEntry<? extends ILogable> le = result.get(8);
		Assert.assertEquals(LogAction.TAG_ADD, le.getLogAction());
		LogEntry<Tag> tagle = (LogEntry<Tag>) le;
		Tag tag = tagle.getBelongsTo();
		Assert.assertEquals("tag1", tag.getName());
		JakeObject jo = tag.getObject();
		Assert.assertEquals(file1, jo);
		FileObject fo = (FileObject) jo;
		Assert.assertEquals(file1, fo);

	}


}