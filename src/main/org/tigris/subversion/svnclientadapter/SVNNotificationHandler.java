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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Notification handler :
 * It sends notifications to all listeners 
 */
public abstract class SVNNotificationHandler {
    protected List notifylisteners = new ArrayList();
    protected int command;
    protected boolean logEnabled = true;
        
    /**
     * Add a notification listener
     */
    public void add(ISVNNotifyListener listener) {
        notifylisteners.add(listener);
    }

    /**
     * Remove a notification listener 
     */
    public void remove(ISVNNotifyListener listener) {
        notifylisteners.remove(listener);
    }
    
    public void enableLog() {
        logEnabled = true;
    }
    
    /**
     * disable logging. Note that errors and exceptions are not disabled
     */
    public void disableLog() {
        logEnabled = false;
    }
        
    public void logMessage(String message) {
        if (logEnabled) {
            for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
                listener.logMessage(message);
            }
        }                        
    }

    public void logError(String message) {
        for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
            ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
            listener.logError(message);
        }                        
    }

    public void logCompleted(String message) {
        if (logEnabled) {
            for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
                listener.logCompleted(message);
            }
        }                        
    }    

    public void setCommand(int command) {
        this.command = command;        
        for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
            ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
            listener.setCommand(command);
        }            
    }
    
    public void logCommandLine(String commandLine) {
        if (logEnabled) {
            for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
                ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
                listener.logCommandLine(commandLine);
            }
        }                        
    }

    /**
     * To call when a method of ClientAdapter throw an exception
     */        
    public void logException(Exception clientException) {
        Throwable e = clientException;
        while (e != null) {
            logError(e.getMessage());
            e = e.getCause();                
        }
    }
    
    public void notifyListenersOfChange(File path, SVNNodeKind kind) {
        for(Iterator it=notifylisteners.iterator(); it.hasNext();) {
            ISVNNotifyListener listener = (ISVNNotifyListener)it.next();
            listener.onNotify(path, kind);
        }  
    }
    
}
