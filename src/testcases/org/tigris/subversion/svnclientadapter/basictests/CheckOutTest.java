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

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;

public class CheckOutTest extends SVNTest {

    /**
     * test the basic SVNCLient.checkout functionality
     * @throws Throwable
     */
    public void testBasicCheckout() throws Throwable
    {
        // build the test setup
        OneTest thisTest = new OneTest("basicCheckout",getGreekTestConfig());
        try
        {
            // obstructed checkout must fail
            client.checkout(new SVNUrl(thisTest.getUrl() + "/A"), thisTest.getWCPath(),
                    SVNRevision.HEAD, true);
            fail("missing exception");
        }
        catch (SVNClientException e)
        {
        }
        // modify file A/mu
        File mu = new File(thisTest.getWorkingCopy(), "A/mu");
        PrintWriter muPW = new PrintWriter(new FileOutputStream(mu, true));
        muPW.print("appended mu text");
        muPW.close();
        thisTest.getWc().setItemTextStatus("A/mu", SVNStatusKind.MODIFIED);

        // delete A/B/lambda without svn
        File lambda = new File(thisTest.getWorkingCopy(), "A/B/lambda");
        lambda.delete();
        thisTest.getWc().setItemTextStatus("A/B/lambda", SVNStatusKind.MISSING);

        // remove A/D/G
        client.remove(new File[]{new File(thisTest.getWCPath() + "/A/D/G")}, false);
        thisTest.getWc().setItemTextStatus("A/D/G", SVNStatusKind.DELETED);
        thisTest.getWc().setItemTextStatus("A/D/G/pi", SVNStatusKind.DELETED);
        thisTest.getWc().setItemTextStatus("A/D/G/rho", SVNStatusKind.DELETED);
        thisTest.getWc().setItemTextStatus("A/D/G/tau", SVNStatusKind.DELETED);

        // check the status of the working copy
        thisTest.checkStatus();

        // recheckout the working copy
        client.checkout(thisTest.getUrl(), thisTest.getWCPath(), SVNRevision.HEAD, true);

        // deleted file should reapear
        thisTest.getWc().setItemTextStatus("A/B/lambda", SVNStatusKind.NORMAL);

        // check the status of the working copy
        thisTest.checkStatus();
    }

	public void testBasicCheckoutDeleted() throws Throwable
	{
	    // create working copy
	    OneTest thisTest = new OneTest("basicCheckout",getGreekTestConfig());
	
	    // delete A/D and its content
	    client.remove(new File[] {new File(thisTest.getWCPath()+"/A/D")}, true);
	    thisTest.getWc().setItemTextStatus("A/D", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/G", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/G/rho", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/G/pi", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/G/tau", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/H", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/H/chi", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/H/psi", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/H/omega", SVNStatusKind.DELETED);
	    thisTest.getWc().setItemTextStatus("A/D/gamma", SVNStatusKind.DELETED);
	
	    // check the working copy status
	    thisTest.checkStatus();
	
	    // commit the change
	    assertEquals("wrong revision from commit",2,
	            client.commit(new File[]{thisTest.getWCPath()}, "log message",
	                    true));
	    thisTest.getWc().removeItem("A/D");
	    thisTest.getWc().removeItem("A/D/G");
	    thisTest.getWc().removeItem("A/D/G/rho");
	    thisTest.getWc().removeItem("A/D/G/pi");
	    thisTest.getWc().removeItem("A/D/G/tau");
	    thisTest.getWc().removeItem("A/D/H");
	    thisTest.getWc().removeItem("A/D/H/chi");
	    thisTest.getWc().removeItem("A/D/H/psi");
	    thisTest.getWc().removeItem("A/D/H/omega");
	    thisTest.getWc().removeItem("A/D/gamma");
	
	    // check the working copy status
	    thisTest.checkStatus();
	
	    // check out the previous revision
	    client.checkout(new SVNUrl(thisTest.getUrl()+"/A/D"), new File(thisTest.getWCPath()+"/new_D"),
	            new SVNRevision.Number(1), true);
	}
    
    
}
