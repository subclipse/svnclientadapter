/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
package org.tigris.subversion.svnclientadapter.commandline;

import java.util.Date;
import java.util.StringTokenizer;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * 
 * @author philip schatz
 */
public class CmdLineLogMessage implements ISVNLogMessage {

	private SVNRevision.Number rev;
	private String author;
	private Date date;
	private String msg;

	/**
	 * ------------------------------------------------------------------------
	 * rev 49:  phil | 2003-06-30 00:14:58 -0500 (Mon, 30 Jun 2003) | 1 line
	 *
	 * Initial version of jabclipse added to svn. Still has hardcoded JID
	 * ------------------------------------------------------------------------
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
		StringTokenizer leftToken = new StringTokenizer(left, " :");
		leftToken.nextToken(); //discard first bit.
		rev = Helper.toRevNum(leftToken.nextToken());
		author = leftToken.nextToken();
		
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

}
