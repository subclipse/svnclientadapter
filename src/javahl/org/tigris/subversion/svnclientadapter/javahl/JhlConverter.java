/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.javahl;

import java.util.logging.Logger;

import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.ConflictDescriptor;
import org.tigris.subversion.javahl.ConflictResult;
import org.tigris.subversion.javahl.DiffSummary;
import org.tigris.subversion.javahl.DirEntry;
import org.tigris.subversion.javahl.Lock;
import org.tigris.subversion.javahl.NodeKind;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.RevisionKind;
import org.tigris.subversion.javahl.RevisionRange;
import org.tigris.subversion.javahl.ScheduleKind;
import org.tigris.subversion.javahl.Status;
import org.tigris.subversion.javahl.StatusKind;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNConflictDescriptor;
import org.tigris.subversion.svnclientadapter.SVNConflictResult;
import org.tigris.subversion.svnclientadapter.SVNConflictVersion;
import org.tigris.subversion.svnclientadapter.SVNDiffSummary;
import org.tigris.subversion.svnclientadapter.SVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNRevisionRange;
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
    	if (svnRevision == null)
    		return null;
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
	 * Convert clientAdapter's {@link SVNRevisionRange} into JavaHL's {@link RevisionRange}
	 * @param svnRevisionRange
	 * @return a {@link RevisionRange} representing suppplied SVNRevisionRange
	 */
    public static RevisionRange convert(SVNRevisionRange svnRevisionRange) {
    	return new RevisionRange(JhlConverter.convert(svnRevisionRange.getFromRevision()), JhlConverter.convert(svnRevisionRange.getToRevision()));
    }

    /**
	 * Convert JavaHL's {@link RevisionRange} into clientAdapter's {@link SVNRevisionRange}
	 * @param RevisionRange
	 * @return a {@link SVNRevisionRange} representing suppplied RevisionRange
	 */
    public static SVNRevisionRange convert(RevisionRange svnRevisionRange) {
    	return new SVNRevisionRange(JhlConverter.convert(svnRevisionRange.getFromRevision()), JhlConverter.convert(svnRevisionRange.getToRevision()));
    }

    public static SVNRevisionRange[] convert(RevisionRange[] jhlRange) {
        SVNRevisionRange[] range = new SVNRevisionRange[jhlRange.length];
        for(int i=0; i < jhlRange.length; i++) {
            range[i] = JhlConverter.convert(jhlRange[i]);
        }
        return range;
	}
    
    public static RevisionRange[] convert(SVNRevisionRange[] range) {
        RevisionRange[] jhlRange = new RevisionRange[range.length];
        for(int i=0; i < range.length; i++) {
            jhlRange[i] = JhlConverter.convert(range[i]);
        }
        return jhlRange;
    }

	/**
	 * Convert JavaHL's {@link Revision} into clientAdapter's {@link SVNRevision} 
	 * @param rev
	 * @return a {@link SVNRevision} representing suppplied Revision
	 */
	public static SVNRevision convert(Revision rev) {
		if (rev == null) return null;
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
    
    public static SVNConflictDescriptor convertConflictDescriptor(ConflictDescriptor d) {
    	if (d == null) return null;
    	SVNConflictVersion srcLeftVersion = null;
    	if (d.getSrcLeftVersion() != null) {
    		srcLeftVersion = new SVNConflictVersion(d.getSrcLeftVersion().getReposURL(), d.getSrcLeftVersion().getPegRevision(), d.getSrcLeftVersion().getPathInRepos(), d.getSrcLeftVersion().getNodeKind());
    	}
    	SVNConflictVersion srcRightVersion = null;
    	if (d.getSrcRightVersion() != null) {
    		srcRightVersion = new SVNConflictVersion(d.getSrcRightVersion().getReposURL(), d.getSrcRightVersion().getPegRevision(), d.getSrcRightVersion().getPathInRepos(), d.getSrcRightVersion().getNodeKind());
    	}
    	return new SVNConflictDescriptor(d.getPath(), d.getKind(), d.getNodeKind(),
    			d.getPropertyName(), d.isBinary(),
                d.getMIMEType(), d.getAction(), d.getReason(), d.getOperation(),
                srcLeftVersion, srcRightVersion,
                d.getBasePath(), d.getTheirPath(),
                d.getMyPath(), d.getMergedPath());
    }
    
    public static SVNConflictResult convertConflictResult(ConflictResult r) {
    	return new SVNConflictResult(r.getChoice(), r.getMergedPath());
    }

	public static SVNDiffSummary convert(DiffSummary d) {
		return new SVNDiffSummary(d.getPath(), JhlConverter.convert(d.getDiffKind()),
				d.propsChanged(), d.getNodeKind());
	}
	
	public static SVNDiffSummary.SVNDiffKind convert(DiffSummary.DiffKind d) {
		if (d == DiffSummary.DiffKind.ADDED) {
			return SVNDiffSummary.SVNDiffKind.ADDED;
		} else if (d == DiffSummary.DiffKind.MODIFIED) {
			return SVNDiffSummary.SVNDiffKind.MODIFIED;
		} else if (d == DiffSummary.DiffKind.DELETED) {
			return SVNDiffSummary.SVNDiffKind.DELETED;
		} else {
			return SVNDiffSummary.SVNDiffKind.NORMAL;
		}
	}
    
}
