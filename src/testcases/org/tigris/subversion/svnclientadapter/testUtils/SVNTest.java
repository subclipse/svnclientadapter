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
package org.tigris.subversion.svnclientadapter.testUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.javahl.JhlClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.svnkit.SvnKitClientAdapterFactory;

/**
 * common base class for the SvnclientAdapter tests
 */
public abstract class SVNTest extends TestCase {
    private static final Logger log = Logger.getLogger(SVNTest.class.getName());

    //Don't forget to put these credentials into ./test/passwd file
    public static String TEST_USER = "cedric";
    public static String TEST_USERPASS = "cedricpass";
	public static final String ANOTHER_TEST_USER = "Mr.Spock";
    
    protected ISVNClientAdapter client;

    protected ISVNClientAdapter clientAdmin;

    /**
     * the directory "local_tmp" in the rootDir. This will be used for the
     * sample repository and for the config directory
     */
    protected File localTmp;

    /**
     * the directory "config" in the localTmp. It will be used as the
     * configuration directory for all the tests.
     */
    protected File conf;

    /**
     * standard log message. Used for all commits.
     */
    protected String logMessage = "Log Message";

    private TestConfig greekTestConfig = null;
    private TestConfig numericTestConfig = null;

    private TestsConfig testsConfig;

    private SvnServer svnServer;

    static {
        log.fine("Initializing client adapters factories");
        try {
            JhlClientAdapterFactory.setup();
        } catch (SVNClientException e) {
            // can't register this factory
        }
        try {
            CmdLineClientAdapterFactory.setup();
        } catch (SVNClientException e1) {
            // can't register this factory
        }
        try {
            SvnKitClientAdapterFactory.setup();
        } catch (SVNClientException e1) {
            // can't register this factory
        }

    }

    /**
     * Standard initialization of one test
     * 
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        testsConfig = TestsConfig.getTestsConfig();

        // create a clean directory for the config files and the sample
        // repository
        localTmp = new File(testsConfig.rootDir, "local_tmp");
        if (localTmp.exists())
            FileUtils.removeDirectoryWithContent(localTmp);
        localTmp.mkdir();
        conf = new File(localTmp, "config");
        conf.mkdir();

        // create and configure the needed subversion objects
        client = SVNClientAdapterFactory
                .createSVNClient(testsConfig.clientType);
        client.setUsername(TEST_USER);
        client.setPassword(TEST_USERPASS);
        //      client.setConfigDirectory(conf.getAbsolutePath());

        clientAdmin = SVNClientAdapterFactory
                .createSVNClient(testsConfig.adminClientType);
        clientAdmin.setUsername(TEST_USER);
        clientAdmin.setPassword(TEST_USERPASS);

        startServer();
    }

    protected void startServer() throws IOException {
        if (testsConfig.protocol.equals("svn")) {
        	//Start it only if it was not running already (e.g. by testSuite)
        	if (!SvnServer.isSvnServerRunning()) {
        		System.out.print("Starting svnserve : ");
        		svnServer = SvnServer.startSvnServer(testsConfig.serverHostname, testsConfig.serverPort, testsConfig.rootDir);;
        		System.out.println("done.");
        	} else {
        		//clear the variable so we'll not stop the server which we didn't started.
        		svnServer = null;
        	}
        }
    }

    protected void stopServer() {
        if (testsConfig.protocol.equals("svn")) {
        	if (svnServer != null) {
        		System.out.print("Stopping svnserve : ");
        		SvnServer.stopSvnServer(svnServer);
        		System.out.println("done.");
        		svnServer = null;
        	}
        }
    }

    public TestConfig getGreekTestConfig() throws IOException, SVNClientException {
    	if (greekTestConfig == null) {
    		// build the sample repository that will be imported
    		File greekFiles = new File(localTmp, "greek_files");
    		greekFiles.mkdir();
    		ExpectedWC greekWC = ExpectedGreekRepositoryFactory.getGreekWC();
    		greekWC.materialize(greekFiles);

    		ExpectedRepository greekRepository = ExpectedGreekRepositoryFactory.getGreekRepository();

    		// create the repository
    		File greekRepos = new File(localTmp, "repos");
    		log.fine("Creating repository :" + greekRepos.toString());
    		clientAdmin.createRepository(greekRepos,
    				ISVNClientAdapter.REPOSITORY_FSTYPE_FSFS);
    		FileUtils.copyFile(new File("test/svnserve.conf"), new File(
    				greekRepos, "conf/svnserve.conf"));
    		FileUtils.copyFile(new File("test/passwd"), new File(greekRepos,
    		"conf/passwd"));
    		log.fine("Importing from : " + greekFiles.toString()
    				+ " to repository :" + greekRepos.toString());
    		clientAdmin.doImport(greekFiles, testsConfig
    				.makeReposUrl(greekRepos), logMessage, true);

    		greekTestConfig = new TestConfig(client, greekRepos, greekWC, greekRepository);
    	}
    	return greekTestConfig;
    }

    public TestConfig getNumericTestConfig() throws IOException,
            SVNClientException {
        if (numericTestConfig == null) {
            // build the sample repository that will be imported
            File numericFiles = new File(localTmp, "numeric_files");
            numericFiles.mkdir();
            ExpectedWC numericWC = ExpectedGreekRepositoryFactory.getNumericWC();
            numericWC.materialize(numericFiles);
            
            ExpectedRepository numericRepository = ExpectedGreekRepositoryFactory.getNumericRepository();
            
            // create the repository
            File numericRepos = new File(localTmp, "repos2");
            log.fine("Creating repository :" + numericRepos.toString());
            clientAdmin.createRepository(numericRepos,
                    ISVNClientAdapter.REPOSITORY_FSTYPE_FSFS);
            FileUtils.copyFile(new File("test/svnserve.conf"), new File(
                    numericRepos, "conf/svnserve.conf"));
            FileUtils.copyFile(new File("test/passwd"), new File(numericRepos,
                    "conf/passwd"));
            log.fine("Importing from : " + numericFiles.toString()
                    + " to repository :" + numericRepos.toString());
            clientAdmin.doImport(numericFiles, testsConfig
                    .makeReposUrl(numericRepos), logMessage, true);

            numericTestConfig = new TestConfig(client, numericRepos, numericWC, numericRepository);
        }
        return numericTestConfig;
    }

    
    /**
     * cleanup after one test
     * 
     * @throws Exception
     */
    protected void tearDown() throws Exception {
        // take care of our subversion objects.
        //        client.dispose();

        // remove the temporary directory
        FileUtils.removeDirectoryWithContent(localTmp);
        stopServer();
        super.tearDown();
    }

}