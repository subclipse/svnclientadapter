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

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;


public class ImportTest extends SVNTest {

	/**
	 * Test the basic SVNClient.import functionality
	 * @throws Throwable
	 */
	public void testBasicImport() throws Throwable
	{
	    // create the working copy
	    OneTest thisTest = new OneTest("basicImport",getGreekTestConfig());
	
	    // create new_file
	    File file = new File(thisTest.getWCPath(),"new_file");
	    PrintWriter pw = new PrintWriter(new FileOutputStream(file));
	    pw.print("some text");
	    pw.close();
	
	    // import new_file info dirA/dirB/newFile
	    client.doImport(file.getAbsoluteFile(),
	            new SVNUrl(thisTest.getUrl()+"/dirA/dirB/new_file"),
	            "log message for new import", true);
	
	    // delete new_file
	    file.delete();
	
	    // update the working
	    assertEquals("wrong revision from update",2,
	            client.update(thisTest.getWCPath(), SVNRevision.HEAD, true));
	    thisTest.getWc().addItem("dirA", null);
	    thisTest.getWc().setItemWorkingCopyRevision("dirA",2);
	    thisTest.getWc().addItem("dirA/dirB", null);
	    thisTest.getWc().setItemWorkingCopyRevision("dirA/dirB",2);
	    thisTest.getWc().addItem("dirA/dirB/new_file", "some text");
	    thisTest.getWc().setItemWorkingCopyRevision("dirA/dirB/new_file",2);
	
	    // test the working copy status
	    thisTest.checkStatus();
	}

	/**
	 * test the basis SVNClient.import functionality with files that should be
	 * ignored
	 * @throws Throwable
	 */
	public void testBasicImportIgnores() throws Throwable
	{
	    // create working copy
	    OneTest thisTest = new OneTest("basicImportIgnores",getGreekTestConfig());
	
	    // create dir
	    File dir = new File(thisTest.getWorkingCopy(), "dir");
	    dir.mkdir();
	
	    // create dir/foo.c
	    File fileC = new File(dir, "foo.c");
	    new FileOutputStream(fileC).close();
	
	    // create dir/foo.o (should be ignored)
	    File fileO = new File(dir, "foo.o");
	    new FileOutputStream(fileO).close();
	
	    // import dir
	    client.doImport(dir, new SVNUrl(thisTest.getUrl()+"/dir"),
	            "log message for import", true);
	
	    // remove dir
	    FileUtils.removeDirectoryWithContent(dir);
	
	    // udpate the working copy
	    assertEquals("wrong revision from update", 2,
	            client.update(thisTest.getWCPath(), SVNRevision.HEAD, true));
	    thisTest.getWc().addItem("dir", null);
	    thisTest.getWc().addItem("dir/foo.c", "");
	
	    // test the working copy status
	    thisTest.checkStatus();
	}

}
