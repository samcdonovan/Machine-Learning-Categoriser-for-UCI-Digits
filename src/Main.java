import java.io.File;

/**
 * Main.java 
 * INSTRUCTIONS:
 * Lines 26 and 30 search the current directory for "cw2DataSet1.csv" and "cw2DataSet2.csv" respectively.
 * If those files are found in the current directory, the main function will run the following algorithms.
 * 
 * First is the Nearest Neighbour algorithm, which should finish in > 10s, with an accuracy of ~98.3%.
 * Next, the Multilayer Perceptron will run. This should finish in ~60s and the accuracy will range from 90% to 94%.
 * Finally, the Genetic Algorithm will run. This should finish in less than 30s and the accuracy will range
 * from roughly 45% to 70%, but will differ every time it is ran.
 * 
 * @author Samuel C. Donovan
 * @created 01/01/22
 * @updated: 03/03/22
 */
public class Main {

	public static void main(String[] args) {

		/* get the file path for both datasets; these should be placed in the current directory */
		String dataFile1 = System.getProperty("user.dir") + File.separator + "cw2DataSet1.csv";
		System.out.println("Loading from " + dataFile1);

		String dataFile2 = System.getProperty("user.dir") + File.separator + "cw2DataSet2.csv";
		System.out.println("Loading from " + dataFile2);

		/* read the two datasets into 2D int arrays */
		int[][] dataset1 = Utility.readFile(dataFile1);
		int[][] dataset2 = Utility.readFile(dataFile2);

		/* check that datasets aren't empty */
		if (dataset1.length == 0 || dataset2.length == 0)
			return;
		
		/* run the Nearest Neighbour algorithm using Euclidean distance */
		System.out.println("\nNearest neighbour (Euclidean):" + "\n-------------------");
		NearestNeighbour nearestNeighbour = new NearestNeighbour();
		nearestNeighbour.twoFold(dataset1, dataset2);

		/* run the Multilayer Perceptron */
		System.out.println(
				"-------------------\n\n" + "Multilayer Perceptron (running time = ~70s):" + "\n-------------------");
		MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();
		multilayerPerceptron.twoFold(dataset1, dataset2);

		/* run the Genetic Algorithm */
		System.out.println(
				"-------------------\n\n" + "Genetic Algorithm (running time = ~30s):" + "\n-------------------");
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
		geneticAlgorithm.twoFold(dataset1, dataset2);

	}
}