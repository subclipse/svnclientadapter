package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.SubversionException;
import org.tigris.subversion.svnclientadapter.SVNClientException;

public class JhlException extends SubversionException {

	protected JhlException(String message) {
		super(message);
	}
	
	public JhlException(SVNClientException e) {
		super(e.getMessage());
	}

}
