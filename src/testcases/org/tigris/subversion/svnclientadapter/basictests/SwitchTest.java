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
