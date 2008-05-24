package org.tigris.subversion.svnclientadapter.javahl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.LogMessageCallback;
import org.tigris.subversion.javahl.Revision;

public class JhlLogMessageCallback implements LogMessageCallback {
	
	private List messages = new ArrayList();
	private Stack stack = new Stack();

	
	public JhlLogMessage[] getLogMessages() {
		JhlLogMessage[] array = new JhlLogMessage[messages.size()];
		return (JhlLogMessage[]) messages.toArray(array);
	}

	public void singleMessage(ChangePath[] changedPaths, long revision,
			Map revprops, boolean hasChildren) {
		if (revision == Revision.SVN_INVALID_REVNUM) {
			if (!stack.empty())
				stack.pop();
			return;
		}
		JhlLogMessage msg = new JhlLogMessage(changedPaths, revision, revprops, hasChildren);
		if (stack.empty()) {
				messages.add(msg);
		} else {
			JhlLogMessage current = (JhlLogMessage) stack.peek();
			current.addChild(msg);
		}
		if (hasChildren)
			stack.push(msg);
	}

}
