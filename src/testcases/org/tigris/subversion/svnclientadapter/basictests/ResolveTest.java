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

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


public class ResolveTest extends SVNTest {

    /**
     * test the basic SVNClient.update functionality with concurrent changes
     * in the repository and the working copy that generate conflicts
     * @throws Throwable
     */
    public void testBasicConflict() throws Throwable
    {
        // build the first working copy
        OneTest thisTest = new OneTest("basicConflict",getGreekTestConfig());

        // copy the first working copy to the backup working copy
        OneTest backupTest = thisTest.copy(".backup");

        // append a line to A/mu in the first working copy
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
        String muContent = thisTest.getExpectedWC().getItemContent("A/mu");
        muPW.print("\nOriginal appended text for mu");
        muContent = muContent + "\nOriginal appended text for mu";
        muPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 2);
        thisTest.getExpectedWC().setItemContent("A/mu", muContent);

        // append a line to A/D/G/rho in the first working copy
        File rho = new File(thisTest.getWorkingCopy(), "A/D/G/rho");
        PrintWriter rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        String rhoContent = thisTest.getExpectedWC().getItemContent("A/D/G/rho");
        rhoPW.print("\nOriginal appended text for rho");
        rhoContent = rhoContent + "\nOriginal appended text for rho";
        rhoPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 2);
        thisTest.getExpectedWC().setItemContent("A/D/G/rho", rhoContent);

        // commit the changes in the first working copy
        assertEquals("wrong revision number from commit",2,
                client.commit(new File[]{thisTest.getWCPath()}, "log msg",
                        true));

        // test the status of the working copy after the commit
        thisTest.checkStatusesExpectedWC();

        // append a different line to A/mu in the backup working copy
        mu = new File(backupTest.getWorkingCopy(), "A/mu");
        muPW = new PrintWriter(new FileOutputStream(mu, true));
        muPW.print("\nConflicting appended text for mu");
        muContent = "<<<<<<< .mine\nThis is the file 'mu'.\n"+
                "Conflicting appended text for mu=======\n"+
                "This is the file 'mu'.\n"+
                "Original appended text for mu>>>>>>> .r2";
        muPW.close();
        backupTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 2);
        backupTest.getExpectedWC().setItemContent("A/mu", muContent);
        backupTest.getExpectedWC().setItemTextStatus("A/mu", SVNStatusKind.CONFLICTED);
        backupTest.getExpectedWC().addItem("A/mu.r1", "");
        backupTest.getExpectedWC().setItemNodeKind("A/mu.r1", SVNNodeKind.UNKNOWN);
        backupTest.getExpectedWC().setItemTextStatus("A/mu.r1",
                SVNStatusKind.UNVERSIONED);
        backupTest.getExpectedWC().addItem("A/mu.r2", "");
        backupTest.getExpectedWC().setItemNodeKind("A/mu.r2", SVNNodeKind.UNKNOWN);
        backupTest.getExpectedWC().setItemTextStatus("A/mu.r2",
                SVNStatusKind.UNVERSIONED);
        backupTest.getExpectedWC().addItem("A/mu.mine", "");
        backupTest.getExpectedWC().setItemNodeKind("A/mu.mine", SVNNodeKind.UNKNOWN);
        backupTest.getExpectedWC().setItemTextStatus("A/mu.mine",
                SVNStatusKind.UNVERSIONED);

        // append a different line to A/D/G/rho in the backup working copy
        rho = new File(backupTest.getWorkingCopy(), "A/D/G/rho");
        rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        rhoPW.print("\nConflicting appended text for rho");
        rhoContent = "<<<<<<< .mine\nThis is the file 'rho'.\n"+
                "Conflicting appended text for rho=======\n"+
                "his is the file 'rho'.\n"+
                "Original appended text for rho>>>>>>> .r2";
        rhoPW.close();
        backupTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 2);
        backupTest.getExpectedWC().setItemContent("A/D/G/rho", rhoContent);
        backupTest.getExpectedWC().setItemTextStatus("A/D/G/rho",
                SVNStatusKind.CONFLICTED);
        backupTest.getExpectedWC().addItem("A/D/G/rho.r1", "");
        backupTest.getExpectedWC().setItemNodeKind("A/D/G/rho.r1", SVNNodeKind.UNKNOWN);
        backupTest.getExpectedWC().setItemTextStatus("A/D/G/rho.r1",
                SVNStatusKind.UNVERSIONED);
        backupTest.getExpectedWC().addItem("A/D/G/rho.r2", "");
        backupTest.getExpectedWC().setItemNodeKind("A/D/G/rho.r2", SVNNodeKind.UNKNOWN);
        backupTest.getExpectedWC().setItemTextStatus("A/D/G/rho.r2",
                SVNStatusKind.UNVERSIONED);
        backupTest.getExpectedWC().addItem("A/D/G/rho.mine", "");

        backupTest.getExpectedWC().setItemNodeKind("A/D/G/rho.mine", SVNNodeKind.UNKNOWN);
        backupTest.getExpectedWC().setItemTextStatus("A/D/G/rho.mine",
                SVNStatusKind.UNVERSIONED);

        // update the backup working copy from the repository
        assertEquals("wrong revision number from update",2,
                client.update(backupTest.getWCPath(), SVNRevision.HEAD, true));

        // check the status of the backup working copy
        backupTest.checkStatusesExpectedWC();
        
        //check the conflicting* fields of status
        ISVNStatus rhoStatus = client.getSingleStatus(rho);
        assertEquals(new File(backupTest.getWorkingCopy() + "/A/D/G/rho.mine"), rhoStatus.getConflictWorking());
        assertEquals(new File(backupTest.getWorkingCopy() + "/A/D/G/rho.r1"), rhoStatus.getConflictOld());
        assertEquals(new File(backupTest.getWorkingCopy() + "/A/D/G/rho.r2"), rhoStatus.getConflictNew());

        // flag A/mu as resolved
        client.resolved(new File(backupTest.getWCPath()+"/A/mu"));
        backupTest.getExpectedWC().setItemTextStatus("A/mu", SVNStatusKind.MODIFIED);
        backupTest.getExpectedWC().removeItem("A/mu.r1");
        backupTest.getExpectedWC().removeItem("A/mu.r2");
        backupTest.getExpectedWC().removeItem("A/mu.mine");

        // flag A/D/G/rho as resolved
        client.resolved(new File(backupTest.getWCPath()+"/A/D/G/rho"));
        backupTest.getExpectedWC().setItemTextStatus("A/D/G/rho", SVNStatusKind.MODIFIED);
        backupTest.getExpectedWC().removeItem("A/D/G/rho.r1");
        backupTest.getExpectedWC().removeItem("A/D/G/rho.r2");
        backupTest.getExpectedWC().removeItem("A/D/G/rho.mine");

        // check the status after the conflicts are flaged as resolved
        backupTest.checkStatusesExpectedWC();
    }
    
    
}
