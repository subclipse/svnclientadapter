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
import java.io.IOException;

import junit.framework.TestCase;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * common base class for the SvnclientAdapter tests
 */
public abstract class SVNTest extends TestCase
{
    protected ISVNClientAdapter client;    
    
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
    
    private ExpectedWC greekWC = null;
    
    private TestConfig greekTestConfig = null;
    
    private TestsConfig testsConfig = TestsConfig.getTestsConfig();
    

    /**
     * Standard initialization of one test
     * @throws Exception
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        // create a clean directory for the config files and the sample
        // repository
        localTmp = new File(testsConfig.rootDir, "local_tmp");
        if(localTmp.exists())
            FileUtils.removeDirectoryWithContent(localTmp);
        localTmp.mkdir();
        conf = new File(localTmp, "config");
        conf.mkdir();

        // create and configure the needed subversion objects
        client = SVNClientAdapterFactory.createSVNClient(testsConfig.clientType);
        client.setUsername("jrandom");
        client.setPassword("rayjandom");
//        client.setConfigDirectory(conf.getAbsolutePath());

    }

    public TestConfig getGreekTestConfig() throws IOException, SVNClientException {
        if (greekTestConfig == null) {
            // build the sample repository that will be imported
            File greekFiles = new File(localTmp, "greek_files");
            greekFiles.mkdir();
            greekWC = getGreekWC();
            greekWC.materialize(greekFiles);
    
            
            // create the repository
            File greekRepos = new File(localTmp, "repos");
            client.createRepository(greekRepos,ISVNClientAdapter.REPOSITORY_BDB);
            client.doImport(greekFiles, testsConfig.makeReposUrl(greekRepos),
                    logMessage, true );
    
            greekTestConfig = new TestConfig();
            greekTestConfig.client = client;
            greekTestConfig.sampleRepos = greekRepos;
            greekTestConfig.sampleWC = greekWC;
        }
    	return greekTestConfig;
    }

    protected ExpectedWC getGreekWC() {
        if (greekWC == null) {
            greekWC = new ExpectedWC();
            greekWC.addItem("",null);
            greekWC.addItem("iota", "This is the file 'iota'.");
            greekWC.addItem("A", null);
            greekWC.addItem("A/mu", "This is the file 'mu'.");
            greekWC.addItem("A/B", null);
            greekWC.addItem("A/B/lambda", "This is the file 'lambda'.");
            greekWC.addItem("A/B/E", null);
            greekWC.addItem("A/B/E/alpha", "This is the file 'alpha'.");
            greekWC.addItem("A/B/E/beta", "This is the file 'beta'.");
            greekWC.addItem("A/B/F", null);
            greekWC.addItem("A/C", null);
            greekWC.addItem("A/D", null);
            greekWC.addItem("A/D/gamma", "This is the file 'gamma'.");
            greekWC.addItem("A/D/H", null);
            greekWC.addItem("A/D/H/chi", "This is the file 'chi'.");
            greekWC.addItem("A/D/H/psi", "This is the file 'psi'.");
            greekWC.addItem("A/D/H/omega", "This is the file 'omega'.");
            greekWC.addItem("A/D/G", null);
            greekWC.addItem("A/D/G/pi", "This is the file 'pi'.");
            greekWC.addItem("A/D/G/rho", "This is the file 'rho'.");
            greekWC.addItem("A/D/G/tau", "This is the file 'tau'.");
        }
        return greekWC;
    }
    
    /**
     * cleanup after one test
     * @throws Exception
     */
    protected void tearDown() throws Exception
    {
        // take care of our subversion objects.
//        client.dispose();
        
        // remove the temporary directory
        FileUtils.removeDirectoryWithContent(localTmp);
        super.tearDown();
    }

}
