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

import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Notify;
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
public class JhlNotificationHandler extends SVNNotificationHandler implements Notify {
    private boolean receivedSomeChange;
    private boolean sentFirstTxdelta;
    
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
    public void onNotify(
        String path,
        int action,
        int kind,
        String mimeType,
        int contentState,
        int propState,
        long revision) {

        // for some actions, we don't want to call notifyListenersOfChange :
        // when the status of the target has not been modified 
        boolean notify = true;

        switch (action) {
            case Notify.Action.skip :
                logMessage("Skipped " + path);
                notify = false;                                
                break;
            case Notify.Action.update_delete :
                logMessage("D  " + path);
                receivedSomeChange = true;
                break;
            case Notify.Action.update_add :
                logMessage("A  " + path);
                receivedSomeChange = true;
                break;
            case Notify.Action.restore :
                logMessage("Restored " + path);
                break;
            case Notify.Action.revert :
                logMessage("Reverted " + path);
                break;
            case Notify.Action.failed_revert :
                logError("Failed to revert " + path + " -- try updating instead.");
                notify = false;
                break;
            case Notify.Action.resolved :
                logMessage("Resolved conflicted state of " + path);
                break;
            case Notify.Action.add :
                logMessage("A         " + path);
                break;
            case Notify.Action.delete :
                logMessage("D         " + path);
                receivedSomeChange = true;
                break;
            case Notify.Action.update_update :
                boolean error = false;
                if (!((kind == NodeKind.dir)
                    && ((propState == Notify.Status.inapplicable)
                        || (propState == Notify.Status.unknown)
                        || (propState == Notify.Status.unchanged)))) {
                    receivedSomeChange = true;
                    char[] statecharBuf = new char[] { ' ', ' ' };
                    if (kind == NodeKind.file) {
                        if (contentState == Notify.Status.conflicted) {
                            statecharBuf[0] = 'C';
                            error = true;
                        }
                        else if (contentState == Notify.Status.merged) {
                            statecharBuf[0] = 'G';
                            error = true;
                        }
                        else if (contentState == Notify.Status.changed)
                            statecharBuf[0] = 'U';
                        else if (contentState == Notify.Status.unchanged && command == ISVNNotifyListener.Command.MERGE
                                && propState < Notify.Status.obstructed)
                            break;
                    }
                    if (propState == Notify.Status.conflicted) {
                        statecharBuf[1] = 'C';
                        error = true;
                    }
                    else if (propState == Notify.Status.merged) {
                        statecharBuf[1] = 'G';
                        error = true;
                    }
                    else if (propState == Notify.Status.changed)
                        statecharBuf[1] = 'U';
                    if (error)
                        logError("" + statecharBuf[0] + statecharBuf[1] + " " + path);                      
                    else
                        logMessage("" + statecharBuf[0] + statecharBuf[1] + " " + path);                      
                }
                break;
            case Notify.Action.update_external :
                logMessage("Updating external location at: " + path);
                break;
            case Notify.Action.update_completed :
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
            case Notify.Action.status_external :
              logMessage("Performing status on external item at "+path);
              notify = false;
              break;
            case Notify.Action.status_completed :
              notify = false;
              if (revision >= 0) {
                logRevision(revision);
                logMessage("Status against revision: "+ revision);
              }
              break;                
            case Notify.Action.commit_modified :
                logMessage("Sending        "+path);
                break;
            case Notify.Action.commit_added :
                logMessage("Adding         "+path);
                break;
            case Notify.Action.commit_deleted :
                logMessage("Deleting       "+path);
                break;
            case Notify.Action.commit_replaced :
                logMessage("Replacing      "+path);
                break;
            case Notify.Action.commit_postfix_txdelta :
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
        super.setCommand(command);
    }
}
