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
