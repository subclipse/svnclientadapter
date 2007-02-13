/*******************************************************************************
 * Copyright (c) 2005, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter;

import java.io.File;
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
}
