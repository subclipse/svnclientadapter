package org.tigris.subversion.svnclientadapter;

import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * This class tests SVNUrl
 * Note that tests for SVNClientAdapter itself are in svnant 
 */
public class SVNUrlTest extends TestCase
{

   public void testNullURLThrowsInvalidURLException() throws Exception
   {
        try
        {
            SVNUrl svnurl= new SVNUrl(null);
            fail("should have thrown malformed url exeption.");
        }
        catch (MalformedURLException e)
        {

        }
    }
    
    public void testHttpsURL() throws Exception {
        SVNUrl https = new SVNUrl("https://svn.collab.net/repos/subclipse/");
        assertEquals("https://svn.collab.net/repos/subclipse",https.toString());
    }
    
    
    public void testCaseInsensitiveHttpProtocols() throws Exception {
       SVNUrl https = new SVNUrl("HTTPS://svn.collab.net/repos/subclipse/");
       SVNUrl http = new SVNUrl("HTTP://svn.collab.net/repos/subclipse/");
    }
    
    public void testGetParent() throws Exception {
		SVNUrl url1 = new SVNUrl("http://svn.collab.net/repos/subclipse/myfile.txt");
		assertEquals("http://svn.collab.net/repos/subclipse",url1.getParent().toString());
		assertEquals("http://svn.collab.net/repos",url1.getParent().getParent().toString());		  		
		assertEquals("http://svn.collab.net",url1.getParent().getParent().getParent().toString());
		assertEquals(null,url1.getParent().getParent().getParent().getParent());
    }
    
    public void testSegments() throws Exception {
		SVNUrl url1 = new SVNUrl("http://svn.collab.net/repos/subclipse/myfile.txt");
		assertEquals(4, url1.getSegments().length);
		assertEquals("svn.collab.net", url1.getSegment(0));
		assertEquals("repos", url1.getSegment(1));
		assertEquals("subclipse", url1.getSegment(2));
		assertEquals("myfile.txt", url1.getSegment(3));
		
		assertEquals(3, url1.getParent().getSegments().length);
    }
    
    public void testHostPort() throws Exception {
        SVNUrl url1 = new SVNUrl("http://svn.collab.net:8080/repos/subclipse/myfile.txt");
        assertEquals("svn.collab.net",url1.getHost());
        assertEquals(8080,url1.getPort());
        url1 = new SVNUrl("svn+ssh://svn.collab.net/repos/subclipse/myfile.txt");
        assertEquals(22,url1.getPort());
    }
    
    public void testEquals() throws Exception {
        SVNUrl http1 = new SVNUrl("HTTP://SVN.collab.net/repos/subclipse/");
        SVNUrl http2 = new SVNUrl("http://svn.collab.net:80/repos/subclipse");
        assertEquals(http1,http2);
    }
    
}
