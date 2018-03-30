package fr.gfi.tools.xmlReader.workflow;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.gfi.tools.utils.FileIOUtil;


public class WorkflowReader {
	private static String filePath = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Worfkow reader start ...");

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
		System.out.println("Usage: windchill fr.gfi.tools.xmlReader.WorkflowReader");
		System.out.println("       -file path to XML file of workflow");
		System.out.println("       [-u user name] [-p user password]");
	}
	
	private static void run() {

		try {
		    
		    File xmlFile = new File(filePath);
	    	String formatedXmlPath = filePath;
		    System.out.println("File: " + filePath);
	    	if (filePath.contains(".")) {
	    		formatedXmlPath = filePath.substring(0, filePath.lastIndexOf(".")) + "_tmp" + filePath.substring(filePath.lastIndexOf("."));
	    	} else {
	    		formatedXmlPath = filePath + "_tmp";		    		
	    	}
	    	
	    	//Encode text content of the file
		    System.out.println("** Encode XML file");
	    	String[] oldStrings = new String[] {"<csvexprBody>", "</csvexprBody>","<csvexpression>","</csvexpression>","&apos;","&lt;","&gt;","&amp;amp;","&amp;","&amp;quot;","&quot;"};
	    	String[] newStrings = new String[] {"<csvexprBody><![CDATA[", "]]></csvexprBody>","<csvexpression><![CDATA[", "]]></csvexpression>","'","<",">","&","&","\"","\""};
	    	File formatedXml = FileIOUtil.replaceInFile(xmlFile, formatedXmlPath, oldStrings, newStrings);
	    	
	    	//Parse XML with SAX
		    SAXParserFactory factory = SAXParserFactory.newInstance();
		    factory.setValidating(false);
		    factory.setSchema(null);
		    SAXParser parser = factory.newSAXParser();
		    
		    WflHandler handler = new WflHandler();
		    FileInputStream fis = new FileInputStream(formatedXml);
		    parser.parse(fis, handler);
		    
		    //Write XML Workflow file
		    System.out.println("** Write output XML file");
	    	String outputXmlPath = filePath;
	    	if (filePath.contains(".")) {
	    		outputXmlPath = filePath.substring(0, filePath.lastIndexOf(".")) + "_ok" + filePath.substring(filePath.lastIndexOf("."));
	    	} else {
	    		outputXmlPath = filePath + "_ok";		    		
	    	}
	    	System.out.println("File: " + outputXmlPath);
	    	
	    	WflXmlWriter wflWriter = new WflXmlWriter();
	    	wflWriter.buildDocument(handler.getWorkflow());
	    	wflWriter.writeDocument(outputXmlPath);
		    
		    System.out.println("... End Workflow reader.");
		} catch (Exception e) {
		    e.printStackTrace();
		}

	}



}
