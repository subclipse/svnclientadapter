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
 * Utility class
 */
public class SVNUrlUtils {

    /**
     * get the common root url for given urls
     * @param url1
     * @param url2
     * @return
     */
    public static SVNUrl getCommonRootUrl(SVNUrl url1, SVNUrl url2) {
        if ( (!url1.getProtocol().equals(url2.getProtocol())) ||
             (!url1.getHost().equals(url2.getHost())) ||
             (url1.getPort() != url2.getPort()) ) {
            return null;
        }
        String url = url1.getProtocol()+"://"+url1.getHost()+":"+url1.getPort(); 
        String[] segs1 = url1.getPathSegments();
        String[] segs2 = url2.getPathSegments();
        int minLength = segs1.length >= segs2.length ? segs2.length : segs1.length;
        for (int i = 0; i < minLength; i++) {
            if (!segs1[i].equals(segs2[i])) {
                break;
            }
            url+="/"+segs1[i];
        }
        try {
            return new SVNUrl(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    /**
     * get the common root url for given urls
     * @param urls
     * @return
     */
    public static SVNUrl getCommonRootUrl(SVNUrl urls[]) {
        SVNUrl commonRoot = urls[0];
        for (int i = 0; i < urls.length; i++) {
            commonRoot = getCommonRootUrl(commonRoot, urls[i]);
            if (commonRoot == null) {
                return null;
            }
        }
        return commonRoot;
    }

    /**
     * get path of url relative to rootUrl 
     * @param rootUrl
     * @param url
     * @return relative path or null if rootUrl is not a parent of url
     */
    public static String getRelativePath(SVNUrl rootUrl, SVNUrl url) {
        String rootUrlStr = rootUrl.toString();
        String urlStr = url.toString();
        if (urlStr.indexOf(rootUrlStr) == -1) {
            return null;
        }
        if (urlStr.length() == rootUrlStr.length()) {
            return "";
        }
        return urlStr.substring(rootUrlStr.length()+1);
    }
    
    
    /**
     * Get url representing the fileName of workink copy.
     * Use the parent's (not necesarily direct parent) WC fileName and SVNUrl to calculate it.
     * E.g.
     * <code>
     *   SVNUrl rootUrl = new SVNUrl("http://svn.collab.net:81/repos/mydir");
     *   String rootPath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir";
     *   String filePath = "C:\\Documents and Settings\\User\\My Documents\\Eclipse\\mydir\\mydir2\\myFile.txt";
     *   SVNUrl expected = new SVNUrl("http://svn.collab.net:81/repos/mydir/mydir2/myFile.txt");
     *   assertEquals(expected,SVNUrlUtils.getUrlFromLocalFileName(filePath, rootUrl, rootPath));
     * </code>
     *  
     * @param localFileName name of the file representing working copy of resource
     * @param parentUrl svnUrl of a resource preceeding the localFileName in hierarchy
     * @param parentPathName WC fileName of a resource preceeding the localFileName in hierarchy
     * @return
     */
    public static SVNUrl getUrlFromLocalFileName(String localFileName, SVNUrl parentUrl, String parentPathName)
    {
    	String parentPath = (parentPathName.indexOf('\\') > 0) ? parentPathName.replaceAll("\\\\","/") : parentPathName;
    	String localFile = (localFileName.indexOf('\\') > 0) ? localFileName.replaceAll("\\\\","/") : localFileName;
    	if (localFile.indexOf(parentPath) != 0) return null;
    	char lastChar = parentPath.charAt(parentPath.length() - 1);
    	String relativeFileName = localFile.substring(parentPath.length() + (((lastChar != '\\') && (lastChar != '/')) ? 1 : 0));
    	String urlPrefix = parentUrl.toString();
        try {
        	if (urlPrefix.charAt(urlPrefix.length()-1) == '/')
        	{
        		return new SVNUrl(urlPrefix + relativeFileName);
        	}
        	else
        	{
        		return new SVNUrl(urlPrefix + "/" + relativeFileName);    	
        	}
        } catch (MalformedURLException e) {
            return null;
        }    	
    }
}
