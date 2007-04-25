/*******************************************************************************
 * Copyright (c) 2006 svnClientAdapter project and others.
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

import java.io.IOException;

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.testUtils.SvnServer;
import org.tigris.subversion.svnclientadapter.testUtils.TestsConfig;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public abstract class SVNBasicTestsSuite extends TestSuite {

    public SVNBasicTestsSuite(String name) {
        super(name);
    }

	public void run(TestResult result) {
		TestsConfig testsConfig = null;
		try {
			testsConfig = TestsConfig.getTestsConfig();
		} catch (SVNClientException e) {
			throw new RuntimeException(e);
		}
		SvnServer svnServer;
		try {
			System.out.print("Staring test suite's svnServer: ");
			svnServer = SvnServer.startSvnServer(testsConfig.serverHostname, testsConfig.serverPort, testsConfig.rootDir);
			System.out.println("done.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.run(result);
		System.out.print("Stopping test suite's svnServer: ");
		SvnServer.stopSvnServer(svnServer);
		System.out.println("done.");
	}

    protected static void addTestsToSuite(TestSuite testSuite) {
        testSuite.addTestSuite(AddTest.class);
        testSuite.addTestSuite(BlameTest.class);
        testSuite.addTestSuite(CatTest.class);
        testSuite.addTestSuite(CheckOutTest.class);
        testSuite.addTestSuite(CleanupTest.class);
        testSuite.addTestSuite(CommitTest.class);
        testSuite.addTestSuite(CopyTest.class);	//
        testSuite.addTestSuite(DeleteTest.class);
        testSuite.addTestSuite(ExportTest.class);	//
        testSuite.addTestSuite(ImportTest.class);
        testSuite.addTestSuite(InfoTest.class);
        testSuite.addTestSuite(LogTest.class);
        testSuite.addTestSuite(LsTest.class);
        testSuite.addTestSuite(MkdirTest.class);
        testSuite.addTestSuite(MoveTest.class);	//
        testSuite.addTestSuite(PropertiesTest.class);
        testSuite.addTestSuite(ResolveTest.class);
        testSuite.addTestSuite(RevertTest.class);
        testSuite.addTestSuite(StatusTest.class);
        testSuite.addTestSuite(SwitchTest.class);
        testSuite.addTestSuite(UpdateTest.class);
    }     

}
