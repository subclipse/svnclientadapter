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

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

public class LsTest extends SVNTest {

	/**
	 * test the basic SVNClient.list functionality
	 * @throws Throwable
	 */
	public void testBasicLs() throws Throwable
	{
	    // create the working copy
	    OneTest thisTest = new OneTest("basicLs",getGreekTestConfig());
	
	    // list the repository root dir
	    ISVNDirEntry[] entries = client.getList(thisTest.getWCPath(), SVNRevision.HEAD, false);
	    thisTest.getExpectedWC().check(entries,"", false);
	
	    // list directory A
	    entries = client.getList(new File(thisTest.getWCPath()+"/A"), SVNRevision.HEAD, false);
	    thisTest.getExpectedWC().check(entries,"A", false);
	
	    // list directory A in BASE revision
	    entries = client.getList(new File(thisTest.getWCPath()+"/A"), SVNRevision.BASE, false);
	    thisTest.getExpectedWC().check(entries,"A", false);
	
	    // list file A/mu
	    entries = client.getList(new File(thisTest.getWCPath()+"/A/mu"), SVNRevision.HEAD, false);
	    thisTest.getExpectedWC().check(entries,"A/mu");
	}

    public void testBasicLsUrl() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("basicLsUrl",getGreekTestConfig());
        
        ISVNDirEntry[] entries = client.getList(thisTest.getUrl(), SVNRevision.HEAD, true);
        thisTest.getExpectedRepository().check(entries,"", true);       
        
        // list directory A
        entries = client.getList(new SVNUrl(thisTest.getUrl()+"/A"), SVNRevision.HEAD, false);
        thisTest.getExpectedRepository().check(entries,"A", false);
    }
    
    public void testGetDirEntryUrl() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("basicGetDirEntryUrl",getGreekTestConfig());
        
        // get the dirEntry of a directory first
        ISVNDirEntry entry = client.getDirEntry(new SVNUrl(thisTest.getUrl()+"/A"), SVNRevision.HEAD);
        assertNotNull(entry);
        assertEquals(SVNNodeKind.DIR, entry.getNodeKind());
        assertEquals(0, entry.getSize());
        assertEquals("A", entry.getPath());
        assertEquals(TEST_USER, entry.getLastCommitAuthor());
        assertNotNull(entry.getLastChangedDate());
        
        // then of a file
        entry = client.getDirEntry(new SVNUrl(thisTest.getUrl()+"/A/mu"), SVNRevision.HEAD);
        assertNotNull(entry);
        assertEquals(SVNNodeKind.FILE, entry.getNodeKind());
        assertEquals("mu", entry.getPath());
        assertEquals(TEST_USER, entry.getLastCommitAuthor());
        assertNotNull(entry.getLastChangedDate());
    }
    
    public void testGetDirEntryFile() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("basicGetDirEntryFile",getGreekTestConfig());
        
        ISVNDirEntry entry;
        
        // directory
        entry = client.getDirEntry(new File(thisTest.getWCPath()+"/A"), SVNRevision.HEAD);
        assertNotNull(entry);
        assertEquals(SVNNodeKind.DIR, entry.getNodeKind());
        assertEquals(0, entry.getSize());
        assertEquals("A", entry.getPath());
        assertEquals(TEST_USER, entry.getLastCommitAuthor());
        assertNotNull(entry.getLastChangedDate());
        
        // file
        entry = client.getDirEntry(new File(thisTest.getWCPath()+"/A/mu"), SVNRevision.HEAD);
        assertNotNull(entry);
        assertEquals(SVNNodeKind.FILE, entry.getNodeKind());
        assertEquals(new File(thisTest.getWCPath()+"/A/mu").length(), entry.getSize());
        assertEquals("mu", entry.getPath());
        assertEquals(TEST_USER, entry.getLastCommitAuthor());
        assertNotNull(entry.getLastChangedDate());
    }
}
