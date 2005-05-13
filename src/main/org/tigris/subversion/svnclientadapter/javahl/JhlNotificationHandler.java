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
package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.Lock;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Notify2;
import org.tigris.subversion.javahl.NotifyAction;
import org.tigris.subversion.javahl.NotifyInformation;
import org.tigris.subversion.javahl.NotifyStatus;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNNotificationHandler;



/**
 * Notification handler :
 * It listens to events from javahl jni implementation and handles 
 * notifications from SVNClientAdapter.
 * It sends notifications to all listeners 
 * 
 * It mimics svn output (see subversion/clients/cmdline/notify.c)
 * 
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 *
 */
public class JhlNotificationHandler extends SVNNotificationHandler implements Notify2 {
    private boolean receivedSomeChange;
    private boolean sentFirstTxdelta;
    
    private int updates;
    private int adds;
    private int deletes;
    private int conflicts;
    private int merges;
    private int propConflicts;
    private int propMerges;
    private int propUpdates;

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.Notify2#onNotify(org.tigris.subversion.javahl.NotifyInformation)
     */
    public void onNotify(NotifyInformation info) {
        this.onNotify(info.getPath(),
                      info.getAction(),
                      info.getKind(),
                      info.getMimeType(),
                      info.getContentState(),
                      info.getPropState(),
                      info.getRevision(),
                      info.getLock());
    }
    
    /**
     * Handler for Subversion notifications.
     *
     * Override this function to allow Subversion to send notifications
     * @param path on which action happen
     * @param action subversion action, see svn_wc_notify_action_t
     * @param kind node kind of path after action occurred
     * @param mimeType mime type of path after action occurred
     * @param contentState state of content after action occurred
     * @param propState state of properties after action occurred
     * @param revision revision number  after action occurred
     */
    private void onNotify(
        String path,
        int action,
        int kind,
        String mimeType,
        int contentState,
        int propState,
        long revision,
        Lock lock) {

        // for some actions, we don't want to call notifyListenersOfChange :
        // when the status of the target has not been modified 
        boolean notify = true;

        switch (action) {
            case NotifyAction.skip :
                logMessage("Skipped " + path);
                notify = false;                                
                break;
            case NotifyAction.failed_lock: 
                logError("Failed to lock " + path);
                notify = false;
                break;
            case NotifyAction.failed_unlock:
                logError("Failed to unlock " + path);
            	notify = false;
            	break;
            case NotifyAction.locked:
                if (lock != null && lock.getOwner() != null)
                	logMessage(lock.getPath() + " locked by user " + lock.getOwner());
                else
                    logMessage(path + "locked");
        	    notify = false; // for JavaHL bug
            	break;
            case NotifyAction.unlocked:
                logMessage(path + " unlocked");
            	notify = false; // for JavaHL bug
            	break;
            case NotifyAction.update_delete :
                logMessage("D  " + path);
                receivedSomeChange = true;
                deletes += 1;
                break;
            case NotifyAction.update_add :
                logMessage("A  " + path);
                receivedSomeChange = true;
                adds += 1;
                break;
            case NotifyAction.restore :
                logMessage("Restored " + path);
                break;
            case NotifyAction.revert :
                logMessage("Reverted " + path);
                break;
            case NotifyAction.failed_revert :
                logError("Failed to revert " + path + " -- try updating instead.");
                notify = false;
                break;
            case NotifyAction.resolved :
                logMessage("Resolved conflicted state of " + path);
                break;
            case NotifyAction.add :
                logMessage("A         " + path);
                break;
            case NotifyAction.delete :
                logMessage("D         " + path);
                receivedSomeChange = true;
                break;
            case NotifyAction.update_update :
                boolean error = false;
                if (!((kind == NodeKind.dir)
                    && ((propState == NotifyStatus.inapplicable)
                        || (propState == NotifyStatus.unknown)
                        || (propState == NotifyStatus.unchanged)))) {
                    receivedSomeChange = true;
                    char[] statecharBuf = new char[] { ' ', ' ' };
                    if (kind == NodeKind.file) {
                        if (contentState == NotifyStatus.conflicted) {
                            statecharBuf[0] = 'C';
                            conflicts += 1;
                            error = true;
                        }
                        else if (contentState == NotifyStatus.merged) {
                            statecharBuf[0] = 'G';
                            merges += 1;
                            error = true;
                        }
                        else if (contentState == NotifyStatus.changed) {
                            statecharBuf[0] = 'U';
                            updates += 1;
                        }
                        else if (contentState == NotifyStatus.unchanged && command == ISVNNotifyListener.Command.MERGE
                                && propState < NotifyStatus.obstructed)
                            break;
                    }
                    if (propState == NotifyStatus.conflicted) {
                        statecharBuf[1] = 'C';
                        propConflicts += 1;
                        error = true;
                    }
                    else if (propState == NotifyStatus.merged) {
                        statecharBuf[1] = 'G';
                        propMerges += 1;
                        error = true;
                    }
                    else if (propState == NotifyStatus.changed) {
                        statecharBuf[1] = 'U';
                        propUpdates += 1;
                    }
                    if (error)
                        logError("" + statecharBuf[0] + statecharBuf[1] + " " + path);                      
                    else
                        logMessage("" + statecharBuf[0] + statecharBuf[1] + " " + path);                      
                }
                break;
            case NotifyAction.update_external :
                logMessage("Updating external location at: " + path);
                break;
            case NotifyAction.update_completed :
                notify = false;
                if (revision >= 0) {
                    logRevision( revision );

                    if (command == ISVNNotifyListener.Command.EXPORT) {
                        logCompleted("Exported revision "+revision+".");
                    }                       
                    else 
                    if (command == ISVNNotifyListener.Command.CHECKOUT) {
                        logCompleted("Checked out revision "+revision+".");
                    }                       
                    else
                    if (receivedSomeChange) {
                        logCompleted("Updated to revision "+revision+".");
                    }
                    else {
                        logCompleted("At revision "+revision+".");
                    }
                } else
                {
                    if (command == ISVNNotifyListener.Command.EXPORT) {
                        logCompleted("Export complete.");
                    }
                    else
                    if (command == ISVNNotifyListener.Command.CHECKOUT) {
                        logCompleted("Checkout complete.");
                    }
                    else {
                        logCompleted("Update complete.");
                    }  
                }
                break;
            case NotifyAction.status_external :
              logMessage("Performing status on external item at "+path);
              notify = false;
              break;
            case NotifyAction.status_completed :
              notify = false;
              if (revision >= 0) {
                logRevision(revision);
                logMessage("Status against revision: "+ revision);
              }
              break;                
            case NotifyAction.commit_modified :
                logMessage("Sending        "+path);
                break;
            case NotifyAction.commit_added :
                logMessage("Adding         "+path);
                break;
            case NotifyAction.commit_deleted :
                logMessage("Deleting       "+path);
                break;
            case NotifyAction.commit_replaced :
                logMessage("Replacing      "+path);
                break;
            case NotifyAction.commit_postfix_txdelta :
                notify = false;
                if (!sentFirstTxdelta) {
                    logMessage("Transmitting file data ...");
                    sentFirstTxdelta = true;
                }
                break;                              
        }
        if (notify) {
            // only when the status changed
            notifyListenersOfChange(path, JhlConverter.convertNodeKind(kind));                
        }
    }

    public void setCommand(int command) {
        receivedSomeChange = false;
        sentFirstTxdelta = false;
        clearStats();
        super.setCommand(command);
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.SVNNotificationHandler#logCompleted(java.lang.String)
     */
    public void logCompleted(String message) {
        super.logCompleted(message);
        logStats();
        
    }

    private void clearStats(){
        adds = 0;
        updates = 0;
        deletes = 0;
        conflicts = 0;
        merges = 0;
        propConflicts = 0;
        propMerges = 0;
        propUpdates = 0;
    }
    
    private void logStats() {
        if (command == ISVNNotifyListener.Command.UPDATE
                || command == ISVNNotifyListener.Command.MERGE
                || command == ISVNNotifyListener.Command.SWITCH) {
	        if (fileStats()) {
	            logMessage("===== File Statistics: =====");
		        if (conflicts > 0)
		            logMessage("   Conflicts: " + conflicts);
		        if (merges > 0)
		            logMessage("      Merged: " + merges);
		        if (deletes > 0)
		            logMessage("     Deleted: " + deletes);
		        if (adds > 0)
		            logMessage("       Added: " + adds);
		        if (updates > 0)
		            logMessage("     Updated: " + updates);
	        }
	        if (propStats()){
	            logMessage("===== Property Statistics: =====");
		        if (propConflicts > 0)
		            logMessage("   Conflicts: " + propConflicts);
		        if (propMerges > 0)
		            logMessage("      Merged: " + propMerges);
		        if (propUpdates > 0)
		            logMessage("     Updated: " + propUpdates);
	        }
        }
    }
    
    private boolean fileStats() {
        if (updates > 0 || adds > 0 || deletes > 0 
                || conflicts > 0 || merges > 0)
            return true;
        return false;
    }
    
    private boolean propStats() {
        if (propUpdates > 0
                || propConflicts > 0
                || propMerges > 0)
            return true;
        return false;
    }
}
