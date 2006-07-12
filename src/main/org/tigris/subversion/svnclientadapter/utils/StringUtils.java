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

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for various string operations
 * 
 */
public class StringUtils {

	/**
	 * we can't use String.split as it is a JDK 1.4 method.
	 * //TODO Java 1.4 is not aproblem anymore. Isn't it ? 
	 * @param str
	 * @param separator
	 * @return an array of string segments
	 */
	static public String[] split(String str, char separator) {
		int pos = 0;
		List list = new ArrayList();
		int length = str.length();
		for (int i = 0; i < length;i++) {
			char ch = str.charAt(i);
			if (ch == separator) {
				list.add(str.substring(pos,i));
				pos = i+1;
			}
		}
		if (pos != length) {
			list.add(str.substring(pos,length));
		}
		return (String[])list.toArray(new String[list.size()]);
	}

	/**
	 * split using a string separator
	 * @param str
	 * @param separator
	 * @return an array of string segments
	 */
	static public String[] split(String str, String separator) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(str);
		int pos;
		
		while ((pos = sb.indexOf(separator)) != -1) {
			list.add(sb.substring(0,pos));
			sb.delete(0,pos+separator.length());
		}
		if (sb.length() > 0) {
			list.add(sb.toString());
		}
		return (String[])list.toArray(new String[list.size()]);
	}

	
	// the following method has been taken from commons-lang StringUtils class
	
	
    /**
     * <p>Strips any of a set of characters from the start of a String.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.</p>
     *
     * <p>If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.stripStart(null, *)          = null
     * StringUtils.stripStart("", *)            = ""
     * StringUtils.stripStart("abc", "")        = "abc"
     * StringUtils.stripStart("abc", null)      = "abc"
     * StringUtils.stripStart("  abc", null)    = "abc"
     * StringUtils.stripStart("abc  ", null)    = "abc  "
     * StringUtils.stripStart(" abc ", null)    = "abc "
     * StringUtils.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                start++;
            }
        }
        return str.substring(start);
    }	
	
}
