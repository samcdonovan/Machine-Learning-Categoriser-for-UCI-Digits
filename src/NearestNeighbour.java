
/**
 * NearestNeighbour.java:
 * Nearest neighbour (using Euclidean distance) solution 
 * to the UCI digits task. Achieves ~98.3% accuracy
 * 
 * @author Samuel C. Donovan
 * @created 17/01/22
 * @updated 06/02/22
 */
public class NearestNeighbour {

	/**
	 * Main function that runs a two fold test using the Nearest Neighbour algorithm. 
	 * Prints the total number of correct categorisations, as well as how accurate it was (as a percentage)
	 * 
	 * @param dataset1, the first dataset
	 * @param dataset2, the second dataset
	 */
	public static void twoFold(int[][] dataset1, int[][] dataset2) {

		/* get the total number of correct categorisations for the first fold */
		int firstFoldTotal = NearestNeighbour.categorise(dataset1, dataset2);

		/* get the total number of correct categorisations for the second fold */
		int secondFoldTotal = NearestNeighbour.categorise(dataset2, dataset1);

		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		Utility.calculatePercentage(firstFoldTotal, secondFoldTotal, dataset1.length, dataset2.length);

	}

	/**
	 * Main categorisation function; uses Euclidean distance to 
	 * calculate the nearest row in the other dataset, and checks if the categories match
	 * to determine whether the categorisation was correct or not.
	 * 
	 * @param dataset1, data from one of the datasets
	 * @param dataset2, data from the other dataset
	 * @return number of correct categorisations
	 */
	private static int categorise(int[][] dataset1, int[][] dataset2) {

		double min = Float.MAX_VALUE; /* current minimum distance, initialised to INF */
		double currentDist; /* current distance to compare to min */
		int minPos = 0; /* position of current min distance neighbour */
		int numCorrect = 0; /* total number of correct categorisations */

		int lastIndex = dataset1[0].length - 1; /* last index of each row (65) */

		/* loop through each row in dataset1 to get its nearest neighbour in dataset2 */
		for (int dataset1Pos = 0; dataset1Pos < dataset1.length; dataset1Pos++) {

			/* reset min to INF before each loop of the second dataset */
			min = Float.MAX_VALUE;

			/* loop through the dateset2, comparing the distance to each row and
			 * retrieving the nearest neighbour to the current row from dataset1 */
			for (int dataset2Pos = 0; dataset2Pos < dataset2.length; dataset2Pos++) {

				/* use Euclidean distance function from Utility class to calculate distance */
				currentDist = Utility.euclideanDistance(dataset1[dataset1Pos], dataset2[dataset2Pos]);

				/* if the distance between the two rows from each dataset is smaller than the current
				 * minimum distance, set minimum distance to this new distance and save the position in minPos*/
				if (currentDist < min) {
					min = currentDist;
					minPos = dataset2Pos;
				}
			}

			/* if the nearest neighbour both have the same category in their last cell (65)
			 * the categorisation is correct, numCorrect is incremented by 1 */
			if (dataset1[dataset1Pos][lastIndex] == dataset2[minPos][lastIndex])
				numCorrect++;
		}

		return numCorrect;
	}

}
