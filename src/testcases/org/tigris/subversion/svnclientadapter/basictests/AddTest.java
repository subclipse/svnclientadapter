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

import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;


public class AddTest extends SVNTest {

    
	/**
	 * test the basis SVNClient.add functionality with files that should be
	 * ignored
	 * @throws Throwable
	 */
	public void testBasicAddIgnores() throws Throwable
	{
	    // create working copy
	    OneTest thisTest = new OneTest("basicAddIgnores",getGreekTestConfig());
	
	    // create dir
	    File dir = new File(thisTest.getWorkingCopy(), "dir");
	    dir.mkdir();
	
	    // create dir/foo.c
	    File fileC = new File(dir, "foo.c");
	    new FileOutputStream(fileC).close();
	
	    // create dir/foo.o (should be ignored)
	    File fileO = new File(dir, "foo.o");
	    new FileOutputStream(fileO).close();
	
	    // add dir
	    client.addDirectory(dir, true);
	    thisTest.getWc().addItem("dir", null);
	    thisTest.getWc().setItemTextStatus("dir",SVNStatusKind.ADDED);
	    thisTest.getWc().addItem("dir/foo.c", "");
	    thisTest.getWc().setItemTextStatus("dir/foo.c",SVNStatusKind.ADDED);
	    thisTest.getWc().addItem("dir/foo.o", "");
	    thisTest.getWc().setItemTextStatus("dir/foo.o",SVNStatusKind.IGNORED);
	    thisTest.getWc().setItemNodeKind("dir/foo.o", SVNNodeKind.UNKNOWN);
	
	    // test the working copy status
	    thisTest.checkStatus();
	}

}
