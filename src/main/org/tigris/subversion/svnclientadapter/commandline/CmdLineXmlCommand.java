/*
 *  Copyright(c) 2003-2004 by the authors indicated in the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tigris.subversion.svnclientadapter.commandline;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CmdLineXmlCommand {

	/**
	 * Not exactly XPath, but finds a named node
	 * @param logEntry
	 * @param elementName
	 * @return
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
