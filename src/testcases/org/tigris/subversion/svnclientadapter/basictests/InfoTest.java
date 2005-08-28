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

import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNScheduleKind;


public class InfoTest extends SVNTest {

	/**
	 * test the basic SVNClient.info functionality
	 * @throws Throwable
	 */
	public void testBasicInfo() throws Throwable
	{
	    // create the working copy
	    OneTest thisTest = new OneTest("basicInfo",getGreekTestConfig());
	
	    // get the item information and test it
	    ISVNInfo info = client.getInfo(new File(thisTest.getWCPath()+"/A/mu"));
	    assertEquals("wrong revision from info", new SVNRevision.Number(1),
	            info.getLastChangedRevision());
	    assertEquals("wrong schedule kind from info", SVNScheduleKind.NORMAL,
	            info.getSchedule());
	    assertEquals("wrong node kind from info", SVNNodeKind.FILE,
	            info.getNodeKind());
	    
	    //Test added file.
	    File fileAdded = new File(thisTest.getWCPath()+"/A/added.txt");
	    new FileOutputStream(fileAdded).close();
	    client.addFile(fileAdded);

	    // get the item information and test it
	    info = client.getInfo(new File(thisTest.getWCPath()+"/A/added.txt"));

	    File fileUnversioned = new File(thisTest.getWCPath()+"/A/unversioned.txt");
	    new FileOutputStream(fileUnversioned).close();

	    // get the item information and test it
	    info = client.getInfo(new File(thisTest.getWCPath()+"/A/unversioned.txt"));
	}

}
