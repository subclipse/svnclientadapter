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
