import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility.java: 
 * General utility functions that are used throughout the project
 * 
 * @author Samuel C. Donovan
 * @created 01/03/22
 * @updated 03/03/22
 */
public class Utility {

	/**
	 * Reads data from file path and puts it into a 2D int array
	 * 
	 * @param filePath, the file path for the data
	 * @return 2D array containing rows of data from the dataset
	 */
	public static int[][] readFile(String filePath) {
		File file = new File(filePath); /* create file object to use a scanner on */

		String line = "", delimiter = ","; /* csv files are delimited by commas */
		String[] tempArray;
		int[] rowOfData; /* there are 65 data points in each row */

		int lineCount = 0;
		int[][] dataset = new int[lineCount][];

		try {

			/* create new file reader and buffer reader to parse data from data files */
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferReader = new BufferedReader(fileReader);

			/* loop until there are no more lines in the dataset */
			while ((line = bufferReader.readLine()) != null) {

				/* create an array from the current line, delimited by commas */
				tempArray = line.split(delimiter);

				rowOfData = new int[tempArray.length]; /* reset current rowOfData array */

				for (int pos = 0; pos < tempArray.length; pos++) {
					/* parse each element from the array into an integer 
					 * and add to the current rowOfData array */
					rowOfData[pos] = Integer.parseInt(tempArray[pos]);
				}

				/* increment lineCount and create a new array with one more row of data in it */
				dataset = Utility.copyArray(dataset, ++lineCount);
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

	/**
	 * Euclidean distance calculator, calculates distance between two arrays  
	 * 
	 * @param firstArray, the first array to compare
	 * @param secondArray, the second array to compare against
	 * @return double, the Euclidean distance between each array 
	 */
	public static double euclideanDistance(int[] firstArray, int[] secondArray) {

		int sum = 0;

		/* sums the distance between each point in both arrays */
		for (int pos = 0; pos < firstArray.length - 1; pos++)
			sum += ((firstArray[pos] - secondArray[pos]) * (firstArray[pos] - secondArray[pos]));

		return Math.sqrt(sum);
	}

	public static void twoFold() {

	}

	/**
	 * Calculates the percentage of correct categorisations, rounds it to 2 d.p and
	 * then prints these values to the console.
	 * 
	 * @param firstFoldTotal, the total correct categorisations from the first fold
	 * @param secondFoldTotal, the total correct categorisations from the second fold
	 * @param firstDatasetLength, the length of the first dataset
	 * @param secondDatasetLength, the length of the second dataset
	 */
	public static void calculatePercentage(int firstFoldTotal, int secondFoldTotal, int firstDatasetLength,
			int secondDatasetLength) {
		
		/* print the total correct categorisations from each fold */
		System.out.println("First fold : " + firstFoldTotal + "/" + firstDatasetLength);
		System.out.println("Second fold : " + secondFoldTotal + "/" + secondDatasetLength);
		
		/* add fold totals together to get the total number of correct categorisations */
		int totalCorrect = firstFoldTotal + secondFoldTotal;
		int totalDatasetLength = firstDatasetLength + secondDatasetLength;

		/* get percentage by dividing total correct by the total dataset lenght, then multiplying by 100 */
		double percentageCorrect = ((double) totalCorrect / (double) totalDatasetLength) * 100.0;

		/* round the percentage to 2 d.p by multiplying it by 100, then rounding that value and dividing by 100 again */
		double percentageRounded = Math.round(percentageCorrect * 100.0) / 100.0;

		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		System.out.println("\nTotal correct: " + totalCorrect + "/" + (firstDatasetLength + secondDatasetLength) + " = "
				+ percentageRounded + "% (" + percentageCorrect + "%)");
	}

}
