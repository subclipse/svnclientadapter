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
import java.io.PrintWriter;

import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.testUtils.FileUtils;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


public class RevertTest extends SVNTest {

	/**
	 * Test the basic SVNClient.revert functionality
	 * @throws Throwable
	 */
	public void testBasicRevert() throws Throwable
	{
	    // create a test working copy
	    OneTest thisTest = new OneTest("basicRevert",getGreekTestConfig());
	
	    // modify A/B/E/beta
	    File file = new File(thisTest.getWorkingCopy(), "A/B/E/beta");
	    PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
	    pw.print("Added some text to 'beta'.");
	    pw.close();
	    thisTest.getExpectedWC().setItemTextStatus("A/B/E/beta", SVNStatusKind.MODIFIED);
	
	    // modify iota
	    file = new File(thisTest.getWorkingCopy(), "iota");
	    pw = new PrintWriter(new FileOutputStream(file, true));
	    pw.print("Added some text to 'iota'.");
	    pw.close();
	    thisTest.getExpectedWC().setItemTextStatus("iota", SVNStatusKind.MODIFIED);
	
	    // modify A/D/G/rho
	    file = new File(thisTest.getWorkingCopy(), "A/D/G/rho");
	    pw = new PrintWriter(new FileOutputStream(file, true));
	    pw.print("Added some text to 'rho'.");
	    pw.close();
	    thisTest.getExpectedWC().setItemTextStatus("A/D/G/rho", SVNStatusKind.MODIFIED);
	
	    // create new file A/D/H/zeta and add it to subversion
	    file = new File(thisTest.getWorkingCopy(), "A/D/H/zeta");
	    pw = new PrintWriter(new FileOutputStream(file, true));
	    pw.print("Added some text to 'zeta'.");
	    pw.close();
	    thisTest.getExpectedWC().addItem("A/D/H/zeta", "Added some text to 'zeta'.");
	    thisTest.getExpectedWC().setItemTextStatus("A/D/H/zeta", SVNStatusKind.ADDED);
	    client.addFile(file);
	
	    // test the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	
	    // revert the changes
	    client.revert(new File(thisTest.getWCPath()+"/A/B/E/beta"), false);
	    thisTest.getExpectedWC().setItemTextStatus("A/B/E/beta", SVNStatusKind.NORMAL);
	    client.revert(new File(thisTest.getWCPath()+"/iota"), false);
	    thisTest.getExpectedWC().setItemTextStatus("iota", SVNStatusKind.NORMAL);
	    client.revert(new File(thisTest.getWCPath()+"/A/D/G/rho"), false);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/G/rho", SVNStatusKind.NORMAL);
	    client.revert(new File(thisTest.getWCPath()+"/A/D/H/zeta"), false);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/H/zeta",
	            SVNStatusKind.UNVERSIONED);
//	    thisTest.getWc().setItemNodeKind("A/D/H/zeta", SVNNodeKind.UNKNOWN);
	
	    // test the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	
	    // delete A/B/E/beta and revert the change
	    file = new File(thisTest.getWorkingCopy(), "A/B/E/beta");
	    file.delete();
	    client.revert(file,false);
	
	    // resurected file should not be readonly
	    assertTrue("reverted file is not readonly",
	            file.canWrite()&& file.canRead());
	
	    // test the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	
	    // create & add the directory X
	    client.mkdir(new File(thisTest.getWCPath()+"/X"));
	    thisTest.getExpectedWC().addItem("X", null);
	    thisTest.getExpectedWC().setItemTextStatus("X", SVNStatusKind.ADDED);
	
	    // test the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	
	    // remove & revert X
	    FileUtils.removeDirectoryWithContent(new File(thisTest.getWorkingCopy(), "X"));
	    client.revert(new File(thisTest.getWCPath()+"/X"), false);
	    thisTest.getExpectedWC().removeItem("X");
	
	    // test the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	
	    // delete the directory A/B/E
	    client.remove(new File[] {new File(thisTest.getWCPath()+"/A/B/E")}, true);
	    FileUtils.removeDirectoryWithContent(new File(thisTest.getWorkingCopy(), "A/B/E"));
	    thisTest.getExpectedWC().setItemTextStatus("A/B/E", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().removeItem("A/B/E/alpha");
	    thisTest.getExpectedWC().removeItem("A/B/E/beta");
	
	    // test the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	
	    // revert A/B/E -> this will not resurect it
	    client.revert(new File(thisTest.getWCPath()+"/A/B/E"), true);
	
	    // test the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	}

    
    
}
