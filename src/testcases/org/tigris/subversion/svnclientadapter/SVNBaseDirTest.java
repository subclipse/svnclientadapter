
package org.tigris.subversion.svnclientadapter;

import java.io.File;

import junit.framework.TestCase;

/**
 * This class tests SVNBaseDir
 */
public class SVNBaseDirTest extends TestCase {

	public void testBaseDir() throws Exception {
		
		File workingCopy =  new File("/home/cedric/programmation/sources/test");
		File currentDir = new File("/home/cedric/projects/subversion/subclipse");		
		File baseDir = SVNBaseDir.getCommonPart(workingCopy, currentDir);
		assertEquals(new File("/home/cedric/").getCanonicalFile(), baseDir);
		
		// there was a bug before : it returned /home/cedric/programmation/projets/subversion ...
		workingCopy = new File("/home/cedric/programmation/projets/subversion/svnant/test/svn/workingcopy/listenerTest");
		currentDir = new File("/home/cedric/programmation/projets/subversion/svnant/");
		baseDir = SVNBaseDir.getCommonPart(workingCopy, currentDir);
		assertEquals(new File("/home/cedric/programmation/projets/subversion/svnant/").getCanonicalFile(), baseDir);			
	}

}
