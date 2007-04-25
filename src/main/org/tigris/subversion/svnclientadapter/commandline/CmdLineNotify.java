/*******************************************************************************
 * Copyright (c) 2005, 2006 svnClientAdapter project and others.
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
