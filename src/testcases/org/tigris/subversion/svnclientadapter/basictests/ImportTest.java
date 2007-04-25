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
import java.io.PrintWriter;

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.FileUtils;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


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
	    thisTest.getExpectedWC().addItem("dirA", null);
	    thisTest.getExpectedWC().setItemWorkingCopyRevision("dirA",2);
	    thisTest.getExpectedWC().addItem("dirA/dirB", null);
	    thisTest.getExpectedWC().setItemWorkingCopyRevision("dirA/dirB",2);
	    thisTest.getExpectedWC().addItem("dirA/dirB/new_file", "some text");
	    thisTest.getExpectedWC().setItemWorkingCopyRevision("dirA/dirB/new_file",2);
	
	    // test the working copy status
	    thisTest.checkStatusesExpectedWC();
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
	    thisTest.getExpectedWC().addItem("dir", null);
	    thisTest.getExpectedWC().addItem("dir/foo.c", "");
	
	    // test the working copy status
	    thisTest.checkStatusesExpectedWC();
	}

}
