/**
 * @copyright ====================================================================
 *            Copyright (c) 2003-2004 CollabNet. All rights reserved.
 * 
 * This software is licensed as described in the file COPYING, which you should
 * have received as part of this distribution. The terms are also available at
 * http://subversion.tigris.org/license-1.html. If newer versions of this
 * license are posted there, you may use a newer version instead, at your
 * option.
 * 
 * This software consists of voluntary contributions made by many individuals.
 * For exact contribution history, see the revision history and logs, available
 * at http://subversion.tigris.org/.
 * ====================================================================
 * @endcopyright
 */
package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

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

}