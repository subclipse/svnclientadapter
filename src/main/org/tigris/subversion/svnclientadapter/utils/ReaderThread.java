package org.tigris.subversion.svnclientadapter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class has been taken from javasvn 
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
                myOutputStream.flush();
            } catch (IOException e) {
            }
        }
    }
}
