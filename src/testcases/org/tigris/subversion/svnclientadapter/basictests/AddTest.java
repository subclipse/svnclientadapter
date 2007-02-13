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

import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


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
	    thisTest.getExpectedWC().addItem("dir", null);
	    thisTest.getExpectedWC().setItemTextStatus("dir",SVNStatusKind.ADDED);
	    thisTest.getExpectedWC().addItem("dir/foo.c", "");
	    thisTest.getExpectedWC().setItemTextStatus("dir/foo.c",SVNStatusKind.ADDED);
	    thisTest.getExpectedWC().addItem("dir/foo.o", "");
	    thisTest.getExpectedWC().setItemTextStatus("dir/foo.o",SVNStatusKind.IGNORED);	
        thisTest.getExpectedWC().setItemNodeKind("dir/foo.o", SVNNodeKind.UNKNOWN);
	
	    // test the working copy status
	    thisTest.checkStatusesExpectedWC();
	}

}
