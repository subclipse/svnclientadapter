/*******************************************************************************
 * Copyright (c) 2004, 2006 svnClientAdapter project and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     svnClientAdapter project committers - initial API and implementation
 ******************************************************************************/
package org.tigris.subversion.svnclientadapter.testUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class used to copy an entire directory or files 
 */
public class FileUtils {

   /** Recursively copy all files from one directory to another.
    *
    * @param src file or directory to copy from.
    * @param dest file or directory to copy to.
    * @throws IOException
    */
   public static void copyFiles(File src, File dest) throws IOException {
       if (!src.exists()) {
           return;
       }

       if (src.isDirectory()) {
           dest.mkdirs();

           String list[] = src.list();
           for (int i = 0; i < list.length; i++) {
               File src1 = new File(src, list[i]);
               File dest1 = new File(dest, list[i]);
               copyFiles(src1 , dest1);
           }

       } else {
           copyFile(src, dest);
       }
   }

   /**
    * copy a file from a source to a destination
    *
    * @param src
    * @param dest
    * @throws FileNotFoundException
    * @throws IOException
    */
   public static void copyFile(File src, File dest)
           throws FileNotFoundException, IOException {

       if (dest.exists()) {
           dest.delete();
       }
       
       // this part comes from org.apache.tools.ant.util.FileUtils
       
       FileInputStream in = null;
       FileOutputStream out = null;
       try {
           in = new FileInputStream(src);
           out = new FileOutputStream(dest);

           byte[] buffer = new byte[8 * 1024];
           int count = 0;
           do {
               out.write(buffer, 0, count);
               count = in.read(buffer, 0, buffer.length);
           } while (count != -1);
       } finally {
           if (out != null) {
               out.close();
           }
           if (in != null) {
               in.close();
           }
       }       
   }

    /**
     * Remove a directory with all files and directories it may contain.
     * @param localTmp
     */
    public static void removeDirectoryWithContent(File localTmp)
    {
        if(localTmp.isDirectory())
        {
            File[] content = localTmp.listFiles();
            for(int i = 0; i < content.length; i++)
                removeDirectoryWithContent(content[i]);
        }
        localTmp.delete();
    }    
}
