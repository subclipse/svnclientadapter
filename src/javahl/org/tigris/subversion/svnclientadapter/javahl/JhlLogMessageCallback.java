package org.tigris.subversion.svnclientadapter.javahl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.LogMessageCallback;

public class JhlLogMessageCallback implements LogMessageCallback {
	
	private List messages = new ArrayList();
	private Stack stack = new Stack();

	public void singleMessage(ChangePath[] changedPaths, long revision,
			String author, long timeMicros, String message, long numberChildren) {
		JhlLogMessage msg = new JhlLogMessage(changedPaths, revision, author, timeMicros, message, numberChildren);
		if (stack.empty())
			messages.add(msg);
		else {
			JhlLogMessage current = (JhlLogMessage) stack.peek();
			current.addChild(msg);
			if (current.allChildrenAdded())
				stack.pop();
		}
		if (numberChildren > 0)
			stack.push(msg);
	}
	
	public JhlLogMessage[] getLogMessages() {
		JhlLogMessage[] array = new JhlLogMessage[messages.size()];
		return (JhlLogMessage[]) messages.toArray(array);
	}

}
