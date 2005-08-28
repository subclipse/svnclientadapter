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