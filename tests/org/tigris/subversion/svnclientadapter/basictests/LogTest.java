/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
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
package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class LogTest extends SVNTest {

    /**
     * test the basic SVNClientInfo.logMessage functionality
     * 
     * @throws Throwable
     */
    public void testBasicLogMessage() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("basicLogMessages", getGreekTestConfig());

        // get the commit message of the initial import and test it
        ISVNLogMessage lm[] = client.getLogMessages(thisTest.getWCPath(),
                new SVNRevision.Number(1), SVNRevision.HEAD);
        assertEquals("wrong number of objects", 1, lm.length);
        assertEquals("wrong message", "Log Message", lm[0].getMessage());
        assertEquals("wrong revision", 1, lm[0].getRevision().getNumber());
        assertEquals("wrong user", "cedric", lm[0].getAuthor());
        assertNotNull("changed paths set", lm[0].getChangedPaths());
        ISVNLogMessageChangePath cp[] = lm[0].getChangedPaths();
        assertEquals("wrong number of chang pathes", 20, cp.length);

        ISVNLogMessageChangePath cpA = null;
        for (int i = 0; i < cp.length; i++) {
            if ("/A".equals(cp[i].getPath())) {
                cpA = cp[i];
                break;
            }
        }
        assertNotNull("/A is not in the changed pathes", cpA);
        assertEquals("wrong path", "/A", cpA.getPath());
        assertEquals("wrong copy source rev", null, cpA.getCopySrcRevision());
        assertNull("wrong copy source path", cpA.getCopySrcPath());
        assertEquals("wrong action", 'A', cpA.getAction());
    }

    public void testBasicLogUrlMessage() throws Exception {
        // create the working copy
        OneTest thisTest = new OneTest("basicLogUrlMessages",
                getGreekTestConfig());

        // modify file iota
        File iota = new File(thisTest.getWorkingCopy(), "iota");
        PrintWriter iotaPW = new PrintWriter(new FileOutputStream(iota, true));
        iotaPW.print("new appended text for rho");
        iotaPW.close();

        assertEquals("wrong revision number from commit", 2, client.commit(
                new File[] { thisTest.getWCPath() }, "iota modified", true));

        ISVNLogMessage lm[] = client.getLogMessages(new SVNUrl(thisTest
                .getUrl()
                + "/iota"), new SVNRevision.Number(1), SVNRevision.HEAD, true);
        assertEquals("wrong number of objects", 2, lm.length);
        assertEquals("wrong message", "Log Message", lm[0].getMessage());
        assertEquals("wrong message", "iota modified", lm[1].getMessage());
        ISVNLogMessageChangePath cp[] = lm[1].getChangedPaths();
        assertEquals("wrong number of chang pathes", 1, cp.length);
    }

    /**
     * test the SVNClientInfo.logMessage functionality on outgoing/uncommitted rename/move
     * 
     * @throws Throwable
     */
    public void testUncommitedRenameLogMessage() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("ucommitedRenameLogMessage", getGreekTestConfig());

        // move file iota
        File iota = new File(thisTest.getWorkingCopy(), "iota");
        File iota2 = new File(thisTest.getWorkingCopy(), "iota2");
        client.move(iota, iota2, true);
        
        ISVNLogMessage lm[] = client.getLogMessages(iota2, new SVNRevision.Number(1), SVNRevision.HEAD, true);
        assertEquals("wrong number of objects", 1, lm.length);
        assertEquals("wrong message", "Log Message", lm[0].getMessage());
        assertEquals("wrong revision", 1, lm[0].getRevision().getNumber());
        assertEquals("wrong user", "cedric", lm[0].getAuthor());
        assertNotNull("changed paths set", lm[0].getChangedPaths());
        ISVNLogMessageChangePath cp[] = lm[0].getChangedPaths();
        assertEquals("wrong number of chang pathes", 20, cp.length);
    }

    //TODO enable this test when ISVNClientAdapter#getLogMessages(SVNUrl, java.lang.String[], SVNRevision, SVNRevision, boolean, boolean) will be implemented
//    public void testBasicLogUrlMultiPathMessage() throws Exception {
//        // create the working copy
//        OneTest thisTest = new OneTest("basicLogUrlMultiPathMessages",
//                getGreekTestConfig());
//
//        // modify file iota
//        File iota = new File(thisTest.getWorkingCopy(), "iota");
//        PrintWriter iotaPW = new PrintWriter(new FileOutputStream(iota, true));
//        iotaPW.print("new appended text for iota");
//        iotaPW.close();
//
//        assertEquals("wrong revision number from commit", 2, client.commit(
//                new File[] { thisTest.getWCPath() }, "iota modified", true));
//
//        // modify file mu
//        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
//        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
//        muPW.print("new appended text for mu");
//        muPW.close();
//
//        assertEquals("wrong revision number from commit", 3, client.commit(
//                new File[] { thisTest.getWCPath() }, "mu modified", true));
//
//        ISVNLogMessage lm[] = client.getLogMessages(thisTest.getUrl(),
//                new String[] { "iota", "A/mu" }, new SVNRevision.Number(1), SVNRevision.HEAD, false, true);
//        assertEquals("wrong number of objects", 3, lm.length);
//        assertEquals("wrong message", "Log Message", lm[0].getMessage());
//        assertEquals("wrong message", "iota modified", lm[1].getMessage());
//        assertEquals("wrong message", "mu modified", lm[2].getMessage());
//        ISVNLogMessageChangePath cp[] = lm[1].getChangedPaths();
//        assertEquals("wrong number of chang pathes", 1, cp.length);
//        cp = lm[2].getChangedPaths();
//        assertEquals("wrong number of chang pathes", 1, cp.length);
//    }

}