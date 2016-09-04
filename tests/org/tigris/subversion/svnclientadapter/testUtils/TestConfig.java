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
