/*******************************************************************************
 * Copyright (c) 2005, 2006 svnClientAdapter project and others.
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

import org.tigris.subversion.svnclientadapter.utils.SVNUrlUtils;

import junit.framework.TestCase;

/**
 */
public class SVNUrlUtilsTest extends TestCase {

    public void testGetCommonRootUrl() throws Exception {
        SVNUrl[] urls = new SVNUrl[] {
                new SVNUrl("http://svn.collab.net/repos/subclipse/myfile.txt"),
                new SVNUrl("http://svn.collab.net:80/repos/subclipse/myfile2.txt"),
                new SVNUrl("HTTP://svn.collab.net/repos/subclipse/mydir/myfile.txt"),
                new SVNUrl("http://svn.collab.net/repos/subclipse/mydir/mydir2/myfile.txt")
        };
        assertEquals("http://svn.collab.net/repos/subclipse", SVNUrlUtils.getCommonRootUrl(urls).toString());
    }

    public void testGetCommonRootUrlNoRoot() throws Exception {
        SVNUrl[] urls = new SVNUrl[] {
                new SVNUrl("http://svn.collab.net:81/repos/subclipse/myfile.txt"),
                new SVNUrl("http://svn.collab.net:80/repos/subclipse/myfile2.txt"),
                new SVNUrl("HTTP://svn.collab.net/repos/subclipse/mydir/myfile.txt"),
                new SVNUrl("http://svn.collab.net/repos/subclipse/mydir/mydir2/myfile.txt")
        };
        assertEquals(null, SVNUrlUtils.getCommonRootUrl(urls));
    }
    
    public void testRelativePath() throws Exception {
        SVNUrl url = new SVNUrl("http://svn.collab.net:81/repos/subclipse/myfile.txt");
        SVNUrl rootUrl = new SVNUrl("http://svn.collab.net:81/repos");
        assertEquals("subclipse/myfile.txt",SVNUrlUtils.getRelativePath(rootUrl,url));
		assertEquals("/subclipse/myfile.txt", SVNUrlUtils.getRelativePath(rootUrl, url, true));
		assertEquals("/subclipse/myfile.txt", url.toString().substring(rootUrl.toString().length()));
    }
    
    public void testGetUrlFromLocalFileName() throws Exception {
        SVNUrl rootUrl = new SVNUrl("http://svn.collab.net:81/repos/mydir");
        String rootPath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir";
        String filePath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir\\mydir2\\myFile.txt";
        SVNUrl expected = new SVNUrl("http://svn.collab.net:81/repos/mydir/mydir2/myFile.txt");
        assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));

        rootUrl = new SVNUrl("http://svn.collab.net:81/repos/mydir/");
        assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));

        rootPath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir\\";
        assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));
        rootUrl = new SVNUrl("http://svn.collab.net:81/repos/mydir");

        rootPath = "C:/Documents and Settings/User/My Documents/Eclipse/mydir";
        filePath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir\\mydir2\\myFile.txt";
        assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));

        rootPath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir";
        filePath = "C:/Documents and Settings/User/My Documents/Eclipse/mydir/mydir2/myFile.txt";
        assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));

        rootPath = "C:/Documents and Settings/User/My Documents/Eclipse/mydir/";
        assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));

        expected = new SVNUrl("http://svn.collab.net:81/repos/mydir");
        rootUrl = new SVNUrl("http://svn.collab.net:81/repos/mydir");
        rootPath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir";
        filePath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir";
        assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));
    }
    
}
