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


/**
 * <p>
 * Base class for enumerating the possible types for a <code>Status</code>.
 * </p>
 */
public class SVNStatusKind {
    private final int kind;

    private static final int none = 0;
    private static final int normal = 1;
    private static final int modified = 2;
    private static final int added = 3;
    private static final int deleted = 4;
    private static final int unversioned = 5;
    private static final int missing = 6;
    private static final int replaced = 7;
    private static final int merged = 8;
    private static final int conflicted = 9;
    private static final int obstructed = 10;
    private static final int ignored = 11;
    private static final int incomplete = 12;
    private static final int external = 13;        
    
    public static SVNStatusKind NONE = new SVNStatusKind(none);
    public static SVNStatusKind NORMAL = new SVNStatusKind(normal);
    public static SVNStatusKind ADDED = new SVNStatusKind(added);
    public static SVNStatusKind MISSING = new SVNStatusKind(missing);
    public static SVNStatusKind INCOMPLETE = new SVNStatusKind(incomplete);
    public static SVNStatusKind DELETED = new SVNStatusKind(deleted);
    public static SVNStatusKind REPLACED = new SVNStatusKind(replaced);
    public static SVNStatusKind MODIFIED = new SVNStatusKind(modified);
    public static SVNStatusKind MERGED = new SVNStatusKind(merged);
    public static SVNStatusKind CONFLICTED = new SVNStatusKind(conflicted);
    public static SVNStatusKind OBSTRUCTED = new SVNStatusKind(obstructed);
    public static SVNStatusKind IGNORED = new SVNStatusKind(ignored);
    public static SVNStatusKind EXTERNAL = new SVNStatusKind(external);
    public static SVNStatusKind UNVERSIONED = new SVNStatusKind(unversioned);        
    
    //Constructors
    /**
     * <p>
     * Constructs a <code>Type</code> for the given a type name.</p>
     *
     *
     * @param type Name of the type.
     * @throws IllegalArgumentException If the parameter is invalid.
     */
    private SVNStatusKind(int kind) throws IllegalArgumentException {
        this.kind = kind;
    }
    
    public int toInt() {
    	return kind;
    }
    
    public static SVNStatusKind fromInt(int kind) {
        switch (kind)
        {
        case none:
            return NONE;
        case normal:
            return NORMAL;
        case added:
            return ADDED;
        case missing:
            return MISSING;
        case deleted:
            return DELETED;
        case replaced:
            return REPLACED;
        case modified:
            return MODIFIED;
        case merged:
            return MERGED;
        case conflicted:
            return CONFLICTED;
        case ignored:
            return IGNORED;
        case incomplete:
            return INCOMPLETE;
        case external:
            return EXTERNAL;
        case unversioned:
            return UNVERSIONED;
        default:
            return null;
        }
    }
        
    public String toString() {
        switch (kind)
        {
        case none:
            return "non-svn";
        case normal:
            return "normal";
        case added:
            return "added";
        case missing:
            return "missing";
        case deleted:
            return "deleted";
        case replaced:
            return "replaced";
        case modified:
            return "modified";
        case merged:
            return "merged";
        case conflicted:
            return "conflicted";
        case ignored:
            return "ignored";
        case incomplete:
            return "incomplete";
        case external:
            return "external";
        case unversioned:
        default:
            return "unversioned";
        }
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof SVNStatusKind)) {
            return false;
        }
        return ((SVNStatusKind)obj).kind == kind;
	}
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new Integer(kind).hashCode();
	}
}