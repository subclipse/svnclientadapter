/*******************************************************************************
 * Copyright (c) 2005, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
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
import org.tigris.subversion.svnclientadapter.testUtils.TestsConfig;

public class CmdLineBugRegressionTest extends SVNTest {

    protected void setUp() throws Exception {
        TestsConfig.getTestsConfig().clientType = "commandline";
        TestsConfig.getTestsConfig().adminClientType = "commandline";
        super.setUp();
    }
    
	/**
	 * Test the issue 135
	 * @throws Exception
	 * @see <a href="http://subclipse.tigris.org/issues/show_bug.cgi?id=135">Issue 135</a>
	 */
	public void testIssue135() throws Exception
	{		
		final byte[] cedricInUTF8bytes = new byte[] {67, -61, -87, 100, 114, 105, 99};
		final String userName = new String(cedricInUTF8bytes, "UTF-8");
		final String passwd = "cedricPass";
		
		//Just check that String(, charset) creates proper local string (windows only)
		//final String cedricIn8859_1 = "Cédric";  //ISO 8859-1
		//assertEquals(cedricIn8859_1, userName);
		
        client = SVNClientAdapterFactory.createSVNClient(CmdLineClientAdapterFactory.COMMANDLINE_CLIENT);
        client.setUsername(userName);
        client.setPassword(passwd);

        //final byte[] utf8Message = new byte[] {65, 110, 32, 85, 84, 70, 45, 56, 32, 108, 111, 103, 32, 109, 101, 115, 115, 97, 103, 101, 58, 32, 39, -60, -66, 39, 32, 45, 32, 39, -48, -106, 39, 32, 45, 32, 39, -38, -80, 39, 32};
        //String theLogMessage = new String(utf8Message, 0, utf8Message.length, "UTF-8");       
        String theLogMessage = "Log message";
		
        // build the test setup
        OneTest thisTest = new OneTest("testUTF8", getGreekTestConfig());

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

        ISVNLogMessage logMmsg = client.getLogMessages(mu, SVNRevision.getRevision("HEAD"), SVNRevision.getRevision("HEAD"))[0];
        assertEquals(userName, logMmsg.getAuthor());
        assertEquals(theLogMessage, logMmsg.getMessage());
	}

	/**
	 * Test the issue 137
	 * @throws Exception
	 * @see <a href="http://subclipse.tigris.org/issues/show_bug.cgi?id=137">Issue 137</a>
	 */
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
