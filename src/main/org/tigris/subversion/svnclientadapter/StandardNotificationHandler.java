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
package org.tigris.subversion.svnclientadapter;

import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Notify;

/**
 * This is the standard notification handler
 * It has the same output than the standard svn client
 * 
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 *
 */
public class StandardNotificationHandler implements ISVNNotifyListener {
    final public static int LOG_MESSAGE = 1;
    final public static int LOG_COMPLETED = 2;
    final public static int LOG_ERROR = 3;
    
	private boolean receivedSomeChange;
	private boolean sentFirstTxdelta;
    private int command;
    private String commandLine;
	
    /**
     * this method is called by onNotify.
     * You will probably want to override this method
     * @param logType either LOG_MESSAGE, LOG_COMPLETED or LOG_ERROR
     * @param message
     */
	protected void log(int logType, String message) {
		System.out.println(message);
	}

    public void setCommand(int command) {
        this.command = command;
        receivedSomeChange = false;
        sentFirstTxdelta = false;
    }
    
    public void setCommandLine(String commandLine) {
       this.commandLine = commandLine; 
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
	public void onNotify(
		String path,
		int action,
		int kind,
		String mimeType,
		int contentState,
		int propState,
		long revision) {

		switch (action) {
			case Notify.Action.skip :
				log(LOG_MESSAGE,"Skipped " + path);
				break;
			case Notify.Action.update_delete :
				log(LOG_MESSAGE,"D  " + path);
				receivedSomeChange = true;
				break;
			case Notify.Action.update_add :
				log(LOG_MESSAGE,"A  " + path);
				receivedSomeChange = true;
				break;
			case Notify.Action.restore :
				log(LOG_MESSAGE,"Restored " + path);
				break;
			case Notify.Action.revert :
				log(LOG_MESSAGE,"Reverted " + path);
				break;
			case Notify.Action.failed_revert :
				log(LOG_ERROR,"Failed to revert " + path + " -- try updating instead.");
				break;
			case Notify.Action.resolve :
                log(LOG_MESSAGE,"Resolved conflicted state of " + path);
				break;
			case Notify.Action.add :
                log(LOG_MESSAGE,"A         " + path);
				break;
			case Notify.Action.delete :
                log(LOG_MESSAGE,"D         " + path);
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
                    log(LOG_MESSAGE,statecharBuf[0]+statecharBuf[1]+" "+path);                    	
				}
				break;
			case Notify.Action.update_completed :
            	if (revision >= 0) {
					if (command == ISVNNotifyListener.COMMAND_EXPORT)
                        log(LOG_COMPLETED,"Exported revision "+revision+".");
					else 
					if (command == ISVNNotifyListener.COMMAND_CHECKOUT)
                        log(LOG_COMPLETED,"Checked out revision "+revision+".");
					else
					if (receivedSomeChange)
                        log(LOG_COMPLETED,"Updated to revision "+revision+".");
					else
                        log(LOG_COMPLETED,"At revision "+revision+".");
	           	} else
	           	{
	           		if (command == ISVNNotifyListener.COMMAND_EXPORT)
                        log(LOG_COMPLETED,"Export complete.");
	           		else
	           		if (command == ISVNNotifyListener.COMMAND_CHECKOUT)
                        log(LOG_COMPLETED,"Checkout complete.");
	           		else
                        log(LOG_COMPLETED,"Update complete."); 
	           	}
				break;
			case Notify.Action.commit_modified :
                log(LOG_MESSAGE,"Sending        "+path);
				break;
			case Notify.Action.commit_added :
                log(LOG_MESSAGE,"Adding         "+path);
				break;
			case Notify.Action.commit_deleted :
                log(LOG_MESSAGE,"Deleting       "+path);
				break;
			case Notify.Action.commit_replaced :
                log(LOG_MESSAGE,"Replacing      "+path);
				break;
			case Notify.Action.commit_postfix_txdelta :
				if (!sentFirstTxdelta) {
                    log(LOG_MESSAGE,"Transmitting file data ...");
					sentFirstTxdelta = true;
				}
				break;				            	
		}
	}

    /**
     * Called when a method of SVNClientAdapter throw an exception
     */
    public void setException(ClientException clientException) {
        Throwable e = clientException;
        while (e != null) {
            log(LOG_ERROR,e.getMessage());
            e = e.getCause();
        }
    }

}
