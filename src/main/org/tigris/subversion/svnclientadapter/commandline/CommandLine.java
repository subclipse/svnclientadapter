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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * common methods for both SvnCommandLine and SvnAdminCommandLine 
 *  
 * @author Philip Schatz (schatz at tigris)
 * @author Cédric Chabanois (cchabanois at no-log.org)
 * @author Daniel Rall
 */
abstract class CommandLine {

    /**
     * Environment variables set when invoking the command-line.
     * Includes <code>LANG</code> and <code>LC_ALL</code>, set such
     * that Subversion's output is not localized.
     */
    private static final String[] ENV_VARS = { "LANG=C", "LC_ALL=C" };

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
     * Executes the given svn command and returns the corresponding
     * <code>Process</code> object.
     *
     * @param svnArguments The command-line arguments to execute.
     */
	protected Process execProcess(ArrayList svnArguments)
        throws CmdLineException {
		// We add "svn" or "svnadmin" to the arguments (as
		// appropriate), and convert it to an array of strings.
        int svnArgsLen = svnArguments.size();
        String[] cmdline = new String[svnArgsLen + 1];
        cmdline[0] = CMD;

		StringBuffer svnCommand = new StringBuffer();
		boolean nextIsPassword = false;

		for (int i = 0; i < svnArgsLen; i++) {
			if (i != 0)
				svnCommand.append(' ');
			
			Object arg = svnArguments.get(i);
            if (arg != null)
                arg = arg.toString();
			
			if ("".equals(arg)) {
				arg = "\"\"";
			}
			
			if (nextIsPassword) {
				// Avoid showing the password on the console.
				svnCommand.append("*******");
				nextIsPassword = false;	
			} else {
				svnCommand.append(arg);
			}
			
			if ("--password".equals(arg)) {
				nextIsPassword = true;
			}

            // Regardless of the data type passed in via svnArguments,
            // at this point we expect to have a String object.
            cmdline[i + 1] = (String) arg;
		}
        notificationHandler.logCommandLine(svnCommand.toString());

		// Run the command, and return the associated Process object.
		try {
            return Runtime.getRuntime().exec(cmdline, ENV_VARS);
		} catch (IOException e) {
			throw new CmdLineException(e);
		}
	}

    /**
     * Pumps the output from both provided streams, blocking until
     * complete.
     *
     * @param outPumper The process output stream.
     * @param outPumper The process error stream.
     */
    private void pumpProcessStreams(StreamPumper outPumper,
                                    StreamPumper errPumper) {
        new Thread(outPumper).start();
        new Thread(errPumper).start();

        try {
            outPumper.waitFor();
            errPumper.waitFor();
        } catch (InterruptedException ignored) {
        }
    }

	/**
	 * Runs the process and returns the results.
     *
	 * @param svnArguments The command-line arguments to execute.
     * @param coalesceLines
	 * @return Any output returned from execution of the command-line.
	 */
	protected String execString(ArrayList svnArguments, boolean coalesceLines)
        throws CmdLineException {
		Process proc = execProcess(svnArguments);
        StreamPumper outPumper =
            new CharacterStreamPumper(proc.getInputStream(), coalesceLines);
        StreamPumper errPumper =
            new CharacterStreamPumper(proc.getErrorStream(), false);
        pumpProcessStreams(outPumper, errPumper);

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
        ByteStreamPumper outPumper =
            new ByteStreamPumper(proc.getInputStream());
        StreamPumper errPumper =
            new CharacterStreamPumper(proc.getErrorStream(), false);
        pumpProcessStreams(outPumper, errPumper);
        
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
    	
    /**
     * Pulls all the data out of a stream.  Inspired by Ant's
     * StreamPumper (by Robert Field).
     */
    private static abstract class StreamPumper implements Runnable {
        private boolean finished;

        /**
         * Copies data from the input stream to the internal buffer.
         * Terminates as soon as the input stream is closed, or an
         * error occurs.
         */
        public void run() {
            synchronized (this) {
                // Just in case this object is reused in the future.
                this.finished = false;
            }

            try {
                pumpStream();
            } catch (IOException ignored) {
            } finally {
                synchronized (this) {
                    this.finished = true;
                    notify();
                }
            }
        }

        /**
         * Called by {@link #run()} to pull the data out of the
         * stream.
         */
        protected abstract void pumpStream()
            throws IOException;

        /**
         * Tells whether the end of the stream has been reached.
         * @return true is the stream has been exhausted.
         **/
        public synchronized boolean isFinished() {
            return this.finished;
        }

        /**
         * This method blocks until the stream pumper finishes.
         * @see #isFinished()
         **/
        public synchronized void waitFor()
            throws InterruptedException {
            while (!isFinished()) {
                wait();
            }
        }
    }

    /** Extracts character data from streams. */
    private static class CharacterStreamPumper extends StreamPumper {
        private BufferedReader reader;
        private StringBuffer sb = new StringBuffer();
        private boolean coalesceLines = false;

        /**
         * @param is Input stream from which to read the data.
         * @param coalesceLines Whether to coalesce lines.
         */
        public CharacterStreamPumper(InputStream is, boolean coalesceLines) {
            this.reader = new BufferedReader(new InputStreamReader(is));
            this.coalesceLines = coalesceLines;
        }

        /**
         * Copies data from the input stream to the internal string
         * buffer.
         */
        protected void pumpStream()
            throws IOException {
            String line;
            while((line = this.reader.readLine()) != null) {
                if (this.coalesceLines) {
                    this.sb.append(line);
                } else {
                    this.sb.append(line).append(Helper.NEWLINE);
                }
            }
        }

        public synchronized String toString() {
            return this.sb.toString();
        }
    }

    /** Extracts byte data from streams. */
    private static class ByteStreamPumper extends StreamPumper {
        private InputStream bis;
        private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private final static int BUFFER_LENGTH = 1024;
        private byte[] inputBuffer = new byte[BUFFER_LENGTH];

        /**
         * Create a new stream pumper.
         *
         * @param is input stream to read data from
         * @param coaleasceLines if true, it will coaleasce lines
         */
        public ByteStreamPumper(InputStream is) {
            this.bis = is;
        }

        /**
         * Copies data from the input stream to the string buffer
         *
         * Terminates as soon as the input stream is closed or an error occurs.
         */
        protected void pumpStream()
            throws IOException {
            int bytesRead;
            while ((bytesRead = this.bis.read(this.inputBuffer)) != -1) {
                this.bytes.write(this.inputBuffer, 0, bytesRead);
            }
            this.bytes.flush();
            this.bytes.close();
            this.bis.close();
        }
    
        /**
         * @return A byte array contaning the raw bytes read from the
         * input stream.
         */
        public synchronized byte[] getBytes() {
            return bytes.toByteArray();
        }
    }
}
