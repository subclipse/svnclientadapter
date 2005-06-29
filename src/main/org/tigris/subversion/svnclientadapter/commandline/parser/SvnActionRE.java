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
package org.tigris.subversion.svnclientadapter.commandline.parser;

import org.apache.regexp.RE;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineNotify;

/**
 * regular expression to parse an svn notification line 
 * 
 * @author C�dric Chabanois (cchabanois at no-log.org) 
 */
class SvnActionRE {
	public static final String PATH = "path";
	public static final String CONTENTSTATE = "contentState";
	public static final String PROPSTATE = "propState";
	public static final String REVISION = "revision";
	
	private RE re;
	private int action;
	private int contentStatus = CmdLineNotify.Status.unknown;
	private int propStatus = CmdLineNotify.Status.unknown;
	private String[] notificationProperties;
	
	/**
	 * each parenthesized subexpression in the regular expression can be associated to a notificationProperty
	 * which is either PATH, CONTENTSTATE, PROPSTATE or REVISION
	 * @see Notify#Action
	 * @see SvnOutputParser
	 * @param re the regular expression to parse the svn line
	 * @param action the action corresponding to this line
	 * @param notificationProperties an array containing some of the following constants
	 * PATH, CONTENTSTATE, PROPSTATE, REVISION
	 */
	public SvnActionRE(String re, int action, String[] notificationProperties) {
		this.re = new RE('^'+re+'$');
		this.action = action;
		this.notificationProperties = notificationProperties;
	}

	public SvnActionRE(String re, int action, String notificationProperty) {
		this.re = new RE('^'+re+'$');
		this.action = action;
		this.notificationProperties = new String[] { notificationProperty };
	}

	public SvnActionRE(String re, int action) {
		this.re = new RE('^'+re+'$');
		this.action = action;
		this.notificationProperties = new String[] { };
	}

	public SvnActionRE(String re, int action, int contentStatus, int propStatus) {
		this(re,action);
		this.contentStatus = contentStatus;
		this.propStatus = propStatus;
	}
	
	public SvnActionRE(String re, int action, int contentStatus, String[] notificationProperties) {
		this(re,action,notificationProperties);
		this.contentStatus = contentStatus;
	}
	
	
	public SvnActionRE(String re, int action, int contentStatus, int propStatus,String[] notificationProperties) {
		this(re,action,notificationProperties);
		this.contentStatus = contentStatus;
		this.propStatus = propStatus;
	}
	
	/**
	 * get the action
	 * @see Notify#Action
	 */
	public int getAction() {
		return action;
	}
	
	private int getIndex(String notificationProperty) {
		for (int i = 0; i < notificationProperties.length;i++) {
			if (notificationProperties[i].equals(notificationProperty)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean match(String line) {
		return re.match(line);
	}
	
	/**
	 * get the path on which action happen or null
	 */
	public String getPath() {
		int index = getIndex(PATH); 
		if (index == -1) {
			return null;
		} else {
			return re.getParen(index+1);
		}
	}

	private int getStatus(char statusChar) {
		if (statusChar == ' ')
			return CmdLineNotify.Status.unchanged;
		else
	    if (statusChar == 'C')
	    	return CmdLineNotify.Status.conflicted;
	    else
		if (statusChar == 'G')
		   	return CmdLineNotify.Status.merged;		    
	    else
	    if (statusChar == 'U')
		   	return CmdLineNotify.Status.changed;
	    else
	    	return CmdLineNotify.Status.unknown;
	}

	/**
	 * get the content state
	 * @see Notify#Status
	 */
	public int getContentState() {
		if (contentStatus != CmdLineNotify.Status.unknown) {
			return contentStatus;
		}
		int index = getIndex(CONTENTSTATE);
		if (index == -1) {
			return CmdLineNotify.Status.unknown;
		} else {
			String stateChar = re.getParen(index+1);
			return getStatus(stateChar.charAt(0));
		}			
	}
	
	/**
	 * get the prop status
	 * @see Notify#Status
	 */
	public int getPropStatus() {
		if (propStatus != CmdLineNotify.Status.unknown) {
			return propStatus;
		}
		int index = getIndex(PROPSTATE);
		if (index == -1) {
			return CmdLineNotify.Status.unknown;
		} else {
			String stateChar = re.getParen(index+1);
			return getStatus(stateChar.charAt(0));
		}			
	}

	/**
	 * get the revision or null
	 */
	public long getRevision() {
		int index = getIndex(REVISION);
		if (index == -1) {
			return SVNRevision.SVN_INVALID_REVNUM;
		} else {
			String revisionString = re.getParen(index+1);
			return Long.parseLong(revisionString);
		}					
	}
	
}