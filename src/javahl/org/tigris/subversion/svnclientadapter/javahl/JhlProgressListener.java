package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.ProgressEvent;
import org.tigris.subversion.javahl.ProgressListener;
import org.tigris.subversion.svnclientadapter.ISVNProgressListener;
import org.tigris.subversion.svnclientadapter.SVNProgressEvent;

public class JhlProgressListener implements ProgressListener {
	ISVNProgressListener worker;
	
	public JhlProgressListener(ISVNProgressListener worker) {
		super();
		this.worker = worker;
	}

	public void onProgress(ProgressEvent event) {
		worker.onProgress(new SVNProgressEvent(event.getProgress(), event.getTotal()));
	}

}
