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
package org.tigris.subversion.svnclientadapter.commandline;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;

/**
 * Concrete implementation of SVNClientAdapterFactory for command line interface.
 * To register this factory, just call {@link CmdLineClientAdapterFactory#setup()} 
 */
public class CmdLineClientAdapterFactory extends SVNClientAdapterFactory {
    public static final String COMMANDLINE_CLIENT = "commandline";
    
    private static boolean is13ClientAvailable = false;
    
    private CmdLineClientAdapterFactory() {
    }

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#createSVNClientImpl()
	 */
	protected ISVNClientAdapter createSVNClientImpl() {
		if (is13ClientAvailable) {
			return new CmdLineClientAdapter(new CmdLineNotificationHandler());
		} else {
			return new CmdLineClientAdapter12(new CmdLineNotificationHandler());
		}
	}

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory#getClientType()
     */
    protected String getClientType() {
        return COMMANDLINE_CLIENT;
    }    
    
    public static void setup() throws SVNClientException {
        if (!CmdLineClientAdapter12.isAvailable()) {
            throw new SVNClientException("Command line client adapter is not available");
        }
        
        is13ClientAvailable = CmdLineClientAdapter.isAvailable();
        
        SVNClientAdapterFactory.registerAdapterFactory(new CmdLineClientAdapterFactory());
    }

}
