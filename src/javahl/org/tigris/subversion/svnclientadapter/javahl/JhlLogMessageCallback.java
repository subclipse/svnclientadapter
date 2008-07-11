package org.tigris.subversion.svnclientadapter.javahl;

import java.util.Map;

import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.LogMessageCallback;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageCallback;

public class JhlLogMessageCallback implements LogMessageCallback {
	
	private ISVNLogMessageCallback worker = null;

	public JhlLogMessageCallback(ISVNLogMessageCallback callback) {
		super();
		worker = callback;
	}
	
	public JhlLogMessageCallback() {
		super();
	}

	public void singleMessage(ChangePath[] changedPaths, long revision,
			Map revprops, boolean hasChildren) {

		if (revision == Revision.SVN_INVALID_REVNUM) {
			worker.singleMessage(null);
		} else {
			worker.singleMessage(new JhlLogMessage(changedPaths, revision, revprops, hasChildren));
		}
				
	}

}
