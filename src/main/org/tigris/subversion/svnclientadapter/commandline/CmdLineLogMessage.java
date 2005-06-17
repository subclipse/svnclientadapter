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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * <p>Implements a Log message using "svn log".</p>
 * 
 * @author Philip Schatz (schatz at tigris)
 */
class CmdLineLogMessage implements ISVNLogMessage {

	private SVNRevision.Number rev;
	private String author;
	private Date date;
	private String msg;
    private ISVNLogMessageChangePath[] logMessageChangePaths;
	
	CmdLineLogMessage(
            SVNRevision.Number rev, 
            String author, 
            Date date, 
            String msg,
            ISVNLogMessageChangePath[] logMessageChangePaths){
        this.rev = rev;
        this.author = author;
        this.date = date;
        this.msg = msg;
        this.logMessageChangePaths = logMessageChangePaths;
    }

    /**
     * creates a log message from the output of svn log 
     * This constructor is not used anymore. 
     * The factory method createLogMessages is used instead   
     */
	CmdLineLogMessage(StringTokenizer st) {
		//NOTE: the leading dashes are ommitted by ClientAdapter.
		
		//grab "rev 49:  phil | 2003-06-30 00:14:58 -0500 (Mon, 30 Jun 2003) | 1 line"
		String headerLine = st.nextToken();
		//split the line up into 3 parts, left, middle, and right.
		StringTokenizer ltr = new StringTokenizer(headerLine, "|");
		String left = ltr.nextToken();
		String middle = ltr.nextToken();
		String right = ltr.nextToken();
		
		//Now, we have the header, so set the internal variables
		
		//set info gotten from top-left.
		StringTokenizer leftToken = new StringTokenizer(left, ":");
		String revStr = leftToken.nextToken().trim(); //discard first bit.
		rev = Helper.toRevNum(revStr.substring(4, revStr.length()));
		
		// author is optional
		if(leftToken.hasMoreTokens())
			author = leftToken.nextToken();
		else
			author = "";
		
		//set info from top-mid (date)
		date = Helper.toDate(middle.trim());
		
		//get the number of lines.
		StringTokenizer rightToken = new StringTokenizer(right, " ");
		int messageLineCount = Integer.parseInt(rightToken.nextToken());
		
		//get the body of the log.
		StringBuffer sb = new StringBuffer();
		//st.nextToken(); //next line is always blank.
		for(int i=0; i < messageLineCount; i++) {
			sb.append(st.nextToken());
			
			//dont add a newline to the last line.
			if(i < messageLineCount - 1)
				sb.append('\n');
		}
		msg = sb.toString();
		
		//take off the last dashes "-----------------------------------------"
		st.nextToken();
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNLogMessage#getRevision()
	 */
	public SVNRevision.Number getRevision() {
		return rev;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNLogMessage#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNLogMessage#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.client.ISVNLogMessage#getMessage()
	 */
	public String getMessage() {
		return msg;
	}
	
    /**
     * @return The value of {@link #getMesssage()}.
     */
    public String toString() {
        return getMessage();
    }

    /**
     * creates CmdLineLogMessages from a xml string (see svn log --xml -v) 
     * @param cmdLineResults
     * @return
     */
	public static CmdLineLogMessage[] createLogMessages(String cmdLineResults) throws SVNClientException {
		Collection logMessages = new ArrayList();
		
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
    
			// Create the builder and parse the file
			InputSource source = new InputSource(new StringReader(cmdLineResults));

			Document doc = factory.newDocumentBuilder().parse(source);
			
			NodeList nodes = doc.getElementsByTagName("logentry");
			
			for(int i = 0; i < nodes.getLength(); i++){
				Node logEntry = nodes.item(i);
				
				Node authorNode = logEntry.getFirstChild();
				Node dateNode = authorNode.getNextSibling();
                Node pathsNode = dateNode.getNextSibling();
                Node msgNode = pathsNode.getNextSibling();
                // TODO: mybe get the nodes by their name
                // if the msgNode is empty, the pathsNode is the msgNode
                int pathsNodeLength = 0;
                if (msgNode == null) {
                	msgNode = pathsNode;
                	pathsNode = null;
                } else {
                	pathsNodeLength = pathsNode.getChildNodes().getLength();
                }
                
				Node revisionAttribute = logEntry.getAttributes().getNamedItem("revision");

                SVNRevision.Number rev = Helper.toRevNum(revisionAttribute.getNodeValue());
				String author = authorNode.getFirstChild().getNodeValue();
				Date date = Helper.convertXMLDate(dateNode.getFirstChild().getNodeValue());
				Node msgTextNode = msgNode.getFirstChild();
                String msg;
				if(msgTextNode != null)
					msg = msgTextNode.getNodeValue();
				else
					msg = "";
                
                ISVNLogMessageChangePath[] logMessageChangePath = new ISVNLogMessageChangePath[pathsNodeLength];
                for (int j = 0; j < pathsNodeLength;j++) {
                	Node pathNode = pathsNode.getChildNodes().item(j);
                    String path = pathNode.getFirstChild().getNodeValue();
                    NamedNodeMap attributes = pathNode.getAttributes();
                    char action = attributes.getNamedItem("action").getNodeValue().charAt(0);
                    Node copyFromPathNode = attributes.getNamedItem("copyfrom-path");
                    Node copyFromRevNode = attributes.getNamedItem("copyfrom-rev");
                    String copyFromPath = null;
                    if (copyFromPathNode != null) {
                        copyFromPath = copyFromPathNode.getNodeValue();
                    }
                    SVNRevision.Number copyFromRev = null;
                    if (copyFromRevNode != null) {
                        copyFromRev = Helper.toRevNum(copyFromRevNode.getNodeValue());
                    }
                    logMessageChangePath[j] = new SVNLogMessageChangePath(
                            path, copyFromRev, copyFromPath, action);
                }
                
                CmdLineLogMessage logMessage = new CmdLineLogMessage(rev, author, date, msg, logMessageChangePath);

				logMessages.add(logMessage);				
			}
			
		} catch (Exception e) {
			throw new SVNClientException(e);
		} 
		
		return (CmdLineLogMessage[]) logMessages.toArray(new CmdLineLogMessage[logMessages.size()]);		
	
	}

	/* (non-Javadoc)
	 * @see org.tigris.subversion.svnclientadapter.ISVNLogMessage#getChangedPaths()
	 */
	public ISVNLogMessageChangePath[] getChangedPaths() {
		return logMessageChangePaths;
	}
}
