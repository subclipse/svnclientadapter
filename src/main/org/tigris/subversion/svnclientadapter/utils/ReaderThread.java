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
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class has been taken from SVNKit 
 */
public class ReaderThread extends Thread {
    
    private final InputStream myInputStream;
    private final OutputStream myOutputStream;

    public ReaderThread(InputStream is, OutputStream os) {
        myInputStream = is;
        myOutputStream = os;
        setDaemon(true);            
    }

    public void run() {
        try {
            while(true) {
                int read = myInputStream.read();
                if (read < 0) {
                    return;
                }
                myOutputStream.write(read);
            }
        } catch (IOException e) {
        } finally {
            try {
            	myInputStream.close();
                myOutputStream.flush();
            } catch (IOException e) {
            	//Just ignore. Stream closing.
            }
        }
    }
}
