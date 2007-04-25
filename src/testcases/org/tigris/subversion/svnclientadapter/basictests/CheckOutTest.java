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

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;

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
        thisTest.getExpectedWC().setItemTextStatus("A/mu", SVNStatusKind.MODIFIED);

        // delete A/B/lambda without svn
        File lambda = new File(thisTest.getWorkingCopy(), "A/B/lambda");
        lambda.delete();
        thisTest.getExpectedWC().setItemTextStatus("A/B/lambda", SVNStatusKind.MISSING);

        // remove A/D/G
        client.remove(new File[]{new File(thisTest.getWCPath() + "/A/D/G")}, false);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G/pi", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G/rho", SVNStatusKind.DELETED);
        thisTest.getExpectedWC().setItemTextStatus("A/D/G/tau", SVNStatusKind.DELETED);

        // check the status of the working copy
        thisTest.checkStatusesExpectedWC();
    }

	public void testBasicCheckoutDeleted() throws Throwable
	{
	    // create working copy
	    OneTest thisTest = new OneTest("basicCheckout",getGreekTestConfig());
	
	    // delete A/D and its content
	    client.remove(new File[] {new File(thisTest.getWCPath()+"/A/D")}, true);
	    thisTest.getExpectedWC().setItemTextStatus("A/D", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/G", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/G/rho", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/G/pi", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/G/tau", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/H", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/H/chi", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/H/psi", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/H/omega", SVNStatusKind.DELETED);
	    thisTest.getExpectedWC().setItemTextStatus("A/D/gamma", SVNStatusKind.DELETED);
	
	    // check the working copy status
	    thisTest.checkStatusesExpectedWC();
	
	    // commit the change
	    assertEquals("wrong revision from commit",2,
	            client.commit(new File[]{thisTest.getWCPath()}, "log message",
	                    true));
	    thisTest.getExpectedWC().removeItem("A/D");
	    thisTest.getExpectedWC().removeItem("A/D/G");
	    thisTest.getExpectedWC().removeItem("A/D/G/rho");
	    thisTest.getExpectedWC().removeItem("A/D/G/pi");
	    thisTest.getExpectedWC().removeItem("A/D/G/tau");
	    thisTest.getExpectedWC().removeItem("A/D/H");
	    thisTest.getExpectedWC().removeItem("A/D/H/chi");
	    thisTest.getExpectedWC().removeItem("A/D/H/psi");
	    thisTest.getExpectedWC().removeItem("A/D/H/omega");
	    thisTest.getExpectedWC().removeItem("A/D/gamma");
	
	    // check the working copy status
	    thisTest.checkStatusesExpectedWC();
	
	    // check out the previous revision
	    client.checkout(new SVNUrl(thisTest.getUrl()+"/A/D"), new File(thisTest.getWCPath()+"/new_D"),
	            new SVNRevision.Number(1), true);
	}
    
    
}
