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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * 
 * @author philip schatz
 */
public class CmdLineRemoteDirEntry implements ISVNDirEntry {

	private static DateFormat df = new SimpleDateFormat("MMM dd hh:mm");

	private String path;
	private URL url;
	private SVNRevision.Number revision;
	private SVNNodeKind nodeKind;
	private String lastCommitAuthor;
	private Date lastChangedDate;

	/**
	 * @param line
	 */
	public CmdLineRemoteDirEntry(String baseUrl, String line) {
		int last = line.length() - 1;
		boolean folder = ('/' == line.charAt(last));

		path = (folder) ? line.substring(41, last) : line.substring(41);

		try {
			url = new URL(baseUrl + '/' + path);
		} catch (MalformedURLException e) {
			//do nothing
		}

		revision = new SVNRevision.Number(Long.parseLong(line.substring(1, 9).trim()));
		nodeKind = (folder) ? SVNNodeKind.DIR : SVNNodeKind.FILE;
		lastCommitAuthor = line.substring(9, 18).trim();

		try {
			lastChangedDate = df.parse(line.substring(28, 39));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getHasProps()
	 */
	public boolean getHasProps() {
		//TODO unhardcode this
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getNodeKind()
	 */
	public SVNNodeKind getNodeKind() {
		return nodeKind;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getLastChangedRevision()
	 */
	public SVNRevision.Number getLastChangedRevision() {
		return revision;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		return lastChangedDate;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		return lastCommitAuthor;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getPath()
	 */
	public String getPath() {
		return path;
	}

    public long getSize() {
        // TODO : implement getSize
        return 0;
    }

}
