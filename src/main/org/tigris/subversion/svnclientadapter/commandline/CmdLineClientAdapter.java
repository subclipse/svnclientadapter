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
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author philip schatz
 */
public class CmdLineClientAdapter implements ISVNClientAdapter {

	//PHIL this expects svn to be in the path. should be able to set it in Window, Preferences
	private CommandLine _cmd = new CommandLine("svn");
	private List _listeners = new LinkedList();

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#addNotifyListener(org.tigris.subversion.subclipse.client.ISVNClientNotifyListener)
	 */
	public void addNotifyListener(ISVNNotifyListener listener) {
		_listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#removeNotifyListener(org.tigris.subversion.subclipse.client.ISVNClientNotifyListener)
	 */
	public void removeNotifyListener(ISVNNotifyListener listener) {
		_listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getSingleStatus(java.io.File)
	 */
	public ISVNStatus getSingleStatus(File file) throws SVNClientException {
		String path = null;
		try {
			path = file.getCanonicalPath();
			String infoLine = _cmd.info(path);
			String statusLine = _cmd.status(path, false);			
			return new CmdLineStatus(statusLine, infoLine);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		} catch (CmdLineException e) {
			if (e
				.getMessage()
				.startsWith("svn: Path is not a working copy directory")) {
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
				CmdLineRemoteDirEntry entry =
					new CmdLineRemoteDirEntry(toString(svnUrl), dirLine);
				entries.add(entry);
			}
			return (ISVNDirEntry[]) entries.toArray(
				new ISVNDirEntry[entries.size()]);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#remove(java.io.File[], boolean)
	 */
	public void remove(File[] arg0, boolean arg1) throws SVNClientException {
		StringBuffer sb = new StringBuffer();
		try {
			for (int i = 0; i < arg0.length; i++) {
				sb.append(arg0[i].getCanonicalPath());
				sb.append(' ');
			}
			_cmd.delete(sb.toString(), null);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#revert(java.io.File, boolean)
	 */
	public void revert(File arg0, boolean arg1) throws SVNClientException {
		try {
			String changedFiles = _cmd.revert(arg0.getCanonicalPath(), arg1);
			refreshChangedResources(changedFiles);			
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}
		catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
				
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getContent(java.net.SVNUrl, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public InputStream getContent(SVNUrl arg0, SVNRevision arg1)
		throws SVNClientException {

		InputStream content = _cmd.cat(toString(arg0), toString(arg1));

		//read byte-by-byte and put it in a vector.
		//then take the vector and fill a byteArray.
		byte[] byteArray;
		try {
			byteArray = streamToByteArray(content, false);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}
		return new ByteArrayInputStream(byteArray);

	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#mkdir(java.net.URL, java.lang.String)
	 */
	public void mkdir(SVNUrl arg0, String arg1) throws SVNClientException {
		_cmd.mkdir(toString(arg0), arg1);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getLogMessages(java.net.URL, org.tigris.subversion.subclipse.client.ISVNRevision, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public ISVNLogMessage[] getLogMessages(
		SVNUrl arg0,
		SVNRevision arg1,
		SVNRevision arg2)
		throws SVNClientException {
		List tempLogs = new java.util.LinkedList();
		String revRange = arg1.toString() + ":" + arg2.toString();

		try {
			String messages = _cmd.log(arg0.toString(), revRange);

			StringTokenizer st = new StringTokenizer(messages, Helper.NEWLINE);
			st.nextToken();
			while (st.hasMoreTokens()) {
				tempLogs.add(new CmdLineLogMessage(st));
			}

			return (ISVNLogMessage[]) tempLogs.toArray(
				new ISVNLogMessage[tempLogs.size()]);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#remove(java.net.URL[], java.lang.String)
	 */
	public void remove(SVNUrl[] arg0, String arg1) throws SVNClientException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arg0.length; i++) {
			sb.append(arg0[i].toString());
			sb.append(' ');
		}
		try {
			_cmd.delete(sb.toString(), arg1);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#copy(java.net.URL, java.net.URL, java.lang.String, org.tigris.subversion.subclipse.client.ISVNRevision)
	 */
	public void copy(SVNUrl src, SVNUrl dest, String message, SVNRevision rev)
		throws SVNClientException {
		_cmd.copy(src.toString(), dest.toString(), message, rev.toString());
	}

	public void copy(File srcPath, File destPath) throws SVNClientException {
		_cmd.copy(srcPath.toString(), destPath.toString());
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
	public void move(
		SVNUrl url,
		SVNUrl destUrl,
		String message,
		SVNRevision revision)
		throws SVNClientException {
		try {
			String changedResources =
				_cmd.move(
					url.toString(),
					destUrl.toString(),
					message,
					revision.toString());
			refreshChangedResources(changedResources);
		} catch (CmdLineException e) {
			SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#move(java.io.File, java.io.File, boolean)
	 */
	public void move(File file, File file2, boolean b)
		throws SVNClientException {
		try {
			String changedResources =
				_cmd.move(
					file.getCanonicalPath(),
					file2.getCanonicalPath(),
					null,
					null);
			refreshChangedResources(changedResources);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
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
	public void addDirectory(File file, boolean b) throws SVNClientException {
		try {
			_cmd.add(file.getCanonicalPath(), true);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
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
			String changedResources = _cmd.add(file.getCanonicalPath(), false);
			refreshChangedResources(changedResources);			
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
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
	public long commit(File[] parents, String comment, boolean b)
		throws SVNClientException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parents.length; i++) {
			sb.append(parents[i].toString());
			sb.append(' ');
		}
		try {
			String changedResources = _cmd.checkin(sb.toString(), comment);
			return refreshChangedResources(changedResources);
		} catch (CmdLineException e) {
			if ("".equals(e.getMessage()))
				return SVNRevision.SVN_INVALID_REVNUM;
			if (e
				.getMessage()
				.startsWith("svn: Attempted to lock an already-locked dir")) {
				//PHIL is this the best way to handle pending locks? (ie caused by "svn cp")
				//loop through up to 5 sec, waiting for locks
				//to be removed.
				for (int i = 0; i < 50; i++) {
					try {
						String changedResources =
							_cmd.checkin(sb.toString(), comment);
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
	public void update(File file, SVNRevision revision, boolean b)
		throws SVNClientException {
		try {
			String changedResources =
				_cmd.update(file.getCanonicalPath(), revision.toString());
			refreshChangedResources(changedResources);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#checkout(java.net.URL, java.io.File, org.tigris.subversion.subclipse.client.ISVNRevision, boolean)
	 */
	public void checkout(
		SVNUrl url,
		File destPath,
		SVNRevision revision,
		boolean b)
		throws SVNClientException {
		try {
			String dest = destPath.getCanonicalPath();
			String changedResources =
				_cmd.checkout(url.toString(), dest, revision.toString(), b);
			refreshChangedResources(changedResources);

		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getStatusRecursively(java.io.File,boolean)
	 */
	public ISVNStatus[] getStatusRecursively(File file, boolean getAll)
		throws SVNClientException {
		String path = null;
		List statuses = new LinkedList();
		try {
			path = file.getCanonicalPath();
			String statusLines = _cmd.recursiveStatus(path);
			StringTokenizer st =
				new StringTokenizer(statusLines, Helper.NEWLINE);
			while (st.hasMoreTokens()) {
				String statusLine = st.nextToken();
				String infoLine = _cmd.info(statusLine.substring(7));
				CmdLineStatus status =
					new CmdLineStatus(statusLine, infoLine);
				statuses.add(status);
			}

			return (ISVNStatus[]) statuses.toArray(
				new ISVNStatus[statuses.size()]);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		} catch (CmdLineException e) {
			if (e
				.getMessage()
				.startsWith("svn: Path is not a working copy directory")) {
				return new ISVNStatus[0];
			}
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getRevision(java.lang.String)
	 */
	public SVNRevision getRevision(String revName) {
		// TODO : implement
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getRevision(long)
	 */
	public SVNRevision.Number getRevision(long revNum) {
		return new SVNRevision.Number(revNum);
	}

	private void notifyListenersOfChange(String path, SVNNodeKind type) {
		for (Iterator i = _listeners.iterator(); i.hasNext();) {
			ISVNNotifyListener listener = (ISVNNotifyListener) i.next();
			listener.onNotify(path, type);
		}
	}

	private long refreshChangedResources(String changedResourcesList) {
		StringTokenizer st =
			new StringTokenizer(changedResourcesList, Helper.NEWLINE);
		while (st.hasMoreTokens()) {
			String line = st.nextToken();

			//check and see if we are at the last line (nothing to do)
			if (line.startsWith("At revision "))
				return Long.parseLong(line.substring(12, line.length() - 1));
			if (line.startsWith("Updated to revision "))
				return Long.parseLong(line.substring(18, line.length() - 1));
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

			notifyListenersOfChange(
				fileName,
				f.isDirectory() ? SVNNodeKind.DIR : SVNNodeKind.FILE);
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

		InputStream is =
			_cmd.diff(
				oldPath,
				toString(oldPathRevision),
				newPath,
				toString(newPathRevision),
				recurse);
		try {
			streamToFile(is, outFile);
		} catch (IOException e) {
			//this should never happen
		}
	}

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

	public void diff(File path, File outFile, boolean recurse)
		throws SVNClientException {
		diff(path, null, null, null, outFile, recurse);
	}
	/**
	 * display the differences between two urls. 
	 */
	public void diff(
		SVNUrl oldUrl,
		SVNRevision oldUrlRevision,
		SVNUrl newUrl,
		SVNRevision newUrlRevision,
		File outFile,
		boolean recurse)
		throws SVNClientException {
		diff(
			toString(oldUrl),
			oldUrlRevision,
			toString(newUrl),
			newUrlRevision,
			outFile,
			recurse);
	}

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

	public ISVNProperty propertyGet(File path, String propertyName)
		throws SVNClientException {
		try {
			String pathString = toString(path);
			InputStream valueAndData =
				_cmd.propget(toString(path), propertyName);

			byte[] bytes = streamToByteArray(valueAndData, true);

			String value = new String(bytes);
			value = new StringTokenizer(value, Helper.NEWLINE).nextToken();

			return new CmdLineProperty(propertyName, value, pathString, bytes);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	public void propertyDel(File path, String propertyName, boolean recurse)
		throws SVNClientException {
		// TODO : implement        
	}

	public List getIgnoredPatterns(File path) throws SVNClientException {
		// TODO : implement
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#addToIgnoredPatterns(java.io.File, java.lang.String)
	 */
	public void addToIgnoredPatterns(File file, String pattern)
		throws SVNClientException {
		// TODO : implement
	}

	public void setIgnoredPatterns(File path, List patterns)
		throws SVNClientException {
		// TODO : implement
	}

	public void mkdir(File file) throws SVNClientException {
		_cmd.mkdir(toString(file));
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

	public void doImport(
		File path,
		SVNUrl url,
		String message,
		boolean recurse)
		throws SVNClientException {
		// TODO : implement        
	}

	public void doExport(
		SVNUrl srcUrl,
		File destPath,
		SVNRevision revision,
		boolean force)
		throws SVNClientException {
		try {
			_cmd.export(
				toString(srcUrl),
				toString(destPath),
				toString(revision),
				force);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	public void doExport(File srcPath, File destPath, boolean force)
		throws SVNClientException {
		// TODO : test
		try {
			_cmd.export(toString(srcPath), toString(destPath), null, force);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	public void propertySet(
		File path,
		String propertyName,
		File propertyFile,
		boolean recurse)
		throws SVNClientException, IOException {
		try {
			_cmd.propsetFile(
				propertyName,
				toString(propertyFile),
				toString(path),
				recurse);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	public void copy(File srcPath, SVNUrl destUrl, String message)
		throws SVNClientException {
		// TODO : implement
	}

	public ISVNLogMessage[] getLogMessages(
		File path,
		SVNRevision revisionStart,
		SVNRevision revisionEnd)
		throws SVNClientException {
		List tempLogs = new java.util.LinkedList();
		String revRange =
			revisionStart.toString() + ":" + revisionEnd.toString();

		try {
			String messages = _cmd.log(path.toString(), revRange);

			StringTokenizer st = new StringTokenizer(messages, Helper.NEWLINE);
			st.nextToken();
			while (st.hasMoreTokens()) {
				tempLogs.add(new CmdLineLogMessage(st));
			}

			return (ISVNLogMessage[]) tempLogs.toArray(
				new ISVNLogMessage[tempLogs.size()]);
		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl, java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
	 */
	public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
		throws SVNClientException {
		_cmd.copy(
			srcUrl.toString(),
			destPath.toString(),
			null,
			revision.toString());
		// TODO : test
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#propertySet(java.io.File, java.lang.String, java.lang.String, boolean)
	 */
	public void propertySet(
		File path,
		String propertyName,
		String propertyValue,
		boolean recurse)
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

	private static void streamToFile(InputStream stream, File outFile)
		throws IOException {
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

	private static byte[] streamToByteArray(
		InputStream stream,
		boolean removeTrailing)
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
}
