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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class to specify a revision in a svn command.
 * This class has been copied directly from javahl and renamed to SVNRevision
 * the static method getRevision has then been added to the class
 *
 */
public class SVNRevision 
{
    protected int revKind;

    public SVNRevision(int kind)
    {
        revKind = kind;
    }

    public int getKind()
    {
        return revKind;
    }

    public String toString()
    {
        switch(revKind) {
            case Kind.base : return "BASE";
            case Kind.committed : return "COMMITTED";
            case Kind.head : return "HEAD";
            case Kind.previous : return "PREV";
            case Kind.working : return "WORKING";
        }
        return super.toString();
    }

    public boolean equals(Object target) {
        if (this == target)
            return true;
        if (!(target instanceof SVNRevision))
            return false;

        return ((SVNRevision)target).revKind == revKind;        
    }

    public static final SVNRevision HEAD = new SVNRevision(Kind.head);
    public static final SVNRevision START = new SVNRevision(Kind.unspecified);
    public static final SVNRevision COMMITTED = new SVNRevision(Kind.committed);
    public static final SVNRevision PREVIOUS = new SVNRevision(Kind.previous);
    public static final SVNRevision BASE = new SVNRevision(Kind.base);
    public static final SVNRevision WORKING = new SVNRevision(Kind.working);
    public static final int SVN_INVALID_REVNUM = -1;    
    public static final SVNRevision.Number INVALID_REVISION = new SVNRevision.Number(SVN_INVALID_REVNUM);


    public static class Number extends SVNRevision
    {
        protected long revNumber;

        public Number(long number)
        {
            super(Kind.number);
            revNumber = number;
        }

        public long getNumber()
        {
            return revNumber;
        }

        public String toString() {
            return Long.toString(revNumber);
        }
        
        public boolean equals(Object target) {
            if (!super.equals(target))
                return false;

            return ((SVNRevision.Number)target).revNumber == revNumber;        
        }
    }

    public static class DateSpec extends SVNRevision
    {
        protected Date revDate;
        public DateSpec(Date date)
        {
            super(Kind.date);
            revDate = date;
        }
        public Date getDate()
        {
            return revDate;
        }

        public String toString() {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US); 
            return '{'+dateFormat.format(revDate)+'}';
        }

        public boolean equals(Object target) {
            if (!super.equals(target))
                return false;

            return ((SVNRevision.DateSpec)target).revDate.equals(revDate);        
        }
        
    }

    /** Various ways of specifying revisions.
     *
     * Various ways of specifying revisions.
     *
     * Note:
     * In contexts where local mods are relevant, the `working' kind
     * refers to the uncommitted "working" revision, which may be modified
     * with respect to its base revision.  In other contexts, `working'
     * should behave the same as `committed' or `current'.
     */
    public static final class Kind
    {
        /** No revision information given. */
        public static final int unspecified = 0;

        /** revision given as number */
        public static final int number = 1;

        /** revision given as date */
        public static final int date = 2;

        /** rev of most recent change */
        public static final int committed = 3;

        /** (rev of most recent change) - 1 */
        public static final int previous = 4;

        /** .svn/entries current revision */
        public static final int base = 5;

        /** current, plus local mods */
        public static final int working = 6;

        /** repository youngest */
        public static final int head = 7;

    }
    
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
    public static SVNRevision getRevision(String revision) throws ParseException {

    	if ((revision == null) || (revision.equals("")))
    		return null;
    	
        // try special KEYWORDS
        if (revision.compareToIgnoreCase("HEAD") == 0)
            return SVNRevision.HEAD; // latest in repository
        else
        if (revision.compareToIgnoreCase("BASE") == 0)
            return new SVNRevision(SVNRevision.Kind.base); // base revision of item's working copy
        else
        if (revision.compareToIgnoreCase("COMMITED") == 0)
            return new SVNRevision(SVNRevision.Kind.committed); // revision of item's last commit
        else
        if (revision.compareToIgnoreCase("PREV") == 0) // revision before item's last commit
            return new SVNRevision(SVNRevision.Kind.previous);
        
        // try revision number
        try
        {
            int revisionNumber = Integer.parseInt(revision);
            if (revisionNumber >= 0)
                return new SVNRevision.Number(revisionNumber); 
        } catch (NumberFormatException e)
        {
        }
        
        // try date
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        try
        {
            Date revisionDate = df.parse(revision);
            return new SVNRevision.DateSpec(revisionDate);
        } catch (ParseException e)
        {
        }
        
        throw new ParseException("Invalid revision. Revision should be a number, a date in MM/DD/YYYY HH:MM AM_PM format or HEAD, BASE, COMMITED or PREV",0);
    }    
    
}
