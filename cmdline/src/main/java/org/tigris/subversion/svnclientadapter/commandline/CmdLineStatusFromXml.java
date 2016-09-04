/*******************************************************************************
 * Copyright (c) 2006 svnClientAdapter project and others.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CmdLineStatusFromXml extends CmdLineXmlCommand {
	
	private SVNRevision.Number lastChangedRevision;
	private Date lastChangedDate;
	private String lastCommitAuthor;
	private SVNStatusKind textStatus;
	private SVNStatusKind repositoryTextStatus;
	private SVNStatusKind propStatus;
	private SVNStatusKind repositoryPropStatus;
	private SVNRevision.Number revision;
	private String path;
	private boolean copied;
	private boolean wcLocked;
	private boolean switched;
	private File conflictNew;
	private File conflictOld;
	private File conflictWorking;
	private String lockOwner;
	private Date lockCreationDate;
	private String lockComment;
	
	protected CmdLineStatusFromXml(String path)
	{
		super();
		this.path = path;
	}
	
	/**
	 * @return Returns the conflictNew.
	 */
	public File getConflictNew() {
		return conflictNew;
	}
	/**
	 * @return Returns the conflictOld.
	 */
	public File getConflictOld() {
		return conflictOld;
	}
	/**
	 * @return Returns the conflictWorking.
	 */
	public File getConflictWorking() {
		return conflictWorking;
	}
	/**
	 * @return Returns the copied.
	 */
	public boolean isCopied() {
		return copied;
	}
	/**
	 * @return Returns the wcLocked.
	 */
	public boolean isWcLocked() {
		return wcLocked;
	}
	/**
	 * @return Returns the switched.
	 */
	public boolean isSwitched() {
		return switched;
	}
	/**
	 * @return Returns the file.
	 */
	public File getFile() {
        return new File(getPath()).getAbsoluteFile();
	}
	/**
	 * @return Returns the lastCommitAuthor.
	 */
	public String getLastCommitAuthor() {
		return lastCommitAuthor;
	}
	/**
	 * @return Returns the lastChangedDate.
	 */
	public Date getLastChangedDate() {
		return lastChangedDate;
	}
	/**
	 * @return Returns the lastChangedRevision.
	 */
	public SVNRevision.Number getLastChangedRevision() {
		return lastChangedRevision;
	}
	/**
	 * @return Returns the lockComment.
	 */
	public String getLockComment() {
		return lockComment;
	}
	/**
	 * @return Returns the lockCreationDate.
	 */
	public Date getLockCreationDate() {
		return lockCreationDate;
	}
	/**
	 * @return Returns the lockOwner.
	 */
	public String getLockOwner() {
		return lockOwner;
	}
	/**
	 * @return Returns the path.
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @return Returns the propStatus.
	 */
	public SVNStatusKind getPropStatus() {
		return propStatus;
	}
	/**
	 * @return Returns the repositoryPropStatus.
	 */
	public SVNStatusKind getRepositoryPropStatus() {
		return repositoryPropStatus;
	}
	/**
	 * @return Returns the repositoryTextStatus.
	 */
	public SVNStatusKind getRepositoryTextStatus() {
		return repositoryTextStatus;
	}
	/**
	 * @return Returns the revision.
	 */
	public SVNRevision.Number getRevision() {
		return revision;
	}
	/**
	 * @return Returns the textStatus.
	 */
	public SVNStatusKind getTextStatus() {
		return textStatus;
	}
	/**
	 * @param conflictNew The conflictNew to set.
	 */
	protected void setConflictNew(File conflictNew) {
		this.conflictNew = conflictNew;
	}
	/**
	 * @param conflictOld The conflictOld to set.
	 */
	protected void setConflictOld(File conflictOld) {
		this.conflictOld = conflictOld;
	}
	/**
	 * @param conflictWorking The conflictWorking to set.
	 */
	protected void setConflictWorking(File conflictWorking) {
		this.conflictWorking = conflictWorking;
	}
	/**
	 * @param copied The copied to set.
	 */
	protected void setCopied(boolean copied) {
		this.copied = copied;
	}
	/**
	 * @param wcLocked The wcLocked to set.
	 */
	protected void setWcLocked(boolean wcLocked) {
		this.wcLocked = wcLocked;
	}
	/**
	 * @param switched The switched to set.
	 */
	protected void setSwitched(boolean switched) {
		this.switched = switched;
	}
	/**
	 * @param lastCommitAuthor The lastCommitAuthor to set.
	 */
	protected void setLastCommitAuthor(String lastCommitAuthor) {
		this.lastCommitAuthor = lastCommitAuthor;
	}
	/**
	 * @param lastChangedDate The lastChangedDate to set.
	 */
	protected void setLastChangedDate(Date lastChangedDate) {
		this.lastChangedDate = lastChangedDate;
	}
	/**
	 * @param lastChangedRevision The lastChangedRevision to set.
	 */
	protected void setLastChangedRevision(SVNRevision.Number lastChangedRevision) {
		this.lastChangedRevision = lastChangedRevision;
	}
	/**
	 * @param lockComment The lockComment to set.
	 */
	protected void setLockComment(String lockComment) {
		this.lockComment = lockComment;
	}
	/**
	 * @param lockCreationDate The lockCreationDate to set.
	 */
	protected void setLockCreationDate(Date lockCreationDate) {
		this.lockCreationDate = lockCreationDate;
	}
	/**
	 * @param lockOwner The lockOwner to set.
	 */
	protected void setLockOwner(String lockOwner) {
		this.lockOwner = lockOwner;
	}
	/**
	 * @param path The path to set.
	 */
	protected void setPath(String path) {
		this.path = path;
	}
	/**
	 * @param propStatus The propStatus to set.
	 */
	protected void setPropStatus(SVNStatusKind propStatus) {
		this.propStatus = propStatus;
	}
	/**
	 * @param repositoryPropStatus The repositoryPropStatus to set.
	 */
	protected void setRepositoryPropStatus(SVNStatusKind repositoryPropStatus) {
		this.repositoryPropStatus = repositoryPropStatus;
	}
	/**
	 * @param repositoryTextStatus The repositoryTextStatus to set.
	 */
	protected void setRepositoryTextStatus(SVNStatusKind repositoryTextStatus) {
		this.repositoryTextStatus = repositoryTextStatus;
	}
	/**
	 * @param revision The revision to set.
	 */
	protected void setRevision(SVNRevision.Number revision) {
		this.revision = revision;
	}
	/**
	 * @param textStatus The textStatus to set.
	 */
	protected void setTextStatus(SVNStatusKind textStatus) {
		this.textStatus = textStatus;
	}
	/**
     * creates CmdLineStatus(es) from a xml string (see svn status --xml) 
     * @param cmdLineResults
     * @return CmdLineStatusFromXml[] array created from the supplied xml
     * @throws CmdLineException
     */
	public static CmdLineStatusFromXml[] createStatuses(byte[] cmdLineResults) throws CmdLineException {
		Collection statuses = new ArrayList();
		
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
    
			// Create the builder and parse the file
			InputSource source = new InputSource(new ByteArrayInputStream(cmdLineResults));

			Document doc = factory.newDocumentBuilder().parse(source);
			
//			<!-- XML DTD for Subversion command-line client output. -->
//
//			<!-- For "svn status" -->
//			<!ENTITY % BOOL '(true | false) "false"'>
//
//			<!ELEMENT status (target*)>
//
//			<!ELEMENT target (entry*, against?)>
//			<!-- path: target path -->
//			<!ATTLIST target
//			  path CDATA #REQUIRED>
//
//			<!ELEMENT entry (wc-status, repos-status?)>
//			<!-- path: entry path -->
//			<!ATTLIST entry
//			  path CDATA #REQUIRED>
//
//			<!ELEMENT wc-status (commit?, lock?)>
//			<!-- item: item/text status -->
//			<!-- props: properites status -->
//			<!-- revision: base revision numer -->
//			<!-- wc-locked: WC dir locked? -->
//			<!-- copied: add with history? -->
//			<!-- switched: item switched relative to its parent? -->
//			<!ATTLIST wc-status
//			  item (added | conflicted | deleted | ignored | modified |
//			  replaced | external | unversioned | incomplete | obstructed |
//			  normal | none) #REQUIRED
//			  props (conflicted | modified | normal | none) #REQUIRED
//			  revision CDATA #IMPLIED
//			  wc-locked %BOOL;
//			  copied %BOOL;
//			  switched %BOOL;
//			>
//
//			<!ELEMENT repos-status (lock?)>
//			<!-- item: repository status of the item -->
//			<!-- props: repository status of the item's properties -->
//			<!ATTLIST repos-status
//			  item (added | deleted | modified | replaced | none) #REQUIRED
//			  props (modified | none) #REQUIRED
//			>
//
//			<!ELEMENT commit (author?, date?)>
//			<!-- revision: last committed revision -->
//			<!ATTLIST commit revision CDATA #REQUIRED>
//			<!ELEMENT author (#PCDATA)>  <!-- author -->
//			<!ELEMENT date (#PCDATA)>  <!-- date in ISO format -->
//
//			<!-- Lock info stored in WC or repos. -->
//			<!ELEMENT lock (token, owner, comment?, created, expires?)>
//
//			<!ELEMENT token (#PCDATA)>    <!-- lock token URI -->
//			<!ELEMENT owner (#PCDATA)>    <!-- lock owner -->
//			<!ELEMENT comment (#PCDATA)>  <!-- lock comment -->
//			<!ELEMENT created (#PCDATA)>  <!-- creation date in ISO format -->
//			<!ELEMENT expires (#PCDATA)>  <!-- expiration date in ISO format -->
//
//			<!ELEMENT against EMPTY>
//			<!-- revision: revision number at which the repository information was -->
//			<!-- obtained -->
//			<!ATTLIST against revision CDATA #REQUIRED>

			
			NodeList nodes = doc.getElementsByTagName("entry");
			
			for(int i = 0; i < nodes.getLength(); i++) {
				Node statusEntry = nodes.item(i);

				String entryPath = statusEntry.getAttributes().getNamedItem("path").getNodeValue();

				CmdLineStatusFromXml status = new CmdLineStatusFromXml(entryPath);
				
				Element wcStatusNode = getFirstNamedElement(statusEntry, "wc-status");
				if (wcStatusNode == null) throw new Exception("'wc-status' tag expected under 'entry'");

				Node wcItemStatusAttr = wcStatusNode.getAttributes().getNamedItem("item");
                status.setTextStatus(SVNStatusKind.fromString(wcItemStatusAttr.getNodeValue()));
				Node wcPpropStatusAttr = wcStatusNode.getAttributes().getNamedItem("props");
                status.setPropStatus(SVNStatusKind.fromString(wcPpropStatusAttr.getNodeValue()));
				Node wcRevisionAttribute = wcStatusNode.getAttributes().getNamedItem("revision");
				if (wcRevisionAttribute != null) {
					status.setRevision(Helper.toRevNum(wcRevisionAttribute.getNodeValue()));
				}
				Node wcLockedAttr = wcStatusNode.getAttributes().getNamedItem("wc-locked");
                status.setWcLocked((wcLockedAttr != null) && "true".equals(wcLockedAttr.getNodeValue()));
				Node copiedAttr = wcStatusNode.getAttributes().getNamedItem("copied");
                status.setCopied((copiedAttr != null) && "true".equals(copiedAttr.getNodeValue()));
				Node switchedAttr = wcStatusNode.getAttributes().getNamedItem("switched");                 
                status.setSwitched((switchedAttr != null) && "true".equals(switchedAttr.getNodeValue()));
				
				Element commitNode = getFirstNamedElement(wcStatusNode, "commit");
				if (commitNode != null) {
					Node commitRevisionAttribute = commitNode.getAttributes().getNamedItem("revision");
					status.setLastChangedRevision(Helper.toRevNum(commitRevisionAttribute.getNodeValue()));
					Element authorNode = getFirstNamedElement(commitNode, "author");
					if (authorNode != null) {
						status.setLastCommitAuthor(authorNode.getFirstChild().getNodeValue());
					}
					Element dateNode = getNextNamedElement(authorNode, "date");
					if (dateNode != null) {
						status.setLastChangedDate(Helper.convertXMLDate(dateNode.getFirstChild().getNodeValue()));
					}
				}

				Element lockNode = getNextNamedElement(commitNode, "lock");
				if (lockNode != null) {
					Element tokenNode = getFirstNamedElement(lockNode, "token");
					if (tokenNode == null) throw new Exception("'token' tag expected under 'lock'");
//					String token = authorNode.getFirstChild().getNodeValue();				
					Element ownerNode = getNextNamedElement(lockNode, "owner");
					if (ownerNode == null) throw new Exception("'owner' tag expected under 'lock'");
					status.setLockOwner(ownerNode.getFirstChild().getNodeValue());					
					Element lockCommentNode = getNextNamedElement(ownerNode, "comment");
					status.setLockComment((lockCommentNode != null) ? lockCommentNode.getFirstChild().getNodeValue() : null);					
					Element lockCreatedNode = getNextNamedElement(lockCommentNode, "created");
					status.setLockCreationDate(Helper.convertXMLDate((lockCreatedNode != null) ? lockCreatedNode.getFirstChild().getNodeValue() : null));					
//					Element lockExpiresNode = getNextNamedElement(lockCreatedNode, "expires");
//					String lockExpires = (lockExpiresNode != null) ? lockExpiresNode.getFirstChild().getNodeValue() : null;					
				}

				Element reposStatusNode = getNextNamedElement(wcStatusNode, "repos-status");
				if (reposStatusNode != null) {
					Node reposItemStatusAttr = reposStatusNode.getAttributes().getNamedItem("item");
	                status.setRepositoryTextStatus(SVNStatusKind.fromString(reposItemStatusAttr.getNodeValue()));
					Node reposPropStatusAttr = reposStatusNode.getAttributes().getNamedItem("props");
	                status.setRepositoryPropStatus(SVNStatusKind.fromString(reposPropStatusAttr.getNodeValue()));
				}				

				statuses.add(status);			
			}
		} catch (Exception e) {
			throw new CmdLineException(e);
		} 
		
		return (CmdLineStatusFromXml[]) statuses.toArray(new CmdLineStatusFromXml[statuses.size()]);		
	
	}


}
