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

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;


public class SwitchTest extends SVNTest {

	/**
	 * thest the basic SVNClient.switch functionality
	 * @throws Throwable
	 */
	public void testBasicSwitch() throws Throwable
	{
	    // create the test working copy
	    OneTest thisTest = new OneTest("basicSwitch",getGreekTestConfig());
	
	    // switch iota to A/D/gamma
	    File iotaPath = new File(thisTest.getWCPath() + "/iota");
	    SVNUrl gammaUrl = new SVNUrl(thisTest.getUrl() + "/A/D/gamma");
	    thisTest.getWc().setItemContent("iota",
	            getGreekWC().getItemContent("A/D/gamma"));
	    thisTest.getWc().setItemIsSwitched("iota", true);
	    client.switchToUrl(iotaPath, gammaUrl, SVNRevision.HEAD, true);
	
	    // check the status of the working copy
	    thisTest.checkStatus();
	
	    // switch A/D/H to /A/D/G
	    File adhPath = new File(thisTest.getWCPath() + "/A/D/H");
	    SVNUrl adgURL = new SVNUrl(thisTest.getUrl() + "/A/D/G");
	    thisTest.getWc().setItemIsSwitched("A/D/H",true);
	    thisTest.getWc().removeItem("A/D/H/chi");
	    thisTest.getWc().removeItem("A/D/H/omega");
	    thisTest.getWc().removeItem("A/D/H/psi");
	    thisTest.getWc().addItem("A/D/H/pi",
	            thisTest.getWc().getItemContent("A/D/G/pi"));
	    thisTest.getWc().addItem("A/D/H/rho",
	            thisTest.getWc().getItemContent("A/D/G/rho"));
	    thisTest.getWc().addItem("A/D/H/tau",
	            thisTest.getWc().getItemContent("A/D/G/tau"));
	    client.switchToUrl(adhPath, adgURL, SVNRevision.HEAD, true);
	
	    // check the status of the working copy
	    thisTest.checkStatus();
	}

}
