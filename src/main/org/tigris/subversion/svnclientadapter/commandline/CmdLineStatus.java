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

import java.io.File;
import java.util.StringTokenizer;

import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;

/**
 * <p>
 * Implements a ISVNStatus using "svn status" and "svn info".</p>
 * 
 * @author Philip Schatz (schatz at tigris)
 */
class CmdLineStatus extends CmdLineInfo implements ISVNStatus {

	//"Constants"
	public static final int STATUS_FILE_WIDTH = 40;

	//Fields
	private char flag = '?';
	private char history;

	/**
	 * <p>
	 * Creates a new status. Takes in 2 lines, one of the
	 * "svn status" information, and one is the "svn info"
	 * line.</p>
	 * 
	 * @param statusLine Generated from "svn status"
	 * @param infoLine Generated from "svn info"
	 */
	CmdLineStatus(String statusLine, String infoLine) {
		super(infoLine);
		setStatus(statusLine);
	}

	//Methods
	private void setStatus(String statusLine) {

		// if the status is for a DIR then find the single line
		if (getNodeKind() == SVNNodeKind.DIR) {
			StringTokenizer st = new StringTokenizer(statusLine, Helper.NEWLINE);

			while (st.hasMoreTokens()) {
				String line = st.nextToken();

				String fileName = line.substring(STATUS_FILE_WIDTH, line.length());
				File file1 = new File(fileName);
				File file2 = new File(getPath());

				if (file1.equals(file2)) {
					statusLine = line;
					break;
				}
			}
		}

		char[] l = statusLine.toCharArray();
		flag = l[0];
		history = l[3];
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isIgnored()
	 */
	public boolean isIgnored() {
		return (flag == 'I');
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isManaged()
	 */
	public boolean isManaged() {
		return flag != '?';
	}

	/**
	 * tells if the resource has a remote counter-part
	 * @return
	 */
	public boolean hasRemote() {
		ISVNStatus.Kind textStatus = getTextStatus();
		return ((isManaged()) && (textStatus != ISVNStatus.Kind.ADDED));
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getTextStatus()
	 */
	public ISVNStatus.Kind getTextStatus() {
		switch (flag) {
			case ' ' :
				return ISVNStatus.Kind.NORMAL;
			case 'A' :
				return ISVNStatus.Kind.ADDED;
			case 'D' :
				return ISVNStatus.Kind.DELETED;
			case 'M' :
				return ISVNStatus.Kind.MODIFIED;
			case 'C' :
				return ISVNStatus.Kind.CONFLICTED;
			case 'I' :
				return ISVNStatus.Kind.IGNORED;
			case '?' :
				return ISVNStatus.Kind.UNVERSIONED;
			case '!' :
				return ISVNStatus.Kind.ABSENT;
			default :
				return ISVNStatus.Kind.NONE;
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isMerged()
	 */
	public boolean isMerged() {
		// TODO : implement
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isDeleted()
	 */
	public boolean isDeleted() {
		return (flag == 'D');
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isModified()
	 */
	public boolean isModified() {
		return (flag == 'M');
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isAdded()
	 */
	public boolean isAdded() {
		return (flag == 'A');
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#isCopied()
	 */
	public boolean isCopied() {
		return (history == '+');
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNClientStatus#getUrlCopiedFrom()
	 */
	public String getUrlCopiedFrom() {
		// TODO Auto-generated method stub
		return null;
	}

}
