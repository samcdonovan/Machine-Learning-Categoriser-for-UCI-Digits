
/**
 * Euclidean.java
 * 
 * @author Samuel C. Donovan
 * Created: 17/01/22
 * Updated: 06/02/22
 * 
 * Nearest neighbour (using Euclidean distance) solution 
 * to the UCI digits task. Achieves ~98.3% accuracy
 */
public class Euclidean {

	/**
	 * Main function that runs the Euclidean categoriser and prints the 
	 * total number of correct categorisations, as well as how accurate it was (as a percentage)
	 * 
	 * @param dataset1 (int[][]), the first dataset
	 * @param dataset2 (int[][]), the second dataset
	 */
	public static void run(int[][] dataset1, int[][] dataset2) {
		/* get total dataset size; both datasets combined */
		int fullDatasetSize = dataset1.length + dataset2.length;

		/* use both datsets to run a 2-fold test, returning the 
		 * total number of correct categorisations from both folds */
		int totalCorrect = Euclidean.categorise(dataset1, dataset2) + Euclidean.categorise(dataset2, dataset1);

		/* check that datset is not empty */
		if (dataset1.length > 0 && dataset2.length > 0) {

			/* print the correct total and the caalculated percentage of correct categorisations */
			System.out.println("Total correct: " + totalCorrect + "/" + fullDatasetSize + " = "
					+ (((double) totalCorrect / (double) fullDatasetSize) * 100) + "%");
		}
	}

	/**
	 * Main categorisation function; uses Euclidean distance to 
	 * calculate the nearest row in the other dataset
	 * 
	 * @param dataset1 (int[][]), data from one of the datasets
	 * @param dataset2 (int[][]), data from the other dataset
	 * @return (int), number of correct categorisations
	 */
	public static int categorise(int[][] dataset1, int[][] dataset2) {

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

				currentDist = euclideanDistance(dataset1[dataset1Pos], dataset2[dataset2Pos]);

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

	/**
	 * Euclidean distance calculator, calculates distance
	 * between two rows of data from the dataset 
	 * 
	 * @param row1 (int[]), array representing a row in the dataset
	 * @param row2 (int[]), array representing another row in the dataset
	 * @return (double), the Euclidean distance between each row
	 */
	public static double euclideanDistance(int[] row1, int[] row2) {

		int sum = 0;

		/* sums the distance between each point in both arrays */
		for (int pos = 0; pos < row1.length - 1; pos++)
			sum += ((row1[pos] - row2[pos]) * (row1[pos] - row2[pos]));

		return Math.sqrt(sum);
	}
}