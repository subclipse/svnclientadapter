/*******************************************************************************
 * Copyright (c) 2003, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
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
            new SVNUrl(null);
            fail("should have thrown malformed url exeption.");
        }
        catch (MalformedURLException e)
        {

        }
    }

   public void testNullSegmentsThrowsInvalidURLException() throws Exception
   {
        try
        {
            new SVNUrl("http:///");
            fail("should have thrown malformed url exeption.");
        }
        catch (MalformedURLException e)
        {

        }
    }

    public void testHostPortUrl() throws Exception {
        SVNUrl svnurl= new SVNUrl("https://svn.collab.net");
        assertEquals("https://svn.collab.net",svnurl.toString());
        svnurl= new SVNUrl("https://svn.collab.net/");
        assertEquals("https://svn.collab.net",svnurl.toString());
    }
   
    public void testHttpsURL() throws Exception {
        SVNUrl https = new SVNUrl("https://svn.collab.net/repos/subclipse/");
        assertEquals("https://svn.collab.net/repos/subclipse",https.toString());
    }
    
    
    public void testCaseInsensitiveHttpProtocols() throws Exception {
       SVNUrl https = new SVNUrl("HTTPS://svn.collab.net/repos/subclipse/");
       assertEquals("https://svn.collab.net/repos/subclipse",https.toString());
       SVNUrl http = new SVNUrl("HTTP://svn.collab.net/repos/subclipse/");
       assertEquals("http://svn.collab.net/repos/subclipse",http.toString());
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
		assertEquals(3, url1.getPathSegments().length);
		assertEquals("repos", url1.getPathSegments()[0]);
		assertEquals("subclipse", url1.getPathSegments()[1]);
		assertEquals("myfile.txt", url1.getPathSegments()[2]);
		
		assertEquals(2, url1.getParent().getPathSegments().length);
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
    
    public void testNonStandardPort() throws Exception {
        SVNUrl svnurl= new SVNUrl("http://svn.collab.net:8080/repos/subclipse/myfile.txt");
        assertEquals("http://svn.collab.net:8080/repos/subclipse/myfile.txt",svnurl.toString());
    }
    
    public void testFileURL() throws Exception {
        SVNUrl svnurl= new SVNUrl("file:///repos/subclipse/myfile.txt");
        assertEquals("file:///repos/subclipse/myfile.txt",svnurl.toString());
        SVNUrl svnurl2= new SVNUrl("file://d/repos/subclipse/myfile.txt");
        assertEquals("file://d/repos/subclipse/myfile.txt",svnurl2.toString());
        SVNUrl svnurl3= new SVNUrl("file://repos/subclipse/myfile.txt");
        assertEquals("file://repos/subclipse/myfile.txt",svnurl3.toString());
        SVNUrl svnurl4= new SVNUrl("file:///f:/svn/trunk");
        assertEquals("file:///f:/svn/trunk",svnurl4.toString());
		SVNUrl url1 = new SVNUrl("file:///repos/subclipse/myfile.txt");
		assertEquals("file:///repos/subclipse",url1.getParent().toString());
		assertEquals("file:///repos",url1.getParent().getParent().toString());		  		
		assertEquals(null,url1.getParent().getParent().getParent());
   }
    
	public void testUtilGetLastSegment() throws Exception 
	{
        SVNUrl http1 = new SVNUrl("HTTP://SVN.collab.net/repos/subclipse/");
        SVNUrl http2 = new SVNUrl("http://svn.collab.net:80/repos/subclipse");
        SVNUrl http3 = new SVNUrl("http://svn.collab.net/");
        SVNUrl svn1 = new SVNUrl("svn://svn.collab.net/repos/subclipse");
        SVNUrl svn2 = new SVNUrl("svn://svn.collab.net/");
		assertEquals("subclipse", http1.getLastPathSegment());
		assertEquals("subclipse", http2.getLastPathSegment());
		assertEquals("", http3.getLastPathSegment());
		assertEquals("subclipse", svn1.getLastPathSegment());
		assertEquals("", svn2.getLastPathSegment());
	}

	public void testAppendPath() throws Exception
	{
        SVNUrl http1 = new SVNUrl("HTTP://SVN.collab.net/repos/subclipse/");
        SVNUrl http2 = new SVNUrl("HTTP://SVN.collab.net/repos/subclipse/mydirectory/myfile.txt");
		assertEquals(http2, http1.appendPath("/mydirectory/myfile.txt"));
		assertEquals(http2, http1.appendPath("mydirectory/myfile.txt"));
		assertEquals(http1, http1.appendPath(""));
	}

}
