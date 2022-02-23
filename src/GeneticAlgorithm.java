
/**
 * GeneticAlgorithm.java
 * 
 * @author Samuel C. Donovan
 * Created: 31/01/22
 * Updated: 20/02/22
 *
 * Genetic algorithm 
 */
public class GeneticAlgorithm {

	int[][] population; /* 2D array that holds the current population */
	private static final int POPULATION_SIZE = 40; /* size of the population */
	private static final int GENE_LENGTH = 640; /* length of each gene in the population */
	private static final int GENERATIONS = 300; /* number of generations for breeding */
	private static final double MUTATION_RATE = 2; /* rate at which each gene mutates */

	int[][] trainSet;
	int[][] testSet;

	/**
	 * Function that runs the genetic algorithm with a 2-fold test
	 * 
	 * @param dataset1 (int[][]), the first dataset
	 * @param dataset2 (int[][]), the second dataset
	 */
	public void run(int[][] dataset1, int[][] dataset2) {
		double percentageCorrect = 0.0;
		while (percentageCorrect < 64.0) {
			/* generate an initial population using dataset1 */
			generatePopulation(dataset1);

			for (int i = 0; i < GENERATIONS; i++) {

				// uniformCrossover(dataset1);
				// bestGeneSelection(dataset1);
				tournamentSelection(dataset1);
			}

			int firstFoldTotal = twoFold(dataset2);
			System.out.println("Training set: cw2DataSet1.csv, test set: cw2DataSet2.csv");
			System.out.println("Correct categorisations = " + firstFoldTotal + "/" + dataset2.length + "\n");

			/* generate an initial population using dataset1 */
			generatePopulation(dataset2);

			/* run the genetic algorithm for the specified number of generations */
			for (int i = 0; i < GENERATIONS; i++) {

				// uniformCrossover(dataset2);
				// bestGeneSelection(dataset2);
				tournamentSelection(dataset2);
			}

			int secondFoldTotal = twoFold(dataset1);
			System.out.println("Training set: cw2DataSet2.csv, test set: cw2DataSet1.csv");
			System.out.println("Correct categorisations = " + secondFoldTotal + "/" + dataset1.length);

			int totalCorrect = firstFoldTotal + secondFoldTotal;

			percentageCorrect = ((double) totalCorrect / (double) (dataset1.length + dataset2.length)) * 100;

			System.out.println("\nTotal correct: " + totalCorrect + "/" + (dataset1.length + dataset2.length) + " = "
					+ Math.round(percentageCorrect * 10.0) / 10.0 + "% (" + percentageCorrect + "%)");
		}
	}

	public int[] generateGene() {
		int[] gene = new int[GENE_LENGTH];

		for (int genePos = 0; genePos < gene.length; genePos++) {
			gene[genePos] = (int) (Math.random() * 17);
		}

		return gene;
	}

	public void generatePopulation(int[][] dataset) {
		population = new int[POPULATION_SIZE][GENE_LENGTH];

		for (int currentGene = 0; currentGene < POPULATION_SIZE; currentGene++)

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
	public void bestGeneSelection(int[][] dataset) {

		int currentEval;
		int firstHighest = 0, secondHighest = 0;
		int firstHighestPos = 0, secondHighestPos = 0;

		/* loop through each gene in the population in order 
		 * to find the two genes with the highest fitnesses */
		for (int currentGene = 0; currentGene < population.length; currentGene++) {

			/* get fitness for the current gene */
			currentEval = euclideanFitness(population[currentGene], dataset);

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

		int[] bestGene1 = population[firstHighestPos].clone(), bestGene2 = population[secondHighestPos].clone();
		for (int populationPos = 0; populationPos < population.length; populationPos += 2)
			uniformCrossover(populationPos, bestGene1, bestGene2);
		// return highestArr;
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

		return highestArr;
	}

	/**
	* Retrieves the two genes in the population with the highest fitness
	* 
	* @param dataset (int[][]), current dataset that is being trained on
	* @return
	*/
	public int[] findBestGenesTournament(int[][] tournament, int[][] dataset) {
		int currentEval;
		int firstHighest = 0, secondHighest = 0;
		int firstHighestPos = 0, secondHighestPos = 0;

		/* loop through each gene in the population in order 
		 * to find the two genes with the highest fitnesses */
		for (int currentGene = 0; currentGene < tournament.length; currentGene++) {

			/* get fitness for the current gene */
			currentEval = euclideanFitness(tournament[currentGene], dataset);

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

		return highestArr;
	}

	public void fisherYatesShuffle() {
		int randomIndex;
		int[] tempGene;

		for (int populationPos = 0; populationPos < population.length; populationPos++) {

			randomIndex = (int) (Math.random() * (population.length - populationPos));

			tempGene = population[randomIndex].clone();
			population[randomIndex] = population[populationPos];
			population[populationPos] = tempGene;
		}
	}

	public void tournamentSelection(int[][] dataset) {
		System.out.println("no shuff");
		for(int i = 0; i < 10; i++)
			System.out.println(population[i]);
		fisherYatesShuffle();
		System.out.println("shuffle");
for(int i = 0; i < 10; i++)
			System.out.println(population[i]);
		int numContestants = 10;
		int[][] tournament = new int[numContestants][GENE_LENGTH];
		int[] bestGenes;

		for (int populationPos = 0; populationPos < population.length; populationPos += 2) {
			for (int pos = 0; pos < numContestants; pos++)
				tournament[pos] = population[pos];

			bestGenes = findBestGenesTournament(tournament, dataset);

			uniformCrossover(populationPos, tournament[bestGenes[0]], tournament[bestGenes[1]]);
		}
	}

	/**
		 * Uniform crossover function; for each category in the gene, there
		 * is a 50% chance for it to crossover. 
		 * 
		 * @param dataset (int[][]), current dataset that is being trained
		 */
	public void uniformCrossover(int populationPos, int[] gene1, int[] gene2) {
		double crossoverChance = 50.0;
		double randomChance = Math.random();
		int tempElement;
		/*int[] bestGenes = findBestGenes(dataset);
		
		int[] gene1 = population[bestGenes[0]], gene2 = population[bestGenes[1]];
		*/
		int[] newGene1 = gene1.clone(), newGene2 = gene2.clone();

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
		population[populationPos] = newGene1.clone();

		population[populationPos + 1] = newGene2.clone();

	}

	/**
	 * Two-point crossover; randomly chooses to positions in the gene
	 * to crossover with the other parent gene.
	 * 
	 * @param dataset (int[][]), current dataset that is being trained
	 */
	public void twoPointCrossover(int populationPos, int[] gene1, int[] gene2) {

		double ratio = ((int) (Math.random() * 10)) / 10;
		int crossPoint1 = (int) (ratio * GENE_LENGTH);

		ratio = ((int) (Math.random() * 10)) / 10.0;
		int crossPoint2 = (int) (crossPoint1 + (ratio * (GENE_LENGTH - crossPoint1)));

		int[] tempGene1 = new int[GENE_LENGTH], tempGene2 = new int[GENE_LENGTH];

		for (int i = 0; i < crossPoint1; i++) {
			tempGene1[i] = gene1[i];
			tempGene2[i] = gene2[i];
		}
		for (int i = crossPoint1; i < crossPoint2; i++) {
			tempGene1[i] = gene2[i];
			tempGene2[i] = gene1[i];
		}

		for (int i = crossPoint2; i < GENE_LENGTH; i++) {
			tempGene1[i] = gene1[i];
			tempGene2[i] = gene2[i];
		}

		mutate(tempGene1);
		mutate(tempGene2);

		population[populationPos] = tempGene1;
		population[++populationPos] = tempGene2;
	}

	/**
	 * Multi-point crossover function; randomly chooses the amount of crossovers
	 * and randomly chooses the size of each crossover, then performs all of these
	 * crossovers between the two parent genes. 
	 * 
	 * @param dataset (int[][]), current dataset that is being trained
	 */
	public void multiPointCrossover(int populationPos, int[] gene1, int[] gene2) {

		int numCrossPoints = (int) (Math.random() * 63) + 1;
		int maxCross = (int) (GENE_LENGTH / numCrossPoints);
		int crossSize;
		int crossPoint;

		int[] tempGene1 = new int[GENE_LENGTH], tempGene2 = new int[GENE_LENGTH];
		int[] sections = new int[numCrossPoints];

		for (int i = 0; i < numCrossPoints; i++) {
			sections[i] = i * maxCross;
		}

		for (int section : sections) {

			crossSize = (int) (Math.random() * maxCross - 1) + 1;

			crossPoint = section + ((int) (Math.random() * (((section + maxCross - crossSize) - section) + 1)));

			for (int j = section; j < crossPoint; j++) {

				tempGene1[j] = gene1[j];
				tempGene2[j] = gene2[j];

			}

			for (int j = crossPoint; j < crossPoint + crossSize; j++) {

				tempGene1[j] = gene2[j];
				tempGene2[j] = gene1[j];

			}

			for (int j = crossPoint + crossSize; j < section + maxCross; j++) {

				tempGene1[j] = gene1[j];
				tempGene2[j] = gene2[j];

			}
		}

		mutate(tempGene1);
		mutate(tempGene2);

		population[populationPos] = tempGene1.clone();
		population[++populationPos] = tempGene2.clone();

	}

	/**
	 * Uniform crossover function; for each category in the gene, there
	 * is a 50% chance for it to crossover. 
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
		for (int newGenePos = 0; newGenePos < POPULATION_SIZE; newGenePos++) {

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
	 * Two-point crossover; randomly chooses to positions in the gene
	 * to crossover with the other parent gene.
	 * 
	 * @param dataset (int[][]), current dataset that is being trained
	 */
	public void twoPointCrossover(int[][] dataset) {

		double ratio = ((int) (Math.random() * 10)) / 10;

		int crossPoint1 = (int) (ratio * GENE_LENGTH);
		ratio = ((int) (Math.random() * 10)) / 10.0;
		int crossPoint2 = (int) (crossPoint1 + (ratio * (GENE_LENGTH - crossPoint1)));
		int[] bestGenes = findBestGenes(dataset);
		int[] gene1 = population[bestGenes[0]], gene2 = population[bestGenes[1]];

		int[] tempGene1 = new int[GENE_LENGTH], tempGene2 = new int[GENE_LENGTH];

		for (int newGenePos = 0; newGenePos < POPULATION_SIZE; newGenePos++) {
			tempGene1 = new int[GENE_LENGTH];
			tempGene2 = new int[GENE_LENGTH];
			ratio = ((int) (Math.random() * 10)) / 10;
			crossPoint1 = (int) (ratio * GENE_LENGTH);
			ratio = ((int) (Math.random() * 10)) / 10;
			crossPoint2 = (int) (crossPoint1 + (ratio * (GENE_LENGTH - crossPoint1)));
			for (int i = 0; i < crossPoint1; i++) {
				tempGene1[i] = gene1[i];
				tempGene2[i] = gene2[i];
			}
			for (int i = crossPoint1; i < crossPoint2; i++) {
				tempGene1[i] = gene2[i];
				tempGene2[i] = gene1[i];
			}

			for (int i = crossPoint2; i < GENE_LENGTH; i++) {
				tempGene1[i] = gene1[i];
				tempGene2[i] = gene2[i];
			}

			mutate(tempGene1);
			mutate(tempGene2);

			population[newGenePos] = tempGene1;
			population[++newGenePos] = tempGene2;
		}
	}

	/**
	 * Multi-point crossover function; randomly chooses the amount of crossovers
	 * and randomly chooses the size of each crossover, then performs all of these
	 * crossovers between the two parent genes. 
	 * 
	 * @param dataset (int[][]), current dataset that is being trained
	 */
	public void multiPointCrossover(int[][] dataset) {

		int numCrossPoints;
		int maxCross;
		int crossSize;
		int crossPoint;

		int[] bestGenes = findBestGenes(dataset);
		int[] gene1 = population[bestGenes[0]], gene2 = population[bestGenes[1]];

		int[] tempGene1 = new int[GENE_LENGTH], tempGene2 = new int[GENE_LENGTH];
		int[] sections;

		for (int newGenePos = 0; newGenePos < POPULATION_SIZE; newGenePos++) {
			tempGene1 = new int[GENE_LENGTH];
			tempGene2 = new int[GENE_LENGTH];
			numCrossPoints = (int) (Math.random() * 63) + 1;
			maxCross = (int) (GENE_LENGTH / numCrossPoints);

			sections = new int[numCrossPoints];
			for (int i = 0; i < numCrossPoints; i++) {
				sections[i] = i * maxCross;
			}

			for (int section : sections) {

				crossSize = (int) (Math.random() * maxCross - 1) + 1;

				crossPoint = section + ((int) (Math.random() * (((section + maxCross - crossSize) - section) + 1)));

				for (int j = section; j < crossPoint; j++) {

					tempGene1[j] = gene1[j];
					tempGene2[j] = gene2[j];

				}

				for (int j = crossPoint; j < crossPoint + crossSize; j++) {

					tempGene1[j] = gene2[j];
					tempGene2[j] = gene1[j];

				}

				for (int j = crossPoint + crossSize; j < section + maxCross; j++) {

					tempGene1[j] = gene1[j];
					tempGene2[j] = gene2[j];

				}
			}

			mutate(tempGene1);
			mutate(tempGene2);

			population[newGenePos] = tempGene1.clone();
			population[++newGenePos] = tempGene2.clone();

		}

	}

	/**
	 * Gene mutation function (for individual element in gene), 1.5% chance to change current bit 
	 * in the gene to a random category (0-9)
	 * 
	 * @param element (int), the gene bit to perform mutation on
	 * @return (int) the gene bit that is either mutated or unchanged
	 */
	public int mutate(int element) {

		double randomChance = Math.random() * 100;

		/* if the random number is less than or equal to the mutation rate,
		 * the gene bit is mutated */
		if (MUTATION_RATE >= randomChance)
			element = (int) (Math.random() * 17);

		return element;
	}

	/**
	 * Gene mutation function (for whole gene), loops through every element in
	 * the gene, with a 2.0% chance to mutate each element to a random number
	 * (between 0 and 16).
	 * 
	 * @param gene (int[]) the gene to be mutated
	 */
	public void mutate(int[] gene) {

		double randomChance;

		/* loop through every position in the gene */
		for (int genePos = 0; genePos < gene.length; genePos++) {

			/* generate random number from 0 to 100 */
			randomChance = Math.random() * 100;

			/* if random number is less than or equal to the mutation rate,
			 * the element at the current position is mutated */
			if (MUTATION_RATE >= randomChance)
				gene[genePos] = (int) (Math.random() * 17);
		}
	}

}
