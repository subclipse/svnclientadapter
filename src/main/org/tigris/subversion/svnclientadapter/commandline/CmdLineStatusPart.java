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
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.File;

import org.tigris.subversion.svnclientadapter.SVNStatusKind;

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
		SVNStatusKind textStatus = getTextStatus();
		return ((isManaged()) && (textStatus != SVNStatusKind.ADDED));
	}

	public SVNStatusKind getTextStatus() {
		switch (textStatus) {
			case ' ' : // none or normal
				return SVNStatusKind.NORMAL;
			case 'A' :
				return SVNStatusKind.ADDED;
            case '!' : // missing or incomplete
                return SVNStatusKind.MISSING;
			case 'D' :
				return SVNStatusKind.DELETED;
            case 'R' :
                return SVNStatusKind.REPLACED;
			case 'M' :
				return SVNStatusKind.MODIFIED;
            case 'G' :
                return SVNStatusKind.MERGED;
			case 'C' :
				return SVNStatusKind.CONFLICTED;
            case '~' :
                return SVNStatusKind.OBSTRUCTED;
			case 'I' :
				return SVNStatusKind.IGNORED;
            case 'X' :
                return SVNStatusKind.EXTERNAL;
			case '?' :
				return SVNStatusKind.UNVERSIONED;
            case 'L' :
                return SVNStatusKind.LOCKED;
			default :
				return SVNStatusKind.NONE;
		}
	}

	public SVNStatusKind getPropStatus() {
		switch (propStatus) {
			case ' ' : // no modifications
				return SVNStatusKind.NORMAL;
			case 'C' :
				return SVNStatusKind.CONFLICTED;
			case 'M' :
				return SVNStatusKind.MODIFIED;
			default :
				return SVNStatusKind.NORMAL;
		}		
	}

	public boolean isCopied() {
		return (history == '+');
	}

    public File getFile() {
        return file.getAbsoluteFile();
    }

}
