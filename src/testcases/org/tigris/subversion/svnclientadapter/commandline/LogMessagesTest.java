package org.tigris.subversion.svnclientadapter.commandline;

import java.io.File;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SubversionTestCase;

/**
 * @author Brock Janiczak
 */
public class LogMessagesTest extends SubversionTestCase {

	private static final String COMMIT_MESSAGE = "Initial Version";
	
	/**
	 * Tests that in the most basic case a log message will be returned for a single change
	 * @throws Exception
	 */
	public void testSimpleLogMessages() throws Exception {
		createProject("/trunk/project", "project");
		
		File sampleFile = createFile("project" + File.separatorChar + "sample.file", "Sample File Content");
		
		client.addFile(sampleFile);
		
		client.commit(new File[] {sampleFile}, COMMIT_MESSAGE, false);
		
		ISVNLogMessage[] messages = client.getLogMessages(sampleFile, new SVNRevision.Number(0), SVNRevision.HEAD);
		
		assertEquals(1, messages.length);
		assertNotNull(messages[0]);
		assertNotNull(messages[0].getAuthor());
		assertNotNull(messages[0].getDate());
		assertNotNull(messages[0].getMessage());
		assertNotNull(messages[0].getRevision());
		assertEquals(COMMIT_MESSAGE, messages[0].getMessage());
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.SubversionTestCase#getClientType()
	 */
	protected int getClientType() {
		return SVNClientAdapterFactory.COMMANDLINE_CLIENT;
	}
}
