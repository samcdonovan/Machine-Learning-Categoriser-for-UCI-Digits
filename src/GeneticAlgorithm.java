
/**
 * GeneticAlgorithm.java:
 * Genetic algorithm that categorises UCI digits. The fitness function uses
 * Euclidean distance to find the row in the dataset that is closest to the gene.
 * Various crossover and selection techniques have been implemented, though the best 
 * achieved ~70.4% accuracy (simple best genes selection and uniform crossover).
 * 
 * @author Samuel C. Donovan
 * @created 31/01/22
 * @updated 25/02/22
 */
public class GeneticAlgorithm {

	private static final int POPULATION_SIZE = 40; /* size of the population */
	private static final int GENE_LENGTH = 640; /* length of each gene in the population */
	private static final int GENERATIONS = 300; /* number of generations for breeding */
	private static final double MUTATION_RATE = 2.0; /* rate at which each gene mutates */

	int[][] population = new int[POPULATION_SIZE][GENE_LENGTH]; /* 2D array that holds the current population */
	int[][] tempPopulation = new int[POPULATION_SIZE][GENE_LENGTH]; /* 2D array created from gene selection techniques */

	/**
	 * Main function that runs the genetic algorithm with a 2-fold test. For the first
	 * fold, it trains the population on the first dataset, then tests on the second dataset.
	 * For the second fold, it trains the population on the second dataset, then tests on the first dataset.
	 * 
	 * @param dataset1, the first dataset
	 * @param dataset2, the second dataset
	 * @return the percentage of correct categorisations
	 */
	public double twoFold(int[][] dataset1, int[][] dataset2) {

		/* if the paramaters are currently being tested/experimented with, 
		   start a timer for the algorithm. This is to measure running time */
		long startTime;
		if (Utility.GA_PARAMATER_TESTING) {
			startTime = System.nanoTime(); /* start the timer */
		}

		/* get the total number of correct categorisations from the first fold */
		int firstFoldTotal = trainAndTestPopulation(dataset1, dataset2);

		/* get the total number of correct categorisations from the second fold */
		int secondFoldTotal = trainAndTestPopulation(dataset2, dataset1);

		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		double percentCorrect = Utility.calculatePercentage(firstFoldTotal, secondFoldTotal, dataset1.length,
				dataset2.length);

		/* if the GA parameters are currently being tested, calculate running time of the algorithm */
		if (Utility.GA_PARAMATER_TESTING) {
			long endTime = System.nanoTime();
			long totalTime = endTime - startTime;

			double seconds = (double) totalTime / 1000000000.0;

			System.out.println("Running time = " + totalTime + " nano seconds, " + seconds + " seconds");
		}

		return percentCorrect;
	}

	/**
	 * Trains the population on a training set, then tests that population against the test set.
	 * 
	 * @param trainSet, the training set to form the population on
	 * @param testSet, the set to test the newly formed population on
	 * @return the number of correct categorisations for this fold
	 */
	private int trainAndTestPopulation(int[][] trainSet, int[][] testSet) {
		/* generate an initial, randomised population */
		generateNewPopulation();

		/* loops for as many generations as specified */
		for (int generation = 0; generation < GENERATIONS; generation++) {

			/* run selection on the current population and then crossover */
			bestGeneSelection(trainSet);
			uniformCrossover();
		}

		/* after generations have finished, get the total number of correct categorisations from this fold */
		int totalCorrect = testPopulation(testSet);

		return totalCorrect;
	}

	/**
	 * Generates a new, randomised population of genes 
	 */
	private void generateNewPopulation() {

		/* empty the population */
		population = new int[POPULATION_SIZE][GENE_LENGTH];
		int[] gene = new int[GENE_LENGTH];

		/* create as many genes as specified in the variable POPULATION_SIZE */
		for (int currentGene = 0; currentGene < POPULATION_SIZE; currentGene++) {
			gene = new int[GENE_LENGTH]; /* empty gene */

			/* for every position in gene, generate a random number between 0 and 16 */
			for (int genePos = 0; genePos < gene.length; genePos++)
				gene[genePos] = (int) (Math.random() * 17);

			/* set position in population to the newly created gene */
			population[currentGene] = gene;
		}
	}

	/**
	 * Gene fitness evaluation function. Finds the nearest neighbour in the dataset 
	 * for each section of the gene and if the categories match, the fitness increases by 1 
	 * 
	 * @param gene, the gene to be evaluated
	 * @param dataset, the dataset to test the gene against
	 * @return the fitness score for the given gene
	 */
	private int fitness(int[] gene, int[][] dataset) {

		int fitness = 0;
		double currentDist;
		double min = Double.MAX_VALUE;
		int minPos = -1;
		int[] currentRow;

		/* loop through the 10 sections of the gene, each representing one of the categories */
		for (int category = 0; category < 10; category++) {

			/* generate a pseudo-row using the current section of the gene */
			currentRow = getRow(gene, category);

			/* loop through every 10 rows in the dataset, if the neareset neighbour in the current section
			 * matches the current category, the categorisation was correct */
			for (int datasetSection = 0; datasetSection < dataset.length; datasetSection += 10) {
				for (int datasetPos = datasetSection; datasetPos < datasetSection + 10; datasetPos++) {

					currentDist = Utility.euclideanDistance(currentRow, dataset[datasetPos]);

					/* if the distance between the two rows from each dataset is smaller than the current
					 * minimum distance, set minimum distance to this new distance and save the position in minPos*/
					if (currentDist < min) {
						min = currentDist;
						minPos = datasetPos;
					}
				}

				/* if the nearest neighbour both have the same category in their last cell (65)
				 * the categorisation is correct, numCorrect is incremented by 1 */
				if (category == dataset[minPos][dataset[0].length - 1])
					fitness++;

			}
		}
		return fitness;
	}

	/**
	 * Helper function for fitness evaluation. Creates a temporary row from the current gene
	 * so that it can be evaluated against each row in the dataset
	 * 
	 * @param gene, the gene to create a row from
	 * @param category, the current category of the row from the gene (0-9)
	 * @return a temporary row that can be compared against the dataset
	 */
	private int[] getRow(int[] gene, int category) {
		int rowLength = 64; /* length of each row in the dataset, without the category */
		int[] row = new int[rowLength];

		/* every 64 elements in the gene represents a different category, so we multiply
		 * the category by the row length to get the index of the current section 
		 * (e.g. if the category is 2, the first element in the section will be at position 128) */
		int currentSection = category * rowLength;

		/* loop through each element in the current section of the gene and add it to the temporary row */
		for (int genePos = currentSection, rowPos = 0; genePos < currentSection + rowLength; genePos++, rowPos++)
			row[rowPos] = gene[genePos];

		return row;
	}

	/**
	 * This function runs the second fold for the two fold test. After the 
	 * population has been trained on the training set, this function compares
	 * it against the test set and returns the number of correct categorisations.
	 * 
	 * @param dataset, the test set
	 * @return the number of correct categorisations (0-2810)
	 */
	private int testPopulation(int[][] dataset) {
		int bestFitness = 0;
		int currentFitness = 0;

		/* loop through every gene in the population and measure their fitness */
		for (int pos = 0; pos < population.length; pos++) {

			currentFitness = fitness(population[pos], dataset);

			/* find the best fitness, and this will be used to represent the total number
			   of correct categorisations for this fold. */
			if (currentFitness > bestFitness)
				bestFitness = currentFitness;
		}

		return bestFitness;
	}

	/**
	 * Retrieves the two genes in the population with the highest fitness. This is
	 * a very simple selection function, but because of this simplicity it has proven
	 * particularly effective in generating populations with a high average fitness quickly
	 * 
	 * @param dataset, current dataset that is being trained on
	 */
	private void bestGeneSelection(int[][] dataset) {

		tempPopulation = new int[POPULATION_SIZE][GENE_LENGTH]; /* reset temporary population */

		/* find the positions of the two best genes in the population */
		int[] bestGenesInPopulation = findTwoBestGenes(population, dataset);
		int[] bestGene1 = population[bestGenesInPopulation[0]], bestGene2 = population[bestGenesInPopulation[1]];

		/* populate the temporary population with the two best genes */
		for (int populationPos = 0; populationPos < population.length; populationPos++) {
			tempPopulation[populationPos] = bestGene1.clone();
			tempPopulation[++populationPos] = bestGene2.clone();
		}
	}

	/**
	* Helper function that retrieves the two genes in the given set of genes with the highest fitness
	* 
	* @param genes, the set of genes to find the best genes for
	* @param dataset, current dataset that is being trained on
	* @return an array containing the positions of the best genes in the set
	*/
	private int[] findTwoBestGenes(int[][] genes, int[][] dataset) {
		int currentFitness;
		int bestFitness = 0, secondBestFitness = 0;
		int bestPos = -1, secondBestPos = -1;

		/* loop through each gene in the population in order 
		 * to find the two genes with the highest fitnesses */
		for (int currentGene = 0; currentGene < genes.length; currentGene++) {

			/* get fitness for the current gene */
			currentFitness = fitness(genes[currentGene], dataset);

			/* if the current fitness is more than the best fitness, set best fitness
			   to current, and set second best fitness to previous best */
			if (currentFitness >= bestFitness) {
				secondBestFitness = bestFitness;
				secondBestPos = bestPos;

				bestFitness = currentFitness;
				bestPos = currentGene;
			} else if (currentFitness >= secondBestFitness) {
				/* if current fitness is less than the best fitness, but more than the second best,
				   set secondBestFitness to currentFitness */
				secondBestFitness = currentFitness;
				secondBestPos = currentGene;
			}
		}

		/* return an array containing the positions of the two best genes in the given set of genes */
		return new int[] { bestPos, secondBestPos };
	}

	/**
	 * Tournament selection method for selecting parent genes. Chooses a random set of 10
	 * genes in the population and the two best genes from that set become parents for the next population
	 * 
	 * @param dataset, current training set
	 */
	private void tournamentSelection(int[][] dataset) {

		/* use Fisher Yates shuffle to shuffle the population */
		fisherYatesShuffle();

		tempPopulation = new int[POPULATION_SIZE][GENE_LENGTH]; /* reset temporary population */
		int numContestants = 10; /* 10 genes are in each tournament */
		int[][] tournament = new int[numContestants][GENE_LENGTH]; /* 2D array to hold tournament genes */
		int[] bestGenes;

		/* loop through every two elements in population */
		for (int populationPos = 0; populationPos < population.length; populationPos++) {

			/* loop through every position in the tournament */
			for (int tournamentPos = 0; tournamentPos < numContestants; tournamentPos++)
				tournament[tournamentPos] = population[tournamentPos];

			bestGenes = findTwoBestGenes(tournament, dataset);

			/* add two best genes from tournament to the temporary population */
			tempPopulation[populationPos] = tournament[bestGenes[0]];
			tempPopulation[++populationPos] = tournament[bestGenes[1]];

		}
	}

	/**
	 * Helper function for tournament selection. Based on the
	 * Fisher-Yates shuffle algorithm, shuffles the current population
	 * in order to select random genes for the tournament
	 */
	private void fisherYatesShuffle() {

		int randomIndex;
		int[] tempGene;

		/* loop through every element of the population */
		for (int populationPos = 0; populationPos < population.length; populationPos++) {

			/* generate a random index between 0 and population length minus current position */
			randomIndex = (int) (Math.random() * (population.length - populationPos));

			/* swap genes in positions populationPos and randomIndex */
			tempGene = population[randomIndex].clone();
			population[randomIndex] = population[populationPos];
			population[populationPos] = tempGene;
		}
	}

	/**
	 * Uniform crossover function; for each element in the gene, there
	 * is a 50% chance for it to crossover.  
	 */
	private void uniformCrossover() {
		double crossoverChance = 50.0;
		double randomChance; /* random chance that the gene element will crossover */
		int tempElement;

		int[] newGene1, newGene2;

		/* loop through every two genes in the temporary population and generate two new genes from them */
		for (int populationPos = 0; populationPos < tempPopulation.length; populationPos++) {

			newGene1 = tempPopulation[populationPos];
			newGene2 = tempPopulation[populationPos + 1];

			/* loop through each element in the gene with a 50% chance for a crossover to occur */
			for (int genePos = 0; genePos < newGene1.length; genePos++) {

				randomChance = Math.random() * 100;

				/* if the random chance is higher than the crossover chance, perform crossover */
				if (randomChance >= crossoverChance) {
					tempElement = newGene1[genePos];
					newGene1[genePos] = newGene2[genePos];
					newGene2[genePos] = tempElement;
				}

				/* call mutate on the current gene elements */
				newGene1[genePos] = mutate(newGene1[genePos]);
				newGene2[genePos] = mutate(newGene2[genePos]);

			}

			/* insert the new genes into the population */
			population[populationPos] = newGene1.clone();
			population[++populationPos] = newGene2.clone();
		}
	}

	/**
	 * Two-point crossover; randomly chooses two positions in the gene
	 * to crossover with the other parent gene. Uses the temporary population
	 * created through gene selection.
	 */
	private void twoPointCrossover() {

		double ratio; /* ratio for generating cross points */
		int crossPoint1, crossPoint2; /* positions for the genes to crossover */

		/* loop through every two genes in the temporary population and create new genes from those two */
		for (int populationPos = 0; populationPos < tempPopulation.length; populationPos += 2) {

			/* generate a new ratio and the first cross point */
			ratio = ((int) (Math.random() * 10)) / 10.0;
			crossPoint1 = (int) (ratio * GENE_LENGTH);

			/* generate a new ratio and the second cross point */
			ratio = ((int) (Math.random() * 10)) / 10.0;
			crossPoint2 = (int) (crossPoint1 + (ratio * (GENE_LENGTH - crossPoint1)));

			/* use helper function to crossover the genes at the
			   generated cross points and add them to the population */
			crossoverGenesAndAddToPopulation(populationPos, 0, crossPoint1, crossPoint2, GENE_LENGTH);
		}
	}

	/**
	 * Multi-point (K-point) crossover function; randomly chooses the amount of crossovers
	 * and randomly chooses the size of each crossover, then performs all of these
	 * crossovers between the two parent genes. 
	 */
	private void multiPointCrossover() {

		int numCrossPoints; /* random number of cross points, different for each set of genes */
		int maxCross; /* max crossover size, generated using numCrossPoints */
		int crossSize; /* current crossover size, generated using maxCross */
		int crossPoint1, crossPoint2; /* the two cross points for the current section */

		int[] sections; /* each gene is split into sections, the number of sections is dictated by numCrossPoints */

		/* loop through every two genes in the temporary population and create new genes from those two */
		for (int populationPos = 0; populationPos < POPULATION_SIZE; populationPos += 2) {

			numCrossPoints = (int) (Math.random() * 64) + 1; /* generate random number of cross points (1-64) */

			/* generate max cross size, given the number of cross points */
			maxCross = (int) (GENE_LENGTH / numCrossPoints);

			sections = new int[numCrossPoints]; /* set number of sections to the number of cross points */

			/* put the first index of each section into an array */
			for (int crossPointStart = 0; crossPointStart < numCrossPoints; crossPointStart++)
				sections[crossPointStart] = crossPointStart * maxCross;

			/* loop through each section */
			for (int currentSection : sections) {

				/* generate new crossover size: between 0 and max cross size */
				crossSize = (int) (Math.random() * maxCross - 1) + 1;

				/* generate first cross point; between currentSection and maxCross - crossSize*/
				crossPoint1 = currentSection + ((int) (Math.random() * (maxCross - crossSize))) + 1;

				crossPoint2 = crossPoint1 + crossSize;

				/* use helper function to crossover the genes at the
				generated cross points and add them to the population */
				crossoverGenesAndAddToPopulation(populationPos, currentSection, crossPoint1, crossPoint2,
						currentSection + maxCross);
			}
		}
	}

	/**
	 * Helper function for twoPoint and multiPoint crossover. Uses cross points to
	 * crossover two parent genes.
	 * 
	 * @param populationPos, current iteration of the loop, represents the position in the population
	 * @param startPos, start position of the crossover
	 * @param crossPoint1, first crossover point
	 * @param crossPoint2, second crossover point
	 * @param endPos, end position of the crossover
	 */
	private void crossoverGenesAndAddToPopulation(int populationPos, int startPos, int crossPoint1, int crossPoint2,
			int endPos) {

		/* new genes to be created from parent genes */
		int[] newGene1 = new int[GENE_LENGTH], newGene2 = new int[GENE_LENGTH];

		/* loop from the start of the current section of the parent gene until the first cross point, 
		   add each element from current parents (from temporary population) to the new genes */
		for (int genePos = startPos; genePos < crossPoint1; genePos++) {
			newGene1[genePos] = tempPopulation[populationPos][genePos];
			newGene2[genePos] = tempPopulation[populationPos + 1][genePos];
		}

		/* loop from the start of the first cross point to the second cross point, add elements 
		   from the other parent to new genes*/
		for (int genePos = crossPoint1; genePos < crossPoint2; genePos++) {
			newGene1[genePos] = tempPopulation[populationPos + 1][genePos];
			newGene2[genePos] = tempPopulation[populationPos][genePos];
		}

		/* loop from end of the second cross point to end of current section, 
		   add the rest of the elements to respective new genes */
		for (int genePos = crossPoint2; genePos < endPos; genePos++) {
			newGene1[genePos] = tempPopulation[populationPos][genePos];
			newGene2[genePos] = tempPopulation[populationPos + 1][genePos];
		}

		/* mutate new genes */
		mutate(newGene1);
		mutate(newGene2);

		/* add new genes to actual population */
		population[populationPos] = newGene1;
		population[populationPos + 1] = newGene2;
	}

	/**
	 * Gene mutation function (for individual element in gene), 2.0% chance to change 
	 * current element in the gene to a random feature value (0-16)
	 * 
	 * @param element, the gene element to perform mutation on
	 * @return the gene element that is either mutated or unchanged
	 */
	private int mutate(int element) {

		double randomChance = Math.random() * 100; /* generate random number from 0 to 100 */

		/* if the random number is less than or equal to the mutation rate,
		 * the gene bit is mutated */
		if (MUTATION_RATE >= randomChance)
			element = (int) (Math.random() * 17);

		return element;
	}

	/**
	 * Gene mutation function (for whole gene), loops through every element in
	 * the gene, with a 2.0% chance to mutate each element to a random feature value (0-16)
	 * 
	 * @param gene the gene to be mutated
	 */
	private void mutate(int[] gene) {

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
