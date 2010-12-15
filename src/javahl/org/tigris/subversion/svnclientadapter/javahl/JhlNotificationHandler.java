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

import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Notify2;
import org.tigris.subversion.javahl.NotifyAction;
import org.tigris.subversion.javahl.NotifyInformation;
import org.tigris.subversion.javahl.NotifyStatus;
import org.tigris.subversion.javahl.Revision;
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
    private int treeConflicts;
    private int propMerges;
    private int propUpdates;
    private boolean inExternal;
    private boolean holdStats;
    private String lastUpdate;
    private String lastExternalUpdate;
    
    private boolean statsCommand = false;
    
    private static final int COMMIT_ACROSS_WC_COMPLETED = -11;
    private static final int ENDED_ABNORMAL = -1;

    public void onNotify(NotifyInformation info) {

        // for some actions, we don't want to call notifyListenersOfChange :
        // when the status of the target has not been modified 
        boolean notify = true;

        switch (info.getAction()) {
        		case ENDED_ABNORMAL:
        		   if (command == ISVNNotifyListener.Command.COMMIT)
         		   logError(Messages.bind("notify.commit.abnormal")); //$NON-NLS-1$
        		   else
        		       logError(Messages.bind("notify.end.abnormal")); //$NON-NLS-1$
        		   if (info.getErrMsg() != null)
        			  logError(info.getErrMsg()); 
                notify = false;                                
                break;
        	case NotifyAction.foreign_merge_begin :
        		if (info.getMergeRange() != null) {
	        		if (info.getMergeRange().getFromRevision().equals(info.getMergeRange().getToRevision()))
	        			logMessage("--- Merging (from foreign repository) r" + info.getMergeRange().getFromRevision().toString() + " into " + info.getPath());
	        		else
	        			if (info.getMergeRange().getToRevision().equals(Revision.HEAD) || 
	        					RevisionRange.getRevisionAsLong(info.getMergeRange().getToRevision()).longValue() > RevisionRange.getRevisionAsLong(info.getMergeRange().getFromRevision()).longValue())
	        				logMessage("--- Merging (from foreign repository) r" + info.getMergeRange().getFromRevision().toString() + " through r" + info.getMergeRange().getToRevision().toString() + " into " + info.getPath());
	        			else
	        				logMessage("--- Reverse-merging (from foreign repository) r" + info.getMergeRange().getFromRevision().toString() + " through r" + info.getMergeRange().getToRevision().toString() + " into " + info.getPath());
		        } else {
	        		logMessage("--- Merging differences between foreign repository URLs into " + info.getPath());
	        	}
        		notify = false;
        		break;
        	case NotifyAction.merge_begin :
        		if (info.getMergeRange() != null) {
	        		if (info.getMergeRange().getFromRevision().equals(info.getMergeRange().getToRevision()))
	        			logMessage("--- Merging r" + info.getMergeRange().getFromRevision().toString() + " into " + info.getPath());
	        		else
	        			if (info.getMergeRange().getToRevision().equals(Revision.HEAD) || 
	        					RevisionRange.getRevisionAsLong(info.getMergeRange().getToRevision()).longValue() > RevisionRange.getRevisionAsLong(info.getMergeRange().getFromRevision()).longValue())
	        				logMessage("--- Merging r" + info.getMergeRange().getFromRevision().toString() + " through r" + info.getMergeRange().getToRevision().toString() + " into " + info.getPath());
	        			else
	        				logMessage("--- Reverse-merging r" + info.getMergeRange().getFromRevision().toString() + " through r" + info.getMergeRange().getToRevision().toString() + " into " + info.getPath());
		        } else {
	        		logMessage("--- Merging differences between repository URLs into " + info.getPath());
	        	}
        		notify = false;
        		break;
            case NotifyAction.skip :
                notify = false;
                if(info.getErrMsg() != null){
                	logError(info.getErrMsg());
                } else {
                 // When there is an error, the skipped message seems to not be useful
	                logMessage(Messages.bind("notify.skipped", info.getPath())); //$NON-NLS-1$
                }
                break;
            case NotifyAction.failed_lock: 
            	if (info.getErrMsg() == null)
            		logError(Messages.bind("notify.lock.failed", info.getPath())); //$NON-NLS-1$
            	else
            		logError(info.getErrMsg());
                notify = false;
                break;
            case NotifyAction.failed_unlock:
            	if (info.getErrMsg() == null)
            		logError(Messages.bind("notify.unlock.failed", info.getPath())); //$NON-NLS-1$
            	else
            		logError(info.getErrMsg());
            	notify = false;
            	break;
            case NotifyAction.locked:
                if (info.getLock() != null && info.getLock().getOwner() != null)
                    logMessage(Messages.bind("notify.lock.other", info.getLock().getPath(), info.getLock().getOwner())); //$NON-NLS-1$
                else
                    logMessage(Messages.bind("notify.lock", info.getPath())); //$NON-NLS-1$
        	    notify = false; // for JavaHL bug
            	break;
            case NotifyAction.unlocked:
                logMessage(Messages.bind("notify.unlock", info.getPath())); //$NON-NLS-1$
            	notify = false; // for JavaHL bug
            	break;
            case NotifyAction.update_delete :
                logMessage("D   " + info.getPath()); //$NON-NLS-1$
                receivedSomeChange = true;
                deletes += 1;
                break;
            case NotifyAction.update_replaced :
                logMessage("R   " + info.getPath()); //$NON-NLS-1$
                receivedSomeChange = true;
                adds += 1;
                deletes += 1;
                break;
            case NotifyAction.update_add :
                logMessage("A   " + info.getPath()); //$NON-NLS-1$
                receivedSomeChange = true;
                adds += 1;
                break;
            case NotifyAction.exists :
                logMessage("E   " + info.getPath()); //$NON-NLS-1$
                receivedSomeChange = true;
                exists += 1;
                break;
            case NotifyAction.restore :
                logMessage(Messages.bind("notify.restored", info.getPath())); //$NON-NLS-1$
                break;
            case NotifyAction.revert :
                logMessage(Messages.bind("notify.reverted", info.getPath())); //$NON-NLS-1$
                break;
            case NotifyAction.failed_revert :
                logError(Messages.bind("notify.revert.failed", info.getPath())); //$NON-NLS-1$
                notify = false;
                break;
            case NotifyAction.resolved :
                logMessage(Messages.bind("notify.resolved", info.getPath())); //$NON-NLS-1$
                break;
            case NotifyAction.add :
                logMessage("A         " + info.getPath()); //$NON-NLS-1$
                break;
            case NotifyAction.delete :
                logMessage("D         " + info.getPath()); //$NON-NLS-1$
                receivedSomeChange = true;
                break;
            case NotifyAction.tree_conflict :
                logError("  C " + info.getPath()); //$NON-NLS-1$
                receivedSomeChange = true;
                treeConflicts += 1;
            	break;
            case NotifyAction.update_update :
                boolean error = false;
                if (!((info.getKind() == NodeKind.dir)
                    && ((info.getPropState() == NotifyStatus.inapplicable)
                        || (info.getPropState() == NotifyStatus.unknown)
                        || (info.getPropState() == NotifyStatus.unchanged)))) {
                    receivedSomeChange = true;
                    char[] statecharBuf = new char[] { ' ', ' ' };
                    if (info.getKind() == NodeKind.file) {
                        if (info.getContentState() == NotifyStatus.conflicted) {
                            statecharBuf[0] = 'C';
                            conflicts += 1;
                            error = true;
                        }
                        else if (info.getContentState() == NotifyStatus.merged) {
                            statecharBuf[0] = 'G';
                            merges += 1;
                            error = true;
                        }
                        else if (info.getContentState() == NotifyStatus.changed) {
                            statecharBuf[0] = 'U';
                            updates += 1;
                        }
                        else if (info.getContentState() == NotifyStatus.unchanged && info.getPropState() < NotifyStatus.obstructed)
                            break;
                    }
                    if (info.getPropState() == NotifyStatus.conflicted) {
                        statecharBuf[1] = 'C';
                        propConflicts += 1;
                        error = true;
                    }
                    else if (info.getPropState() == NotifyStatus.merged) {
                        statecharBuf[1] = 'G';
                        propMerges += 1;
                        error = true;
                    }
                    else if (info.getPropState() == NotifyStatus.changed) {
                        statecharBuf[1] = 'U';
                        propUpdates += 1;
                    }
                    if (info.getContentState() == NotifyStatus.unknown && info.getPropState() == NotifyStatus.unknown)
                    	break;
                    if (error)
                        logError("" + statecharBuf[0] + statecharBuf[1] + "  " + info.getPath());                       //$NON-NLS-1$ //$NON-NLS-2$
                    else
                        logMessage("" + statecharBuf[0] + statecharBuf[1] + "  " + info.getPath());                       //$NON-NLS-1$ //$NON-NLS-2$
                }
                break;
            case NotifyAction.update_external :
                logMessage(Messages.bind("notify.update.external", info.getPath())); //$NON-NLS-1$
            	inExternal = true;
                break;
            case NotifyAction.update_completed :
                notify = false;
                if (info.getRevision() >= 0) {
                    logRevision( info.getRevision(), info.getPath() );

                    if (command == ISVNNotifyListener.Command.EXPORT) {
                        logCompleted(Messages.bind("notify.export", Long.toString(info.getRevision()))); //$NON-NLS-1$
                    }                       
                    else 
                    if (command == ISVNNotifyListener.Command.CHECKOUT) {
                        logCompleted(Messages.bind("notify.checkout", Long.toString(info.getRevision()))); //$NON-NLS-1$
                    }                       
                    else
                    if (receivedSomeChange) {
                        if (holdStats) {
                        // Hold off until the releaseStats() method
                        // is executed.  Keeps noise out of the log.
                            if (inExternal)
                                lastExternalUpdate = Messages.bind("notify.update", Long.toString(info.getRevision())); //$NON-NLS-1$
                            else
                                lastUpdate = Messages.bind("notify.update", Long.toString(info.getRevision())); //$NON-NLS-1$
                            
                        } else
                            logCompleted(Messages.bind("notify.update", Long.toString(info.getRevision()))); //$NON-NLS-1$
                    }
                    else {
                        logCompleted(Messages.bind("notify.at", Long.toString(info.getRevision()))); //$NON-NLS-1$
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
                logMessage(Messages.bind("notify.status.external", info.getPath())); //$NON-NLS-1$
              notify = false;
              break;
            case NotifyAction.status_completed :
              notify = false;
              if (info.getRevision() >= 0) {
                logRevision(info.getRevision(), info.getPath());
                if (!skipCommand())
                    logMessage(Messages.bind("notify.status.revision", Long.toString(info.getRevision()))); //$NON-NLS-1$
              }
              break;                
            case NotifyAction.commit_modified :
                logMessage(Messages.bind("notify.commit.modified", info.getPath())); //$NON-NLS-1$
                break;
            case NotifyAction.commit_added :
                logMessage(Messages.bind("notify.commit.add", info.getPath())); //$NON-NLS-1$
                break;
            case NotifyAction.commit_deleted :
                logMessage(Messages.bind("notify.commit.delete", info.getPath())); //$NON-NLS-1$
                break;
            case NotifyAction.commit_replaced :
                logMessage(Messages.bind("notify.commit.replace", info.getPath())); //$NON-NLS-1$
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
                logCompleted(Messages.bind("notify.commit", Long.toString(info.getRevision()))); //$NON-NLS-1$
                break;
            case NotifyAction.property_added:
            	logMessage(Messages.bind("notify.property.set", info.getPath())); //$NON-NLS-1$
            	break;
            case NotifyAction.property_modified:
            	logMessage(Messages.bind("notify.property.set", info.getPath())); //$NON-NLS-1$
            	break; 
            case NotifyAction.property_deleted:
            	logMessage(Messages.bind("notify.property.deleted", info.getPath())); //$NON-NLS-1$
            	break;
            case NotifyAction.property_deleted_nonexistent:
            	notify = false;
            	logMessage(Messages.bind("notify.property.deleted.nonexistent")); //$NON-NLS-1$
            	break; 
            case NotifyAction.revprop_set:
            	notify = false;
            	logMessage(Messages.bind("notify.revision.property.set")); //$NON-NLS-1$
            	break;  
            case NotifyAction.revprop_deleted:
            	notify = false;
            	logMessage(Messages.bind("notify.revision.property.deleted")); //$NON-NLS-1$
            	break;    
            case NotifyAction.merge_completed:
            	break;                   	
            case NotifyAction.blame_revision:
            	break;                   	
            default:
            	logMessage("Unknown action received: " + info.getAction());
                	
        }
        if (notify) {
            // only when the status changed
            notifyListenersOfChange(info.getPath(), JhlConverter.convertNodeKind(info.getKind()));                
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
        treeConflicts = 0;
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
		        if (propMerges > 0)
		            logMessage(Messages.bind("notify.stats.merge", Integer.toString(propMerges))); //$NON-NLS-1$
		        if (propUpdates > 0)
		            logMessage(Messages.bind("notify.stats.update", Integer.toString(propUpdates))); //$NON-NLS-1$
	        }
	        if (conflictStats()) {
	            logMessage(Messages.bind("notify.stats.conflict.head")); //$NON-NLS-1$
		        if (conflicts > 0)
		            logMessage(Messages.bind("notify.stats.conflict", Integer.toString(conflicts))); //$NON-NLS-1$
		        if (propConflicts > 0)
		            logMessage(Messages.bind("notify.stats.prop.conflicts", Integer.toString(propConflicts))); //$NON-NLS-1$
		        if (treeConflicts > 0) {
		            logMessage(Messages.bind("notify.stats.tree.conflicts", Integer.toString(treeConflicts))); //$NON-NLS-1$
		        }
	        }
	        statsCommand = false;
	        clearStats();
        }
    }
    
    private boolean fileStats() {
        if (updates > 0 || adds > 0 || deletes > 0 
                || merges > 0 || exists > 0)
            return true;
        return false;
    }

    
    private boolean conflictStats() {
        if (treeConflicts > 0 || propConflicts > 0
                || conflicts > 0)
            return true;
        return false;
    }

    private boolean propStats() {
        if (propUpdates > 0               
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