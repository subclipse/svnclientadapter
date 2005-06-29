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

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

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
        
        // then of a file
        entry = client.getDirEntry(new SVNUrl(thisTest.getUrl()+"/A/mu"), SVNRevision.HEAD);
        assertNotNull(entry);
    }
    
    public void testGetDirEntryFile() throws Throwable {
        // create the working copy
        OneTest thisTest = new OneTest("basicGetDirEntryFile",getGreekTestConfig());
        
        ISVNDirEntry entry;
        
        // directory
        entry = client.getDirEntry(new File(thisTest.getWCPath()+"/A"), SVNRevision.HEAD);
        assertNotNull(entry);
        
        // file
        entry = client.getDirEntry(new File(thisTest.getWCPath()+"/A/mu"), SVNRevision.HEAD);
        assertNotNull(entry);
    }
}
