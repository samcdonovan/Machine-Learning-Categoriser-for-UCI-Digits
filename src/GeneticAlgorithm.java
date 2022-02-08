
/**
 * GeneticAlgorithm.java
 * 
 * @author Samuel C. Donovan
 * Created: 31/01/22
 * Updated: 06/02/22
 *
 * 
 */
public class GeneticAlgorithm {

	int[][] population; /* 2D array that holds the current population */
	private final int populationSize = 40;
	private final int geneLength = 640;
	private final int generations = 300;
	private final double mutationRate = 1.5;

	/**
	 * Function that runs the genetic algorithm with a 2-fold test
	 * 
	 * @param dataset1 (int[][]), the first dataset
	 * @param dataset2 (int[][]), the second dataset
	 */
	public void run(int[][] dataset1, int[][] dataset2) {

		/* generate an initial population using dataset1 */
		generatePopulation(dataset1);

		for (int i = 0; i < generations; i++) {
			
			uniformCrossover(dataset1);

		}

		int firstFoldTotal = twoFold(dataset2);
		System.out.println("Training set: cw2DataSet1.csv, test set: cw2DataSet2.csv");
		System.out.println("Correct categorisations = " + firstFoldTotal + "/" + dataset2.length + "\n");

		/* generate an initial population using dataset1 */
		generatePopulation(dataset2);
	
		/* run the genetic algorithm for the specified number of generations */
		for (int i = 0; i < generations; i++) {

			uniformCrossover(dataset2);

		}

		int secondFoldTotal = twoFold(dataset1);
		System.out.println("Training set: cw2DataSet2.csv, test set: cw2DataSet1.csv");
		System.out.println("Correct categorisations = " + secondFoldTotal + "/" + dataset1.length);
		int totalCorrect = firstFoldTotal + secondFoldTotal;

		System.out.println("\nTotal correct: " + totalCorrect + "/" + (dataset1.length + dataset2.length) + " = "
				+ ((double) totalCorrect / (double) (dataset1.length + dataset2.length)) * 100 + "%");

	}

	public int[] generateGene() {
		int[] gene = new int[geneLength];

		for (int genePos = 0; genePos < gene.length; genePos++) {
			gene[genePos] = (int) (Math.random() * 17);
		}

		return gene;
	}

	public void generatePopulation(int[][] dataset) {
		population = new int[populationSize][geneLength];

		for (int currentGene = 0; currentGene < populationSize; currentGene++)

			population[currentGene] = generateGene();

	}

	public static int[] getRow(int[] gene, int category) {

		int[] row = new int[64];
		// System.out.println("cat" +category);
		int currentSection = category * 64;

		for (int genePos = currentSection, rowPos = 0; genePos < currentSection + row.length; genePos++, rowPos++) {
			// System.out.println("rowgene"+genePos);
			row[rowPos] = gene[genePos];
		}
		return row;
	}

	public int twoFold(int[][] dataset) {
		int bestFitness = 0;
		int currentFitness = 0;
		for (int pos = 0; pos < population.length; pos++) {

			currentFitness = euclideanFitness(population[pos], dataset);
			if (currentFitness > bestFitness)
				bestFitness = currentFitness;
		}

		return bestFitness;
	}

	public static int euclideanFitness(int[] gene, int[][] dataset) {

		int numCorrect = 0;
		double currentDist;
		double min = Double.MAX_VALUE;
		int minPos = -1;
		int[] currentRow;

		for (int pos = 0; pos < 10; pos++) {
			// System.out.println("pos"+pos);
			currentRow = getRow(gene, pos);
			for (int datasetPos = 0; datasetPos < dataset.length; datasetPos += 10) {
				for (int somethign = datasetPos; somethign < datasetPos + 10; somethign++) {
					// System.out.println(datasetPos);
					// System.out.println(somethign);
					currentDist = euclideanDistance(currentRow, dataset[somethign]);

					/* if the distance between the two rows from each dataset is smaller than the current
					 * minimum distance, set minimum distance to this new distance and save the position in minPos*/
					if (currentDist < min) {
						min = currentDist;
						minPos = somethign;
					}
				}

				/* if the nearest neighbour both have the same category in their last cell (65)
				 * the categorisation is correct, numCorrect is incremented by 1 */
				if (pos == dataset[minPos][dataset[0].length - 1])
					numCorrect++;

			}
		}
		return numCorrect;
	}

	/**
	 * Euclidean distance calculator, calculates distance
	 * between two rows of data from the dataset 
	 * @param row1, int array representing a row in the dataset
	 * @param row2, int array representing another row in the dataset
	 * @return double, the Euclidean distance between each row
	 */
	public static double euclideanDistance(int[] row1, int[] row2) {

		int sum = 0;

		/* sums the distance between each point in both arrays */
		for (int pos = 0; pos < row1.length - 1; pos++)
			sum += ((row1[pos] - row2[pos]) * (row1[pos] - row2[pos]));

		return Math.sqrt(sum);
	}

	/**
	 * Fitness function, checks current gene against the test dataset, summing 
	 * the number of correct categories 
	 * 
	 * @param gene (int[]), the gene to be evaluated
	 * @param dataset (int[][]), the test dataset for 2-fold testing
	 * @return (int) the number of correct categorisations
	 */
	public int fitness(int[] gene, int[][] dataset) {
		int numCorrect = 0;

		for (int genePos = 0; genePos < gene.length; genePos++) {

			if (gene[genePos] == dataset[genePos][64])
				numCorrect++;
		}

		return numCorrect;
	}

	/**
	 * Retrieves the two genes in the population with the highest fitness
	 * 
	 * @param dataset (int[][]), current dataset that is being trained on
	 * @return
	 */
	public int[] findBestGenes(int[][] dataset) {
		int currentEval;
		int firstHighest = 0, secondHighest = 0;
		int firstHighestPos = 0, secondHighestPos = 0;

		/* loop through each gene in the population in order 
		 * to find the two genes with the highest fitnesses */
		for (int currentGene = 0; currentGene < population.length; currentGene++) {

			/* get fitness for the current gene */
			currentEval = euclideanFitness(population[currentGene], dataset);
			// System.out.println(currentGene + " : " + currentEval);

			if (currentEval > firstHighest) {
				secondHighest = firstHighest;
				secondHighestPos = firstHighestPos;

				firstHighest = currentEval;
				firstHighestPos = currentGene;
			} else if (currentEval >= secondHighest) {
				secondHighest = currentEval;
				secondHighestPos = currentGene;
			}

		}

		/* return an array containing the positions of the two best genes in the population */
		int[] highestArr = { firstHighestPos, secondHighestPos };
		// System.out.println("Best: " + firstHighestPos + " " +
		// secondHighestPos);
		return highestArr;
	}

	/**
	 * Uniform crossover function; for each category in the gene, there
	 * is a 50% chance for it to crossover. If the category already matches
	 * the category in the dataset, it does not crossover.
	 * 
	 * @param dataset (int[][]), current dataset that is being trained
	 */
	public void uniformCrossover(int[][] dataset) {
		double crossoverChance = 50.0;
		double randomChance = Math.random();
		int tempElement;
		int[] bestGenes = findBestGenes(dataset);

		int[] gene1 = population[bestGenes[0]], gene2 = population[bestGenes[1]];
		int[] newGene1 = gene1.clone(), newGene2 = gene2.clone();

		/* create a new gene using uniform crossover for each position in the population */
		for (int newGenePos = 0; newGenePos < populationSize; newGenePos++) {

			newGene1 = gene1.clone();
			newGene2 = gene2.clone();

			/* loop through each bit in the gene with a 50% chance for a crossover to occur */
			for (int pos = 0; pos < gene1.length; pos++) {

				/* if the current category matches the category in the dataset (at the same position),
				 * crossover and mutation are not performed. This helps ensure fitness remains high */
				// if (gene1[pos] != dataset[pos][64]) {

				randomChance = Math.random() * 100;

				/* if the random chance is higher than the crossover chance, perform crossover */
				if (randomChance >= crossoverChance) {
					tempElement = newGene1[pos];
					newGene1[pos] = gene2[pos];
					newGene2[pos] = tempElement;
				}

				/* call mutate on the current gene bit (category) */
				newGene1[pos] = mutate(newGene1[pos]);
				newGene2[pos] = mutate(newGene2[pos]);
				// }
			}

			/* insert the new gene into the population */
			population[newGenePos] = newGene1.clone();

			population[++newGenePos] = newGene2.clone();
		}

	}

	/**
	 * Gene mutation function, 1.5% chance to change current bit 
	 * in the gene to a random category (0-9)
	 * 
	 * @param geneBit (int), the gene bit to perform mutation on
	 * @return (int) the gene bit that is either mutated or unchanged
	 */
	public int mutate(int geneBit) {

		double rand = Math.random() * 100;

		/* if the random number is less than or equal to the mutation rate,
		 * the gene bit is mutated */
		if (mutationRate >= rand)
			geneBit = (int) (Math.random() * 17);

		return geneBit;
	}

}
