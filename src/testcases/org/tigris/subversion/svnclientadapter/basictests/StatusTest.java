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
import java.util.ArrayList;
import java.util.List;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


public class StatusTest extends SVNTest {

    /**
     * test the basic SVNClient.status functionality
     * @throws Throwable
     */
    public void testBasicStatus() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("basicStatus",getGreekTestConfig());

        // check the status of the working copy
        thisTest.checkStatusesExpectedWC();
    }    

    public void testStatusUpdate() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("statusUpdate",getGreekTestConfig());

        // modify file A/mu
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
        muPW.print("appended mu text");
        muPW.close();
        // commit the changes
        client.commit(new File[]{thisTest.getWCPath()}, "log msg", true);

        // modify file A/D/G/rho
        File rho = new File(thisTest.getWorkingCopy(), "A/D/G/rho");
        PrintWriter rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        rhoPW.print("new appended text for rho");
        rhoPW.close();
        // commit the changes
        client.commit(new File[]{thisTest.getWCPath()}, "log msg", true);

        ISVNStatus[] statuses;
        statuses = client.getStatus(rho, false, true, false);
        assertEquals("WC in wrong revision", SVNRevision.getRevision("3"), statuses[0].getRevision());
        //switch back so we can see incoming changes
        client.switchToUrl(thisTest.getWCPath(), thisTest.getUrl(), SVNRevision.getRevision("1"), true);
        statuses = client.getStatus(rho, false, true, false);
        assertEquals("WC in wrong revision after switch", SVNRevision.getRevision("1"), statuses[0].getRevision());

        //Check the WC status - no outgoing change expected
        statuses = client.getStatus(thisTest.getWCPath(), true, false, false);       
        assertEquals("Wrong nuber of statuses returned", 0, statuses.length);
        //Check the repo status - 2 incoming changes expected.
        statuses = client.getStatus(thisTest.getWCPath(), true, false, true);       
        assertEquals("Wrong nuber of statuses returned", 2, statuses.length);
        assertEquals("Wrong wc text status", SVNStatusKind.NORMAL, statuses[0].getTextStatus());
        assertEquals("Wrong repository text status", SVNStatusKind.MODIFIED, statuses[0].getRepositoryTextStatus());
        assertEquals("Wrong wc text status", SVNStatusKind.NORMAL, statuses[1].getTextStatus());
        assertEquals("Wrong repository text status", SVNStatusKind.MODIFIED, statuses[1].getRepositoryTextStatus());
        
        
        // check the status of the working copy
        thisTest.checkStatusesExpectedWC();
    }    

    public void testStatusOnIgnored() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("statusOnIgnored",getGreekTestConfig());

        // create dir
	    File dir = new File(thisTest.getWorkingCopy(), "dir");
	    dir.mkdir();
	
	    // add dir
	    client.addDirectory(dir, true);
	    List ignoredPatterns = new ArrayList();
	    ignoredPatterns.add("ignored");
	    client.setIgnoredPatterns(dir, ignoredPatterns);
        client.commit(new File[]{thisTest.getWCPath()}, "log msg", true);

	    // create dir/ignored (should be ignored)
	    File dirIgn = new File(dir, "ignored");
	    dirIgn.mkdir();
	    File fileIgn = new File(dirIgn, "ignoredFile");
	    new FileOutputStream(fileIgn).close();	
	    File subdirIgn = new File(dirIgn, "subIgnored");
	    subdirIgn.mkdir();
	    File fileIgn2 = new File(subdirIgn, "ignoredFile2");
	    new FileOutputStream(fileIgn2).close();	

        ISVNStatus[] statuses;

        //Check status of the ignored resource
        statuses = client.getStatus(dirIgn, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.IGNORED, statuses[0].getTextStatus());

        //Check status withing the ignored resource
        statuses = client.getStatus(fileIgn, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, statuses[0].getTextStatus());
        statuses = client.getStatus(subdirIgn, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, statuses[0].getTextStatus());
        statuses = client.getStatus(fileIgn2, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, statuses[0].getTextStatus());

    }
}
