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
package org.tigris.subversion.svnclientadapter;

/**
 * An interface describing a callback used during authentification.
 *
 */
public interface ISVNPromptUserPassword {
	
	/** reject the connection to the server */
	public static final int Reject = 0;
	/** accept the connection to the server one time. */
	public static final int AcceptTemporary = 1;
	 /** accept the connection to the server forever */
	public static final int AcceptPermanently = 2;
	
	/**
	 * ask the user a yes/no question
	 * @param realm         for which server realm this information is requested.
	 * @param question      question to be asked
	 * @param yesIsDefault  if yes should be the default
	 * @return              the answer
	 */
	public boolean askYesNo(String realm, String question, boolean yesIsDefault);
	
	/**
	 * retrieve the username entered during the prompt call
	 * @return the username
	 */
	public String getUsername();
	
	/**
	 * retrieve the password entered during the prompt call
	 * @return the password
	 */
	public String getPassword();
	
   /**
	* If there are problems with the certifcate of the SSL-server, this
	* callback will be used to deside if the connection will be used.
	* @param info              the probblems with the certificate.
	* @param allowPermanently  if AcceptPermantly is a legal answer
	* @return                  one of Reject/AcceptTemporary/AcceptPermanently
	*/
	public int askTrustSSLServer(String info, boolean allowPermanently);
	
	/**
	 * Request the password to be used from the user.
	 * the save data check box status will be queried by userAllowedSave
	 * @param realm     realm for the username
	 * @param username  username in the realm
	 * @param maySave   should a save data check box be enabled.
	 * @return          password as entered or null if canceled.
	 */
	public boolean prompt(String realm, String username, boolean maySave);
	
	/**
	 * Request the username to be used for SVN operation
	 * the save data check box status will be queried by userAllowedSave
	 * @param realm     realm for the username
	 * @param username  username in the realm
	 * @param maySave   should a save data check box be enabled.
	 * @return          password as entered or null if canceled.
	 */
	public boolean promptUser(String realm, String username, boolean maySave);
	
	/**
	 *  Ask the user a question about authentification
	 * the save data check box status will be queried by userAllowedSave
	 * @param realm         real of the question
	 * @param question      text of the question
	 * @param showAnswer    flag if the answer should be displayed
	 * @param maySave       should a save data check box be enabled.
	 * @return              answer as entered or null if canceled
	 */
	public String askQuestion(String realm, String question, boolean showAnswer, boolean maySave);
	
	/**
	 * query if the user allowed the saving of the data of the last call
	 * @return      was the save data check box checked
	 */
	public boolean userAllowedSave();

	/**
	 * Request the SSH info to be used from the user.
	 * the save data check box status will be queried by userAllowedSave
	 * @param realm     realm for the username
	 * @param username  username in the realm
	 * @param sshPort   the port number to use
	 * @param maySave   should a save data check box be enabled.
	 * @return          true if OK was pressed
	 */
    public boolean promptSSH(String realm, String username, int sshPort, boolean maySave);
    
	/**
	 * retrieve the SSH key file entered during the prompt call
	 * @return the key file
	 */
    public String getSSHPrivateKeyPath();
    
	/**
	 * retrieve the passphrase for the key file entered during
	 * the prompt call
	 * @return the passphrase
	 */
    public String getSSHPrivateKeyPassphrase();

	/**
	 * retrieve the SSH port entered during the prompt call
	 * @return the port number
	 */
    public int getSSHPort();

	/**
	 * Request the SSL client certificate info to be used from the user.
	 * the save data check box status will be queried by userAllowedSave
	 * @param realm     realm for the action
	 * @param maySave   should a save data check box be enabled.
	 * @return          true if OK was pressed
	 */
    public boolean promptSSL(String realm, boolean maySave);
    
	/**
	 * retrieve the password for the certifcate
	 * @return the password
	 */
    public String getSSLClientCertPassword();
    
	/**
	 * retrieve the SSL certificate entered during the prompt call
	 * @return the certificate
	 */
    public String getSSLClientCertPath();

}