package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;

public class CopyTest extends SVNTest {

    public void testCopyFile2File() throws Throwable {
        // create working copy
        OneTest thisTest = new OneTest("basicCopyFile2File",
                getGreekTestConfig());

        File src = new File(thisTest.getWorkingCopy() + "/A/mu");
        File dst = new File(thisTest.getWorkingCopy() + "/A/C/mu");

        client.copy(src, dst);

        thisTest.getWc().addItem("A/C/mu", null);
        thisTest.getWc().setItemTextStatus("A/C/mu", SVNStatusKind.ADDED);
        thisTest.getWc().setItemIsCopied("A/C/mu", true);

        // test the working copy status
        thisTest.checkStatus();

        // try to copy an entire directory
        src = new File(thisTest.getWorkingCopy() + "/A/D");
        dst = new File(thisTest.getWorkingCopy() + "/A/E");
        client.copy(src, dst);
        thisTest.getWc().addItem("A/E", null);
        thisTest.getWc().setItemTextStatus("A/E", SVNStatusKind.ADDED);
        thisTest.getWc().setItemIsCopied("A/E", true);
        thisTest.getWc().addItem("A/E/G/tau", null);
        thisTest.getWc().setItemTextStatus("A/E/G/tau", SVNStatusKind.NORMAL);
    }

    public void testCopyUrl2Url() throws Throwable {
        // create working copy
        OneTest thisTest = new OneTest("basicCopyFile2File",
                getGreekTestConfig());

        client.copy(new SVNUrl(thisTest.getUrl() + "/A/B"), new SVNUrl(thisTest
                .getUrl()
                + "/B"), "log msg", SVNRevision.HEAD);
    }

}