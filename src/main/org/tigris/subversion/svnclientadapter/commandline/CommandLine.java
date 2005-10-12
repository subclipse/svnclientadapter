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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
		for (int i = 0; i < svnArguments.size(); i++) {
			if (i != 0)
				svnCommand += " ";
			
			String arg = (String)svnArguments.get(i);
			
			if (arg == "") {
				arg = "\"\"";
				svnArguments.set(i, arg);
			}
			
			if (nextIsPassword) {
				svnCommand += "*******";
				nextIsPassword = false;	
			} else {
				svnCommand += arg;
			}
			
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
			//Set the LANG env variable so the svn's output is not localized
			proc = rt.exec(argsArray, new String[] {"LANG=C", "LC_ALL=C"});
		} catch (IOException e) {
			throw new CmdLineException(e);
		}

		return proc;
	}

	/**
	 * Runs the process and returns the results.
	 * @param svnArguments The arguments to pass to the command-line
	 * binary.
     * @param coalesceLines
	 * @return String
	 */
	protected String execString(ArrayList svnArguments, boolean coalesceLines)
        throws CmdLineException {
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

            notifyFromSvnOutput(outputString);
			return outputString;
		} catch (CmdLineException e) {
            notificationHandler.logException(e);
			throw e;
		}
	}

	/**
	 * Runs the process and returns the results.
	 * @param svnArguments The arguments to pass to the command-line
	 * binary.
     * @param assumeUTF8 Whether the output of the command should be
     * treated as UTF-8 (as opposed to the JVM's default encoding).
	 * @return String
	 */
	protected byte[] execBytes(ArrayList svnArguments, boolean assumeUTF8)
        throws CmdLineException {
		Process proc = execProcess(svnArguments);

        CmdLineByteStreamPumper outPumper = new CmdLineByteStreamPumper(proc.getInputStream());
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
            byte[] bytes = outPumper.getBytes(); 

            String notifyMessage = "";
            if (assumeUTF8) {
            	try {
            		notifyMessage = new String(bytes, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// It is guaranteed to be there!
				}
            } else {
            	// This uses the default charset, which is likely
            	// wrong if we are trying to get the bytes, anyway...
            	notifyMessage = new String(bytes);
            }
			notifyFromSvnOutput(notifyMessage);
			
			return bytes;
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

	/**
	 * notify the listeners from the output. This is the default implementation
     *
	 * @param svnOutput
	 */
    protected void notifyFromSvnOutput(String svnOutput) {
		StringTokenizer st = new StringTokenizer(svnOutput, Helper.NEWLINE);
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

