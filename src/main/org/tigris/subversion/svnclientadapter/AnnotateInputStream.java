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
package org.tigris.subversion.svnclientadapter;

import java.io.IOException;
import java.io.InputStream;


/**
 * not a very efficient implementation for now ... 
 */
public class AnnotateInputStream extends InputStream {
    private ISVNAnnotations annotations;
	private int currentLineNumber;
    private int currentPos;
    private String currentLine;
    private int available;
    
    public AnnotateInputStream(ISVNAnnotations annotations) {
		this.annotations = annotations;
		initialize();
    }
    
    private void initialize() {
    	currentLine = annotations.getLine(0);
        currentLineNumber = 0;
        currentPos = 0;
        
        available = 0;
        int annotationsSize = annotations.size();
        for (int i = 0; i < annotationsSize;i++) {
        	available += annotations.getLine(i).length(); 
        	if (i != annotationsSize-1) {
        		available++; // +1 for \n
        	}
        }
    }
    
    /*
     *  (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        if (currentLineNumber >= annotations.size())
            return -1; // end of stream
        if (currentPos > currentLine.length()) {
            getNextLine();
            if (currentLineNumber >= annotations.size())
                return -1; // end of stream                
        }
        int character;
        if (currentPos == currentLine.length())
        	character = '\n';
        else
        	character = currentLine.charAt(currentPos);
        currentPos++;
        available--;
        return character;
    }
    
    private void getNextLine() {
        currentLineNumber++;
        currentPos = 0;
        currentLine = annotations.getLine(currentLineNumber);
    }

    /*
     *  (non-Javadoc)
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return available;
    }
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		initialize();
	}
}