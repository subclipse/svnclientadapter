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
package org.tigris.subversion.svnclientadapter;

import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapter;
import org.tigris.subversion.svnclientadapter.javahl.JhlClientAdapter;

/**
 * Factory for SVNClientAdapter 
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 * 
 * @author Panagiotis Korros 
 *         <a href="mailto:pkorros@bigfoot.com">pkorros@bigfoot.com</a>
 * 
 */
public class SVNClientAdapterFactory {

    public static int JAVAHL_CLIENT = 1;
    public static int COMMANDLINE_CLIENT = 2;

    /**
     * creates a new ISVNClientAdapter. You can create a javahl client or a command line
     * client.
     * 
     * @param clientType
     * @return the client adapter that was requested or null if that client adapter is not
     *         available or doesn't exist.
     */
    public static ISVNClientAdapter createSVNClient(int clientType) {
        if (clientType == JAVAHL_CLIENT && JhlClientAdapter.isAvailable() )
        	return new JhlClientAdapter();
        if (clientType == COMMANDLINE_CLIENT && CmdLineClientAdapter.isAvailable() )
            return new CmdLineClientAdapter();
        return null;
    }

    /**
     * tells if the given clientType is available or not
     * 
     * @param clientType
     * @return
     */
    public static boolean isSVNClientAvailable(int clientType) {
        if (clientType == COMMANDLINE_CLIENT)
        {
            return CmdLineClientAdapter.isAvailable();
        } 
        else
        {
            return JhlClientAdapter.isAvailable();
        }
    }

	/**
	 * @return the best svn client interface
	 * @throws SVNClientException
	 */
	public static int getBestSVNClientType() throws SVNClientException {
		if (JhlClientAdapter.isAvailable())
			return JAVAHL_CLIENT;
		else
		if (CmdLineClientAdapter.isAvailable())
			return COMMANDLINE_CLIENT;
		else
			throw new SVNClientException("No subversion client interface found.");
	}

}
