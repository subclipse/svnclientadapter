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

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.testUtils.OneTest;
import org.tigris.subversion.svnclientadapter.testUtils.SVNTest;


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
	    thisTest.getExpectedWC().setItemContent("iota",
	    		thisTest.getExpectedWC().getItemContent("A/D/gamma"));
	    thisTest.getExpectedWC().setItemIsSwitched("iota", true);
	    client.switchToUrl(iotaPath, gammaUrl, SVNRevision.HEAD, true);
	
	    // check the status of the working copy
	    thisTest.checkStatusesExpectedWC();
    
	    // switch A/D/H to /A/D/G
	    File adhPath = new File(thisTest.getWCPath() + "/A/D/H");
	    SVNUrl adgURL = new SVNUrl(thisTest.getUrl() + "/A/D/G");
	    thisTest.getExpectedWC().setItemIsSwitched("A/D/H",true);
	    thisTest.getExpectedWC().removeItem("A/D/H/chi");
	    thisTest.getExpectedWC().removeItem("A/D/H/omega");
	    thisTest.getExpectedWC().removeItem("A/D/H/psi");
	    thisTest.getExpectedWC().addItem("A/D/H/pi",
	            thisTest.getExpectedWC().getItemContent("A/D/G/pi"));
	    thisTest.getExpectedWC().addItem("A/D/H/rho",
	            thisTest.getExpectedWC().getItemContent("A/D/G/rho"));
	    thisTest.getExpectedWC().addItem("A/D/H/tau",
	            thisTest.getExpectedWC().getItemContent("A/D/G/tau"));
	    client.switchToUrl(adhPath, adgURL, SVNRevision.HEAD, true);
	
	    // check the status of the working copy
	    thisTest.checkStatusesExpectedWC();
	}

}
