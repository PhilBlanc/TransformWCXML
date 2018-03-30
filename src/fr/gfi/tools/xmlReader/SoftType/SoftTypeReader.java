package fr.gfi.tools.xmlReader.SoftType;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.gfi.tools.utils.FileIOUtil;


public class SoftTypeReader {
	private static String filePath = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("SoftType reader start ...");

		/*
		 *  Read arguments
		 */
		boolean areArgsOk = false;
		for (int j = 0; j < args.length; ++j) {
			if (args[j].equals("-file")) {
				if (++j < args.length) filePath = new String(args[j]);
			} 
		}

		if (filePath != null) {
			areArgsOk = true;
		}
		
		if (!areArgsOk) {
			printUsage();
		} else {
			run();
		}

	}

	/**
	 * help instruction
	 */
	private static void printUsage() {
		System.out.println("Usage: windchill fr.gfi.tools.xmlReader.SoftTypeReader");
		System.out.println("       -file path to XML file of SoftTypes or path list separated by ';'");
		System.out.println("       [-u user name] [-p user password]");
	}
	
	private static void run() {

		if (filePath.contains(";")) {
			// Process XML list
			String[] paths = filePath.split(";");
			for (int i = 0; i < paths.length; i++) {
				processXml(paths[i]);
			}
			
		} else {
			processXml(filePath);
		}
				    
	    System.out.println("... End SoftType reader.");


	}

	private static void processXml(String filePath) {
	    System.out.println("\n* Read File: " + filePath);
		try {
		    File xmlFile = new File(filePath);
	    	String formatedXmlPath = filePath;
	    	if (filePath.contains(".")) {
	    		formatedXmlPath = filePath.substring(0, filePath.lastIndexOf(".")) + "_tmp" + filePath.substring(filePath.lastIndexOf("."));
	    	} else {
	    		formatedXmlPath = filePath + "_tmp";		    		
	    	}
	    	
	    	//Encode text content of the file
		    System.out.println("** Encode XML file");
	    	String[] oldStrings = new String[] {"<csvruleData>","</csvruleData>", "&apos;","&lt;","&gt;","&amp;amp;","&amp;","&amp;quot;","&quot;"};
	    	String[] newStrings = new String[] {"<csvruleData><![CDATA[", "]]></csvruleData>", "'","<",">","&","&","\"","\""};
	    	File formatedXml = FileIOUtil.replaceInFile(xmlFile, formatedXmlPath, oldStrings, newStrings);
	    	
	    	//Parse XML with SAX
		    SAXParserFactory factory = SAXParserFactory.newInstance();
		    factory.setValidating(false);
		    factory.setSchema(null);
		    SAXParser parser = factory.newSAXParser();
		    
		    SoftTypeHandler handler = new SoftTypeHandler();
		    FileInputStream fis = new FileInputStream(formatedXml);
		    parser.parse(fis, handler);
		    
		    //Write XML SoftType file
		    System.out.println("** Write output XML file");
	    	String outputXmlPath = filePath;
	    	if (filePath.contains(".")) {
	    		outputXmlPath = filePath.substring(0, filePath.lastIndexOf(".")) + "_ok" + filePath.substring(filePath.lastIndexOf("."));
	    	} else {
	    		outputXmlPath = filePath + "_ok";		    		
	    	}
	    	System.out.println("File: " + outputXmlPath);
	    	
	    	SoftTypeXmlWriter typeWriter = new SoftTypeXmlWriter();
	    	typeWriter.buildDocument(handler.getTypes());
	    	typeWriter.writeDocument(outputXmlPath);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
