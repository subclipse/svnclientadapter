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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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

	//TODO this expects svn to be in the path. should be able to set it in Window, Preferences
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
	public ISVNStatus getSingleStatus(File file)
		throws SVNClientException {
		String path = null;
		try {
			path = file.getCanonicalPath();
			String infoLine = _cmd.info(path);
			String statusLine = _cmd.status(path, false);			
			return new CmdLineStatus(statusLine, infoLine);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		} catch (CmdLineException e) {
			if(e.getMessage().startsWith("svn: Path is not a working copy directory")) {
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
			listLine = _cmd.list(svnUrl.toString(), rev.toString());

			StringTokenizer st = new StringTokenizer(listLine, Helper.NEWLINE);
			while (st.hasMoreTokens()) {
				String dirLine = st.nextToken();
				CmdLineRemoteDirEntry entry =
					new CmdLineRemoteDirEntry(svnUrl.toString(), dirLine);
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

		InputStream content = _cmd.cat(arg0.toString(), arg1.toString());

		//read byte-by-byte and put it in a vector.
		//then take the vector and fill a byteArray.
		Vector buffer = new Vector(1024);
		int tempByte;
		try {
			while ((tempByte = content.read()) != -1) {
				buffer.add(new Byte((byte) tempByte));
			}
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}

		byte[] byteArray = new byte[buffer.size()];
		for (int i = 0; i < byteArray.length; i++) {
			Byte b = (Byte) buffer.get(i);
			byteArray[i] = b.byteValue();
		}
		return new ByteArrayInputStream(byteArray);

	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#mkdir(java.net.URL, java.lang.String)
	 */
	public void mkdir(SVNUrl arg0, String arg1) throws SVNClientException {
		_cmd.mkdir(arg0.toString(), arg1);
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
		_cmd.copy(
			src.toString(),
			dest.toString(),
			message,
			rev.toString());
	}

    public void copy(File srcPath, File destPath)
        throws SVNClientException {
        // TODO : implement this        
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
		if(string == null || string.length() == 0)
			return;
		_cmd.setUsername(string);
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		if(password == null)
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
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#commit(java.io.File[], java.lang.String, boolean)
	 */
	public long commit(File[] parents, String comment, boolean b)
		throws SVNClientException {
		StringBuffer sb = new StringBuffer();
		try {
			for (int i = 0; i < parents.length; i++) {
				sb.append(parents[i].getCanonicalPath());
				sb.append(' ');
			}
			String changedResources = _cmd.checkin(sb.toString(), comment);
			refreshChangedResources(changedResources);
            // TODO : return the version number !
            return 0;
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		} catch (CmdLineException e) {
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
				_cmd.checkout(
					url.toString(),
					dest,
					revision.toString(),
					b);
			refreshChangedResources(changedResources);

		} catch (CmdLineException e) {
			throw SVNClientException.wrapException(e);
		} catch (IOException e) {
			throw SVNClientException.wrapException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getRevision(java.lang.String)
	 */
	public SVNRevision getRevision(String revName) {
		// TODO Auto-generated method stub
		System.out.println("[CommandLineClientAdapter] getRevision.");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#getRevision(long)
	 */
	public SVNRevision.Number getRevision(long revNum) {
		return new SVNRevision.Number(revNum);
	}

	private void notifyListenersOfChange(
		String path,
		SVNNodeKind type) {
		for (Iterator i = _listeners.iterator(); i.hasNext();) {
			ISVNNotifyListener listener =
				(ISVNNotifyListener) i.next();
			listener.onNotify(path, type);
		}
	}

	private void refreshChangedResources(String changedResourcesList) {
		StringTokenizer st =
			new StringTokenizer(changedResourcesList, Helper.NEWLINE);
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			
			//check and see if we are at the last line (nothing to do)
			if (line.startsWith("At revision "))
				return;
			if(line.startsWith("Updated to revision "))
				return;
			if(line.startsWith("Committed revision "))
				return;
			if(line.startsWith("Checked out revision "))
				return;
			//Jump to the next line if we encounter this: (when checking in)
			if(line.startsWith("Transmitting file data "))
				continue;

			String fileName = line.substring(line.indexOf(' ')).trim();

			//check to see if this is a file or a dir.
			File f = new File(fileName);

			notifyListenersOfChange(
				fileName,
				f.isDirectory()
					? SVNNodeKind.DIR
					: SVNNodeKind.FILE);
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
        // TODO : implement        
    }
    
    public void diff(File path, File outFile, boolean recurse)
        throws SVNClientException {
        // TODO : implement        
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
        // TODO : implement          
   }
        
    public void diff(
        SVNUrl url,
        SVNRevision oldUrlRevision,
        SVNRevision newUrlRevision,
        File outFile,
        boolean recurse)
        throws SVNClientException {
        // TODO : implement        
    }

    public ISVNProperty propertyGet(File path, String propertyName)
        throws SVNClientException {
        // TODO : implement
        return null;        
    }
    
    public void propertyDel(
        File path,
        String propertyName,
        boolean recurse)
        throws SVNClientException {
        // TODO : implement        
    }

    public List getIgnoredPatterns(File path)
        throws SVNClientException {
        // TODO : implement
        return null;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.subclipse.client.ISVNClientAdapter#addToIgnoredPatterns(java.io.File, java.lang.String)
     */
    public void addToIgnoredPatterns(File file, String pattern)
        throws SVNClientException {
        // TODO Auto-generated method stub
        System.out.println("[CommandLineClientAdapter] addToIgnoredPatterns.");
    }
    
    public void setIgnoredPatterns(File path, List patterns)
        throws SVNClientException {
        // TODO : implement
    }

    public void mkdir(File file) throws SVNClientException {
        // TODO : implement
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
        // TODO : implement
    }

    public void doExport(File srcPath, File destPath, boolean force)
        throws SVNClientException {
        // TODO : implement
    }

    public ISVNStatus[] getStatusRecursively(File file, boolean getAll)
        throws SVNClientException {
			Collection statuses = new ArrayList();
			String path = null;
			try {
				path = file.getCanonicalPath();
				
				String statusLines = _cmd.recursiveStatus(path);
				StringTokenizer st = new StringTokenizer(statusLines, Helper.NEWLINE);

				while (st.hasMoreTokens()) {
					String line = st.nextToken();
					String fileName = line.substring(CmdLineStatus.STATUS_FILE_WIDTH, line.length());
					String infoLine = _cmd.info(fileName);
			
					statuses.add(new CmdLineStatus(line, infoLine));
				} 

			} catch (IOException e) {
				throw SVNClientException.wrapException(e);
			} catch (CmdLineException e) {
				throw SVNClientException.wrapException(e);
			}
			return (ISVNStatus[])statuses.toArray(new ISVNStatus[statuses.size()]);
        }

    public void propertySet(
        File path,
        String propertyName,
        File propertyFile,
        boolean recurse)
        throws SVNClientException, IOException {
        // TODO : implement    
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
        // TODO : implement
        return null;
    }
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNClientAdapter#copy(org.tigris.subversion.svnclientadapter.SVNUrl, java.io.File, org.tigris.subversion.svnclientadapter.SVNRevision)
	 */
	public void copy(SVNUrl srcUrl, File destPath, SVNRevision revision)
		throws SVNClientException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
