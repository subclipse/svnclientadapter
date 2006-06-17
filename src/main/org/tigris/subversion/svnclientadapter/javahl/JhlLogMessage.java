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

import java.util.Date;

import org.tigris.subversion.javahl.LogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * adapter : convert from LogMessage to ISVNLogMessage
 * 
 * @author philip schatz
 */
public class JhlLogMessage implements ISVNLogMessage {

	private LogMessage _m;

	public JhlLogMessage(LogMessage msg) {
		super();
		_m = msg;
	}


    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getRevision()
     */
	public SVNRevision.Number getRevision() {
		return (SVNRevision.Number)JhlConverter.convert(_m.getRevision());
	}

    /*
     *  (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getAuthor()
     */
	public String getAuthor() {
		return _m.getAuthor();
	}

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getDate()
     */
	public Date getDate() {
		return _m.getDate();
	}

	/*
     * (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getMessage()
	 */
	public String getMessage() {
		return _m.getMessage();
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getMessage();
    }

    /*
     * (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getChangedPaths()
     */
    public ISVNLogMessageChangePath[] getChangedPaths() {
    	return JhlConverter.convert(_m.getChangedPaths());
    }
    
}
