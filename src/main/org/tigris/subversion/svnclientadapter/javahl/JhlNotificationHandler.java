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
package org.tigris.subversion.svnclientadapter.javahl;

import java.io.File;
import java.io.IOException;

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
                if (!((kind == NodeKind.dir)
                    && ((propState == Notify.Status.inapplicable)
                        || (propState == Notify.Status.unknown)
                        || (propState == Notify.Status.unchanged)))) {
                    receivedSomeChange = true;
                    char[] statecharBuf = new char[] { ' ', ' ' };
                    if (kind == NodeKind.file) {
                        if (contentState == Notify.Status.conflicted)
                            statecharBuf[0] = 'C';
                        else if (contentState == Notify.Status.merged)
                            statecharBuf[0] = 'G';
                        else if (contentState == Notify.Status.changed)
                            statecharBuf[0] = 'U';
                    }
                    if (propState == Notify.Status.conflicted)
                        statecharBuf[1] = 'C';
                    else if (propState == Notify.Status.merged)
                        statecharBuf[1] = 'G';
                    else if (propState == Notify.Status.changed)
                        statecharBuf[1] = 'U';
                    logMessage(statecharBuf[0]+statecharBuf[1]+" "+path);                      
                }
                break;
            case Notify.Action.update_completed :
                notify = false;
                if (revision >= 0) {
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
              if (revision >= 0)
                logMessage("Status against revision: "+ revision);
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
