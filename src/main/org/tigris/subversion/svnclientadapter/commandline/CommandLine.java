/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.tigris.subversion.svnclientadapter.commandline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * common methods for both SvnCommandLine and SvnAdminCommandLine 
 *  
 * @author Philip Schatz (schatz at tigris)
 * @author Cédric Chabanois (cchabanois at no-log.org)
 */
abstract class CommandLine {

	protected String CMD;
    protected CmdLineNotificationHandler notificationHandler;
    
	//Constructors
	CommandLine(String svnPath,CmdLineNotificationHandler notificationHandler) {
		CMD = svnPath;
        this.notificationHandler = notificationHandler;
	}

	//Methods
	String version() throws CmdLineException {
		ArrayList args = new ArrayList();
		args.add("--version");
		return execString(args,false);
	}


    /**
     * execute the given svn command and returns the corresponding process  
     */
	protected Process execProcess(ArrayList svnArguments) throws CmdLineException {
		Runtime rt = Runtime.getRuntime();

		String svnCommand = "";
		boolean nextIsPassword = false;
		for (Iterator it = svnArguments.iterator();it.hasNext();) {
			String arg = (String)it.next();
			
			if (nextIsPassword) {
				svnCommand += "*******";
				nextIsPassword = false;	
			} else {
				svnCommand += arg;
			}
			if (it.hasNext())
				svnCommand += " ";
			if (arg.equals("--password")) {
				// we don't want to show the password in the console ...
				nextIsPassword = true;
			}				
		}
        notificationHandler.logCommandLine(svnCommand);

		// we add "svn" or "svnadmin" to  the arguments and convert it to an array of strings
		ArrayList argsArrayList = new ArrayList(svnArguments);
		argsArrayList.add(0,CMD);
		String[] argsArray = new String[argsArrayList.size()];
		argsArrayList.toArray(argsArray);

		/* run the process */
		Process proc = null;
		try {
			proc = rt.exec(argsArray);
		} catch (IOException e) {
			throw new CmdLineException(e);
		}

		return proc;
	}

	/**
	 * runs the process and returns the results.
	 * @param cmd
	 * @return String
	 */
	protected String execString(ArrayList svnArguments, boolean coalesceLines) throws CmdLineException {
		Process proc = execProcess(svnArguments);

        CmdLineStreamPumper outPumper = new CmdLineStreamPumper(proc.getInputStream(),coalesceLines);
        CmdLineStreamPumper errPumper = new CmdLineStreamPumper(proc.getErrorStream());

        Thread threadOutPumper = new Thread(outPumper);
        Thread threadErrPumper = new Thread(errPumper);
        threadOutPumper.start();         
        threadErrPumper.start();
        try {
            outPumper.waitFor();
            errPumper.waitFor();
        } catch (InterruptedException e) {
        }
        
		try {
            String errMessage = errPumper.toString();
            if (errMessage.length() > 0) {
                throw new CmdLineException(errMessage);        
            }
            String outputString = outPumper.toString(); 

			logMessageAndCompleted(outputString);
			return outputString;
		} catch (CmdLineException e) {
            notificationHandler.logException(e);
			throw e;
		}
	}

    /**
     * runs the command (returns nothing)
     * @param svnCommand
     * @throws CmdLineException
     */
	protected void execVoid(ArrayList svnArguments) throws CmdLineException {
		execString(svnArguments,false);
	}

	protected void logMessageAndCompleted(String messages) {
		StringTokenizer st = new StringTokenizer(messages, Helper.NEWLINE);
		int size = st.countTokens();
		//do everything but the last line
		for (int i = 1; i < size; i++) {
            notificationHandler.logMessage(st.nextToken());
		}

		//log the last line as the completed message.
		if (size > 0)
            notificationHandler.logCompleted(st.nextToken());
	}

}

