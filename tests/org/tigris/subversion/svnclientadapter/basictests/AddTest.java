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
