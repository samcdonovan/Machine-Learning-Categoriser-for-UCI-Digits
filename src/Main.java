
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main.java
 * 
 * @author Samuel C. Donovan
 * Created: 01/01/22
 * Updated: 09/03/22
 * 
 * INSTRUCTIONS:
 * Lines 26 and 30 search the current directory for "cw2DataSet1.csv" and "cw2DataSet2.csv" respectively.
 * If those files are found in the current directory, the main function will first run the Nearest Neighbour algorithm,
 * which should finish in > 10s, with an accuracy of ~98.3%.
 * Next, the Multilayer Perceptron will be ran
 * Finally, the Genetic Algorithm will run. This should finish in > 30s and the accuracy will range
 * from roughly 45% to 70%, but will be different every time it is ran.
 */
public class Main {

	public static void main(String[] args) {

		/* get the file path for both datasets; these should be placed in the current directory */
		String dataFile1 = System.getProperty("user.dir") + File.separator + "data" + File.separator
				+ "cw2DataSet1.csv";
		System.out.println("Loading from " + dataFile1);

		String dataFile2 = System.getProperty("user.dir") + File.separator + "data" + File.separator
				+ "cw2DataSet2.csv";
		System.out.println("Loading from " + dataFile2);

		/* read the two datasets into 2D int arrays */
		int[][] dataset1 = readFile(dataFile1);
		int[][] dataset2 = readFile(dataFile2);

		/* run the Nearest Neighbour algorithm using Euclidean distance */
		System.out.println("\nNearest neighbour (Euclidean): ");
		System.out.println("-------------------");
		NearestNeighbour.twoFold(dataset1, dataset2);

		/* run the Multilayer perceptron */
		System.out.println("\nMultilayer Perceptron: ");
		System.out.println("-------------------");
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		mlp.twoFold(dataset1, dataset2);

		/* run the Genetic Algorithm */
		System.out.println("\nGenetic Algorithm (Simple 'Best' gene selection, uniform crossover): ");
		System.out.println("-------------------");
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
		geneticAlgorithm.twoFold(dataset1, dataset2);

	}

	/**
	 * Reads data from file path and puts it into an ArrayList
	 * 
	 * @param filePath, the file path for the data
	 * @return 2D array containing rows of data from the dataset
	 */
	public static int[][] readFile(String filePath) {
		File file = new File(filePath); /* create file object to use a scanner on */

		String line = "", delimiter = ","; /* csv files are delimited by commas */
		String[] tempArray;
		int[] rowOfData = new int[65]; /* there are 65 data points in each row */

		int lineCount = 0;
		int[][] dataset = new int[lineCount][];

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

				dataset = copyArray(dataset, ++lineCount); /* create a new array with one more line */
				dataset[lineCount - 1] = rowOfData; /* add new row of data to the end of the dataset array */
			}

			bufferReader.close(); /* close buffer reader after all lines have been read */

		} catch (IOException fileNotFound) { /* if file is not found, stop the program */
			System.out.println("File not found at " + filePath);
			return dataset;
		}
		return dataset;
	}

	/**
	 * Helper function to increase array size by one, copying all elements to the new array
	 * 
	 * @param originalArray, 2D array to be copied
	 * @param newSize, the new size of the array
	 * @return array with new size containing all previous elements
	 */
	public static int[][] copyArray(int[][] originalArray, int newSize) {

		/* initialise new array with new size */
		int[][] newArray = new int[newSize][];

		/* loop through every element in original array and copy it to the new array */
		for (int pos = 0; pos < originalArray.length; pos++)
			newArray[pos] = originalArray[pos];

		return newArray; /* return new array */
	}
}