/*******************************************************************************
 * Copyright (c) 2005, 2006 Subclipse project and others.
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

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class MoveTest extends SVNTest {

    public void testBasicMoveFile() throws Throwable
    {
        // build the test setup.
        OneTest thisTest = new OneTest("basicMoveFile",getGreekTestConfig());

        File src = new File(thisTest.getWorkingCopy() + "/A/mu");
        File dst = new File(thisTest.getWorkingCopy() + "/A/C/mu");
        client.move(src, dst, true);
        thisTest.getExpectedWC().setItemTextStatus("A/mu", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().addItem("A/C/mu", null);
        thisTest.getExpectedWC().setItemTextStatus("A/C/mu", SVNStatusKind.ADDED);
        thisTest.getExpectedWC().setItemIsCopied("A/C/mu", true);

        // test the working copy status
        thisTest.checkStatusesExpectedWC();
        
        
        // try to move an entire directory
        src = new File(thisTest.getWorkingCopy() + "/A/D");
        dst = new File(thisTest.getWorkingCopy() + "/A/E");
        client.move(src, dst, true);
        
        thisTest.getExpectedWC().setItemTextStatus("A/D", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/gamma", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/H", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/H/chi", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/H/psi", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/H/omega", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G/pi", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G/rho", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G/tau", SVNStatusKind.DELETED);
        
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

    public void testMoveUrl2Url() throws Throwable {
        // create working copy
        OneTest thisTest = new OneTest("basicMoveUrl2Url",
                getGreekTestConfig());

        client.move(new SVNUrl(thisTest.getUrl() + "/A/B"), new SVNUrl(thisTest
                .getUrl()
                + "/B"), "log msg", SVNRevision.HEAD);
        
        // update the working copy
        client.update(thisTest.getWCPath(), SVNRevision.HEAD, true);        
        
        thisTest.getExpectedWC().removeItem("A/B");
        thisTest.getExpectedWC().removeItem("A/B/lambda");
        thisTest.getExpectedWC().removeItem("A/B/E");
        thisTest.getExpectedWC().removeItem("A/B/E/alpha");
        thisTest.getExpectedWC().removeItem("A/B/E/beta");
        thisTest.getExpectedWC().removeItem("A/B/F");
        
        thisTest.getExpectedWC().addItem("B", null);
        thisTest.getExpectedWC().addItem("B/lambda", "This is the file 'lambda'.");
        thisTest.getExpectedWC().addItem("B/E", null);
        thisTest.getExpectedWC().addItem("B/E/alpha", "This is the file 'alpha'.");
        thisTest.getExpectedWC().addItem("B/E/beta", "This is the file 'beta'.");
        thisTest.getExpectedWC().addItem("B/F", null);
        thisTest.checkStatusesExpectedWC();
    }    
    
}
