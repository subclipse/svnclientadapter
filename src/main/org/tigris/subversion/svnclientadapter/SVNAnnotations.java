/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
package org.tigris.subversion.svnclientadapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Handles annotations (see svn ann) 
 *
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 */
public class SVNAnnotations {
    private Vector lines = new Vector();


    public SVNAnnotations() {
    }

    public SVNAnnotations(byte[] annotations) {
        String annStr = new String(annotations);
        StringTokenizer st = new StringTokenizer(annStr,"\n",false);
        
        while (st.hasMoreTokens()) {
            add(st.nextToken());
        }
    }

    public void add(int revision, String author, String line) {
        // not implemented yet 
    }

    public void add(String annotationLine) {
        lines.add(annotationLine);
    }

    /**
     * get the revision for the given line number
     * @param lineNumber
     * @return
     */
    public int getRevision(int lineNumber) {
        if (lineNumber >= lines.size())
            return -1;
        else
        {
            String line = (String)lines.get(lineNumber);
            return Integer.parseInt(line.substring(0,5));            
        }
    }
    
    /**
     * get the author for the given line number
     * @param lineNumber
     * @return
     */
    public String getAuthor(int lineNumber) {
        if (lineNumber >= lines.size())
            return null;
        else
        {
            String line = (String)lines.get(lineNumber);
            return line.substring(6,16);            
        }
    }

    /**
     * get the given line
     * @param lineNumber
     * @return
     */
    public String getLine(int lineNumber) {
        if (lineNumber >= lines.size())
            return null;
        else
        {
            String line = (String)lines.get(lineNumber);
            return line.substring(17);            
        }
    }    
    
    /**
     * not a very efficient implementation for now ... 
     */
    private class AnnotateInputStream extends InputStream {
        private int currentLineNumber;
        private int currentPos;
        private String currentLine;
        
        public AnnotateInputStream() {
            currentLine = getLine(0);
            currentLineNumber = 0;
            currentPos = 0;                
        }
        
        /*
         *  (non-Javadoc)
         * @see java.io.InputStream#read()
         */
        public int read() throws IOException {
            if (currentLineNumber >= lines.size())
                return -1; // end of stream
            if (currentPos > currentLine.length()) {
                getNextLine();
                if (currentLineNumber >= lines.size())
                    return -1; // end of stream                
            }
            int character;
            if (currentPos == currentLine.length())
                return '\n';
            character = currentLine.charAt(currentPos);
            currentPos++;
            return character;
        }
        
        public void getNextLine() {
            currentLineNumber++;
            currentPos = 0;
            currentLine = getLine(currentLineNumber);
        }

        /*
         *  (non-Javadoc)
         * @see java.io.InputStream#available()
         */
        public int available() throws IOException {
            int avail = 0;
            for (int i = 0; i < lines.size();i++) {
                avail += getLine(i).length()+1; // +1 for \n
            }
            return avail;
        }
        
    }
    
    /**
     * get an inputstream of the content of the file
     * @return
     */
    public InputStream getInputStream() {
        return new AnnotateInputStream();
    }
    
}
