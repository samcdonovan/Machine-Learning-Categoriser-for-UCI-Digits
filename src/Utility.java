/**
 * 
 * @author Samuel C. Donovan
 * @created 01/03/22
 * @updated 03/03/22
 */
public class Utility {

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
	 * Euclidean distance calculator, calculates distance
	 * between two arrays  
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

	public static void twoFold(){
		
	}
	
	public static void calculatePercentage(int firstFoldTotal, int secondFoldTotal) {

		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		int totalCorrect = firstFoldTotal + secondFoldTotal;
		double percentageCorrect = ((double) totalCorrect / (double) (dataset1.length + dataset2.length)) * 100.0;
		double percentageRounded = Math.round(percentageCorrect * 100.0) / 100.0;
		
		System.out.println("\nTotal correct: " + totalCorrect + "/" + (dataset1.length + dataset2.length) + " = "
				+ percentageRounded + "% (" + percentageCorrect + "%)");
	}
}
