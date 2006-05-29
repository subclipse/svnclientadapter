package org.tigris.subversion.svnclientadapter.regression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class CmdLineBugRegressionTest extends SVNTest {
	
	public void testIssue137() throws Exception
	{
        client = SVNClientAdapterFactory.createSVNClient(CmdLineClientAdapterFactory.COMMANDLINE_CLIENT);
        client.setUsername("cedric");
        client.setPassword("cedricpass");

		String theLogMessage = "A log message";
		
        // build the test setup
        OneTest thisTest = new OneTest("basicCommit", getGreekTestConfig());

        // modify file A/mu
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
        muPW.print("appended mu text");
        muPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 2);
        thisTest.getExpectedWC().setItemContent("A/mu",
                thisTest.getExpectedWC().getItemContent("A/mu") + "appended mu text");

        // commit the changes
        assertEquals("wrong revision number from commit",2,
                client.commit(new File[]{thisTest.getWCPath()}, theLogMessage,
                        true));

        // modify file A/D/G/rho
        File rho = new File(thisTest.getWorkingCopy(), "A/D/G/rho");
        PrintWriter rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        rhoPW.print("new appended text for rho");
        rhoPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 3);
        thisTest.getExpectedWC().setItemContent("A/D/G/rho",
                thisTest.getExpectedWC().getItemContent("A/D/G/rho")
                + "new appended text for rho");

        // commit the changes
        assertEquals("wrong revision number from commit",3,
                client.commit(new File[]{thisTest.getWCPath()}, "",
                        true));

        // check the status of the working copy
        thisTest.checkStatusesExpectedWC();
        
        ISVNLogMessage lm[] = client.getLogMessages(mu,
        		SVNRevision.BASE, SVNRevision.HEAD);
        assertEquals("wrong message", theLogMessage, lm[0].getMessage());

        lm = client.getLogMessages(rho,
        		SVNRevision.HEAD, SVNRevision.HEAD);
        assertEquals("wrong message", "", lm[0].getMessage());

	}

}
