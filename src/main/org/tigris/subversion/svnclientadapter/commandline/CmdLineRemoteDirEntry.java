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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * <p>
 * Implements a DirEntry on a remote location using the
 * "svn list" command.</p>
 * 
 * @author Philip Schatz (schatz at tigris)
 */
class CmdLineRemoteDirEntry implements ISVNDirEntry {

	//Fields
	private static DateFormat df1 = new SimpleDateFormat("MMM dd hh:mm", Locale.US);
    private static DateFormat df2 = new SimpleDateFormat("MMM dd  yyyy", Locale.US);

	private String path;
	private URL url;
	private SVNRevision.Number revision;
	private SVNNodeKind nodeKind;
	private String lastCommitAuthor;
	private Date lastChangedDate;
	private long size;

	//Constructors
	CmdLineRemoteDirEntry(String baseUrl, String line) {

        // see ls-cmd.c for the format used
        
		int last = line.length() - 1;
		boolean folder = ('/' == line.charAt(last));

		path = (folder) ? line.substring(41, last) : line.substring(41);

		try {
			url = new URL(baseUrl + '/' + path);
		} catch (MalformedURLException e) {
			//do nothing
		}

        // "%7ld %-8.8s %10s %12s %s%s
		revision = new SVNRevision.Number(Long.parseLong(line.substring(0, 7).trim()));
		nodeKind = (folder) ? SVNNodeKind.DIR : SVNNodeKind.FILE;
		lastCommitAuthor = line.substring(8, 16).trim();

        String sizeStr = line.substring(17, 27).trim();
        if (sizeStr.equals("")) {
            // since svn revision 7530, the file size column is left blank for directories
            size = 0;
        }
        else {
		    size = Long.parseLong(sizeStr);
        }
        
        String dateString = line.substring(28, 39);
        
        try {
            // two formats are possible (see ls-cmd.c) depending on the numbers of days between current date
            // and lastChangedDate
            if (dateString.indexOf(':') != -1) {
                // %b %d %H:%M
                lastChangedDate = df1.parse(dateString); // something like "Sep 24 18:01"
            }
            else
            {
                // %b %d  %Y
                lastChangedDate = df2.parse(dateString); // something like "Mar 01  2003"
            }
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	//Methods
	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getHasProps()
	 */
	public boolean getHasProps() {
		//TODO unhardcode this
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getNodeKind()
	 */
	public SVNNodeKind getNodeKind() {
		return nodeKind;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getLastChangedRevision()
	 */
	public SVNRevision.Number getLastChangedRevision() {
		return revision;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getLastChangedDate()
	 */
	public Date getLastChangedDate() {
		return lastChangedDate;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getLastCommitAuthor()
	 */
	public String getLastCommitAuthor() {
		return lastCommitAuthor;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNDirEntry#getPath()
	 */
	public String getPath() {
		return path;
	}

    public long getSize() {
        return size;
    }

}
