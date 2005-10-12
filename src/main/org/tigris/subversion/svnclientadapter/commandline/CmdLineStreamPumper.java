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
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copies all data from an input stream to a String
 * This class is inspired by ant StreamPumper from Robert Field
 *
 */
public class CmdLineStreamPumper implements Runnable {
    private static final String NEWLINE = "\n\r";
    private BufferedReader br;
    private boolean finished;
    private StringBuffer sb = new StringBuffer();
    private boolean coalesceLines = false;

    /**
     * Create a new stream pumper instance.
     *
     * @param is input stream to read data from
     * @param coalesceLines Whether to coalesce lines.
     */
    public CmdLineStreamPumper(InputStream is, boolean coalesceLines) {
        br = new BufferedReader(new InputStreamReader(is));
        this.coalesceLines = coalesceLines;
    }

    /**
     * Create a new stream pumper instance which does not coalesce
     * lines.
     *
     * @param is input stream to read data from
     */
    public CmdLineStreamPumper(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
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
            String st;
            while((st=br.readLine())!=null) {
                if (coalesceLines) {
                    sb.append(st);
                } else {
                    sb.append(st+NEWLINE);                    
                }
            }
        } catch (Exception e) {
            // ignore
        } finally {
            synchronized (this) {
                finished = true;
                notify();
            }
        }
    }

    public synchronized String toString() {
        return sb.toString();
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
}
