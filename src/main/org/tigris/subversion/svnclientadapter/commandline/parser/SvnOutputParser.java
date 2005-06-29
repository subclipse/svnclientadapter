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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineNotify;

/**
 * parser for the output of svn
 * 
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
public class SvnOutputParser {
	private static Logger log = Logger.getLogger(SvnOutputParser.class.getName());
	
	private static final String NEWLINE = "\n\r";
	
	// See see subversion/clients/cmdline/notify.c for possible outputs
    // we depend on javahl because it would be a waste to duplicate the notification actions 
	private SvnActionRE[] svnActionsRE = new SvnActionRE[] { 
		new SvnActionRE("Skipped missing target: '(.+)'",CmdLineNotify.Action.skip, CmdLineNotify.Status.missing,new String[] { SvnActionRE.PATH } ),
		new SvnActionRE("Skipped '(.+)'",CmdLineNotify.Action.skip,SvnActionRE.PATH),
		new SvnActionRE("D    ([^ ].+)",CmdLineNotify.Action.update_delete,SvnActionRE.PATH),
		new SvnActionRE("A    ([^ ].+)",CmdLineNotify.Action.update_add,SvnActionRE.PATH),
		new SvnActionRE("Restored '(.+)'",CmdLineNotify.Action.restore,SvnActionRE.PATH),
		new SvnActionRE("Reverted '(.+)'",CmdLineNotify.Action.revert,SvnActionRE.PATH),
		new SvnActionRE("Failed to revert '(.+)' -- try updating instead\\.",CmdLineNotify.Action.failed_revert,SvnActionRE.PATH),
		new SvnActionRE("Resolved conflicted state of '(.+)'",CmdLineNotify.Action.resolved,SvnActionRE.PATH),
		new SvnActionRE("A  (bin)  ([^ ].+)",CmdLineNotify.Action.add,SvnActionRE.PATH),
		new SvnActionRE("A         ([^ ].+)",CmdLineNotify.Action.add,SvnActionRE.PATH),
		new SvnActionRE("D         ([^ ].+)",CmdLineNotify.Action.delete,SvnActionRE.PATH),
		new SvnActionRE("([CGU ])([CGU ])   (.+)",CmdLineNotify.Action.update_update,new String[] {SvnActionRE.CONTENTSTATE, SvnActionRE.PROPSTATE,SvnActionRE.PATH}),
		new SvnActionRE("Fetching external item into '(.+)'",CmdLineNotify.Action.update_external,SvnActionRE.PATH),
		new SvnActionRE("Exported external at revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Exported revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Checked out external at revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Checked out revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Updated external to revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("Updated to revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("External at revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("At revision (\\d+)\\.",CmdLineNotify.Action.update_completed,SvnActionRE.REVISION),
		new SvnActionRE("External export complete\\.",CmdLineNotify.Action.update_completed, CmdLineNotify.Status.inapplicable, CmdLineNotify.Status.inapplicable),
		new SvnActionRE("Export complete\\.",CmdLineNotify.Action.update_completed, CmdLineNotify.Status.inapplicable, CmdLineNotify.Status.inapplicable),
		new SvnActionRE("External checkout complete\\.",CmdLineNotify.Action.update_completed, CmdLineNotify.Status.inapplicable, CmdLineNotify.Status.inapplicable),
		new SvnActionRE("Checkout complete\\.",CmdLineNotify.Action.update_completed, CmdLineNotify.Status.inapplicable, CmdLineNotify.Status.inapplicable),
		new SvnActionRE("External update complete\\.",CmdLineNotify.Action.update_completed, CmdLineNotify.Status.inapplicable, CmdLineNotify.Status.inapplicable),
		new SvnActionRE("Update complete\\.",CmdLineNotify.Action.update_completed, CmdLineNotify.Status.inapplicable, CmdLineNotify.Status.inapplicable),
		new SvnActionRE("Performing status on external item at '(.+)'",CmdLineNotify.Action.status_external,SvnActionRE.PATH),
		new SvnActionRE("Status against revision:  *(\\d+)",CmdLineNotify.Action.status_completed,SvnActionRE.REVISION),
		new SvnActionRE("Sending        (.+)",CmdLineNotify.Action.commit_modified,SvnActionRE.PATH),
		new SvnActionRE("Adding  (bin)  (.+)",CmdLineNotify.Action.commit_added,SvnActionRE.PATH),
		new SvnActionRE("Adding         (.+)",CmdLineNotify.Action.commit_added,SvnActionRE.PATH),
		new SvnActionRE("Deleting       (.+)",CmdLineNotify.Action.commit_deleted,SvnActionRE.PATH),
		new SvnActionRE("Replacing      (.+)",CmdLineNotify.Action.commit_replaced,SvnActionRE.PATH),
		new SvnActionRE("Transmitting file data \\.*",CmdLineNotify.Action.commit_postfix_txdelta),
		new SvnActionRE("'(.+)' locked by user.*",CmdLineNotify.Action.locked),
		new SvnActionRE("'(.+)' unlocked.*",CmdLineNotify.Action.unlocked),
        
        // this one is not a notification 
        new SvnActionRE("Committed revision (\\d+)\\.",-1,SvnActionRE.REVISION)
	};
	private List listeners = new LinkedList();
	
	/**
	 * add a listener
	 * @param listener
	 */
	public void addListener(CmdLineNotify listener) {
		listeners.add(listener);
	}
	
	/**
	 * remove a listener
	 * @param listener
	 */
	public void removeListener(CmdLineNotify listener) {
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
					log.warning("Can't find a svn action for svn output line : "+line);
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
					log.severe("Multiple regular expressions match : "+line);
				} else {
					result = svnActionsRE[i]; 
				}
			}
		}
		return result;
	}

	private void notifyListeners(SvnActionRE svnActionRE) {
		for (Iterator it = listeners.iterator();it.hasNext();) {
			CmdLineNotify listener = (CmdLineNotify)it.next();
			listener.onNotify(
					svnActionRE.getPath(),
					svnActionRE.getAction(),
			        SVNNodeKind.UNKNOWN.toInt(),   // we don't know the kind
			        (String)null,       // we don't know the mimeType
			        svnActionRE.getContentState(),
			        svnActionRE.getPropStatus(),
			        svnActionRE.getRevision());
		}
	}
	

}
