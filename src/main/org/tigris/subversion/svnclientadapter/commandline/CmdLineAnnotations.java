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

import java.io.InputStream;
import java.util.Date;

import org.tigris.subversion.svnclientadapter.AnnotateInputStream;
import org.tigris.subversion.svnclientadapter.ISVNAnnotations;
import org.tigris.subversion.svnclientadapter.StringUtils;

/**
 * Handles annotations (see svn ann) 
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 */
public class CmdLineAnnotations implements ISVNAnnotations {

    private String[] lines;


    public CmdLineAnnotations() {
    }

    public CmdLineAnnotations(String annotations, String lineSeparator) {
    	lines = StringUtils.split(annotations, lineSeparator);
    }
    
    public CmdLineAnnotations(byte[] annotations, String lineSeparator) {
        this(new String(annotations), lineSeparator);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getRevision(int)
     */
    public long getRevision(int lineNumber) {
        if (lineNumber >= lines.length)
            return -1;
        else
        {
            String line = lines[lineNumber];
            String version = line.substring(0,6).trim();
            if (version.equals("-")) {
            	// if we annotate from revision 2 to HEAD, the author and revision 
            	// will be "-" if some lines have revision 1
            	return -1;
            } else {
                return Integer.parseInt(version);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getChanged(int)
     */
    public Date getChanged(int lineNumber) {
    	//Client adapter does not support verbose output with dates
    	return null;
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getAuthor(int)
     */
    public String getAuthor(int lineNumber) {
        if (lineNumber >= lines.length)
            return null;
        else
        {
        	String line = lines[lineNumber];
        	String author = StringUtils.stripStart(line.substring(7,17),null);
        	

            if (author.equals("-")) {
            	// if we annotate from revision 2 to HEAD, the author and revision 
            	// will be "-" if some lines have revision 1
            	return null;
            } else {
                return author;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getLine(int)
     */
    public String getLine(int lineNumber) {
        if (lineNumber >= lines.length)
            return null;
        else
        {
            String line = lines[lineNumber];
            return line.substring(18);            
        }
    }    

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#getInputStream()
	 */
	public InputStream getInputStream() {
		return new AnnotateInputStream(this);
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNAnnotations#size()
	 */
	public int size() {
		return lines.length;
	}
}
