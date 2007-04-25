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

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class CopyTest extends SVNTest {

    public void testCopyFile2File() throws Throwable {
        // create working copy
        OneTest thisTest = new OneTest("basicCopyFile2File",
                getGreekTestConfig());

        File src = new File(thisTest.getWorkingCopy() + "/A/mu");
        File dst = new File(thisTest.getWorkingCopy() + "/A/C/mu");

        client.copy(src, dst);

        thisTest.getExpectedWC().addItem("A/C/mu", null);
        thisTest.getExpectedWC().setItemTextStatus("A/C/mu", SVNStatusKind.ADDED);
        thisTest.getExpectedWC().setItemIsCopied("A/C/mu", true);

        // test the working copy status
        thisTest.checkStatusesExpectedWC();

        // try to copy an entire directory
        src = new File(thisTest.getWorkingCopy() + "/A/D");
        dst = new File(thisTest.getWorkingCopy() + "/A/E");
        client.copy(src, dst);
        thisTest.getExpectedWC().addItem("A/E", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E", SVNStatusKind.ADDED);
        thisTest.getExpectedWC().setItemIsCopied("A/E", true);
        
        thisTest.getExpectedWC().addItem("A/E/gamma", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/gamma", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/gamma", true);
        thisTest.getExpectedWC().addItem("A/E/H", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/H", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/H", true);
        thisTest.getExpectedWC().addItem("A/E/H/chi", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/H/chi", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/H/chi", true);
        thisTest.getExpectedWC().addItem("A/E/H/psi", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/H/psi", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/H/psi", true);
        thisTest.getExpectedWC().addItem("A/E/H/omega", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/H/omega", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/H/omega", true);
        thisTest.getExpectedWC().addItem("A/E/G", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/G", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/G", true);
        thisTest.getExpectedWC().addItem("A/E/G/pi", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/G/pi", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/G/pi", true);
        thisTest.getExpectedWC().addItem("A/E/G/rho", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/G/rho", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/G/rho", true);
        thisTest.getExpectedWC().addItem("A/E/G/tau", null);
        thisTest.getExpectedWC().setItemTextStatus("A/E/G/tau", SVNStatusKind.NORMAL);
        thisTest.getExpectedWC().setItemIsCopied("A/E/G/tau", true);
        
        thisTest.checkStatusesExpectedWC();
    }

    public void testCopyUrl2UrlWithoutMsg() throws Throwable {
        // create working copy
        OneTest thisTest = new OneTest("basicCopyUrl2Url",
                getGreekTestConfig());

        client.copy(new SVNUrl(thisTest.getUrl() + "/A/B"), new SVNUrl(thisTest
                .getUrl()
                + "/B"), null, SVNRevision.HEAD);
        
        // update the working copy
        client.update(thisTest.getWCPath(), SVNRevision.HEAD, true);        
        
        thisTest.getExpectedWC().addItem("B", null);
        thisTest.getExpectedWC().addItem("B/lambda", "This is the file 'lambda'.");
        thisTest.getExpectedWC().addItem("B/E", null);
        thisTest.getExpectedWC().addItem("B/E/alpha", "This is the file 'alpha'.");
        thisTest.getExpectedWC().addItem("B/E/beta", "This is the file 'beta'.");
        thisTest.getExpectedWC().addItem("B/F", null);
        thisTest.checkStatusesExpectedWC();
    }

}