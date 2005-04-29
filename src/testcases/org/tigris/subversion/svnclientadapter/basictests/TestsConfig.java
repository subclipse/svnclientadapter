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
import java.net.MalformedURLException;

import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * configuration parameters that are common to all tests
 */
public class TestsConfig {

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
    
    public String protocol;

    private static TestsConfig testsConfig;
    
    
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
        if (adminClientType.equals("javasvn")) {
            adminClientType = "javahl";
        }
        System.out.println("Using "+adminClientType+" factory for admin commands...");
        
        rootDirectoryName = System.getProperty("test.rootdir");
        if (rootDirectoryName == null)
            rootDirectoryName = System.getProperty("user.dir");
        rootDir = new File(rootDirectoryName);
    
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
        	rootUrlStr = "svn://localhost";
        } else
        if (protocol.equals("http")) {
            rootUrlStr = "http://localhost:8080/svn/repos";
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
