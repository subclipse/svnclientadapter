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
package org.tigris.subversion.svnclientadapter.javahl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.DirEntry;
import org.tigris.subversion.javahl.LogMessage;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.ScheduleKind;
import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNScheduleKind;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;

/**
 * Convert from javahl types to subversion.svnclientadapter.* types 
 *  
 * @author philip schatz
 */
public class JhlConverter {

	private static Log log = LogFactory.getLog(JhlConverter.class);	
	
	private JhlConverter() {
		//non-instantiable
	}
	
    public static Revision convert(SVNRevision svnRevision) {
        switch(svnRevision.getKind()) {
            case SVNRevision.Kind.base : return Revision.BASE;
            case SVNRevision.Kind.committed : return Revision.COMMITTED;
            case SVNRevision.Kind.date : return new Revision.DateSpec(((SVNRevision.DateSpec)svnRevision).getDate());
            case SVNRevision.Kind.head : return Revision.HEAD;
            case SVNRevision.Kind.number : return new Revision.Number(((SVNRevision.Number)svnRevision).getNumber());
            case SVNRevision.Kind.previous : return Revision.PREVIOUS;
            case SVNRevision.Kind.unspecified : return new Revision(Revision.Kind.unspecified);
            case SVNRevision.Kind.working : return Revision.WORKING;
            default: {
        		log.error("unknown revision kind :"+svnRevision.getKind());
            	return new Revision(Revision.Kind.unspecified); // should never go here
            }
        }
    }

	static SVNRevision convert(Revision rev) {
		switch (rev.getKind()) {
			case Revision.Kind.base :
				return SVNRevision.BASE;
			case Revision.Kind.committed :
				return SVNRevision.COMMITTED;
			case Revision.Kind.number :
				Revision.Number n = (Revision.Number) rev;
				if (n.getNumber() == -1) {
					// we return null when resource is not managed ...
					return null;
				} else {
					return new SVNRevision.Number(n.getNumber());
				}
			case Revision.Kind.previous :
				return SVNRevision.PREVIOUS;
			case Revision.Kind.working :
				return SVNRevision.WORKING;
			default :
				return SVNRevision.HEAD;
		}
	}
    
    static SVNRevision.Number convertRevisionNumber(long revisionNumber) {
    	if (revisionNumber == -1) {
    		return null;
        } else {
        	return new SVNRevision.Number(revisionNumber); 
        }
    }

    public static SVNNodeKind convertNodeKind(int javahlNodeKind) {
        switch(javahlNodeKind) {
            case NodeKind.dir  : return SVNNodeKind.DIR; 
            case NodeKind.file : return SVNNodeKind.FILE; 
            case NodeKind.none : return SVNNodeKind.NONE; 
            case NodeKind.unknown : return SVNNodeKind.UNKNOWN;
            default: {
            	log.error("unknown node kind :"+javahlNodeKind);
            	return SVNNodeKind.UNKNOWN; // should never go here
            }
        }
    }

	public static JhlStatus convert(Status status) {
		return new JhlStatus(status);
	}

    public static SVNStatusKind convertStatusKind(int kind) {
        switch (kind) {
            case Status.Kind.none :
                return SVNStatusKind.NONE;
            case Status.Kind.normal :
                return SVNStatusKind.NORMAL;                
            case Status.Kind.added :
                return SVNStatusKind.ADDED;
            case Status.Kind.missing :
                return SVNStatusKind.MISSING;
            case Status.Kind.incomplete :
                return SVNStatusKind.INCOMPLETE;
            case Status.Kind.deleted :
                return SVNStatusKind.DELETED;
            case Status.Kind.replaced :
                return SVNStatusKind.REPLACED;                                                
            case Status.Kind.modified :
                return SVNStatusKind.MODIFIED;
            case Status.Kind.merged :
                return SVNStatusKind.MERGED;                
            case Status.Kind.conflicted :
                return SVNStatusKind.CONFLICTED;
            case Status.Kind.obstructed :
                return SVNStatusKind.OBSTRUCTED;
            case Status.Kind.ignored :
                return SVNStatusKind.IGNORED;  
            // Status.Kind.external should be added to javahl ...              
//            case Status.Kind.external:
//                return ISVNStatus.Kind.EXTERNAL;
            case Status.Kind.unversioned :
                return SVNStatusKind.UNVERSIONED;
            default : {
            	log.error("unknown status kind :"+kind);
                return SVNStatusKind.NONE;
            }
        }
    }

	
	/**
	 * Wrap everything up.
	 * @param dirEntry
	 * @return
	 */
	static JhlDirEntry[] convert(DirEntry[] dirEntry) {
		JhlDirEntry[] entries = new JhlDirEntry[dirEntry.length];
		for(int i=0; i < dirEntry.length; i++) {
			entries[i] = new JhlDirEntry(dirEntry[i]);
		}
		return entries;
	}

	static JhlDirEntry convert(DirEntry dirEntry) {
		return new JhlDirEntry(dirEntry);
	}

	static ISVNLogMessage[] convert(LogMessage[] msg) {
		JhlLogMessage[] messages = new JhlLogMessage[msg.length];
		for(int i=0; i < msg.length; i++) {
			messages[i] = new JhlLogMessage(msg[i]);
		}
		return messages;
	}
    
    static ISVNStatus[] convert(Status[] status) {
        JhlStatus[] jhlStatus = new JhlStatus[status.length];
        for(int i=0; i < status.length; i++) {
            jhlStatus[i] = new JhlStatus(status[i]);
        }
        return jhlStatus;
    }
    
    static ISVNLogMessageChangePath[] convert(ChangePath[] changePaths) {
        SVNLogMessageChangePath[] jhlChangePaths = new SVNLogMessageChangePath[changePaths.length];
        for(int i=0; i < changePaths.length; i++) {
            ChangePath changePath = changePaths[i];
            
            SVNRevision.Number copySrcRevision = null;
            if (changePath.getCopySrcRevision() != -1) {
                copySrcRevision = new SVNRevision.Number(changePath.getCopySrcRevision());	
            }
            
        	jhlChangePaths[i] = new SVNLogMessageChangePath(
                    changePath.getPath(),
                    copySrcRevision,
                    changePath.getCopySrcPath(),
                    changePath.getAction());
        }
        return jhlChangePaths;
    }
    
    public static SVNScheduleKind convertScheduleKind(int kind) {
        switch (kind) {
        	case ScheduleKind.normal:
        		return SVNScheduleKind.NORMAL;
        	case ScheduleKind.delete:
        		return SVNScheduleKind.DELETE;
        	case ScheduleKind.add:
        		return SVNScheduleKind.ADD;
        	case ScheduleKind.replace:
        		return SVNScheduleKind.REPLACE;        	
        	default : {
        		log.error("unknown schedule kind :"+kind);
        		return SVNScheduleKind.NORMAL;
        	}
        }
    }
    
}
