/*******************************************************************************
 * Copyright (c) 2003, 2006 svnClientAdapter project and others.
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
package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.Lock;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Notify2;
import org.tigris.subversion.javahl.NotifyAction;
import org.tigris.subversion.javahl.NotifyInformation;
import org.tigris.subversion.javahl.NotifyStatus;
import org.tigris.subversion.javahl.RevisionRange;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNNotificationHandler;
import org.tigris.subversion.svnclientadapter.utils.Messages;



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
    private int exists;
    private int propConflicts;
    private int propMerges;
    private int propUpdates;
    private boolean inExternal;
    private boolean holdStats;
    private String lastUpdate;
    private String lastExternalUpdate;
    
    private boolean statsCommand = false;
    
    private static final int COMMIT_ACROSS_WC_COMPLETED = -11;
    private static final int ENDED_ABNORMAL = -1;

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
                      info.getLock(),
                      info.getErrMsg(),
                      info.getMergeRange(),
                      info.getChangelistName());
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
        Lock lock,
		String errorMsg,
		RevisionRange mergeRange,
		String changeListName) {

        // for some actions, we don't want to call notifyListenersOfChange :
        // when the status of the target has not been modified 
        boolean notify = true;

        switch (action) {
        		case ENDED_ABNORMAL:
        		   if (command == ISVNNotifyListener.Command.COMMIT)
         		   logError(Messages.bind("notify.commit.abnormal")); //$NON-NLS-1$
        		   else
        		       logError(Messages.bind("notify.end.abnormal")); //$NON-NLS-1$
        		   if (errorMsg != null)
        			  logError(errorMsg); 
                notify = false;                                
                break;
        	case NotifyAction.merge_begin :
        		if (mergeRange != null) {
	        		if (mergeRange.getFromRevision().equals(mergeRange.getToRevision()))
	        			logMessage("Merging r" + mergeRange.getFromRevision().toString());
	        		else
	        			logMessage("Merging r" + mergeRange.getFromRevision().toString() + " through r" + mergeRange.getToRevision().toString());
	        	}
        		notify = false;
        		break;
            case NotifyAction.skip :
                logMessage(Messages.bind("notify.skipped", path)); //$NON-NLS-1$
                notify = false;                                
                break;
            case NotifyAction.failed_lock: 
            	if (errorMsg == null)
            		logError(Messages.bind("notify.lock.failed", path)); //$NON-NLS-1$
            	else
            		logError(errorMsg);
                notify = false;
                break;
            case NotifyAction.failed_unlock:
            	if (errorMsg == null)
            		logError(Messages.bind("notify.unlock.failed", path)); //$NON-NLS-1$
            	else
            		logError(errorMsg);
            	notify = false;
            	break;
            case NotifyAction.locked:
                if (lock != null && lock.getOwner() != null)
                    logMessage(Messages.bind("notify.lock.other", lock.getPath(), lock.getOwner())); //$NON-NLS-1$
                else
                    logMessage(Messages.bind("notify.lock", path)); //$NON-NLS-1$
        	    notify = false; // for JavaHL bug
            	break;
            case NotifyAction.unlocked:
                logMessage(Messages.bind("notify.unlock", path)); //$NON-NLS-1$
            	notify = false; // for JavaHL bug
            	break;
            case NotifyAction.update_delete :
                logMessage("D  " + path); //$NON-NLS-1$
                receivedSomeChange = true;
                deletes += 1;
                break;
            case NotifyAction.update_add :
                logMessage("A  " + path); //$NON-NLS-1$
                receivedSomeChange = true;
                adds += 1;
                break;
            case NotifyAction.exists :
                logMessage("E  " + path); //$NON-NLS-1$
                receivedSomeChange = true;
                exists += 1;
                break;
            case NotifyAction.restore :
                logMessage(Messages.bind("notify.restored", path)); //$NON-NLS-1$
                break;
            case NotifyAction.revert :
                logMessage(Messages.bind("notify.reverted", path)); //$NON-NLS-1$
                break;
            case NotifyAction.failed_revert :
                logError(Messages.bind("notify.revert.failed", path)); //$NON-NLS-1$
                notify = false;
                break;
            case NotifyAction.resolved :
                logMessage(Messages.bind("notify.resolved", path)); //$NON-NLS-1$
                break;
            case NotifyAction.add :
                logMessage("A         " + path); //$NON-NLS-1$
                break;
            case NotifyAction.delete :
                logMessage("D         " + path); //$NON-NLS-1$
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
                        logError("" + statecharBuf[0] + statecharBuf[1] + " " + path);                       //$NON-NLS-1$ //$NON-NLS-2$
                    else
                        logMessage("" + statecharBuf[0] + statecharBuf[1] + " " + path);                       //$NON-NLS-1$ //$NON-NLS-2$
                }
                break;
            case NotifyAction.update_external :
                logMessage(Messages.bind("notify.update.external", path)); //$NON-NLS-1$
            	inExternal = true;
                break;
            case NotifyAction.update_completed :
                notify = false;
                if (revision >= 0) {
                    logRevision( revision, path );

                    if (command == ISVNNotifyListener.Command.EXPORT) {
                        logCompleted(Messages.bind("notify.export", Long.toString(revision))); //$NON-NLS-1$
                    }                       
                    else 
                    if (command == ISVNNotifyListener.Command.CHECKOUT) {
                        logCompleted(Messages.bind("notify.checkout", Long.toString(revision))); //$NON-NLS-1$
                    }                       
                    else
                    if (receivedSomeChange) {
                        if (holdStats) {
                        // Hold off until the releaseStats() method
                        // is executed.  Keeps noise out of the log.
                            if (inExternal)
                                lastExternalUpdate = Messages.bind("notify.update", Long.toString(revision)); //$NON-NLS-1$
                            else
                                lastUpdate = Messages.bind("notify.update", Long.toString(revision)); //$NON-NLS-1$
                            
                        } else
                            logCompleted(Messages.bind("notify.update", Long.toString(revision))); //$NON-NLS-1$
                    }
                    else {
                        logCompleted(Messages.bind("notify.at", Long.toString(revision))); //$NON-NLS-1$
                    }
                } else
                {
                    if (command == ISVNNotifyListener.Command.EXPORT) {
                        logCompleted(Messages.bind("notify.export.complete")); //$NON-NLS-1$
                    }
                    else
                    if (command == ISVNNotifyListener.Command.CHECKOUT) {
                        logCompleted(Messages.bind("notify.checkout.complete")); //$NON-NLS-1$
                    }
                    else {
                        logCompleted(Messages.bind("notify.update.complete")); //$NON-NLS-1$
                    }  
                }
                break;
            case NotifyAction.status_external :
              if (!skipCommand())
                logMessage(Messages.bind("notify.status.external", path)); //$NON-NLS-1$
              notify = false;
              break;
            case NotifyAction.status_completed :
              notify = false;
              if (revision >= 0) {
                logRevision(revision, path);
                if (!skipCommand())
                    logMessage(Messages.bind("notify.status.revision", Long.toString(revision))); //$NON-NLS-1$
              }
              break;                
            case NotifyAction.commit_modified :
                logMessage(Messages.bind("notify.commit.modified", path)); //$NON-NLS-1$
                break;
            case NotifyAction.commit_added :
                logMessage(Messages.bind("notify.commit.add", path)); //$NON-NLS-1$
                break;
            case NotifyAction.commit_deleted :
                logMessage(Messages.bind("notify.commit.delete", path)); //$NON-NLS-1$
                break;
            case NotifyAction.commit_replaced :
                logMessage(Messages.bind("notify.commit.replace", path)); //$NON-NLS-1$
                break;
            case NotifyAction.commit_postfix_txdelta :
                notify = false;
                if (!sentFirstTxdelta) {
                    logMessage(Messages.bind("notify.commit.transmit")); //$NON-NLS-1$
                    sentFirstTxdelta = true;
                }
                break;                              
            case COMMIT_ACROSS_WC_COMPLETED :
                notify = false;
                logCompleted(Messages.bind("notify.commit", Long.toString(revision))); //$NON-NLS-1$
        }
        if (notify) {
            // only when the status changed
            notifyListenersOfChange(path, JhlConverter.convertNodeKind(kind));                
        }
    }

    public void setCommand(int command) {
        receivedSomeChange = false;
        sentFirstTxdelta = false;
        if (command == ISVNNotifyListener.Command.UPDATE
                || command == ISVNNotifyListener.Command.MERGE
                || command == ISVNNotifyListener.Command.SWITCH) {
        	clearStats();
        	statsCommand = true;
        }
        super.setCommand(command);
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.SVNNotificationHandler#logCompleted(java.lang.String)
     */
    public void logCompleted(String message) {
        super.logCompleted(message);
        if (inExternal)
            inExternal = false;
        else
            logStats();
    }

    private void clearStats(){
        adds = 0;
        updates = 0;
        deletes = 0;
        conflicts = 0;
        merges = 0;
        exists = 0;
        propConflicts = 0;
        propMerges = 0;
        propUpdates = 0;
        inExternal = false;
        holdStats = false;
        lastUpdate = null;
        lastExternalUpdate = null;
    }
    
    private void logStats() {
        if (holdStats)
            return;
        if (statsCommand) {
	        if (fileStats()) {
	            logMessage(Messages.bind("notify.stats.file.head")); //$NON-NLS-1$
		        if (conflicts > 0)
		            logMessage(Messages.bind("notify.stats.conflict", Integer.toString(conflicts))); //$NON-NLS-1$
		        if (merges > 0)
		            logMessage(Messages.bind("notify.stats.merge", Integer.toString(merges))); //$NON-NLS-1$
		        if (deletes > 0)
		            logMessage(Messages.bind("notify.stats.delete", Integer.toString(deletes))); //$NON-NLS-1$
		        if (adds > 0)
		            logMessage(Messages.bind("notify.stats.add", Integer.toString(adds))); //$NON-NLS-1$
		        if (updates > 0)
		            logMessage(Messages.bind("notify.stats.update", Integer.toString(updates))); //$NON-NLS-1$
		        if (exists > 0)
		            logMessage(Messages.bind("notify.stats.exists", Integer.toString(exists))); //$NON-NLS-1$
	        }
	        if (propStats()){
	            logMessage(Messages.bind("notify.stats.prop.head")); //$NON-NLS-1$
		        if (propConflicts > 0)
		            logMessage(Messages.bind("notify.stats.conflict", Integer.toString(propConflicts))); //$NON-NLS-1$
		        if (propMerges > 0)
		            logMessage(Messages.bind("notify.stats.merge", Integer.toString(propMerges))); //$NON-NLS-1$
		        if (propUpdates > 0)
		            logMessage(Messages.bind("notify.stats.update", Integer.toString(propUpdates))); //$NON-NLS-1$
	        }
	        statsCommand = false;
	        clearStats();
        }
    }
    
    private boolean fileStats() {
        if (updates > 0 || adds > 0 || deletes > 0 
                || conflicts > 0 || merges > 0 || exists > 0)
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

   
    /**
     * Put a hold on the logging of stats.  This method allows
     * the update method to hold off logging stats until all of
     * a set of updates are completed.
     */
    public void holdStats() {
        this.holdStats = true;
    }
    
    
    /**
     * Perform the logging of any accumulated stats.
     * The update method will call this after the command completes
     * so that the stats logging can wait until the very end.
     */
    public void releaseStats() {
        this.holdStats = false;
        if (command == ISVNNotifyListener.Command.UPDATE) {
            // In addition to the stats, need to send the 
            // Updated to revision N. messages that normally
            // appear in the log.
            if (lastExternalUpdate != null)
                logCompleted(lastExternalUpdate);
            if (lastUpdate != null)
                logCompleted(lastUpdate);
        }
        logStats();
    }
}
