/**
 * @copyright ====================================================================
 *            Copyright (c) 2003-2004 CollabNet. All rights reserved.
 * 
 * This software is licensed as described in the file COPYING, which you should
 * have received as part of this distribution. The terms are also available at
 * http://subversion.tigris.org/license-1.html. If newer versions of this
 * license are posted there, you may use a newer version instead, at your
 * option.
 * 
 * This software consists of voluntary contributions made by many individuals.
 * For exact contribution history, see the revision history and logs, available
 * at http://subversion.tigris.org/.
 * ====================================================================
 * @endcopyright
 */
package org.tigris.subversion.svnclientadapter.javasvn;

import java.io.File;

import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.basictests.OneTest;
import org.tigris.subversion.svnclientadapter.basictests.SVNTest;
import org.tmatesoft.svn.core.ISVNWorkspace;
import org.tmatesoft.svn.core.io.SVNRepository;

public class JavaSvnUtilsTest extends SVNTest {

    public void testJavaSvnUtils() throws Throwable {
        // build the test setup
        OneTest thisTest = new OneTest("javasvnUtils", getGreekTestConfig());

        JavaSvnUtils.setupJavaSvn();

        JavaSvnConfig config = new JavaSvnConfig();
        config.setUsername("cedric");
        config.setPassword("cedricpass");

        // getRootWorkspace
        File file = new File(thisTest.getWorkingCopy() + "/A/D/H/psi");
        ISVNWorkspace ws = JavaSvnUtils.getRootWorkspace(config, file);
        assertEquals("/repositories/javasvnUtils", ws.getLocation().getPath());

        // getWorkspacePath
        String wsPath = JavaSvnUtils.getWorkspacePath(ws, file);
        assertEquals("A/D/H/psi", wsPath);

        // getRepository
        SVNRepository repository = JavaSvnUtils.getRepository(config, ws.getLocation());
        // something like svn://localhost:3690/repositories/javasvnUtils
        assertEquals(thisTest.getUrl(),new SVNUrl(repository.getLocation().toString())); 
        
        // getRepositoryPath
        assertEquals("/A/D/H/psi",JavaSvnUtils.getRepositoryPath(repository, new SVNUrl(thisTest.getUrl()+"/A/D/H/psi")));
        
        // getRepositoryRootPath
        repository = JavaSvnUtils.getRepository(config, new SVNUrl(thisTest.getUrl()+"/A/D"));
        assertEquals("/H/psi",JavaSvnUtils.getRepositoryPath(repository, new SVNUrl(thisTest.getUrl()+"/A/D/H/psi")));
        assertEquals("/A/D/H/psi",JavaSvnUtils.getRepositoryRootPath(repository, new SVNUrl(thisTest.getUrl()+"/A/D/H/psi")));
    }
}