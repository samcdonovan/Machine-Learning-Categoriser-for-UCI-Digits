
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Main.java
 * 
 * @author Samuel C. Donovan
 * 
 * INSTRUCTIONS:
*/
public class Main {

	public static void main(String[] args) {

	
		String dataFile = System.getProperty("user.dir") + File.separator + "data" + File.separator + "cw2DataSet1.csv";
		System.out.println("Loading from " + dataFile);

		File file = new File(dataFile); /* create file object to use a scanner on */

		try {
			
			Scanner scanner = new Scanner(file);
			scanner.useDelimiter(","); /* delimit file by commas */   
			while (scanner.hasNext())    
			{  
			System.out.print(scanner.next() + " ");   
			}   

		} catch (FileNotFoundException fileNotFound) { /* if file is not found, stop the program */
			System.out.println("File not found at " + dataFile);
			return;
		} finally {

		}

	}

}
