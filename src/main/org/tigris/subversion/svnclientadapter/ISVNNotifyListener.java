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


/**
 * 
 * @author Cédric Chabanois 
 *         <a href="mailto:cchabanois@ifrance.com">cchabanois@ifrance.com</a>
 */
public interface ISVNNotifyListener {
    
    
    public static final class Command {
        public static final int UNDEFINED = 0;
        public static final int ADD = 1;
        public static final int CHECKOUT = 2;
        public static final int COMMIT = 3;
        public static final int UPDATE = 4;
        public static final int MOVE = 5;
        public static final int COPY = 6;
        public static final int REMOVE = 7;
        public static final int EXPORT = 8;
        public static final int IMPORT = 9;    
        public static final int MKDIR = 10;
        public static final int LS = 11;
        public static final int STATUS = 12;
        public static final int LOG = 13;
        public static final int PROPSET = 14;
        public static final int PROPDEL = 15;
        public static final int REVERT = 16;
        public static final int DIFF = 17;
        public static final int CAT = 18;
        public static final int INFO = 19;
        public static final int PROPGET = 20;
    }    

    public void setCommand(int command);
    
    /**
     * called at the beginning of the command
     * @param commandLine
     */
    public void logCommandLine(String commandLine);
    
    /**
     * called multiple times during the execution of a command
     * @param message
     */
    public void logMessage(String message);
    
    /**
     * called when an error happen during a command
     * @param message
     */
    public void logError(String message);

    /**
     * called when a command has completed
     * @param message
     */    
    public void logCompleted(String message);
    
    /**
     * called when a subversion action happen on a file (add, delete, update ...)
     * @param path the path of the file or dir
     * @param kind file or dir
     */
    public void onNotify(String path, SVNNodeKind kind);
    
}
