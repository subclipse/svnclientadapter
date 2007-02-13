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

import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNScheduleKind;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


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
	    info = client.getInfo(fileAdded);
	    assertEquals(fileAdded, info.getFile());
	    assertNull("wrong revision from info", info.getLastChangedRevision());
	    assertEquals("wrong schedule kind from info", SVNScheduleKind.ADD,
	            info.getSchedule());
	    assertEquals("wrong node kind from info", SVNNodeKind.FILE,
	            info.getNodeKind());

	    File fileUnversioned = new File(thisTest.getWCPath()+"/A/unversioned.txt");
	    new FileOutputStream(fileUnversioned).close();

	    // get the item information and test it
	    info = client.getInfo(fileUnversioned);
	    assertEquals(fileUnversioned, info.getFile());
	    assertNull("wrong revision from info", info.getLastChangedRevision());
	    assertNull("wrong schedule kind from info", info.getSchedule());
	    assertNull("wrong node kind from info", info.getNodeKind());
	}

	public void testInfoFromWorkingCopy() throws Throwable
	{
	    // create the working copy
	    OneTest thisTest = new OneTest("basicInfo",getGreekTestConfig());
	
	    // get the item information and test it
	    ISVNInfo info = client.getInfoFromWorkingCopy(new File(thisTest.getWCPath()+"/A/mu"));
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
	    info = client.getInfoFromWorkingCopy(fileAdded);
	    assertEquals(fileAdded, info.getFile());
	    assertNull("wrong revision from info", info.getLastChangedRevision());
	    assertEquals("wrong schedule kind from info", SVNScheduleKind.ADD,
	            info.getSchedule());
	    assertEquals("wrong node kind from info", SVNNodeKind.FILE,
	            info.getNodeKind());

	    File fileUnversioned = new File(thisTest.getWCPath()+"/A/unversioned.txt");
	    new FileOutputStream(fileUnversioned).close();

	    // get the item information and test it
	    info = client.getInfoFromWorkingCopy(fileUnversioned);
	    assertEquals(fileUnversioned, info.getFile());
	    assertNull("wrong revision from info", info.getLastChangedRevision());
	    assertNull("wrong schedule kind from info", info.getSchedule());
	    assertNull("wrong node kind from info", info.getNodeKind());
	    
	    //test the wc root directotry info
	    info = client.getInfoFromWorkingCopy(thisTest.getWorkingCopy());
	    assertEquals(thisTest.getWorkingCopy(), info.getFile());
	    assertEquals("wrong revision from info", new SVNRevision.Number(1),
	            info.getLastChangedRevision());
	    assertEquals("wrong schedule kind from info", SVNScheduleKind.NORMAL,
	            info.getSchedule());
	    assertEquals("wrong node kind from info", SVNNodeKind.DIR,
	            info.getNodeKind());
	}
}
