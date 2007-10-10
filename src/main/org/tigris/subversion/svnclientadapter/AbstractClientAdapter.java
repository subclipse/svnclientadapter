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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Default implementation of some of the methods of ISVNClientAdapter
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org)
 * @author Panagiotis Korros (pkorros at bigfoot.com)   
 */
public abstract class AbstractClientAdapter implements ISVNClientAdapter {

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setKeywords(java.io.File, org.tigris.subversion.svnclientadapter.SVNKeywords, boolean)
     */
    public void setKeywords(File path, SVNKeywords keywords, boolean recurse) throws SVNClientException {
        propertySet(path, ISVNProperty.KEYWORDS, keywords.toString(), recurse);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addKeywords(java.io.File, org.tigris.subversion.svnclientadapter.SVNKeywords)
     */
    public SVNKeywords addKeywords(File path, SVNKeywords keywords) throws SVNClientException {
        SVNKeywords currentKeywords = getKeywords(path);
        if (keywords.isHeadUrl())
            currentKeywords.setHeadUrl(true);
        if (keywords.isId())
            currentKeywords.setId(true);
        if (keywords.isLastChangedBy())
            currentKeywords.setLastChangedBy(true);
        if (keywords.isLastChangedDate())
            currentKeywords.setLastChangedBy(true);
        if (keywords.isLastChangedRevision())
            currentKeywords.setLastChangedRevision(true);
        setKeywords(path,currentKeywords,false);
        
        return currentKeywords;                
    }    

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#removeKeywords(java.io.File, org.tigris.subversion.svnclientadapter.SVNKeywords)
     */
    public SVNKeywords removeKeywords(File path, SVNKeywords keywords) throws SVNClientException {
        SVNKeywords currentKeywords = getKeywords(path);
        if (keywords.isHeadUrl())
            currentKeywords.setHeadUrl(false);
        if (keywords.isId())
            currentKeywords.setId(false);
        if (keywords.isLastChangedBy())
            currentKeywords.setLastChangedBy(false);
        if (keywords.isLastChangedDate())
            currentKeywords.setLastChangedBy(false);
        if (keywords.isLastChangedRevision())
            currentKeywords.setLastChangedRevision(false);
        setKeywords(path,currentKeywords,false);
        
        return currentKeywords;                
    }    

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getIgnoredPatterns(java.io.File)
     */
    public List getIgnoredPatterns(File path) throws SVNClientException {
        if (!path.isDirectory())
            return null;
        List list = new ArrayList();
        ISVNProperty pd = propertyGet(path, ISVNProperty.IGNORE);
        if (pd == null)
            return list;
        String patterns = pd.getValue();
        StringTokenizer st = new StringTokenizer(patterns,"\n\r");
        while (st.hasMoreTokens()) {
            String entry = st.nextToken();
            if ((entry != null) && (entry.length() > 0)) {
                list.add(entry);
            }
        }
        return list;
    }
    
    /* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getLogMessages(java.io.File, org.tigris.subversion.subclipse.client.ISVNRevision, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public ISVNLogMessage[] getLogMessages(File arg0, SVNRevision arg1, SVNRevision arg2)
		throws SVNClientException {
		return getLogMessages(arg0, arg1, arg2, true);
	}
    
    /* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getLogMessages(java.net.URL, org.tigris.subversion.subclipse.client.ISVNRevision, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public ISVNLogMessage[] getLogMessages(SVNUrl arg0, SVNRevision arg1, SVNRevision arg2)
		throws SVNClientException {
		return getLogMessages(arg0, arg1, arg2, true);
	}
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setIgnoredPatterns(java.io.File, java.util.List)
     */
    public void setIgnoredPatterns(File path, List patterns) throws SVNClientException {
        if (!path.isDirectory())
            return;
        String separator = System.getProperty("line.separator");
        StringBuffer value = new StringBuffer();
        for (Iterator it = patterns.iterator(); it.hasNext();) {
            String pattern = (String)it.next();
            value.append(pattern + separator);
        }
        propertySet(path, ISVNProperty.IGNORE, value.toString(), false);
    }    

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addToIgnoredPatterns(java.io.File, java.lang.String)
     */
    public void addToIgnoredPatterns(File path, String pattern)  throws SVNClientException {
        List patterns = getIgnoredPatterns(path);
        if (patterns == null) // not a directory
            return;
 
        // verify that the pattern has not already been added
        for (Iterator it = patterns.iterator(); it.hasNext();) {
            if (((String)it.next()).equals(pattern))
                return; // already added
        }
            
        patterns.add(pattern);
        setIgnoredPatterns(path,patterns);
    }    

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getKeywords(java.io.File)
     */
    public SVNKeywords getKeywords(File path) throws SVNClientException {
        ISVNProperty prop = propertyGet(path, ISVNProperty.KEYWORDS);
        if (prop == null)
            return new SVNKeywords(); 

        // value is a space-delimited list of the keywords names
        String value = prop.getValue();
        
        return new SVNKeywords(value);
    }    
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addPasswordCallback(org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword)
     */
    public void addPasswordCallback(ISVNPromptUserPassword callback) {
        // Default implementation does nothing
    }
 
    public boolean statusReturnsRemoteInfo() {
         return false;
    }
    public long[] commitAcrossWC(File[] paths, String message, boolean recurse,
            boolean keepLocks, boolean Atomic) throws SVNClientException {
        notImplementedYet();
        return null;
    }
    
    protected void notImplementedYet() throws SVNClientException {
        throw new SVNClientException("Not implemented yet");
    }

    public boolean canCommitAcrossWC() {
        return false;
    }
    
    public void mkdir(SVNUrl url, boolean makeParents, String message)
            throws SVNClientException {
        if (makeParents) {
            SVNUrl parent = url.getParent();
            if (parent != null) {
		        ISVNInfo info = null;
		        try {
		            info = this.getInfo(parent);
		        } catch (SVNClientException e) {
		        }
		        if (info == null)
		            this.mkdir(parent, makeParents, message);
            }
        }
        this.mkdir(url, message);
    }
    

	/**
	 * Answer whether running on Windows OS.
	 * (Actual code extracted from org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS)
	 * (For such one simple method it does make sense to introduce dependency on whole commons-lang.jar)
	 * @return true when the underlying 
	 */
	public static boolean isOsWindows()
	{
        try {
            return System.getProperty("os.name").startsWith("Windows");
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            return false;
        }
	}

	public ISVNInfo getInfo(SVNUrl url) throws SVNClientException {
		return getInfo(url, SVNRevision.HEAD, SVNRevision.HEAD);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#merge(org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, boolean, boolean, boolean)
	 */
	public void merge(SVNUrl path1, SVNRevision revision1, SVNUrl path2, SVNRevision revision2, File localPath, boolean force, boolean recurse, boolean dryRun) throws SVNClientException {
		merge(path1, revision1, path2, revision2, localPath, force, recurse, dryRun, false);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#merge(org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, boolean, boolean)
	 */
	public void merge(SVNUrl path1, SVNRevision revision1, SVNUrl path2, SVNRevision revision2, File localPath, boolean force, boolean recurse) throws SVNClientException {
		merge(path1, revision1, path2, revision2, localPath, force, recurse, false, false);
	}
	

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyGet(org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String)
	 */
	public ISVNProperty propertyGet(SVNUrl url, String propertyName)
		throws SVNClientException {
		return propertyGet(url, SVNRevision.HEAD, SVNRevision.HEAD, propertyName);
	}
	
	public void diff(File[] paths, File outFile, boolean recurse) throws SVNClientException {
		FileOutputStream os = null;
		try {
			ArrayList tempFiles = new ArrayList();
			for (int i = 0; i < paths.length; i++) {
				File tempFile = File.createTempFile("tempDiff", ".txt");
				tempFile.deleteOnExit();
				diff(paths[i], tempFile, recurse);
				tempFiles.add(tempFile);
			}
			os = new FileOutputStream(outFile);
			Iterator iter = tempFiles.iterator();
			while (iter.hasNext()) {
				File tempFile = (File)iter.next();
				FileInputStream is = new FileInputStream(tempFile);
				byte[] buffer = new byte[4096];
				int bytes_read;
				while ((bytes_read = is.read(buffer)) != -1)
					os.write(buffer, 0, bytes_read);				
				is.close();
			}
		} catch (Exception e) {
			throw new SVNClientException(e);
		} finally {
			if (os != null) try {os.close();} catch (IOException e) {}
		}
	}

	public ISVNLogMessage[] getLogMessagesForRevisions(SVNUrl url,
			SVNRevision pegRevision, SVNRevisionRange[] range,
			boolean fetchChangePath, boolean includeMergedRevisions) throws SVNClientException {
		if (range == null || range.length == 0) {
			return new ISVNLogMessage[0];
		}
		SVNRevision revisionStart = range[0].getFromRevision();
		SVNRevision revisionEnd = range[range.length - 1].getToRevision();
		boolean stopOnCopy = false;
		int limit = 0;
		ISVNLogMessage[] messages = getLogMessages(url, pegRevision,
				revisionStart, revisionEnd, stopOnCopy, fetchChangePath, limit, includeMergedRevisions);
		return applyFilterToLogs(range, messages);
	}

	private ISVNLogMessage[] applyFilterToLogs(SVNRevisionRange[] range,
			ISVNLogMessage[] messages) {
		List msgList = new ArrayList();
		boolean inclusiveFromRev = false;
		for (int i = 0; i < messages.length; i++) {
			for (int j = 0; j < range.length; j++) {
				if (range[j].contains(messages[i].getRevision(), inclusiveFromRev)) {
					msgList.add(messages[i]);
					break;
				}
			}
		}
		ISVNLogMessage[] msgArray = new ISVNLogMessage[msgList.size()];
		msgList.toArray(msgArray);
		return msgArray;
	}	

}
