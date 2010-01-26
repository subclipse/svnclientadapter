package org.tigris.subversion.svnclientadapter.commandline;

import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNDirEntryWithLock;
import org.tigris.subversion.svnclientadapter.ISVNLock;

public class CmdLineRemoteDirEntryWithLock implements ISVNDirEntryWithLock {
	
	private ISVNDirEntry dirEntry;
	private ISVNLock lock;
	
	public CmdLineRemoteDirEntryWithLock(ISVNDirEntry dirEntry, ISVNLock lock) {
		super();
		this.dirEntry = dirEntry;
		this.lock = lock;
	}

	public ISVNDirEntry getDirEntry() {
		return dirEntry;
	}

	public ISVNLock getLock() {
		return lock;
	}

}
