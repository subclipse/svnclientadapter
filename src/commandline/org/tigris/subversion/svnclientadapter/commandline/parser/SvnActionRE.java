/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.commandline.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineNotifyStatus;

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

	private Pattern pattern;
	private Matcher matcher;
	private int action;
	private int contentStatus = CmdLineNotifyStatus.unknown;
	private int propStatus = CmdLineNotifyStatus.unknown;
	private String[] notificationProperties;
	
	/**
	 * each parenthesized subexpression in the regular expression can be associated to a notificationProperty
	 * which is either PATH, CONTENTSTATE, PROPSTATE or REVISION
	 * @see SvnOutputParser
	 * @param re the regular expression to parse the svn line
	 * @param action the action corresponding to this line
	 * @param notificationProperties an array containing some of the following constants
	 * PATH, CONTENTSTATE, PROPSTATE, REVISION
	 */
	public SvnActionRE(String re, int action, String[] notificationProperties) {
		this.pattern = Pattern.compile('^'+re+'$');
		this.action = action;
		this.notificationProperties = notificationProperties;
	}

	public SvnActionRE(String re, int action, String notificationProperty) {
		this.pattern = Pattern.compile('^'+re+'$');
		this.action = action;
		this.notificationProperties = new String[] { notificationProperty };
	}

	public SvnActionRE(String re, int action) {
		this.pattern = Pattern.compile('^'+re+'$');
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
	 * @return the action
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
		this.matcher = pattern.matcher(line);
		return this.matcher.matches();
	}
	
	/**
	 * @return the path on which action happen or null
	 */
	public String getPath() {
		int index = getIndex(PATH); 
		if (index == -1) {
			return null;
		} else {
			return matcher.group(index+1);
		}
	}

	private int getStatus(char statusChar) {
		if (statusChar == ' ')
			return CmdLineNotifyStatus.unchanged;
		else
	    if (statusChar == 'C')
	    	return CmdLineNotifyStatus.conflicted;
	    else
		if (statusChar == 'G')
		   	return CmdLineNotifyStatus.merged;		    
	    else
	    if (statusChar == 'U')
		   	return CmdLineNotifyStatus.changed;
	    else
	    	return CmdLineNotifyStatus.unknown;
	}

	/**
	 * @return the content state
	 */
	public int getContentState() {
		if (contentStatus != CmdLineNotifyStatus.unknown) {
			return contentStatus;
		}
		int index = getIndex(CONTENTSTATE);
		if (index == -1) {
			return CmdLineNotifyStatus.unknown;
		} else {
			String stateChar = matcher.group(index+1);
			return getStatus(stateChar.charAt(0));
		}			
	}
	
	/**
	 * @return the prop status
	 */
	public int getPropStatus() {
		if (propStatus != CmdLineNotifyStatus.unknown) {
			return propStatus;
		}
		int index = getIndex(PROPSTATE);
		if (index == -1) {
			return CmdLineNotifyStatus.unknown;
		} else {
			String stateChar = matcher.group(index+1);
			return getStatus(stateChar.charAt(0));
		}			
	}

	/**
	 * @return the revision or null
	 */
	public long getRevision() {
		int index = getIndex(REVISION);
		if (index == -1) {
			return SVNRevision.SVN_INVALID_REVNUM;
		} else {
			String revisionString = matcher.group(index+1);
			return Long.parseLong(revisionString);
		}					
	}
	
}