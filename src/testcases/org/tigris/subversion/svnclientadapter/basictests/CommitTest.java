/**
 * @copyright
 * ====================================================================
 * Copyright (c) 2003-2004 CollabNet.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://subversion.tigris.org/license-1.html.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 *
 * This software consists of voluntary contributions made by many
 * individuals.  For exact contribution history, see the revision
 * history and logs, available at http://subversion.tigris.org/.
 * ====================================================================
 * @endcopyright
 */
package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;


public class CommitTest extends SVNTest {

    /**
     * test the basic SVNClient.commit functionality
     * @throws Throwable
     */
    public void testBasicCommit() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("basicCommit",getGreekTestConfig());

        // modify file A/mu
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
        muPW.print("appended mu text");
        muPW.close();
        thisTest.getWc().setItemWorkingCopyRevision("A/mu", 2);
        thisTest.getWc().setItemContent("A/mu",
                thisTest.getWc().getItemContent("A/mu") + "appended mu text");

        // modify file A/D/G/rho
        File rho = new File(thisTest.getWorkingCopy(), "A/D/G/rho");
        PrintWriter rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        rhoPW.print("new appended text for rho");
        rhoPW.close();
        thisTest.getWc().setItemWorkingCopyRevision("A/D/G/rho", 2);
        thisTest.getWc().setItemContent("A/D/G/rho",
                thisTest.getWc().getItemContent("A/D/G/rho")
                + "new appended text for rho");

        // commit the changes
        assertEquals("wrong revision number from commit",2,
                client.commit(new File[]{thisTest.getWCPath()}, "log msg",
                        true));

        // check the status of the working copy
        thisTest.checkStatus();
    }

    
}
