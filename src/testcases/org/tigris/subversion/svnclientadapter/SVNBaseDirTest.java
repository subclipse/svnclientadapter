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
