package org.tigris.subversion.svnclientadapter;

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
    
}
