package org.tigris.subversion.svnclientadapter.javahl;

import java.util.Set;

import org.apache.subversion.javahl.CommitItem;
import org.apache.subversion.javahl.CommitMessage;

public class JhlCommitMessage implements CommitMessage {

	private String message;
	
	public JhlCommitMessage(String message) {
		super();
		this.message = message;
	}


	public String getLogMessage(Set<CommitItem> elementsToBeCommited) {
		return message;
	}

}
