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

import org.tigris.subversion.svnclientadapter.ISVNStatus;

/**
 * Represents one line in the result of a "svn status -v --no-ignore"command
 */
class CmdLineStatusPart {

	//"Constants"
	public static final int STATUS_FILE_WIDTH = 40;

	//Fields
	private char textStatus;
	private char propStatus;
	
	private char history;
    private File file;


    //Constructors
    /**
     * here are some statusLine samples :
     * A               0       ?   ?           added.txt
     * I                                       ignored.txt
     * Note that there is not output for files that do not exist and are not deleted
     */    
	CmdLineStatusPart(String statusLine) {
		setStatus(statusLine);
	}

	//Methods
	private void setStatus(String statusLine) {
		textStatus = statusLine.charAt(0);
		propStatus = statusLine.charAt(1);
		history = statusLine.charAt(3);
        file = new File(statusLine.substring(STATUS_FILE_WIDTH));
	}

	public boolean isIgnored() {
		return (textStatus == 'I');
	}

	public boolean isManaged() {
		return textStatus != '?';
	}

	/**
	 * tells if the resource has a remote counter-part
	 * @return
	 */
	public boolean hasRemote() {
		ISVNStatus.Kind textStatus = getTextStatus();
		return ((isManaged()) && (textStatus != ISVNStatus.Kind.ADDED));
	}

	public ISVNStatus.Kind getTextStatus() {
		switch (textStatus) {
			case ' ' : // none or normal
				return ISVNStatus.Kind.NORMAL;
			case 'A' :
				return ISVNStatus.Kind.ADDED;
            case '!' : // missing or incomplete
                return ISVNStatus.Kind.MISSING;
			case 'D' :
				return ISVNStatus.Kind.DELETED;
            case 'R' :
                return ISVNStatus.Kind.REPLACED;
			case 'M' :
				return ISVNStatus.Kind.MODIFIED;
            case 'G' :
                return ISVNStatus.Kind.MERGED;
			case 'C' :
				return ISVNStatus.Kind.CONFLICTED;
            case '~' :
                return ISVNStatus.Kind.OBSTRUCTED;
			case 'I' :
				return ISVNStatus.Kind.IGNORED;
            case 'X' :
                return ISVNStatus.Kind.EXTERNAL;
			case '?' :
				return ISVNStatus.Kind.UNVERSIONED;
			default :
				return ISVNStatus.Kind.NONE;
		}
	}

	public ISVNStatus.Kind getPropStatus() {
		switch (textStatus) {
			case ' ' : // no modifications
				return ISVNStatus.Kind.NORMAL;
			case 'C' :
				return ISVNStatus.Kind.CONFLICTED;
			case 'M' :
				return ISVNStatus.Kind.MODIFIED;
			default :
				return ISVNStatus.Kind.NORMAL;
		}		
	}

	public boolean isMerged() {
		return (textStatus == 'G');
	}

	public boolean isDeleted() {
		return (textStatus == 'D');
	}

	public boolean isModified() {
		return (textStatus == 'M');
	}

	public boolean isAdded() {
		return (textStatus == 'A');
	}

	public boolean isCopied() {
		return (history == '+');
	}

    public File getFile() {
        return file.getAbsoluteFile();
    }

}
