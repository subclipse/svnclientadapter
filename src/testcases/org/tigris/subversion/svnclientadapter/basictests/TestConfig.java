package org.tigris.subversion.svnclientadapter.basictests;

import java.io.File;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;

/**
 * Configuration for a given OneTest 
 */
public class TestConfig {
    /**
     * the svn client to use to create the working copy and check the status
     */
    public ISVNClientAdapter client;
    /**
     * the directory of the sample repository.
     */
    public File reposDirectory;
    /**
     * the initial working copy of the sample repository.
     */
    public ExpectedWC expectedWC;
    
    /**
     * the initial repository
     */
    public ExpectedRepository expectedRepository;
}
