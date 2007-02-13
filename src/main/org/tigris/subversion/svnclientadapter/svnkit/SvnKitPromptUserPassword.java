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
package org.tigris.subversion.svnclientadapter.svnkit;

import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;
import org.tmatesoft.svn.core.javahl.PromptUserPasswordSSH;
import org.tmatesoft.svn.core.javahl.PromptUserPasswordSSL;
import org.tmatesoft.svn.core.javahl.PromptUserPasswordUser;

/**
 * A SVNKit's PromptUserPassword3 implementation.
 */
public class SvnKitPromptUserPassword implements PromptUserPasswordSSH, PromptUserPasswordSSL, PromptUserPasswordUser {

    private ISVNPromptUserPassword worker;
    
    /**
     * Constructor
     * @param arg0
     */
    public SvnKitPromptUserPassword(ISVNPromptUserPassword arg0) {
        super();
        this.worker = arg0;
    }
    
    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword3#prompt(java.lang.String, java.lang.String, boolean)
     */
    public boolean prompt(String realm, String username, boolean maySave) {
        return this.worker.prompt(realm, username, maySave);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword3#askQuestion(java.lang.String, java.lang.String, boolean, boolean)
     */
    public String askQuestion(String realm, String question, boolean showAnswer, boolean maySave) {
        return this.worker.askQuestion(realm, question, showAnswer, maySave);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword3#userAllowedSave()
     */
    public boolean userAllowedSave() {
        return this.worker.userAllowedSave();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword2#askTrustSSLServer(java.lang.String, boolean)
     */
    public int askTrustSSLServer(String info, boolean allowPermanently) {
        return this.worker.askTrustSSLServer(info, allowPermanently);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword#prompt(java.lang.String, java.lang.String)
     */
    public boolean prompt(String realm, String username) {
        return this.prompt(realm, username, true);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword#askYesNo(java.lang.String, java.lang.String, boolean)
     */
    public boolean askYesNo(String realm, String question, boolean yesIsDefault) {
        return this.worker.askYesNo(realm, question, yesIsDefault);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword#askQuestion(java.lang.String, java.lang.String, boolean)
     */
    public String askQuestion(String realm, String question, boolean showAnswer) {
        return this.askQuestion(realm, question, showAnswer, true);
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword#getUsername()
     */
    public String getUsername() {
        return this.worker.getUsername();
    }

    /* (non-Javadoc)
     * @see org.tigris.subversion.javahl.PromptUserPassword#getPassword()
     */
    public String getPassword() {
        return this.worker.getPassword();
    }

    public int getSSHPort() {
        return this.worker.getSSHPort();
    }
    public String getSSHPrivateKeyPassphrase() {
        return this.worker.getSSHPrivateKeyPassphrase();
    }
    public String getSSHPrivateKeyPath() {
        return this.worker.getSSHPrivateKeyPath();
    }
    public boolean promptSSH(String realm, String username, int sshPort,
            boolean maySave) {
        return this.worker.promptSSH(realm, username, sshPort, maySave);
    }
    public String getSSLClientCertPassword() {
        return this.worker.getSSLClientCertPassword();
    }
    public String getSSLClientCertPath() {
        return this.worker.getSSLClientCertPath();
    }
    public boolean promptSSL(String realm, boolean maySave) {
        return this.worker.promptSSL(realm, maySave);
    }
	public boolean promptUser(String realm, String user, boolean maySave) {
		return this.worker.promptUser(realm, user, maySave);
	}
}
