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
package org.tigris.subversion.svnclientadapter.commandline.parser;

import org.apache.regexp.RE;
import org.tigris.subversion.javahl.Notify;
import org.tigris.subversion.javahl.Revision;

/**
 * regular expression to parse an svn notification line 
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org) 
 */
class SvnActionRE {
	public static final String PATH = "path";
	public static final String CONTENTSTATE = "contentState";
	public static final String PROPSTATE = "propState";
	public static final String REVISION = "revision";
	
	private RE re;
	private int action;
	private int contentStatus = Notify.Status.unknown;
	private int propStatus = Notify.Status.unknown;
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
	
	private RE getRE() {
		return re;
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
			return Notify.Status.unchanged;
		else
	    if (statusChar == 'C')
	    	return Notify.Status.conflicted;
	    else
		if (statusChar == 'G')
		   	return Notify.Status.merged;		    
	    else
	    if (statusChar == 'U')
		   	return Notify.Status.changed;
	    else
	    	return Notify.Status.unknown;
	}

	/**
	 * get the content state
	 * @see Notify#Status
	 */
	public int getContentState() {
		if (contentStatus != Notify.Status.unknown) {
			return contentStatus;
		}
		int index = getIndex(CONTENTSTATE);
		if (index == -1) {
			return Notify.Status.unknown;
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
		if (propStatus != Notify.Status.unknown) {
			return propStatus;
		}
		int index = getIndex(PROPSTATE);
		if (index == -1) {
			return Notify.Status.unknown;
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
			return Revision.SVN_INVALID_REVNUM;
		} else {
			String revisionString = re.getParen(index+1);
			return Long.parseLong(revisionString);
		}					
	}
	
}