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

    private static SVNUrl getCommonRootUrl(SVNUrl url1, SVNUrl url2) {
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
        int max = 0;
        SVNUrl commonRoot = urls[0];
        for (int i = 0; i < urls.length; i++) {
            commonRoot = getCommonRootUrl(commonRoot, urls[i]);
            if (commonRoot == null) {
                return null;
            }
        }
        return commonRoot;
    }
    
}
