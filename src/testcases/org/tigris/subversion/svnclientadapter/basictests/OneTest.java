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

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.utils.SvnServer;

/**
 * this internal class represent the repository and the working copy for one
 * test.
 */
public class OneTest {
	protected String testName;

    protected TestsConfig testsConfig = TestsConfig.getTestsConfig();
    
	/**
	 * the file name of repository (used by SVNAdmin)
	 */
	protected File repository;

	/**
	 * the file name of the working copy directory
	 */
	protected File workingCopy;

	/**
	 * the url of the repository (used by SVNClient)
	 */
	protected SVNUrl url;

	/**
	 * the expected layout of the working copy after the next subversion command
	 */
	protected ExpectedWC wc;

    protected TestConfig config;
    
	/**
	 * build a new test setup with a new repository, a new working and a new
	 * expected working layout
	 * 
	 * @throws Exception
	 */
	protected OneTest(String testName, TestConfig config) throws Exception {
		this.testName = testName;
        this.config = config;
		this.wc = config.sampleWC.copy();
		repository = createStartRepository(testName);
		url = testsConfig.makeReposUrl(repository);
		workingCopy = createStartWorkingCopy(repository, testName);
	}

	/**
	 * Copy the working copy and the expected working copy layout for tests
	 * which need multiple working copy
	 * 
	 * @param append
	 *            append to the working copy name of the original
	 * @return second test object.
	 * @throws Exception
	 */
	protected OneTest copy(String append) throws Exception {
		return new OneTest(this, append);
	}

	/**
	 * constructor for create a copy
	 * 
	 * @param orig
	 *            original test
	 * @param append
	 *            append this to the directory name of the original test
	 * @throws Exception
	 */
	private OneTest(OneTest orig, String append) throws Exception {
		this.testName = orig.testName + append;
		repository = orig.getRepository();
        config = orig.getTestConfig();
		url = orig.getUrl();
		wc = orig.wc.copy();
		workingCopy = createStartWorkingCopy(repository, testName);
	}

	/**
	 * Return the directory of the repository
	 * 
	 * @return the repository directory name
	 */
	public File getRepository() {
		return repository;
	}

	/**
	 * Return the name of the directory of the repository
	 * 
	 * @return the name of repository directory
	 */
	public String getRepositoryPath() {
		return repository.getAbsolutePath();
	}

	/**
	 * Return the working copy directory
	 * 
	 * @return the working copy directory
	 */
	public File getWorkingCopy() {
		return workingCopy;
	}

	/**
	 * Return the working copy directory name
	 * 
	 * @return the name of the working copy directory
	 */
	public File getWCPath() {
		return workingCopy.getAbsoluteFile();
	}

	/**
	 * Returns the url of repository
	 * 
	 * @return the url
	 */
	public SVNUrl getUrl() {
		return url;
	}

	/**
	 * Returns the expected working copy content
	 * 
	 * @return the expected working copy content
	 */
	public ExpectedWC getWc() {
		return wc;
	}
    
	/**
	 * @return Returns the testsConfig.
	 */
	public TestConfig getTestConfig() {
		return config;
	}
    
	/**
	 * Create the repository for the beginning of the test
	 * 
	 * @param testName
	 *            the name of the test
	 * @return the repository directory
	 * @throws Exception
	 */
	protected File createStartRepository(String testName) throws Exception {
		// build a clean repository directory
		File repos = new File(testsConfig.repositories, testName);
		FileUtils.removeDirectoryWithContent(repos);
		FileUtils.copyFiles(config.sampleRepos, repos);

		return repos;
	}

	/**
	 * Create the working copy for the beginning of the test
	 * 
	 * @param repos
	 *            the repository directory
	 * @param testName
	 *            the name of the test
	 * @return the directory of the working copy
	 * @throws Exception
	 */
	protected File createStartWorkingCopy(File repos, String testName)
			throws Exception {
		// build a clean working directory
		SVNUrl url = testsConfig.makeReposUrl(repos);
		workingCopy = new File(testsConfig.workingCopies, testName);
		FileUtils.removeDirectoryWithContent(workingCopy);
		// checkout the repository
		config.client.checkout(url, workingCopy, SVNRevision.HEAD, true);
		// sanity check the working with its expected status
		checkStatus();
		return workingCopy;
	}

	/**
	 * Check if the working copy has the expected status
	 * 
	 * @throws Exception
	 */
	public void checkStatus() throws Exception {
		ISVNStatus[] states = config.client.getStatus(workingCopy, true, true);
		wc.check(states, workingCopy.getAbsolutePath());
	}
}

