package fr.gfi.tools.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe de lecture d'un fichier
 * @author philblan
 *
 */
public class FileIOUtil {
	
	/**
	 * Read a file in string table
	 * @param file	file to read
	 * @return List of readed lines
	 */
	public static List<String> readFile(String inputFilePath) {
		ArrayList<String> lignes = new ArrayList<String>();
		BufferedReader br = null;
		
		//Read text file	
		try {
			File file = new File(inputFilePath);
			InputStream ips=new FileInputStream(file);
			InputStreamReader ipsr=new InputStreamReader(ips);
			//Open cursor
			br=new BufferedReader(ipsr);
			String ligne;
			//Read lines
			while ((ligne=br.readLine())!=null){
				lignes.add(ligne);
			}

		} catch (FileNotFoundException e) {
			System.out.println("ERREUR : Fichier à lire non trouvé.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERREUR : Erreur à la lecture du fichier.");
			e.printStackTrace();
		} finally {
			//Close cursor
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 			
		}

		return lignes;
	}
	
	/**
	 * Write lines in a file
	 * @param outputFilePath	path of the output file
	 * @param lines		lines to write in the file
	 * @return File created or null if error 
	 */
	public static File writeFile(String outputFilePath, List<String> lines){
		PrintWriter printWriter = null;
		String line;
		File file = null;
		
		try {
			//Get file
			file = new File(outputFilePath);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter (fw);
			printWriter = new PrintWriter (bw);
			
			//Write lines in file
			Iterator<String> it = lines.iterator();
			while(it.hasNext()){
				line = it.next();
				printWriter.println(line);
			}
			
		} catch (IOException e) {
			file = null;
			System.out.println("ERREUR : Erreur à l'écriture du fichier.");
			e.printStackTrace();			
		} finally {
			//Close writer
			if (printWriter != null) printWriter.close();	
		}
		
		return file;
	}
	
	/**
	 * Replace string in a file 
	 * @param file	input file
	 * @param outputFilePath	output file path
	 * @param oldString		string to replace
	 * @param newString		replacement string 
	 * @return new file with replacement done
	 */
	public static File replaceInFile(File file, String outputFilePath, String[] oldStrings, String[] newStrings) {
		List<String> newLines = new ArrayList<String>();
		
		if (oldStrings.length!=newStrings.length) return null;
		
		//Read input file
		List<String> lines = readFile(file.getAbsolutePath());
		
		//Replace in the file content
		for (String line : lines) {
			for (int i = 0; i < newStrings.length; i++) {
				line = line.replace(oldStrings[i], newStrings[i]);
			}
			newLines.add(line);
		}
		
		//Write output file
		return writeFile(outputFilePath, newLines);
	}
	
	
	/**
	 * Replace string in a file 
	 * @param file	input file
	 * @param outputFilePath	output file path
	 * @param oldString		string to replace
	 * @param newString		replacement string 
	 * @return new file with replacement done
	 */
	public static File replaceInFile(File file, String outputFilePath, String oldString, String newString) {
		String[] oldStrings = new String[] {oldString};
		String[] newStrings = new String[] {newString};
		
		return replaceInFile(file, outputFilePath, oldStrings, newStrings);
	}

}
