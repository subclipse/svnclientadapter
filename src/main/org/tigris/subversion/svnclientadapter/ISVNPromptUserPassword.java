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
package org.tigris.subversion.svnclientadapter;

public interface ISVNPromptUserPassword {
	/**
	 * reject the connection to the server
	 */
	public static final int Reject = 0;
	/**
	 * accept the connection to the server one time.
	 */
	public static final int AcceptTemporary = 1;
	 /**
	 * accept the connection to the server forever
	 */
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
}