/*******************************************************************************
 * Copyright (c) 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
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
