package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.ConflictDescriptor;
import org.tigris.subversion.javahl.ConflictResolverCallback;
import org.tigris.subversion.javahl.ConflictResult;
import org.tigris.subversion.javahl.SubversionException;
import org.tigris.subversion.svnclientadapter.ISVNConflictResolver;
import org.tigris.subversion.svnclientadapter.SVNClientException;

public class JhlConflictResolver implements ConflictResolverCallback {
	
	ISVNConflictResolver worker;

	public JhlConflictResolver(ISVNConflictResolver worker) {
		super();
		this.worker = worker;
	}

	public ConflictResult resolve(ConflictDescriptor descrip)
			throws SubversionException {
		try {
			return new ConflictResult(worker.resolve(JhlConverter.convertConflictDescriptor(descrip)), null);
		} catch (SVNClientException e) {
			throw new JhlException(e);
		}
	}

}
