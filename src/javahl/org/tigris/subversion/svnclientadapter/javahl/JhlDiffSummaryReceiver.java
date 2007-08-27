package org.tigris.subversion.svnclientadapter.javahl;

import java.util.ArrayList;
import java.util.List;

import org.tigris.subversion.javahl.DiffSummary;
import org.tigris.subversion.javahl.DiffSummaryReceiver;
import org.tigris.subversion.svnclientadapter.SVNDiffSummary;

public class JhlDiffSummaryReceiver implements DiffSummaryReceiver {
	
	List summary = new ArrayList();

	public void onSummary(DiffSummary descriptor) {
		summary.add(JhlConverter.convert(descriptor));

	}

	public SVNDiffSummary[] getDiffSummary() {
		SVNDiffSummary[] diffSummary = new SVNDiffSummary[summary.size()];
		summary.toArray(diffSummary);
		return diffSummary;
	}

}
