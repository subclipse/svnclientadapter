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
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import org.tigris.subversion.svnclientadapter.SVNRevision;


public class CatTest extends SVNTest {

	/**
	 * test the basic SVNClient.fileContent functionality
	 * @throws Throwable
	 */
	public void testBasicCat() throws Throwable
	{
	    // create the working copy
	    OneTest thisTest = new OneTest("basicCat",getGreekTestConfig());
	
	    // modify A/mu
	    File mu = new File(thisTest.getWorkingCopy(), "A/mu");
	    PrintWriter pw = new PrintWriter(new FileOutputStream(mu, true));
	    pw.print("some text");
	    pw.close();
	    // get the content from the repository
	    InputStream is = client.getContent(new File(thisTest.getWCPath()+"/A/mu"), SVNRevision.HEAD);
	    byte[] content = new byte[is.available()];
	    is.read(content);
	    byte[] testContent = thisTest.getWc().getItemContent("A/mu").getBytes();
	
	    // the content should be the same
	    assertTrue("content changed", Arrays.equals(content, testContent));
	}

}
