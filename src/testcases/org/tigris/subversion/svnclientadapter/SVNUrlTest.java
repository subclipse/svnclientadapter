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
    }
    
    
    public void testCaseInsensitiveHttpProtocols() throws Exception {
       SVNUrl https = new SVNUrl("HTTPS://svn.collab.net/repos/subclipse/");
       SVNUrl http = new SVNUrl("HTTP://svn.collab.net/repos/subclipse/");
    }
    
    public void testGetFile() throws Exception {
       SVNUrl url1 = new SVNUrl("http://svn.collab.net/repos/subclipse/myfile.txt");
       assertEquals("myfile.txt",url1.getFile());
       SVNUrl url2 = new SVNUrl("http://svn.collab.net/repos/subclipse/");
       assertEquals("",url2.getFile());
    }
}
