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
package org.tigris.subversion.svnclientadapter.testUtils;

import java.io.File;
import java.net.MalformedURLException;

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * configuration parameters that are common to all tests
 */
public class TestsConfig {

    private static TestsConfig testsConfig;

    public String clientType;    
    
    public String adminClientType;
    
    /**
     * common root URL for all tests. Can be set by system property "test.rooturl". 
     * If not set, the file url of the rootDirectoryName is used.
     */
    public SVNUrl rootUrl;
    
    /**
     * the root directory. All other files and directories will created in
     * here
     */
    public File rootDir;
    
    /**
     * common root directory for all tests. Can be set by the system property 
     * "test.rootdir". If not set, the current directory of this process is used
     */
    public String rootDirectoryName;

    /**
     * the directory "repositories" in the rootDir. All test repositories will
     * be created here.
     */
    public File repositories;

    /**
     * the directory "working_copies" in the rootDir. All test working copies
     * will be created here.
     */
    public File workingCopies;

    public String serverHostname;
    public int serverPort;
    
    public String protocol;    
    
    private TestsConfig() throws SVNClientException {
        clientType = System.getProperty("test.clientType");
        if (clientType == null) {
            clientType = "javahl";
        }
        System.out.println("Using "+clientType+" factory ...");
        
        adminClientType = System.getProperty("test.adminClientType");
        if (adminClientType == null) {
            adminClientType = clientType;
        }
//        if (adminClientType.equals("svnkit")) {
//            adminClientType = "javahl";
//        }
        System.out.println("Using "+adminClientType+" factory for admin commands...");
        
        rootDirectoryName = System.getProperty("test.rootdir");
        if (rootDirectoryName == null)
            rootDirectoryName = System.getProperty("user.dir");
        rootDir = new File(rootDirectoryName);

        if (System.getProperty("test.serverHostname") != null) {         	
        	serverHostname = System.getProperty("test.serverHostname");
        } else {
        	serverHostname = "127.0.0.1";
        }
        if (System.getProperty("test.serverPort") != null) {
        	serverPort = Integer.valueOf(System.getProperty("test.serverPort")).intValue();
        } else {
        	serverPort = 3690;
        }

        protocol = System.getProperty("test.protocol");
        if (protocol == null)
        	protocol = "file";
        
        String rootUrlStr = null;
        if(protocol.equals("file")) {
            rootUrlStr = rootDir.toURI().toString();
            // java may have a different view about the number of '/' to follow
            // "file:" than subversion. We convert to the subversion view.
            if(rootUrlStr.startsWith("file:///"))
                ; // this is the form subversion needs
            else if(rootUrlStr.startsWith("file://"))
                rootUrlStr = rootUrlStr.replaceFirst("file://", "file:///");
            else if(rootUrlStr.startsWith("file:/"))
                rootUrlStr = rootUrlStr.replaceFirst("file:/", "file:///");
        } else
        if (protocol.equals("svn")) {
        	rootUrlStr = "svn://" + serverHostname;
        } else
        if (protocol.equals("http")) {
            rootUrlStr = "http://" + serverHostname + ":8080/svn/repos";
        }
        
        
        try {
			rootUrl = new SVNUrl(rootUrlStr);
		} catch (MalformedURLException e) {
			throw new SVNClientException(e);
		}
        
        // create the directory for the repositories and the working copies
        repositories = new File(rootDir, "repositories");
        repositories.mkdirs();
        workingCopies = new File(rootDir, "working_copies");
        workingCopies.mkdirs();

    }
    
    
    public static TestsConfig getTestsConfig() throws SVNClientException {
        if (testsConfig == null) { 
            testsConfig = new TestsConfig();
        }
        return testsConfig;
    }

    /**
     * Create the url for the repository to be used for the tests.
     * @param file  the directory of the repository
     * @return the URL for the repository
     * @throws MalformedURLException
     */
    public SVNUrl makeReposUrl(File file) throws MalformedURLException
    {
        // split the common part of the root directory
        String path = file.getAbsolutePath().
                substring(rootDirectoryName.length()+1);
        // append to the root url
        return new SVNUrl(rootUrl + "/" + path.replace(File.separatorChar, '/'));
    }
    
    
}
