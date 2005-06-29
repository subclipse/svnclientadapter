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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Copies all data from an input stream to a byte array
 * This class is similar to the CmdLineStreamPumper, but uses a byte array instead for
 * binary or XML content which does not respond well to byte->character conversion
 *
 */
public class CmdLineByteStreamPumper implements Runnable {
    private InputStream bis;
    private boolean finished;
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private final static int BUFFER_LENGTH = 1024;
	private byte[] inputBuffer = new byte[BUFFER_LENGTH];

    /**
     * Create a new stream pumper.
     *
     * @param is input stream to read data from
     * @param coaleasceLines if true, it will coaleasce lines
     */
    public CmdLineByteStreamPumper(InputStream is) {
        bis = is;//new BufferedInputStream(is);
    }

    /**
     * Copies data from the input stream to the string buffer
     *
     * Terminates as soon as the input stream is closed or an error occurs.
     */
    public void run() {
        synchronized (this) {
            // Just in case this object is reused in the future
            finished = false;
        }

        try {
            int bytesRead;
            while((bytesRead = bis.read(inputBuffer))!=-1) {
            	bytes.write(inputBuffer, 0, bytesRead);
            }
            bytes.flush();
            bytes.close();
            bis.close();
        } catch (Exception e) {
            // ignore
        } finally {
            synchronized (this) {
                finished = true;
                notify();
            }
        }
    }

    /**
     * Tells whether the end of the stream has been reached.
     * @return true is the stream has been exhausted.
     **/
    public synchronized boolean isFinished() {
        return finished;
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
    
    /**
     * Returns the bytes read from the input stream.
     * 
     * @return a byte array contaning the raw bytes read. 
     */
    public synchronized byte[] getBytes() {
    	return bytes.toByteArray();
    }
}
