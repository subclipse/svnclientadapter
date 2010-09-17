package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.DirEntry;
import org.tigris.subversion.javahl.Lock;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNDirEntryWithLock;
import org.tigris.subversion.svnclientadapter.ISVNLock;

public class JhlDirEntryWithLock implements ISVNDirEntryWithLock {
	private ISVNDirEntry dirEntry;
	private ISVNLock lock;

	public JhlDirEntryWithLock(DirEntry d, Lock l) {
		dirEntry = new JhlDirEntry(d);
		if (l != null) lock = new JhlLock(l);
	}

	public ISVNDirEntry getDirEntry() {
		return dirEntry;
	}

	public ISVNLock getLock() {
		return lock;
	}

}
