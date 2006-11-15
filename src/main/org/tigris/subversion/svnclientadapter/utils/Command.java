/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * execute a command. Some parts of this class come from SVNKit
 */
public class Command {
	private Process process = null;

	private String command;
    private String[] parameters = new String[] {}; 

	private OutputStream out = System.out;
	private OutputStream err = System.err;

	public Command(String command) {
		this.command = command;
	}

	/**
	 * @param err
	 *            The err to set.
	 */
	public void setErr(OutputStream err) {
		this.err = err;
	}

	/**
	 * @param out
	 *            The out to set.
	 */
	public void setOut(OutputStream out) {
		this.out = out;
	}
    
	/**
	 * @param parameters The parameters to set.
	 */
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
    
	/**
	 * @return Returns the process.
	 */
	public Process getProcess() {
		return process;
	}

	public void kill() {
		if (process != null) {
			process.destroy();
			process = null;
		}
	}

	public void exec() throws IOException {
        String[] cmdArray= new String[parameters.length+1];
        cmdArray[0] = command;
        System.arraycopy(parameters,0,cmdArray,1,parameters.length);
		process = Runtime.getRuntime().exec(cmdArray);
		if (process != null) {
			new ReaderThread(process.getInputStream(), out).start();
			new ReaderThread(process.getErrorStream(), err).start();
			process.getOutputStream().close();
		}
	}

	/**
	 * 
	 * causes the current thread to wait, if necessary, until the process
	 * represented by this <code>Command</code> object has terminated
	 * 
	 * @return the exit value of the process. By convention, <code>0</code>
	 *         indicates normal termination.
	 * @throws InterruptedException
	 */
	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}

}