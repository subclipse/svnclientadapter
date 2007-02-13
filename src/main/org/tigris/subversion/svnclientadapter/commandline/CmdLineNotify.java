/*******************************************************************************
 * Copyright (c) 2005, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.commandline;

/**
 * Subversion notification interface.
 *
 * Implement this interface and implement the onNotify method
 * to provide a custom notification handler to the Modify
 * class.
 * If you need to pass extra information to the notification
 * handler then just add these to you implementing class
 */
public interface CmdLineNotify
{
    /**
     * Handler for Subversion notifications.
     *
     * Override this function to allow Subversion to
     * send notifications
     * @param path on which action happen
     * @param action subversion action, see svn_wc_notify_action_t
     * @param kind node kind of path after action occurred
     * @param mimeType mime type of path after action occurred
     * @param contentState state of content after action occurred
     * @param propState state of properties after action occurred
     * @param revision revision number  after action occurred
     */
    public void onNotify(String path, int action, int kind, String mimeType, int contentState, int propState, long revision);

    /**
     * The type of action occuring.
     * The values are defined in the interface NotifyAction for building reasons.
     */
    public static final class Action implements CmdLineNotifyAction
    {
        /**
         * Returns the textual representation for the action kind
         * @param action kind of action
         * @return english text
         */
		public static final String getActionName(int action)
		{
			return CmdLineNotifyAction.actionNames[action];
		}

    }
    /**
     * The type of notification that is occuring.
     * The values are defined in the interface NotifyStatus for building reasons.
     */
    public static final class Status implements CmdLineNotifyStatus
   {
        /**
         * Returns the textual representation for the notification type
         * @param status    type of the notification
         * @return  english text
         */
		public static final String getStatusName(int status)
		{
			return CmdLineNotifyStatus.statusNames[status];
		}
    }
}
