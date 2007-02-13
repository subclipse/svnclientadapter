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
package org.tigris.subversion.svnclientadapter.testUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.tigris.subversion.svnclientadapter.utils.Command;

/**
 * Start a svn server
 */
public class SvnServer {

	private static SvnServer singleton = null;

	private Command command;

	final private int listenPort;

	final private String listenHost;

	private File repository;

	public static boolean isSvnServerRunning() {
		return singleton != null;
	}

	public static SvnServer startSvnServer(String listenHost, int listenPort,
			File repository) throws IOException {
		singleton = new SvnServer(listenHost, listenPort, repository);
		singleton.start();
		return singleton;
	}

	public static void stopSvnServer(SvnServer instance) {
		singleton.kill();
		singleton = null;
	}

	private SvnServer(String listenHost, int listenPort, File repository) {
		super();
		this.listenHost = listenHost;
		this.listenPort = listenPort;
		this.repository = repository;
		if ((listenHost == null) || (listenHost.length() == 0)) {
			throw new IllegalArgumentException("listenHost must not be null !");
		}
		if (listenPort == 0) {
			throw new IllegalArgumentException("listenPort must not be 0 !");
		}
		if (repository == null) {
			throw new IllegalArgumentException("repository must not be null !");
		}
	}

	/**
	 * @return Returns the listenHost
	 */
	public String getListenHost() {
		return listenHost;
	}

	/**
	 * @return Returns the listenPort.
	 */
	public int getListenPort() {
		return listenPort;
	}

	/**
	 * @return Returns the repository.
	 */
	public File getRepository() {
		return repository;
	}

	public void start() throws IOException {
		command = getSvnServerCommmand();
		command.setParameters(new String[] { "-d", "--foreground",
				"--listen-host", listenHost, "--listen-port",
				Integer.toString(listenPort), "-r", repository.toString() });
		command.exec();
	}

	public void kill() {
		if (command != null) {
			command.kill();
		}
	}

	protected Command getSvnServerCommmand() {
		return new Command(
				(System.getProperty("os.name").indexOf("Win") > -1) ? "svnserve.exe"
						: whichSvnserve());
	}

	private String whichSvnserve() {
		try {
			Process whichProc = Runtime.getRuntime().exec(
					"/usr/bin/which svnserve");

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					whichProc.getInputStream()));

			try {
				String line = reader.readLine();
				whichProc.getInputStream().close();
				whichProc.getOutputStream().close();
				whichProc.getErrorStream().close();
				return line;
			} catch (IOException e) {
				throw new RuntimeException("Cannot locate svnserve command !",
						e);
			}
		} catch (IOException e1) {
			throw new RuntimeException("Cannot locate svnserve command !", e1);
		}
	}

}