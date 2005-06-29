package org.tigris.subversion.svnclientadapter.utils;

import java.io.File;
import java.io.IOException;

/**
 * Start a svn server
 */
public class SvnServer {
	private Command command;

	private int listenPort = 3690;

	private File repository;

	public SvnServer() {
		
	}
    
	/**
	 * @return Returns the listenPort.
	 */
	public int getListenPort() {
		return listenPort;
	}
	/**
	 * @param listenPort The listenPort to set.
	 */
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	/**
	 * @return Returns the repository.
	 */
	public File getRepository() {
		return repository;
	}
	/**
	 * @param repository The repository to set.
	 */
	public void setRepository(File repository) {
		this.repository = repository;
	}
	public void start() throws IOException {
		command = new Command("svnserve.exe");
        command.setParameters(new String[] { "-d",
				"--foreground", "--listen-port", Integer.toString(listenPort),"-r",repository.toString()});
        command.exec();
	}

	public void kill() {
		if (command != null) {
			command.kill();
        }
	}

}