package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.ProgressEvent;
import org.tigris.subversion.javahl.ProgressListener;
import org.tigris.subversion.svnclientadapter.ISVNProgressListener;
import org.tigris.subversion.svnclientadapter.SVNProgressEvent;

public class JhlProgressListener implements ProgressListener {
	ISVNProgressListener worker;
	
	public JhlProgressListener() {
		super();
	}

	public void onProgress(ProgressEvent event) {
		if (worker != null )
			worker.onProgress(new SVNProgressEvent(event.getProgress(), event.getTotal()));
	}
	
	public void setWorker(ISVNProgressListener worker) {
		this.worker = worker;
	}

}
