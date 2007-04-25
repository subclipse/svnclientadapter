/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import org.tigris.subversion.svnclientadapter.Annotations;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Handles annotations (see svn ann) 
 *
 * @author Cédric Chabanois 
 */
public class CmdLineAnnotations extends Annotations {

	private CmdLineAnnotations()
	{
		//Use factory methods		
	}

    public static CmdLineAnnotations createFromXml(byte[] annotations, InputStream contents) throws CmdLineException {
    	CmdLineAnnotations result = new CmdLineAnnotations();
    	
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);    
			// Create the builder and parse the file
			InputSource source = new InputSource(new ByteArrayInputStream(annotations));
			Document doc = factory.newDocumentBuilder().parse(source);

//			<!-- For "svn blame" -->
//			<!ELEMENT blame (target*)>
//			<!ELEMENT target (entry*)>
//			<!ATTLIST target path CDATA #REQUIRED>  <!-- path or URL -->
//			<!-- NOTE: The order of entries in a target element is insignificant. -->
//			<!ELEMENT entry (commit?)>
//			<!ATTLIST entry line-number CDATA #REQUIRED>  <!-- line number: integer -->
//			<!ELEMENT commit (author?, date?)>
//			<!ATTLIST commit revision CDATA #REQUIRED>  <!-- revision number: integer -->
//			<!ELEMENT author (#PCDATA)>  <!-- author -->
//			<!ELEMENT date (#PCDATA)>  <!-- date as "yyyy-mm-ddThh:mm:ss.ssssssZ"-->
			
			NodeList nodes = doc.getElementsByTagName("entry");
			Annotation[] lines = new Annotation[nodes.getLength()];
			
			for(int i = 0; i < nodes.getLength(); i++) {
				Node entry = nodes.item(i);

				SVNRevision.Number revision = SVNRevision.Number.INVALID_REVISION;
				String author = null;
				Date date = null;
				
				int lineNr = Integer.parseInt(entry.getAttributes().getNamedItem("line-number").getNodeValue());
				Element commitNode = CmdLineXmlCommand.getFirstNamedElement(entry, "commit");
				if (commitNode != null) {
					Node revisionAttribute = commitNode.getAttributes().getNamedItem("revision");
					revision = Helper.toRevNum(revisionAttribute.getNodeValue());
					Element authorNode = CmdLineXmlCommand.getFirstNamedElement(commitNode, "author");
					author = authorNode.getFirstChild().getNodeValue();
					Element dateNode = CmdLineXmlCommand.getNextNamedElement(authorNode, "date");
					date = Helper.convertXMLDate(dateNode.getFirstChild().getNodeValue());
				}
				
				lines[lineNr - 1] = (new Annotation(revision.getNumber(), author, date, null));
			}

			BufferedReader bReader = new BufferedReader(new InputStreamReader(contents)); 
			String line = bReader.readLine();
			int i = 0;
			while (line != null) {
				lines[i].setLine(line);
				result.addAnnotation(lines[i]);
				line = bReader.readLine();
				i++;
			}
			bReader.close();
			
		} catch (Exception e) {
			throw new CmdLineException(e);
		} 
    	
    	return result;
    }
	
    public static CmdLineAnnotations createFromStdOut(String annotations, String lineSeparator) {
    	CmdLineAnnotations result = new CmdLineAnnotations();
    	String[] lines = StringUtils.split(annotations, lineSeparator);
    	for (int i = 0; i < lines.length; i++) {
			Annotation ann = new Annotation(getRevisionFrom(lines[i]), getAuthorFrom(lines[i]), getChangedFrom(lines[i]), getLineFrom(lines[i]));
			result.addAnnotation(ann);
		}
    	return result;
    }
    
    private static long getRevisionFrom(String line) {
            String version = line.substring(0,6).trim();
            if (version.equals("-")) {
            	// if we annotate from revision 2 to HEAD, the author and revision 
            	// will be "-" if some lines have revision 1
            	return -1;
            } else {
                return Integer.parseInt(version);
            }
    }

    private static Date getChangedFrom(String line) {
    	//Client adapter does not support verbose output with dates
    	return null;
    }

    private static String getAuthorFrom(String line) {
        	String author = StringUtils.stripStart(line.substring(7,17),null);

            if (author.equals("-")) {
            	// if we annotate from revision 2 to HEAD, the author and revision 
            	// will be "-" if some lines have revision 1
            	return null;
            } else {
                return author;
            }
    }

    private static String getLineFrom(String line) {
            return line.substring(18);            
    }
}
