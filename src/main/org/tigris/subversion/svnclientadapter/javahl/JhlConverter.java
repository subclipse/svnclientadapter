/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import java.util.logging.Logger;

import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.DirEntry;
import org.tigris.subversion.javahl.Lock;
import org.tigris.subversion.javahl.LogMessage;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.RevisionKind;
import org.tigris.subversion.javahl.ScheduleKind;
import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.javahl.StatusKind;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
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

	private static final Logger log = Logger.getLogger(JhlConverter.class.getName());	
	
	private JhlConverter() {
		//non-instantiable
	}
	
	/**
	 * Convert clientAdapter's {@link SVNRevision} into JavaHL's {@link Revision}
	 * @param svnRevision
	 * @return a {@link Revision} representing suppplied SVNRevision
	 */
    public static Revision convert(SVNRevision svnRevision) {
        switch(svnRevision.getKind()) {
            case SVNRevision.Kind.base : return Revision.BASE;
            case SVNRevision.Kind.committed : return Revision.COMMITTED;
            case SVNRevision.Kind.date : return new Revision.DateSpec(((SVNRevision.DateSpec)svnRevision).getDate());
            case SVNRevision.Kind.head : return Revision.HEAD;
            case SVNRevision.Kind.number : return new Revision.Number(((SVNRevision.Number)svnRevision).getNumber());
            case SVNRevision.Kind.previous : return Revision.PREVIOUS;
            case SVNRevision.Kind.unspecified : return Revision.START;
            case SVNRevision.Kind.working : return Revision.WORKING;
            default: {
        		log.severe("unknown revision kind :"+svnRevision.getKind());
            	return Revision.START; // should never go here
            }
        }
    }

	/**
	 * Convert JavaHL's {@link Revision} into clientAdapter's {@link SVNRevision} 
	 * @param rev
	 * @return a {@link SVNRevision} representing suppplied Revision
	 */
	public static SVNRevision convert(Revision rev) {
		switch (rev.getKind()) {
			case RevisionKind.base :
				return SVNRevision.BASE;
			case RevisionKind.committed :
				return SVNRevision.COMMITTED;
			case RevisionKind.number :
				Revision.Number n = (Revision.Number) rev;
				if (n.getNumber() == -1) {
					// we return null when resource is not managed ...
					return null;
				} else {
					return new SVNRevision.Number(n.getNumber());
				}
			case RevisionKind.previous :
				return SVNRevision.PREVIOUS;
			case RevisionKind.working :
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
            	log.severe("unknown node kind :"+javahlNodeKind);
            	return SVNNodeKind.UNKNOWN; // should never go here
            }
        }
    }

	public static JhlStatus convert(Status status) {
		return new JhlStatus(status);
	}

    public static SVNStatusKind convertStatusKind(int kind) {
        switch (kind) {
            case StatusKind.none :
                return SVNStatusKind.NONE;
            case StatusKind.normal :
                return SVNStatusKind.NORMAL;                
            case StatusKind.added :
                return SVNStatusKind.ADDED;
            case StatusKind.missing :
                return SVNStatusKind.MISSING;
            case StatusKind.incomplete :
                return SVNStatusKind.INCOMPLETE;
            case StatusKind.deleted :
                return SVNStatusKind.DELETED;
            case StatusKind.replaced :
                return SVNStatusKind.REPLACED;                                                
            case StatusKind.modified :
                return SVNStatusKind.MODIFIED;
            case StatusKind.merged :
                return SVNStatusKind.MERGED;                
            case StatusKind.conflicted :
                return SVNStatusKind.CONFLICTED;
            case StatusKind.obstructed :
                return SVNStatusKind.OBSTRUCTED;
            case StatusKind.ignored :
                return SVNStatusKind.IGNORED;  
            case StatusKind.external:
                return SVNStatusKind.EXTERNAL;
            case StatusKind.unversioned :
                return SVNStatusKind.UNVERSIONED;
            default : {
            	log.severe("unknown status kind :"+kind);
                return SVNStatusKind.NONE;
            }
        }
    }

	
	/**
	 * Wrap everything up.
	 * @param dirEntry
	 * @return an JhlDirEntry[] array constructed from the given DirEntry[] 
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

	static JhlLogMessage[] convert(LogMessage[] msg) {
		JhlLogMessage[] messages = new JhlLogMessage[msg.length];
		for(int i=0; i < msg.length; i++) {
			messages[i] = new JhlLogMessage(msg[i]);
		}
		return messages;
	}
    
    public static JhlStatus[] convert(Status[] status) {
        JhlStatus[] jhlStatus = new JhlStatus[status.length];
        for(int i=0; i < status.length; i++) {
            jhlStatus[i] = new JhlStatus(status[i]);
        }
        return jhlStatus;
    }
    
    static ISVNLogMessageChangePath[] convert(ChangePath[] changePaths) {
        if (changePaths == null)
            return new SVNLogMessageChangePath[0];
        SVNLogMessageChangePath[] jhlChangePaths = new SVNLogMessageChangePath[changePaths.length];
        for(int i=0; i < changePaths.length; i++) {
        	jhlChangePaths[i] = new JhlLogMessageChangePath(changePaths[i]);
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
        		log.severe("unknown schedule kind :"+kind);
        		return SVNScheduleKind.NORMAL;
        	}
        }
    }
    
    public static JhlLock convertLock(Lock lock) {
        return new JhlLock(lock);
    }
    
}
