/*
 *  Copyright(c) 2003-2004 by the authors indicated in the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tigris.subversion.svnclientadapter;

import java.net.MalformedURLException;

/**
 *
 * we could have used URL, using custom protocols (svn, svn+ssl) 
 * (@see http://developer.java.sun.com/developer/onlineTraining/protocolhandlers/)
 * but this is not really necessary as we don't want to open a connection 
 * directly with this class.
 * We just want a string which represent a SVN url which can be used with our JNI
 * methods.
 *
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 *
 */
public class SVNUrl {
    private String svnUrl;
    private String protocol; // http, file, svn or svn+ssh
    private String[] segments;

    public SVNUrl(String svnUrl) throws MalformedURLException {
        if(svnUrl == null)
            throw new MalformedURLException("Svn url cannot be null. Is this  a versioned resource?");
        this.svnUrl = svnUrl;
        
        // we make sure the url does not end with "/" because
        // in svn 1.0.2 (at least) if a non-canonical path is passed to 
        // svn_path_join(base, component, pool), the assertion "assert (is_canonical (base, blen));" will fail
        if (this.svnUrl.endsWith("/")) { // remove ending "/" if any
        	this.svnUrl = this.svnUrl.substring(0,this.svnUrl.length()-1);
		}
        
        parseUrl();
    }

    /**
     * verifies that the url is correct
     * @throws MalformedURLException
     */
    private void parseUrl() throws MalformedURLException{
        // for now, we don't verify the url, we let subversion do it
        // we just make sure the protocol is one we support
        // (scheme)://(optional_stuff)

        int i = svnUrl.indexOf("://");
        if (i == -1)
            throw new MalformedURLException("Invalid svn url :"+svnUrl);
        protocol = svnUrl.substring(0,i).toLowerCase();
        if ((!protocol.equalsIgnoreCase("http")) &&
            (!protocol.equalsIgnoreCase("https")) &&
            (!protocol.equalsIgnoreCase("file")) &&
            (!protocol.equalsIgnoreCase("svn")) &&
            (!protocol.equalsIgnoreCase("svn+ssh")) ) {
            throw new MalformedURLException("Invalid svn url :"+svnUrl);
        }
        String toSplit = svnUrl.substring(i+3);
		if (toSplit.length() == 0) {
			throw new MalformedURLException("Invalid svn url :"+svnUrl);
		}        
        segments = StringUtils.split(toSplit,'/');
    }

    public String get() {
        return svnUrl;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public String toString() {
        return get();
    }
    
    public String getSegment(int i) {
    	return segments[i];
    }
    
    public String[] getSegments() {
    	return segments;
    }
    
    public String getLastSegment() {
    	return segments[segments.length-1];
    }
    
    /**
     * 
     * @return the parent url or null if no parent
     */
    public SVNUrl getParent() {
    	try {
    		String url = svnUrl;
    		if (url.endsWith("/")) { // remove ending "/" if any
    			url = url.substring(0,url.length()-1);
    		}
    		
    		return new SVNUrl(url.substring(0,url.lastIndexOf('/')));
    	} catch (MalformedURLException e) {
    		return null;
    	}
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object target) {
	    // this method is not very accurate because :
	    // protocol is not case-sensitive
	    // url before repository is not always case sensitive
	    // url after repository is case sensitive
		if (this == target)
			return true;
		if (!(target instanceof SVNUrl))
			return false;
		SVNUrl url = (SVNUrl) target;
		return get().equals(url.get());
	}
}
