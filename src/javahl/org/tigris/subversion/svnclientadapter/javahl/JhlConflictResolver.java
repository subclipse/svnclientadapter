package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.ConflictDescriptor;
import org.tigris.subversion.javahl.ConflictResolverCallback;
import org.tigris.subversion.javahl.ConflictResult;
import org.tigris.subversion.javahl.SubversionException;
import org.tigris.subversion.svnclientadapter.ISVNConflictResolver;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNConflictResult;

public class JhlConflictResolver implements ConflictResolverCallback {
	
	ISVNConflictResolver worker;

	public JhlConflictResolver(ISVNConflictResolver worker) {
		super();
		this.worker = worker;
	}

	public ConflictResult resolve(ConflictDescriptor descrip)
			throws SubversionException {
		try {
			SVNConflictResult svnConflictResult = worker.resolve(JhlConverter.convertConflictDescriptor(descrip));
			return new ConflictResult(svnConflictResult.getChoice(), svnConflictResult.getMergedPath());
		} catch (SVNClientException e) {
			throw new JhlException(e);
		}
	}

}
