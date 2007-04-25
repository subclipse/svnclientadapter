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
package org.tigris.subversion.svnclientadapter.commandline;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNNotificationHandler;

/**
 * Command line specific extension to generic notification handler
 * 
 * @author Cédric Chabanois (cchabanois@ifrance.com)
 */
public class CmdLineNotificationHandler extends SVNNotificationHandler {

	/**
	 * Log the supplied command line exception as Error
	 * @param e an exception to log
	 */
    public void logException(CmdLineException e) {
        StringTokenizer st = new StringTokenizer(e.getMessage(), Helper.NEWLINE);
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            for (Iterator it = notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener) it.next();
                listener.logError(line);
            }
        }
    }

}
