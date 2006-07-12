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
package org.tigris.subversion.svnclientadapter.javahl;

import org.tigris.subversion.javahl.PromptUserPassword3;
import org.tigris.subversion.svnclientadapter.ISVNPromptUserPassword;

/**
 * A JavaHL's PromptUserPassword3 implementation.
 * Actually an adapter to {@link ISVNPromptUserPassword}  
 */
public class JhlPromptUserPassword implements PromptUserPassword3 {

    private ISVNPromptUserPassword worker;
    
    /**
     * Constructor
     * @param arg0
     */
    public JhlPromptUserPassword(ISVNPromptUserPassword arg0) {
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
}
