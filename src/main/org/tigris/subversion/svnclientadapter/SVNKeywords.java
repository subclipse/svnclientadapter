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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * tells which keywords are enabled for a given resource
 */
public class SVNKeywords {
    
    public static final String LAST_CHANGED_DATE = "LastChangedDate";
    public static final String DATE = "Date";
    
    public static final String LAST_CHANGED_REVISION = "LastChangedRevision";
    public static final String REV = "Rev";
    
    public static final String LAST_CHANGED_BY = "LastChangedBy";
    public static final String AUTHOR = "Author";
    
    public static final String HEAD_URL = "HeadURL";
    public static final String URL = "URL";
    
    public static final String ID = "Id";
    
    private boolean lastChangedDate = false;
    private boolean lastChangedRevision = false;
    private boolean lastChangedBy = false;
    private boolean headUrl = false;
    private boolean id = false;

    public SVNKeywords() {
        
    }

    public SVNKeywords(String keywords) {
        if (keywords == null)
            return;
        StringTokenizer st = new StringTokenizer(keywords," ");

        while (st.hasMoreTokens()) {
            String keyword = st.nextToken();
            // don't know if keywords are case sensitive or no
            if ((keyword.equals(SVNKeywords.HEAD_URL)) ||
                (keyword.equals(SVNKeywords.URL)))
                headUrl = true;
            else
            if (keyword.equals(SVNKeywords.ID))
                id = true;
            else
            if ((keyword.equals(SVNKeywords.LAST_CHANGED_BY)) ||
                (keyword.equals(SVNKeywords.AUTHOR)))
                lastChangedBy = true;
            else
            if ((keyword.equals(SVNKeywords.LAST_CHANGED_DATE)) ||
                (keyword.equals(SVNKeywords.DATE)))
                lastChangedDate = true;
            else
            if ((keyword.equals(SVNKeywords.LAST_CHANGED_REVISION)) ||
                (keyword.equals(SVNKeywords.REV)))
                lastChangedRevision = true;
        }
        
    }
    

    public SVNKeywords(
        boolean lastChangedDate, boolean lastChangedRevision,
        boolean lastChangedBy, boolean headUrl, boolean id) {
    
        this.lastChangedDate = lastChangedDate;
        this.lastChangedRevision = lastChangedRevision;
        this.lastChangedBy = lastChangedBy;
        this.headUrl = headUrl;
        this.id = id;        
    }
    
	public boolean isHeadUrl() {
		return headUrl;
	}

	public boolean isId() {
		return id;
	}

	public boolean isLastChangedBy() {
		return lastChangedBy;
	}

	public boolean isLastChangedDate() {
		return lastChangedDate;
	}

	public boolean isLastChangedRevision() {
		return lastChangedRevision;
	}

    /**
     * 
     * @return the list of keywords
     */
    public List getKeywordsList() {
        ArrayList list = new ArrayList();
        if (headUrl)
            list.add(HEAD_URL);
        if (id)
            list.add(ID);
        if (lastChangedBy)  
            list.add(LAST_CHANGED_BY);
        if (lastChangedDate)
            list.add(LAST_CHANGED_DATE);
        if (lastChangedRevision)
            list.add(LAST_CHANGED_REVISION); 
        return list;
    }

    public String toString()
    {
        String result = "";
        
        for (Iterator it = getKeywordsList().iterator(); it.hasNext();) {
            String keyword = (String) it.next();
            result += keyword;
            if (it.hasNext())
                result += ' '; 
        }
        return result;
    }

	public void setHeadUrl(boolean b) {
		headUrl = b;
	}

	public void setId(boolean b) {
		id = b;
	}

	public void setLastChangedBy(boolean b) {
		lastChangedBy = b;
	}

	public void setLastChangedDate(boolean b) {
		lastChangedDate = b;
	}

	public void setLastChangedRevision(boolean b) {
		lastChangedRevision = b;
	}

}
