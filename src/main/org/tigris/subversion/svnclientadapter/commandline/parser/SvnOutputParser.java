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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Notify;

/**
 * parser for the output of svn
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class SvnOutputParser {
	private static Log log = LogFactory.getLog(SvnOutputParser.class);
	
	private static final String NEWLINE = "\n\r";
	
	// See see subversion/clients/cmdline/notify.c for possible outputs
    // we depend on javahl because it would be a waste to duplicate the notification actions 
	private SvnActionRE[] svnActionsRE = new SvnActionRE[] { 
		new SvnActionRE("Skipped missing target: '(.+)'",Notify.Action.skip, Notify.Status.missing,new String[] { SvnActionRE.PATH } ),
		new SvnActionRE("Skipped '(.+)'",Notify.Action.skip,SvnActionRE.PATH),
		new SvnActionRE("D  ([^ ].+)",Notify.Action.update_delete,SvnActionRE.PATH),
		new SvnActionRE("A  ([^ ].+)",Notify.Action.update_add,SvnActionRE.PATH),
		new SvnActionRE("Restored '(.+)'",Notify.Action.restore,SvnActionRE.PATH),
		new SvnActionRE("Reverted '(.+)'",Notify.Action.revert,SvnActionRE.PATH),
		new SvnActionRE("Failed to revert '(.+)' -- try updating instead\\.",Notify.Action.failed_revert,SvnActionRE.PATH),
		new SvnActionRE("Resolved conflicted state of '(.+)'",Notify.Action.resolved,SvnActionRE.PATH),
		new SvnActionRE("A  (bin)  ([^ ].+)",Notify.Action.add,SvnActionRE.PATH),
		new SvnActionRE("A         ([^ ].+)",Notify.Action.add,SvnActionRE.PATH),
		new SvnActionRE("D         ([^ ].+)",Notify.Action.delete,SvnActionRE.PATH),
		new SvnActionRE("([CGU ])([CGU ]) (.+)",Notify.Action.update_update,new String[] {SvnActionRE.CONTENTSTATE, SvnActionRE.PROPSTATE,SvnActionRE.PATH}),
		new SvnActionRE("Fetching external item into '(.+)'",Notify.Action.update_external,SvnActionRE.PATH),
		new SvnActionRE("Exported external at revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Exported revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Checked out external at revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Checked out revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Updated external to revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Updated to revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("External at revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("At revision (\\d+)\\.",Notify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("External export complete\\.",Notify.Action.update_completed, Notify.Status.inapplicable, Notify.Status.inapplicable),
		new SvnActionRE("Export complete\\.",Notify.Action.update_completed, Notify.Status.inapplicable, Notify.Status.inapplicable),
		new SvnActionRE("External checkout complete\\.",Notify.Action.update_completed, Notify.Status.inapplicable, Notify.Status.inapplicable),
		new SvnActionRE("Checkout complete\\.",Notify.Action.update_completed, Notify.Status.inapplicable, Notify.Status.inapplicable),
		new SvnActionRE("External update complete\\.",Notify.Action.update_completed, Notify.Status.inapplicable, Notify.Status.inapplicable),
		new SvnActionRE("Update complete\\.",Notify.Action.update_completed, Notify.Status.inapplicable, Notify.Status.inapplicable),
		new SvnActionRE("Performing status on external item at '(.+)'",Notify.Action.status_external,SvnActionRE.PATH),
		new SvnActionRE("Status against revision:  *(\\d+)",Notify.Action.status_completed,SvnActionRE.REVISION),
		new SvnActionRE("Sending        (.+)",Notify.Action.commit_modified,SvnActionRE.PATH),
		new SvnActionRE("Adding  (bin)  (.+)",Notify.Action.commit_added,SvnActionRE.PATH),
		new SvnActionRE("Adding         (.+)",Notify.Action.commit_added,SvnActionRE.PATH),
		new SvnActionRE("Deleting       (.+)",Notify.Action.commit_deleted,SvnActionRE.PATH),
		new SvnActionRE("Replacing      (.+)",Notify.Action.commit_replaced,SvnActionRE.PATH),
		new SvnActionRE("Transmitting file data \\.*",Notify.Action.commit_postfix_txdelta),
        
        // this one is not a notification 
        new SvnActionRE("Committed revision (\\d+)\\.",-1,SvnActionRE.REVISION)
	};
	private List listeners = new LinkedList();
	
	/**
	 * add a listener
	 * @param listener
	 */
	public void addListener(Notify listener) {
		listeners.add(listener);
	}
	
	/**
	 * remove a listener
	 * @param listener
	 */
	public void removeListener(Notify listener) {
		listeners.remove(listener);
	}
	
	
	/**
	 * parse the given svn output (this can be more than one line)
	 * and notifies listeners
	 * @param svnOutput
	 */
	public void parse(String svnOutput) {
		StringTokenizer st = new StringTokenizer(svnOutput, NEWLINE);
		
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			
			synchronized(this) {
				// only one client must access a given SvnActionRE at a time
				SvnActionRE svnActionRE = getMatchingSvnActionRE(line);
				if (svnActionRE != null) {
					notifyListeners(svnActionRE);
				} else {
					// if we don't find a matching svnActionRE, we just log it
					log.warn("Can't find a svn action for svn output line : "+line);
				}
			}
		}
	}

	/**
	 * return the matching svn action or null if none matches 
	 * @param line
	 * @return
	 */
	private SvnActionRE getMatchingSvnActionRE(String line) {
		SvnActionRE result = null;
		for (int i = 0; i < svnActionsRE.length;i++) {
			if (svnActionsRE[i].match(line)) {
				if (result != null) {
					log.error("Multiple regular expressions match : "+line);
				} else {
					result = svnActionsRE[i]; 
				}
			}
		}
		return result;
	}

	private void notifyListeners(SvnActionRE svnActionRE) {
		for (Iterator it = listeners.iterator();it.hasNext();) {
			Notify listener = (Notify)it.next();
			listener.onNotify(
					svnActionRE.getPath(),
					svnActionRE.getAction(),
			        NodeKind.unknown,   // we don't know the kind
			        (String)null,       // we don't know the mimeType
			        svnActionRE.getContentState(),
			        svnActionRE.getPropStatus(),
			        svnActionRE.getRevision());
		}
	}
	

}
