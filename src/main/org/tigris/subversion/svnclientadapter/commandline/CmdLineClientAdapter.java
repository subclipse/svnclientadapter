/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNAnnotations;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNKeywords;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * <p>
 * Implements a <tt>ISVNClientAdapter</tt> using the
 * Command line client. This expects the <tt>svn</tt>
 * executible to be in the path.</p>
 * 
 * @author Philip Schatz (schatz at tigris)
 */
public class CmdLineClientAdapter implements ISVNClientAdapter {

	//Fields
    private CmdLineNotificationHandler notificationHandler = new CmdLineNotificationHandler();
	private CommandLine _cmd = new CommandLine("svn",notificationHandler);
    private String version = null;
    private final static String[] checkedVersions = 
    {
        "svn, version 0.35.1 (r8050)" 
    };

	//Methods
	public static boolean isAvailable() {
		// this will need to be fixed when path to svn will be customizable 
		CommandLine cmd = new CommandLine("svn", new CmdLineNotificationHandler());
		try {
			String version = cmd.version();
    		return true;
		} catch (Exception e) {
			return false;
		}
	}
    
    /**
     * @return something like "svn, version 0.35.1 (r8050)"
     */
    public String getVersion() throws SVNClientException {
        if (version != null)
            return version;
        try {
            // we don't want to log this ...
            notificationHandler.disableLog();
            version = _cmd.version();
            int i = version.indexOf("\n\r");
            version = version.substring(0,i);
            return version;
        } catch (CmdLineException e) {
            throw SVNClientException.wrapException(e); 
        } finally {
            notificationHandler.enableLog();
        }
    }
    
    /**
     * tells if this version of svn has been tested with this version of
     * command line interface
     * @return
     */
    public boolean checkedVersion() {
        String version;
        try {
            version = getVersion();
        } catch (SVNClientException e) {
            return false;
        }
        for (int i = 0; i < checkedVersions.length;i++) {
            if (version.equals(checkedVersions[i]))
                return true;
        }
        return false;
    }
    
	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#addNotifyListener(org.tigris.subversion.subclipse.client.ISVNClientNotifyListener)
	 */
	public void addNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.add(listener);
       if (!checkedVersion()) {
            listener.logError("Warning : this version of svn has not been tested with command line client interface. Some commands will perhaps not work"); 
       }
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#removeNotifyListener(org.tigris.subversion.subclipse.client.ISVNClientNotifyListener)
	 */
	public void removeNotifyListener(ISVNNotifyListener listener) {
        notificationHandler.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getSingleStatus(java.io.File)
	 */
	public ISVNStatus getSingleStatus(File file) throws SVNClientException {
		try {
			String path = toString(file);
			String infoLine = _cmd.info(path);
			String statusLine = _cmd.status(path, false);
			return new CmdLineStatus(statusLine, infoLine);
		} catch (CmdLineException e) {
            if (e.getMessage().indexOf("is not a working copy") >= 0) {
				return new CmdLineStatusUnversioned();
			}
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getList(java.net.URL, org.tigris.subversion.subclipse.client.ISVNRevision, boolean)
	 */
	public ISVNDirEntry[] getList(SVNUrl svnUrl, SVNRevision rev, boolean flag)
		throws SVNClientException {
		List entries = new java.util.LinkedList();

		String listLine;
		try {
			listLine = _cmd.list(toString(svnUrl), toString(rev));

			StringTokenizer st = new StringTokenizer(listLine, Helper.NEWLINE);
			while (st.hasMoreTokens()) {
				String dirLine = st.nextToken();
				CmdLineRemoteDirEntry entry = new CmdLineRemoteDirEntry(toString(svnUrl), dirLine);
				entries.add(entry);
			}
			return (ISVNDirEntry[]) entries.toArray(new ISVNDirEntry[entries.size()]);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#remove(java.io.File[], boolean)
	 */
	public void remove(File[] files, boolean arg1) throws SVNClientException {
		String[] paths = new String[files.length];
		try {
			for (int i = 0; i < files.length; i++) {
				paths[i] = files[i].toString();
			}
			_cmd.delete(paths, null);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#revert(java.io.File, boolean)
	 */
	public void revert(File arg0, boolean arg1) throws SVNClientException {
		try {
			String changedFiles = _cmd.revert(new String[] { toString(arg0) }, arg1);
			refreshChangedResources(changedFiles);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}

	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getContent(java.net.SVNUrl, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public InputStream getContent(SVNUrl arg0, SVNRevision arg1) throws SVNClientException {

		try {
			InputStream content = _cmd.cat(toString(arg0), toString(arg1));

			//read byte-by-byte and put it in a vector.
			//then take the vector and fill a byteArray.
			byte[] byteArray;
			byteArray = streamToByteArray(content, false);
			return new ByteArrayInputStream(byteArray);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}

	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#mkdir(java.net.URL, java.lang.String)
	 */
	public void mkdir(SVNUrl arg0, String arg1) throws SVNClientException {
		try {
			_cmd.mkdir(toString(arg0), arg1);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getLogMessages(java.net.URL, org.tigris.subversion.subclipse.client.ISVNRevision, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public ISVNLogMessage[] getLogMessages(SVNUrl arg0, SVNRevision arg1, SVNRevision arg2)
		throws SVNClientException {
		List tempLogs = new java.util.LinkedList();
		String revRange = toString(arg1) + ":" + toString(arg2);

		try {
			String messages = _cmd.log(toString(arg0), revRange);
			return CmdLineLogMessage.createLogMessages(messages);			
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#remove(java.net.URL[], java.lang.String)
	 */
	public void remove(SVNUrl[] urls, String message) throws SVNClientException {
		String[] urlsStrings = new String[urls.length];
		for (int i = 0; i < urls.length; i++) {
			urlsStrings[i] = urls[i].toString();
		}
		try {
			_cmd.delete(urlsStrings, message);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#copy(java.net.URL, java.net.URL, java.lang.String, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public void copy(SVNUrl src, SVNUrl dest, String message, SVNRevision rev)
		throws SVNClientException {
		try {
			_cmd.copy(toString(src), toString(dest), message, toString(rev));
		} catch (CmdLineException e) {
			SVNClientException.wrapException(e);
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File, java.io.File)
     */
	public void copy(File srcPath, File destPath) throws SVNClientException {
		try {
			_cmd.copy(toString(srcPath), toString(destPath));
		} catch (CmdLineException e) {
			SVNClientException.wrapException(e);
		}
		//sometimes the dir has not yet been created.
		//wait up to 5 sec for the dir to be created.
		for (int i = 0; i < 50 && !destPath.exists(); i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e2) {
				//do nothing if interrupted
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#move(java.net.URL, java.net.URL, java.lang.String, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public void move(SVNUrl url, SVNUrl destUrl, String message, SVNRevision revision)
		throws SVNClientException {
		try {
			String changedResources =
				_cmd.move(toString(url), toString(destUrl), message, toString(revision));
			refreshChangedResources(changedResources);
		} catch (CmdLineException e) {
			SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#move(java.io.File, java.io.File, boolean)
	 */
	public void move(File file, File file2, boolean b) throws SVNClientException {
		try {
			String changedResources =
				_cmd.move(toString(file), toString(file2), null, null);
			refreshChangedResources(changedResources);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#setUsername(java.lang.String)
	 */
	public void setUsername(String string) {
		if (string == null || string.length() == 0)
			return;
		_cmd.setUsername(string);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		if (password == null)
			return;

		_cmd.setPassword(password);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#addDirectory(java.io.File, boolean)
	 */
	public void addDirectory(File file, boolean recurse) throws SVNClientException {
		try {
			_cmd.add(toString(file), recurse);
		} catch (CmdLineException e) {
			//if something is already in svn and we
			//try to add it, we get a warning.
			//ignore it.\
			if (e.getMessage().startsWith("svn: warning: "))
				return;
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#addFile(java.io.File)
	 */
	public void addFile(File file) throws SVNClientException {
		try {
			String changedResources = _cmd.add(toString(file), false);
			refreshChangedResources(changedResources);
		} catch (CmdLineException e) {
			//if something is already in svn and we
			//try to add it, we get a warning.
			//ignore it.\
			if (e.getMessage().startsWith("svn: warning: "))
				return;
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#commit(java.io.File[], java.lang.String, boolean)
	 */
	public long commit(File[] parents, String comment, boolean b) throws SVNClientException {
		String[] paths = new String[parents.length];
		for (int i = 0; i < parents.length; i++) {
			paths[i] = toString(parents[i]);
		}
		try {
			String changedResources = _cmd.checkin(paths, comment);
			return refreshChangedResources(changedResources);
		} catch (CmdLineException e) {
			if ("".equals(e.getMessage()))
				return SVNRevision.SVN_INVALID_REVNUM;
			if (e.getMessage().startsWith("svn: Attempted to lock an already-locked dir")) {
				//PHIL is this the best way to handle pending locks? (ie caused by "svn cp")
				//loop through up to 5 sec, waiting for locks
				//to be removed.
				for (int i = 0; i < 50; i++) {
					try {
						String changedResources = _cmd.checkin(paths, comment);
						return refreshChangedResources(changedResources);
					} catch (CmdLineException e1) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e2) {
							//do nothing if interrupted
						}
					}
				}
			}
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#update(java.io.File, org.tigris.subversion.subclipse.client.ISVNRevision, boolean)
	 */
	public void update(File file, SVNRevision revision, boolean b) throws SVNClientException {
		try {
			String changedResources = _cmd.update(toString(file), toString(revision));
			refreshChangedResources(changedResources);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#checkout(java.net.URL, java.io.File, org.tigris.subversion.subclipse.client.ISVNRevision, boolean)
	 */
	public void checkout(SVNUrl url, File destPath, SVNRevision revision, boolean b)
		throws SVNClientException {
		try {
			String changedResources = _cmd.checkout(toString(url), toString(destPath), toString(revision), b);
			refreshChangedResources(changedResources);

		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getStatusRecursively(java.io.File,boolean)
	 */
	public ISVNStatus[] getStatusRecursively(File file, boolean getAll) throws SVNClientException {
		String path = null;
		List statuses = new LinkedList();
		try {
			String statusLines = _cmd.recursiveStatus(toString(file));
			StringTokenizer st = new StringTokenizer(statusLines, Helper.NEWLINE);
			while (st.hasMoreTokens()) {
				String statusLine = st.nextToken();
				String infoLine = _cmd.info(statusLine.substring(7));
				CmdLineStatus status = new CmdLineStatus(statusLine, infoLine);
				statuses.add(status);
			}

			return (ISVNStatus[]) statuses.toArray(new ISVNStatus[statuses.size()]);
		} catch (CmdLineException e) {
			if (e.getMessage().startsWith("svn: Path is not a working copy directory")) {
				return new ISVNStatus[0];
			}
			throw SVNClientException.wrapException(e);
		}
	}

	private long refreshChangedResources(String changedResourcesList) {
		StringTokenizer st = new StringTokenizer(changedResourcesList, Helper.NEWLINE);
		while (st.hasMoreTokens()) {
			String line = st.nextToken();

			//check and see if we are at the last line (nothing to do)
			if (line.startsWith("At revision "))
				return Long.parseLong(line.substring(12, line.length() - 1));
			if (line.startsWith("Updated to revision "))
				return Long.parseLong(line.substring(20, line.length() - 1));
			if (line.startsWith("Committed revision "))
				return Long.parseLong(line.substring(19, line.length() - 1));
			if (line.startsWith("Checked out revision "))
				return Long.parseLong(line.substring(21, line.length() - 1));

			//Jump to the next line if we encounter this: (when checking in)
			if (line.startsWith("Transmitting file data "))
				continue;

			String fileName = line.substring(line.indexOf(' ')).trim();

			//check to see if this is a file or a dir.
			File f = new File(fileName);

            notificationHandler.notifyListenersOfChange(fileName, f.isDirectory() ? SVNNodeKind.DIR : SVNNodeKind.FILE);
		}
		return SVNRevision.SVN_INVALID_REVNUM;
	}

	private void diff(
		String oldPath,
		SVNRevision oldPathRevision,
		String newPath,
		SVNRevision newPathRevision,
		File outFile,
		boolean recurse) {
		if (newPath == null)
			newPath = oldPath;
		if (oldPathRevision == null)
			oldPathRevision = SVNRevision.BASE;
		if (newPathRevision == null)
			newPathRevision = SVNRevision.WORKING;

		try {
			InputStream is =
				_cmd.diff(
					oldPath,
					toString(oldPathRevision),
					newPath,
					toString(newPathRevision),
					recurse);

			streamToFile(is, outFile);
		} catch (IOException e) {
			//this should never happen
		} catch (CmdLineException e) {
			SVNClientException.wrapException(e);
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, boolean)
     */
	public void diff(
		File oldPath,
		SVNRevision oldPathRevision,
		File newPath,
		SVNRevision newPathRevision,
		File outFile,
		boolean recurse)
		throws SVNClientException {
		if (oldPath == null)
			oldPath = new File(".");
		diff(
			toString(oldPath),
			oldPathRevision,
			toString(newPath),
			newPathRevision,
			outFile,
			recurse);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(java.io.File, java.io.File, boolean)
     */
	public void diff(File path, File outFile, boolean recurse) throws SVNClientException {
		diff(path, null, null, null, outFile, recurse);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, boolean)
     */
	public void diff(
		SVNUrl oldUrl,
		SVNRevision oldUrlRevision,
		SVNUrl newUrl,
		SVNRevision newUrlRevision,
		File outFile,
		boolean recurse)
		throws SVNClientException {
		diff(toString(oldUrl), oldUrlRevision, toString(newUrl), newUrlRevision, outFile, recurse);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#diff(org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, org.tigris.subversion.svnclientadapter.SVNRevision, java.io.File, boolean)
     */
	public void diff(
		SVNUrl url,
		SVNRevision oldUrlRevision,
		SVNRevision newUrlRevision,
		File outFile,
		boolean recurse)
		throws SVNClientException {
		// TODO : test
		diff(url, oldUrlRevision, url, newUrlRevision, outFile, recurse);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyGet(java.io.File, java.lang.String)
     */
	public ISVNProperty propertyGet(File path, String propertyName) throws SVNClientException {
		try {
			String pathString = toString(path);
			InputStream valueAndData = _cmd.propget(toString(path), propertyName);
            
			byte[] bytes = streamToByteArray(valueAndData, true);
            if (bytes.length == 0) {
                return null; // the property does not exist
            }
            
			String value = new String(bytes);
			value = new StringTokenizer(value, Helper.NEWLINE).nextToken();

			return new CmdLineProperty(propertyName, value, pathString, bytes);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File, java.lang.String, java.io.File, boolean)
     */
	public void propertySet(File path, String propertyName, File propertyFile, boolean recurse)
		throws SVNClientException, IOException {
		try {
			_cmd.propsetFile(propertyName, toString(propertyFile), toString(path), recurse);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertyDel(java.io.File, java.lang.String, boolean)
     */
	public void propertyDel(File path, String propertyName, boolean recurse)
		throws SVNClientException {
            try {
                _cmd.propdel(propertyName, toString(path), recurse);
            } catch (CmdLineException e) {
                throw SVNClientException.wrapException(e);
            }        
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getIgnoredPatterns(java.io.File)
     */
	public List getIgnoredPatterns(File path) throws SVNClientException {
		if (!path.isDirectory())
			return null;
		List list = new ArrayList();
		ISVNProperty pd = propertyGet(path, "svn:ignore");
		if (pd == null)
			return list;
		String patterns = pd.getValue();
		StringTokenizer st = new StringTokenizer(patterns, "\n");
		while (st.hasMoreTokens()) {
			String entry = st.nextToken();
			if (!entry.equals(""))
				list.add(entry);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#addToIgnoredPatterns(java.io.File, java.lang.String)
	 */
	public void addToIgnoredPatterns(File file, String pattern) throws SVNClientException {
		List patterns = getIgnoredPatterns(file);
		if (patterns == null) // not a directory
			return;

		// verify that the pattern has not already been added
		for (Iterator it = patterns.iterator(); it.hasNext();) {
			if (((String) it.next()).equals(pattern))
				return; // already added
		}

		patterns.add(pattern);
		setIgnoredPatterns(file, patterns);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setIgnoredPatterns(java.io.File, java.util.List)
     */
	public void setIgnoredPatterns(File path, List patterns) throws SVNClientException {
		if (!path.isDirectory())
			return;
		StringBuffer values = new StringBuffer();
		for (Iterator it = patterns.iterator(); it.hasNext();) {
			String pattern = (String) it.next();
			values.append('\n');
			values.append(pattern);
		}
		propertySet(path, "svn:ignore", values.toString(), false);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#mkdir(java.io.File)
     */
	public void mkdir(File file) throws SVNClientException {
		try {
			_cmd.mkdir(toString(file));
		} catch (CmdLineException e) {
			SVNClientException.wrapException(e);
		}
		//sometimes the dir has not yet been created.
		//wait up to 5 sec for the dir to be created.
		for (int i = 0; i < 50 && !file.exists(); i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e2) {
				//do nothing if interrupted
			}
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doImport(java.io.File, org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String, boolean)
     */
	public void doImport(File path, SVNUrl url, String message, boolean recurse)
		throws SVNClientException {
		// TODO : implement        
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(org.tigris.subversion.svnclientadapter.SVNUrl, java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision, boolean)
     */
	public void doExport(SVNUrl srcUrl, File destPath, SVNRevision revision, boolean force)
		throws SVNClientException {
		try {
			_cmd.export(toString(srcUrl), toString(destPath), toString(revision), force);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#doExport(java.io.File, java.io.File, boolean)
     */
	public void doExport(File srcPath, File destPath, boolean force) throws SVNClientException {
		// TODO : test
		try {
			_cmd.export(toString(srcPath), toString(destPath), null, force);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(java.io.File, org.tigris.subversion.svnclientadapter.SVNUrl, java.lang.String)
     */
	public void copy(File srcPath, SVNUrl destUrl, String message) throws SVNClientException {
		// TODO : test
		try {
			_cmd.copy(toString(srcPath), toString(destUrl), message, null);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getLogMessages(java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision, org.tigris.subversion.svnclientadapter.SVNRevision)
     */
	public ISVNLogMessage[] getLogMessages(
		File path,
		SVNRevision revisionStart,
		SVNRevision revisionEnd)
		throws SVNClientException {
		List tempLogs = new java.util.LinkedList();
		String revRange = toString(revisionStart) + ":" + toString(revisionEnd);

		try {
			String messages = _cmd.log(toString(path), revRange);
			return CmdLineLogMessage.createLogMessages(messages);
			} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl, java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
	 */
	public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
		throws SVNClientException {
		try {
			_cmd.copy(toString(srcUrl), toString(destPath), null, toString(revision));
		} catch (CmdLineException e) {
			SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File, java.lang.String, java.lang.String, boolean)
	 */
	public void propertySet(File path, String propertyName, String propertyValue, boolean recurse)
		throws SVNClientException {
		try {
			_cmd.propset(propertyName, propertyValue, toString(path), recurse);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	private static String toString(SVNUrl url) {
		return (url == null) ? null : url.toString();
	}

	private static String toString(File file) {
		return (file == null) ? null : file.toString();
	}

	private static String toString(SVNRevision revision) {
		return (revision == null) ? null : revision.toString();
	}

	private static void streamToFile(InputStream stream, File outFile) throws IOException {
		int tempByte;
		try {
			FileOutputStream os = new FileOutputStream(outFile);
			while ((tempByte = stream.read()) != -1) {
				os.write(tempByte);
			}
			os.close();
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static byte[] streamToByteArray(InputStream stream, boolean removeTrailing)
		throws IOException {
		//read byte-by-byte and put it in a vector.
		//then take the vector and fill a byteArray.
		Vector buffer = new Vector(1024);
		int tempByte;
		while ((tempByte = stream.read()) != -1) {
			buffer.add(new Byte((byte) tempByte));
		}

		if (removeTrailing && !buffer.isEmpty())
			buffer.remove(buffer.size() - 1);

		byte[] byteArray = new byte[buffer.size()];
		for (int i = 0; i < byteArray.length; i++) {
			Byte b = (Byte) buffer.get(i);
			byteArray[i] = b.byteValue();
		}
		return byteArray;
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#getKeywords(java.io.File)
     */
	public SVNKeywords getKeywords(File path) throws SVNClientException {
		// copied directly from JhlClientAdapter
		ISVNProperty prop = propertyGet(path, ISVNProperty.KEYWORDS);
		if (prop == null)
			return new SVNKeywords();

		// value is a space-delimited list of the keywords names
		String value = prop.getValue();

		return new SVNKeywords(value);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#setKeywords(java.io.File, org.tigris.subversion.svnclientadapter.SVNKeywords, boolean)
     */
	public void setKeywords(File path, SVNKeywords keywords, boolean recurse)
		throws SVNClientException {
		// copied directly from JhlClientAdapter
		propertySet(path, ISVNProperty.KEYWORDS, keywords.toString(), recurse);
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#addKeywords(java.io.File, org.tigris.subversion.svnclientadapter.SVNKeywords)
     */
	public SVNKeywords addKeywords(File path, SVNKeywords keywords) throws SVNClientException {
		// copied directly from JhlClientAdapter
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
		setKeywords(path, currentKeywords, false);

		return currentKeywords;
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#removeKeywords(java.io.File, org.tigris.subversion.svnclientadapter.SVNKeywords)
     */
	public SVNKeywords removeKeywords(File path, SVNKeywords keywords) throws SVNClientException {
		// copied directly from JhlClientAdapter
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
		setKeywords(path, currentKeywords, false);

		return currentKeywords;
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#blame(org.tigris.subversion.svnclientadapter.SVNUrl, org.tigris.subversion.svnclientadapter.SVNRevision, org.tigris.subversion.svnclientadapter.SVNRevision)
     */
    public SVNAnnotations blame(SVNUrl url, SVNRevision revisionStart, SVNRevision revisionEnd)
        throws SVNClientException
    {
        // TODO : implement
        return null;
    }

}
