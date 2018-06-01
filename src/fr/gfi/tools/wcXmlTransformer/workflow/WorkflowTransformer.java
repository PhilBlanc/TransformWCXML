package fr.gfi.tools.wcXmlTransformer.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.gfi.tools.utils.FileIOUtil;


public class WorkflowTransformer {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = null;
		
		System.out.println("Worfkow reader start ...");

		/*
		 *  Read arguments
		 */
		boolean areArgsOk = false;
		for (int j = 0; j < args.length; ++j) {
			if (args[j].equals("-file")) {
				if (++j < args.length) path = new String(args[j]);
			} 
		}

		if (path != null) {
			areArgsOk = true;
		}
		
		if (!areArgsOk) {
			printUsage();
		} else {
			run(path);
		}

	}

	/**
	 * help instruction
	 */
	private static void printUsage() {
		System.out.println("Usage: windchill fr.gfi.tools.xmlReader.WorkflowReader");
		System.out.println("       -file path to XML file of workflow or path to directory of XML files");
		System.out.println("       [-u user name] [-p user password]");
	}

	private static void run(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			String[] wflFileList = file.list(new WflFilenameFilter());
			for (int i = 0; i < wflFileList.length; i++) {
				trasformFile(file.getAbsolutePath() + File.separator + wflFileList[i]);
			}
		} else {
			trasformFile(path);
		}
	}
	
	private static void trasformFile(String filePath) {
		FileInputStream fis = null;

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
	    	String[] oldStrings = new String[] {"<csvinstructions>", "</csvinstructions>","<csvexprBody>", "</csvexprBody>","<csvexpression>","</csvexpression>","&apos;","&lt;","&gt;","&amp;amp;","&amp;","&amp;quot;","&quot;"};
	    	String[] newStrings = new String[] {"<csvinstructions><![CDATA[", "]]></csvinstructions>","<csvexprBody><![CDATA[", "]]></csvexprBody>","<csvexpression><![CDATA[", "]]></csvexpression>","'","<",">","&","&","\"","\""};
	    	File formatedXml = FileIOUtil.replaceInFile(xmlFile, formatedXmlPath, oldStrings, newStrings);
	    	
	    	//Parse XML with SAX
		    SAXParserFactory factory = SAXParserFactory.newInstance();
		    factory.setValidating(false);
		    factory.setSchema(null);
		    SAXParser parser = factory.newSAXParser();
		    
		    WflHandler handler = new WflHandler();
		    fis = new FileInputStream(formatedXml);
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
	    	
	    	// Delete Temp file
	    	formatedXml.delete();
	    	
		    System.out.println("... End Workflow reader.");
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}


}
