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

import java.io.File;
import java.util.Date;

/**
 * Give information about one subversion item (file or directory) in the
 * working copy
 */
public interface ISVNInfo
{

    /**
     * file on which we get info
     * @return file
     */
    public File getFile();

    /**
     * Retrieves the url of the item
     * @return url of the item
     */
    public SVNUrl getUrl();

    /**
     * Retrieves the uuid of the repository
     * @return  uuid of the repository
     */
    public String getUuid();

    /**
     * Retrieves the url of the repository
     * @return url of the repository
     */
    public SVNUrl getRepository();

    /**
     * Retrieves the schedule of the next commit
     * @return schedule of the next commit
     */
    public SVNScheduleKind getSchedule();

    /**
     * Retrieves the nodeKind
     * @return nodeKind
     */
    public SVNNodeKind getNodeKind();

    /**
     * Retrieves the author of the last commit
     * @return author of the last commit
     */
    public String getLastCommitAuthor();

    /**
     * Retrieves the last revision the item was updated to
     * @return last revision the item was updated to
     */
    public SVNRevision.Number getRevision();

    /**
     * Retrieves the revision of the last commit
     * @return the revision of the last commit
     */
    public SVNRevision.Number getLastChangedRevision();

    /**
     * Retrieves the date of the last commit
     * @return the date of the last commit
     */
    public Date getLastChangedDate();

    /**
     * Retrieves the last date the text content was changed
     * @return last date the text content was changed
     */
    public Date getLastDateTextUpdate();

    /**
     * Retrieves the last date the properties were changed
     * @return last date the properties were changed
     */
    public Date getLastDatePropsUpdate();

    /**
     * Retrieve if the item was copied
     * @return the item was copied
     */
    public boolean isCopied();

    /**
     * Retrieves the copy source revision
     * @return copy source revision
     */
    public SVNRevision.Number getCopyRev();

    /**
     * Retrieves the copy source url
     * @return copy source url
     */
    public SVNUrl getCopyUrl();
}
