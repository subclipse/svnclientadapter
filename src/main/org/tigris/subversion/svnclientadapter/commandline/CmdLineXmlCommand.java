/*******************************************************************************
 * Copyright (c) 2005, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.commandline;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CmdLineXmlCommand {

	/**
	 * Not exactly XPath, but finds a named node
	 * @param parent
	 * @param elementName
	 * @return a node with the given name
	 */
	protected static Element getFirstNamedElement(Node parent, String elementName) {
		if (parent == null) return null;
		return findNamedElementSibling(parent.getFirstChild(), elementName);
	}

	protected static Element getNextNamedElement(Node foundNode, String elementName) {
		if (foundNode == null) return null;
		return findNamedElementSibling(foundNode.getNextSibling(), elementName);
	}

	private static Element findNamedElementSibling(Node sibling, String elementName) {
		if (sibling == null) return null;
		while (sibling != null) {
			if (sibling.getNodeType() == Node.ELEMENT_NODE && sibling.getNodeName().equals(elementName))
				return (Element)sibling;
			sibling = sibling.getNextSibling();
		}
		return null;
	}

}
