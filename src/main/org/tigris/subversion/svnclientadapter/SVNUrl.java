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
 * An SVNUrl is immutable. 
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 *
 */
public class SVNUrl {
    private String protocol; // http, file, svn or svn+ssh
    private String[] segments;
    private String host;
    private int port;

    public SVNUrl(String svnUrl) throws MalformedURLException {
        if(svnUrl == null)
            throw new MalformedURLException("Svn url cannot be null. Is this  a versioned resource?");
   
        parseUrl(svnUrl);
    }

    /**
     * verifies that the url is correct
     * @throws MalformedURLException
     */
    private void parseUrl(String svnUrl) throws MalformedURLException{
        String parsed = svnUrl;

        // SVNUrl have this format :
        // scheme://host[:port]/path
        
        // parse protocol
        int i = parsed.indexOf("://");
        if (i == -1)
            throw new MalformedURLException("Invalid svn url :"+svnUrl);
        protocol = parsed.substring(0,i).toLowerCase();
        if ((!protocol.equalsIgnoreCase("http")) &&
            (!protocol.equalsIgnoreCase("https")) &&
            (!protocol.equalsIgnoreCase("file")) &&
            (!protocol.equalsIgnoreCase("svn")) &&
            (!protocol.equalsIgnoreCase("svn+ssh")) ) {
            throw new MalformedURLException("Invalid svn url :"+svnUrl);
        }
        parsed = parsed.substring(i+3);
		if (parsed.length() == 0) {
			throw new MalformedURLException("Invalid svn url :"+svnUrl);
		}

        // parse host & port        
        i = parsed.indexOf("/");
        if (i == -1) {
            i = parsed.length();
        }
        String hostPort = parsed.substring(0,i).toLowerCase();
        String[] hostportArray = StringUtils.split(hostPort,':');
        if (hostportArray.length == 2) {
            this.host = hostportArray[0];
            try {
                this.port = Integer.parseInt(hostportArray[1]);
            } catch (NumberFormatException e) {
                throw new MalformedURLException("Invalid svn url :"+svnUrl);
            }
        } else {
            this.host = hostportArray[0];
            this.port = getDefaultPort(protocol);
        }
        
        // parse path
        if (i < parsed.length()) {
            parsed = parsed.substring(i+1);
        } else {
            parsed = "";
        }
        segments = StringUtils.split(parsed,'/');
    }

    /**
     * get the default port for given protocol
     * @param protocol
     * @return port number or -1 if protocol is unknown
     */
    public static int getDefaultPort(String protocol) {
        int port = -1;
        if ("svn".equals(protocol)) {
            port = 3690;
        } else if ("http".equals(protocol)) {
            port = 80;
        } else if ("https".equals(protocol)) {
            port = 443;
        } else if ("svn+ssh".equals(protocol)) {
            port = 22;
        }
        return port;
    }
    
    /**
     * get the url. The url returned never ends with "/"
     * @return
     */
    public String get() {
        String result = getProtocol()+"://"+getHost(); 
        if (getPort() != getDefaultPort(getProtocol())) {
            result += ':'+getPort();
        }
        String[] segments = getPathSegments();
        for (int i = 0; i < segments.length;i++) {
            result+='/'+segments[i];
        }
        return result; 
    }
    
    /**
     * get the protocol
     * @return either http, https, file, svn or svn+ssh
     */
    public String getProtocol() {
        return protocol;
    }
    
    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }
    /**
     * @return Returns the port.
     */
    public int getPort() {
        return port;
    }
    public String toString() {
        return get();
    }
    
    /**
     * get the path of the url. 
     * @return
     */
    public String[] getPathSegments() {
    	return segments;
    }
    
    public String getLastPathSegment() {
    	return segments[segments.length-1];
    }
    
    /**
     * 
     * @return the parent url or null if no parent
     */
    public SVNUrl getParent() {
    	try {
    		String url = get();
    		
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
	    // url before repository is not always case sensitive
		if (this == target)
			return true;
		if (!(target instanceof SVNUrl))
			return false;
		SVNUrl url = (SVNUrl) target;
		return get().equals(url.get());
	}
}
