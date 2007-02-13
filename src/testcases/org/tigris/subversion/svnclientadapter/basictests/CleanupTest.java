/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


/**
 * @author Brock Janiczak
 */
public class CleanupTest extends SVNTest {

    /**
     * test the basic SVNClient.cleanup functionality
     * @throws Throwable
     */
    public void testBasicCleanup() throws Throwable
    {
        // create a test working copy
        OneTest thisTest = new OneTest("basicCleanup", getGreekTestConfig());

        // create a lock file in A/B
        File adminLock = new File(thisTest.getWorkingCopy(),"A/B/" + client.getAdminDirectoryName() + "/lock");
        PrintWriter pw = new PrintWriter(new FileOutputStream(adminLock));
        pw.print("stop looking!");
        pw.close();
        thisTest.getExpectedWC().setItemIsLocked("A/B", true);

        // create a lock file in A/D/G
        adminLock = new File(thisTest.getWorkingCopy(),"A/D/G/" + client.getAdminDirectoryName() + "/lock");
        pw = new PrintWriter(new FileOutputStream(adminLock));
        pw.print("stop looking!");
        pw.close();
        thisTest.getExpectedWC().setItemIsLocked("A/D/G", true);

        // create a lock file in A/C
        adminLock = new File(thisTest.getWorkingCopy(),"A/C/" + client.getAdminDirectoryName() + "/lock");
        pw = new PrintWriter(new FileOutputStream(adminLock));
        pw.print("stop looking!");
        pw.close();
        thisTest.getExpectedWC().setItemIsLocked("A/C", true);

        // test the status of the working copy
        thisTest.checkStatusesExpectedWC();

        // run cleanup
        client.cleanup(thisTest.getWCPath());
        thisTest.getExpectedWC().setItemIsLocked("A/B", false);
        thisTest.getExpectedWC().setItemIsLocked("A/D/G", false);
        thisTest.getExpectedWC().setItemIsLocked("A/C", false);

        // test the status of the working copy
        thisTest.checkStatusesExpectedWC();
    }
}
