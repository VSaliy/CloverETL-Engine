/*
 *  jETeL/Clover - Java based ETL application framework.
 *  Copyright (C) 2002-04  David Pavlis
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Created on May 31, 2003
 */
package org.jetel.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.*;
import java.util.zip.Adler32;
import org.jetel.exception.*;

/**
 * Helper class for dynamic compiling of Java source code. Offers instantiating of compiled code.
 *
 *
 * @author      David Pavlis
 * @since       21. January 2004
 * @revision    $Revision$
 */
public class DynamicJavaCode {

	
	private String srcCode;
	private String srcPath;
	private String className;
	private String fileSeparator;
	private String fileName;

	public DynamicJavaCode(String srcCode) {
		this.srcCode=srcCode;
		srcPath = System.getProperty("java.io.tmpdir", ".");
		fileSeparator = System.getProperty("file.separator", "/");
		Pattern pattern=Pattern.compile("class\\s+([A-Za-z0-9_]+)\\s*{");
		Matcher matcher=pattern.matcher(srcCode);
		className=matcher.group();
		if (className.length()==0){
			throw new RuntimeException("Can't extract class name from source code !");
		}
		
	}

	private void saveSrc(){
		long checkSumFile;
		fileName=srcPath+fileSeparator+className+".java";
		Adler32 checkSumSrc=new Adler32();
		checkSumSrc.update(srcCode.getBytes());
		// try to get checksum of already (may be) saved src)
		checkSumFile=FileUtils.calculateFileCheckSum(fileName);
		
		// do we need to save src ? 
		if (checkSumFile!=checkSumSrc.getValue()){
			try{
				FileWriter writer=new FileWriter(fileName);
				writer.write(srcCode);
			}catch(IOException ex){
				throw new RuntimeException("Error when trying to save source code: "+ex.getMessage());
			}
		}
	}
	
	private void compile(){
		Compile compiler=new Compile(fileName);
		if (compiler.compile()!=0){
			throw new RuntimeException("Error(s) when compiling: "+fileName); 
		}
	}
	
	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public Object instantiate() {
		Class tClass;
		String urlString = "file:" + srcPath;
		URL[] myURLs;
		
		// firstly, save source
		saveSrc();
		// secondly, try to compile it
		compile();
		
		// now, load in class file 
		try {
			myURLs = new URL[]{new URL(urlString)};
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}

		URLClassLoader classLoader = new URLClassLoader(myURLs);
		try {
			tClass = Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		Object myObject;
		try {
			myObject = tClass.newInstance();
		} catch (Exception ex) {
			System.out.println("Error when crating object of class " + className + " : " + ex.getMessage());
			myObject = null;
		}

		return myObject;
	}


	/**
	 *  Gets the className attribute of the DynamicJavaCode object
	 *
	 * @return    The className value
	 */
	public String getClassName() {
		return className;
	}


	/**  Deletes dynamicaly created file. */
	public void clean() {
		try {
			
			new File(fileName).delete();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	

//	public static void main(String args[]){
//		DynamicJavaCode code = new DynamicJavaCode(args[0],args[1],args[1]);
//		try{
//			code.compile();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		DataRecordTransform trans;
//		Object testClass=code.instantiate();
//
//		trans=(DataRecordTransform)testClass;
//
//		System.out.println(trans.toString());
//	}


	public static DynamicJavaCode fromXML(org.w3c.dom.Node nodeXML){
		ComponentXMLAttributes xattribs = new ComponentXMLAttributes(nodeXML);
		String srcCode;
		
		try {
			// do we have child TEXT node -> possibly containing Java Source code ?
			srcCode=xattribs.getText(nodeXML);
			if (srcCode==null){
				throw new RuntimeException("Can't find SourceCode !");
			}
			
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return null;
		}
		return new DynamicJavaCode(srcCode);
	}
	
}

