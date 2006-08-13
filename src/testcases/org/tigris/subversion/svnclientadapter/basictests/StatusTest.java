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
import java.util.ArrayList;
import java.util.List;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
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
        ISVNStatus status;

        //Check status of the ignored resource
        statuses = client.getStatus(dirIgn, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.IGNORED, statuses[0].getTextStatus());
        status = client.getSingleStatus(dirIgn);
        assertEquals("Wrong text status", SVNStatusKind.IGNORED, status.getTextStatus());

        //Check status withing the ignored resource
        statuses = client.getStatus(fileIgn, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, statuses[0].getTextStatus());
        status = client.getSingleStatus(fileIgn);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, status.getTextStatus());

        statuses = client.getStatus(subdirIgn, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, statuses[0].getTextStatus());
        status = client.getSingleStatus(subdirIgn);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, status.getTextStatus());

        statuses = client.getStatus(fileIgn2, true, true, false);       
        assertEquals("Wrong nuber of statuses returned", 1, statuses.length);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, statuses[0].getTextStatus());
        status = client.getSingleStatus(fileIgn2);
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, status.getTextStatus());
    }
    
    public void testSingleStatus() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("singleStatus",getGreekTestConfig());

        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        File g = new File(thisTest.getWorkingCopy(), "A/D/G");

	    File unversionedDir = new File(thisTest.getWorkingCopy(), "unversionedDir");
	    unversionedDir.mkdir();
	    File unversionedFile = new File(unversionedDir, "unversionedFile");
	    new FileOutputStream(unversionedFile).close();	
	    File unversionedSubDir = new File(unversionedDir, "unversionedSubDir");
	    unversionedSubDir.mkdir();
	    File nonExistentFile = new File(thisTest.getWorkingCopy(), "nonExistentFile");

        ISVNStatus status;

        status = client.getSingleStatus(mu);       
        assertEquals("Wrong text status", SVNStatusKind.NORMAL, status.getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, status.getPropStatus());
        assertEquals("Wrong revision", SVNRevision.getRevision("1"), status.getRevision());
        assertEquals("Wrong last changed revision", SVNRevision.getRevision("1"), status.getLastChangedRevision());
        assertEquals("Wrong nodeKind", SVNNodeKind.FILE, status.getNodeKind());
        assertEquals("Wrong path", mu, status.getFile());

        status = client.getSingleStatus(g);       
        assertEquals("Wrong text status", SVNStatusKind.NORMAL, status.getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, status.getPropStatus());
        assertEquals("Wrong revision", SVNRevision.getRevision("1"), status.getRevision());
        assertEquals("Wrong last changed revision", SVNRevision.getRevision("1"), status.getLastChangedRevision());
        assertEquals("Wrong nodeKind", SVNNodeKind.DIR, status.getNodeKind());
        assertEquals("Wrong path", g, status.getFile());

        status = client.getSingleStatus(unversionedDir);       
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, status.getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, status.getPropStatus());
        assertEquals("Wrong revision", null, status.getRevision());
        assertEquals("Wrong path", unversionedDir, status.getFile());

        status = client.getSingleStatus(unversionedFile);       
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, status.getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, status.getPropStatus());
        assertEquals("Wrong revision", null, status.getRevision());
        assertEquals("Wrong path", unversionedFile, status.getFile());
        
        status = client.getSingleStatus(unversionedSubDir);       
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, status.getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, status.getPropStatus());
        assertEquals("Wrong revision", null, status.getRevision());
        assertEquals("Wrong path", unversionedSubDir, status.getFile());
        
        status = client.getSingleStatus(nonExistentFile);       
        assertEquals("Wrong text status", SVNStatusKind.UNVERSIONED, status.getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, status.getPropStatus());
        assertEquals("Wrong revision", null, status.getRevision());
        assertEquals("Wrong path", nonExistentFile, status.getFile());

        status = client.getSingleStatus(thisTest.getWorkingCopy());       
        assertEquals("Wrong text status", SVNStatusKind.NORMAL, status.getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, status.getPropStatus());
        assertEquals("Wrong revision", SVNRevision.getRevision("1"), status.getRevision());
        assertEquals("Wrong last changed revision", SVNRevision.getRevision("1"), status.getLastChangedRevision());
        assertEquals("Wrong nodeKind", SVNNodeKind.DIR, status.getNodeKind());
        assertEquals("Wrong path", thisTest.getWorkingCopy(), status.getFile());
    }

    public void testMulitStatus() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("multiStatus",getGreekTestConfig());

        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        File g = new File(thisTest.getWorkingCopy(), "A/D/G");
        File rho = new File(thisTest.getWorkingCopy(), "A/D/G/rho");

        File[] files = new File[] {g ,mu, rho};
        
        ISVNStatus[] statuses;

        statuses = client.getStatus(files);       

        assertEquals("Wrong text status", SVNStatusKind.NORMAL, statuses[0].getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, statuses[0].getPropStatus());
        assertEquals("Wrong revision", SVNRevision.getRevision("1"), statuses[0].getRevision());
        assertEquals("Wrong last changed revision", SVNRevision.getRevision("1"), statuses[0].getLastChangedRevision());
        assertEquals("Wrong nodeKind", SVNNodeKind.DIR, statuses[0].getNodeKind());
        assertEquals("Wrong path", g, statuses[0].getFile());

        assertEquals("Wrong text status", SVNStatusKind.NORMAL, statuses[1].getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, statuses[1].getPropStatus());
        assertEquals("Wrong revision", SVNRevision.getRevision("1"), statuses[1].getRevision());
        assertEquals("Wrong last changed revision", SVNRevision.getRevision("1"), statuses[1].getLastChangedRevision());
        assertEquals("Wrong nodeKind", SVNNodeKind.FILE, statuses[1].getNodeKind());
        assertEquals("Wrong path", mu, statuses[1].getFile());

        assertEquals("Wrong text status", SVNStatusKind.NORMAL, statuses[2].getTextStatus());
        assertEquals("Wrong prop status", SVNStatusKind.NONE, statuses[2].getPropStatus());
        assertEquals("Wrong revision", SVNRevision.getRevision("1"), statuses[2].getRevision());
        assertEquals("Wrong last changed revision", SVNRevision.getRevision("1"), statuses[2].getLastChangedRevision());
        assertEquals("Wrong nodeKind", SVNNodeKind.FILE, statuses[2].getNodeKind());
        assertEquals("Wrong path", rho, statuses[2].getFile());
    }

    public void testStatusWithExternals() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("statusWithExternals",getGreekTestConfig(), getNumericTestConfig());

        // check the status of the working copy
        thisTest.checkStatusesExpectedWCIgnoreExternals();

        //Test with the ignoreExternals flag on first
        //Check that url is always present. Used to be missing on the external resource itself
		ISVNStatus[] states = client.getStatus(thisTest.getWorkingCopy(), true, true, false, true);
		for (int i = 0; i < states.length; i++) {
			assertNotNull(states[i].getUrlString());
		}

        //Test with the ignoreExternals flag off now
		thisTest.getExpectedWC().addExternalPartWC(getNumericTestConfig().getExpectedWC(), "A/E");
		thisTest.getExpectedWC().setItemTextStatus("A/E", SVNStatusKind.EXTERNAL);
        thisTest.checkStatusesExpectedWC();
    }    

}
