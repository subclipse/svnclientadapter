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
