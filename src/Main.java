
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main.java
 * 
 * @author Samuel C. Donovan
 * Created: 01/01/22
 * Updated: 06/02/22
 * 
 * INSTRUCTIONS:
*/
public class Main {

	public static void main(String[] args) {

		String dataFile1 = System.getProperty("user.dir") + File.separator + "data" + File.separator
				+ "cw2DataSet1.csv";
		System.out.println("Loading from " + dataFile1);

		String dataFile2 = System.getProperty("user.dir") + File.separator + "data" + File.separator
				+ "cw2DataSet2.csv";
		System.out.println("Loading from " + dataFile2);

		ArrayList<int[]> dataset1 = readFile(dataFile1);
		ArrayList<int[]> dataset2 = readFile(dataFile2);
		
		System.out.println("\nEuclidean: ");
		System.out.println("-------------------");
		Euclidean.run(dataset1, dataset2);
		
		System.out.println("\nGenetic Algorithm: ");
		System.out.println("-------------------");

	}

	/**
	 * Reads data from file path and puts it into an ArrayList
	 * @param filePath, String containing the file path for the data
	 * @return ArrayList containing the rows of data from the dataset
	 */
	public static ArrayList<int[]> readFile(String filePath) {
		File file = new File(filePath); /* create file object to use a scanner on */

		String line = "", delimiter = ","; /* csv files are delimited by commas */
		String[] tempArray;
		int[] rowOfData = new int[65]; /* there are 65 data points in each row */
		ArrayList<int[]> dateset = new ArrayList<int[]>();

		try {

			/* create new file reader and buffer reader to parse data from data files */
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferReader = new BufferedReader(fileReader);

			/* loop until there are no more lines in the dataset */
			while ((line = bufferReader.readLine()) != null) {
				rowOfData = new int[65]; /* reset current rowOfData array */
				
				/* create an array from the current line, delimited by commas */
				tempArray = line.split(delimiter);

				for (int pos = 0; pos < tempArray.length; pos++) {
					/* parse each element from the array into an integer 
					 * and add to the current rowOfData array */
					rowOfData[pos] = Integer.parseInt(tempArray[pos]);
				}

				dateset.add(rowOfData); /* add row to dataset list */
			}

			bufferReader.close(); /* close buffer reader after all lines have been read */

		} catch (IOException fileNotFound) { /* if file is not found, stop the program */
			System.out.println("File not found at " + filePath);
			return dateset;
		} finally {
			return dateset;
		}
	}
}