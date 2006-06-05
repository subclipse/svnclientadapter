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
import org.tigris.subversion.svnclientadapter.SVNStatusUtils;

abstract class CmdLineStatusPart {

	protected SVNStatusKind textStatus;
	protected SVNStatusKind propStatus;
	
	protected CmdLineStatusPart(SVNStatusKind textStatus, SVNStatusKind propStatus) {
		super();
		this.textStatus = textStatus;
		this.propStatus = propStatus;
	}

	public boolean isManaged()
	{
		return SVNStatusUtils.isManaged(textStatus);
	}

	/**
	 * @return Whether this item was copied from another location.
	 */
	public abstract boolean isCopied();

	/**
	 * tells if the resource has a remote counter-part
	 * @return
	 */
	public boolean hasRemote() {
		return (isManaged() && getTextStatus() != SVNStatusKind.ADDED);
	}

	/**
	 * @return The status of the item itself (e.g. directory or file).
	 */
	public SVNStatusKind getTextStatus()
	{
		return textStatus;
	}
	
	public SVNStatusKind getPropStatus()
	{
		return propStatus;
	}

    public abstract SVNStatusKind getRepositoryTextStatus();

    public abstract SVNStatusKind getRepositoryPropStatus();

	/**
	 * @return The absolute path to this item.
	 */
	public abstract File getFile();

	public abstract String getPath();

	static class CmdLineStatusPartFromStdout extends CmdLineStatusPart {

		public static final int STATUS_FILE_WIDTH = 40;
		protected String path;
		protected File file;
		protected char history;

		/**
	     * here are some statusLine samples :
	     * A               0       ?   ?           added.txt
	     * I                                       ignored.txt
	     * Note that there is not output for files that do not exist and are not deleted
	     */    
		CmdLineStatusPartFromStdout(String statusLine) {
			super(getTextStatus(statusLine.charAt(0)), getPropStatus(statusLine.charAt(1)));
			history = statusLine.charAt(3);
			path =  statusLine.substring(STATUS_FILE_WIDTH).trim();	                
	        file = new File(path);
		}

		/**
		 * @return Whether this item was copied from another location.
		 */
		public boolean isCopied() {
			return (history == '+');
		}

		/**
		 * @return The status of the item itself (e.g. directory or file).
		 */
		private static SVNStatusKind getTextStatus(char statusChar) {
			switch (statusChar) {
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

		private static SVNStatusKind getPropStatus(char statusChar) {
			switch (statusChar) {
				case ' ' : // no modifications
					return SVNStatusKind.NONE;
				case 'C' :
					return SVNStatusKind.CONFLICTED;
				case 'M' :
					return SVNStatusKind.MODIFIED;
				default :
					return SVNStatusKind.NORMAL;
			}		
		}

		/**
		 * @return The absolute path to this item.
		 */
		public File getFile() {
		    return file.getAbsoluteFile();
		}

		public String getPath() {
		    return path;
		}

	    public SVNStatusKind getRepositoryTextStatus()
	    {
	    	return null;
	    }

	    public SVNStatusKind getRepositoryPropStatus()
	    {
	    	return null;
	    }

	}
	
	static class CmdLineStatusPartFromXml extends CmdLineStatusPart {

		private CmdLineStatusFromXml status;
		
		private CmdLineStatusPartFromXml(CmdLineStatusFromXml status)
		{
			super(status.getTextStatus(), status.getPropStatus());
			this.status = status;
		}
		
		public static CmdLineStatusPartFromXml[] createStatusParts(byte[] cmdLineResults) throws CmdLineException {
			CmdLineStatusFromXml[] statuses = CmdLineStatusFromXml.createStatuses(cmdLineResults);
			CmdLineStatusPartFromXml[] result = new CmdLineStatusPartFromXml[statuses.length];
			for (int i = 0; i < statuses.length; i++) {
				result[i] = new CmdLineStatusPartFromXml(statuses[i]);
			}
			return result;
		}

		public File getFile() {
			return status.getFile();
		}

		public boolean isCopied() {
			return status.isCopied();
		}

	    public SVNStatusKind getRepositoryTextStatus()
	    {
	    	return status.getRepositoryTextStatus();
	    }

	    public SVNStatusKind getRepositoryPropStatus()
	    {
	    	return status.getRepositoryPropStatus();
	    }

		public String getPath() {
		    return status.getPath();
		}

	}
}

