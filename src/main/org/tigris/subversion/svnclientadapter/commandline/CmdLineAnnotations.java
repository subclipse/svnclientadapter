/*
 * Created on 5 août 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.InputStream;

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

    /**
     * get the revision for the given line number
     * @param lineNumber
     * @return
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
    
    /**
     * get the author for the given line number or null
     * @param lineNumber
     * @return
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

    /**
     * get the given line
     * @param lineNumber
     * @return
     */
    public String getLine(int lineNumber) {
        if (lineNumber >= lines.length)
            return null;
        else
        {
            String line = (String)lines[lineNumber];
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
