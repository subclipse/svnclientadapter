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

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class UpdateTest extends SVNTest {

    /**
     * test the basic SVNClient.update functionality
     * @throws Throwable
     */
    public void testBasicUpdate() throws Throwable
    {
        // build the test setup. Used for the changes
        OneTest thisTest = new OneTest("basicUpdate",getGreekTestConfig());

        // build the backup test setup. That is the one that will be updated
        OneTest backupTest = thisTest.copy(".backup");

        // modify A/mu
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
        muPW.print("appended mu text");
        muPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 2);
        thisTest.getExpectedWC().setItemContent("A/mu",
                thisTest.getExpectedWC().getItemContent("A/mu") + "appended mu text");

        // modify A/D/G/rho
        File rho = new File(thisTest.getWorkingCopy(), "A/D/G/rho");
        PrintWriter rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        rhoPW.print("new appended text for rho");
        rhoPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 2);
        thisTest.getExpectedWC().setItemContent("A/D/G/rho",
                thisTest.getExpectedWC().getItemContent("A/D/G/rho")
                + "new appended text for rho");

        // commit the changes
        assertEquals("wrong revision number from commit",2,
                client.commit(new File[]{thisTest.getWCPath()}, "log msg",
                        true));

        // check the status of the working copy
        thisTest.checkStatusesExpectedWC();

        // update the backup test
        assertEquals("wrong revision number from update",2,
                client.update(backupTest.getWCPath(), SVNRevision.HEAD, true));

        // set the expected working copy layout for the backup test
        backupTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 2);
        backupTest.getExpectedWC().setItemContent("A/mu",
                backupTest.getExpectedWC().getItemContent("A/mu") + "appended mu text");
        backupTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 2);
        backupTest.getExpectedWC().setItemContent("A/D/G/rho",
                backupTest.getExpectedWC().getItemContent("A/D/G/rho")
                + "new appended text for rho");

        // check the status of the working copy of the backup test
        backupTest.checkStatusesExpectedWC();
    }

    /**
     * test the basic SVNClient.update functionality with concurrent changes
     * in the repository and the working copy
     * @throws Throwable
     */
    public void testBasicMergingUpdate() throws Throwable
    {
        // build the first working copy
        OneTest thisTest = new OneTest("BasicMergingUpdate",getGreekTestConfig());

        // append 10 lines to A/mu
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
        String muContent = thisTest.getExpectedWC().getItemContent("A/mu");
        for (int i = 2; i < 11; i++)
        {
            muPW.print("\nThis is line " + i + " in mu");
            muContent = muContent + "\nThis is line " + i + " in mu";
        }
        muPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 2);
        thisTest.getExpectedWC().setItemContent("A/mu", muContent);

        // append 10 line to A/D/G/rho
        File rho = new File(thisTest.getWorkingCopy(), "A/D/G/rho");
        PrintWriter rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        String rhoContent = thisTest.getExpectedWC().getItemContent("A/D/G/rho");
        for (int i = 2; i < 11; i++)
        {
            rhoPW.print("\nThis is line " + i + " in rho");
            rhoContent = rhoContent + "\nThis is line " + i + " in rho";
        }
        rhoPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 2);
        thisTest.getExpectedWC().setItemContent("A/D/G/rho", rhoContent);

        // commit the changes
        assertEquals("wrong revision number from commit",2,
                client.commit(new File[]{thisTest.getWCPath()}, "log msg",
                        true));

        // check the status of the first working copy
        thisTest.checkStatusesExpectedWC();

        // create a backup copy of the working copy
        OneTest backupTest = thisTest.copy(".backup");

        // change the last line of A/mu in the first working copy
        muPW = new PrintWriter(new FileOutputStream(mu, true));
        muContent = thisTest.getExpectedWC().getItemContent("A/mu");
        muPW.print(" Appended to line 10 of mu");
        muContent = muContent + " Appended to line 10 of mu";
        muPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 3);
        thisTest.getExpectedWC().setItemContent("A/mu", muContent);

        // change the last line of A/mu in the first working copy
        rhoPW = new PrintWriter(new FileOutputStream(rho, true));
        rhoContent = thisTest.getExpectedWC().getItemContent("A/D/G/rho");
        rhoPW.print(" Appended to line 10 of rho");
        rhoContent = rhoContent + " Appended to line 10 of rho";
        rhoPW.close();
        thisTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 3);
        thisTest.getExpectedWC().setItemContent("A/D/G/rho", rhoContent);

        // commit these changes to the repository
        assertEquals("wrong revision number from commit",3,
                client.commit(new File[]{thisTest.getWCPath()}, "log msg",
                        true));

        // check the status of the first working copy
        thisTest.checkStatusesExpectedWC();

        // modify the first line of A/mu in the backup working copy
        mu = new File(backupTest.getWorkingCopy(), "A/mu");
        muPW = new PrintWriter(new FileOutputStream(mu));
        muPW.print("This is the new line 1 in the backup copy of mu");
        muContent = "This is the new line 1 in the backup copy of mu";
        for (int i = 2; i < 11; i++)
        {
            muPW.print("\nThis is line " + i + " in mu");
            muContent = muContent + "\nThis is line " + i + " in mu";
        }
        muPW.close();
        backupTest.getExpectedWC().setItemWorkingCopyRevision("A/mu", 3);
        muContent = muContent + " Appended to line 10 of mu";
        backupTest.getExpectedWC().setItemContent("A/mu", muContent);
        backupTest.getExpectedWC().setItemTextStatus("A/mu", SVNStatusKind.MODIFIED);

        // modify the first line of A/D/G/rho in the backup working copy
        rho = new File(backupTest.getWorkingCopy(), "A/D/G/rho");
        rhoPW = new PrintWriter(new FileOutputStream(rho));
        rhoPW.print("This is the new line 1 in the backup copy of rho");
        rhoContent = "This is the new line 1 in the backup copy of rho";
        for (int i = 2; i < 11; i++)
        {
            rhoPW.print("\nThis is line " + i + " in rho");
            rhoContent = rhoContent + "\nThis is line " + i + " in rho";
        }
        rhoPW.close();
        backupTest.getExpectedWC().setItemWorkingCopyRevision("A/D/G/rho", 3);
        rhoContent = rhoContent + " Appended to line 10 of rho";
        backupTest.getExpectedWC().setItemContent("A/D/G/rho", rhoContent);
        backupTest.getExpectedWC().setItemTextStatus("A/D/G/rho", SVNStatusKind.MODIFIED);

        // update the backup working copy
        assertEquals("wrong revision number from update",3,
                client.update(backupTest.getWCPath(), SVNRevision.HEAD, true));

        // check the status of the backup working copy
        backupTest.checkStatusesExpectedWC();
    }    
    
    
}
