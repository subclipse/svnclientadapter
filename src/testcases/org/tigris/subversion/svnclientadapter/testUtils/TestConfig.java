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

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;

/**
 * Configuration for a given OneTest 
 */
public class TestConfig {
	
    /** the svn client to use to create the working copy and check the status */
    private ISVNClientAdapter client;
    /** the directory of the sample repository */
    private File reposDirectory;
    /** the initial working copy of the sample repository */
    private ExpectedWC expectedWC;    
    /** the initial repository */
    private ExpectedRepository expectedRepository;
    
    /**
     * Constructor
     * @param client
     * @param reposDirectory
     * @param expectedWC
     * @param expectedRepository
     */
	public TestConfig(ISVNClientAdapter client, File reposDirectory, ExpectedWC expectedWC, ExpectedRepository expectedRepository) {
		super();
		this.client = client;
		this.reposDirectory = reposDirectory;
		this.expectedWC = expectedWC;
		this.expectedRepository = expectedRepository;
	}

	/**
	 * @return Returns the client.
	 */
	public ISVNClientAdapter getClient() {
		return client;
	}

	/**
	 * @return Returns the expectedRepository.
	 */
	public ExpectedRepository getExpectedRepository() {
		return expectedRepository;
	}

	/**
	 * @return Returns the expectedWC.
	 */
	public ExpectedWC getExpectedWC() {
		return expectedWC;
	}

	/**
	 * @return Returns the reposDirectory.
	 */
	public File getReposDirectory() {
		return reposDirectory;
	}
        
}
