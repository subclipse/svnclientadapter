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
package org.tigris.subversion.svnclientadapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.tigris.subversion.javahl.Revision;

/**
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 *
 */
public class RevisionUtils {

	
	/**
	 * get a revision from a string
	 * revision can be :
	 * - a date with the following format : MM/DD/YYYY HH:MM AM_PM
	 * - a revision number
	 * - HEAD, BASE, COMMITED or PREV
	 * 
	 * @param revision
	 * @return Revision
	 */
	public static Revision getRevision(String revision) throws ParseException {
		
		// try special KEYWORDS
		if (revision.compareToIgnoreCase("HEAD") == 0)
			return Revision.HEAD; // latest in repository
		else
		if (revision.compareToIgnoreCase("BASE") == 0)
			return new Revision(Revision.Kind.base); // base revision of item's working copy
		else
		if (revision.compareToIgnoreCase("COMMITED") == 0)
			return new Revision(Revision.Kind.committed); // revision of item's last commit
		else
		if (revision.compareToIgnoreCase("PREV") == 0) // revision before item's last commit
			return new Revision(Revision.Kind.previous);
		
		// try revision number
		try
		{
			int revisionNumber = Integer.parseInt(revision);
			if (revisionNumber >= 0)
				return new Revision.Number(revisionNumber); 
		} catch (NumberFormatException e)
		{
		}
		
		// try date
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
		try
		{
			Date revisionDate = df.parse(revision);
			return new Revision.DateSpec(revisionDate);
		} catch (ParseException e)
		{
		}
		
		throw new ParseException("Invalid revision. Revision should be a number, a date in MM/DD/YYYY HH:MM AM_PM format or HEAD, BASE, COMMITED or PREV",0);
	}

}
