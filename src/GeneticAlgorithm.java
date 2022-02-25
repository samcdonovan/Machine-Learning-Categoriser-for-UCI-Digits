
/**
 * GeneticAlgorithm.java
 * 
 * @author Samuel C. Donovan
 * Created: 31/01/22
 * Updated: 25/02/22
 *
 * Genetic algorithm that categorises UCI digits. The fitness function uses
 * Euclidean distance to find the row in the dataset that is closest to the gene.
 * Various crossover and selection techniques have been implemented, though the best 
 * achieved ~65% accuracy (simple best genes selection and uniform crossover).
 */
public class GeneticAlgorithm {

	private static final int POPULATION_SIZE = 40; /* size of the population */
	private static final int GENE_LENGTH = 640; /* length of each gene in the population */
	private static final int GENERATIONS = 300; /* number of generations for breeding */
	private static final double MUTATION_RATE = 2; /* rate at which each gene mutates */

	int[][] population = new int[POPULATION_SIZE][GENE_LENGTH]; /* 2D array that holds the current population */
	int[][] tempPopulation = new int[POPULATION_SIZE][GENE_LENGTH]; /* 2D array created from gene selection techniques */

	int[][] trainSet;
	int[][] testSet;

	/**
	 * Function that runs the genetic algorithm with a 2-fold test
	 * 
	 * @param dataset1, the first dataset
	 * @param dataset2, the second dataset
	 */
	public void run(int[][] dataset1, int[][] dataset2) {

		double percentageCorrect = 0.0;

		while (percentageCorrect < 64.0) {
			/* generate an initial population using dataset1 */
			generateNewPopulation();

			for (int i = 0; i < GENERATIONS; i++) {
				bestGeneSelection(dataset1);
				twoPointCrossover();
				// multiPointCrossover();
				// uniformCrossover(tempPopulation);
				// uniformCrossover(dataset1);
				// bestGeneSelection(dataset1);
				// tournamentSelection(dataset1);
			}

			int firstFoldTotal = twoFold(dataset2);
			System.out.println("Training set: cw2DataSet1.csv, test set: cw2DataSet2.csv");
			System.out.println("Correct categorisations = " + firstFoldTotal + "/" + dataset2.length + "\n");

			/* generate an initial population using dataset1 */
			generateNewPopulation();

			/* run the genetic algorithm for the specified number of generations */
			for (int i = 0; i < GENERATIONS; i++) {
				bestGeneSelection(dataset2);
				// uniformCrossover(tempPopulation);
				twoPointCrossover();
				// multiPointCrossover();
				// uniformCrossover(dataset2);
				// bestGeneSelection(dataset2);
				// tournamentSelection(dataset2);
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

	/**
	 * Generates a new, randomised population using the P
	 */
	public void generateNewPopulation() {

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
	 * Helper function for fitness evaluation. Creates a temporary row from the current gene
	 * so that it can be evaluated against each row in the dataset
	 * 
	 * @param gene, the gene to create a row from
	 * @param category, the current category of the row from the gene (0-9)
	 * @return a temporary row that can be compared against the dataset
	 */
	public static int[] getRow(int[] gene, int category) {
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

	public int twoFold(int[][] dataset) {
		int bestFitness = 0;
		int currentFitness = 0;
		for (int pos = 0; pos < population.length; pos++) {

			currentFitness = fitness(population[pos], dataset);
			if (currentFitness > bestFitness)
				bestFitness = currentFitness;
		}

		return bestFitness;
	}

	/**
	 * Gene fitness evaluation function. Finds the nearest neighbour in the dataset 
	 * for each section of the gene and if the categories match, the fitness increases by 1 
	 * 
	 * @param gene, the gene to be evaluated
	 * @param dataset, the dataset to test the gene against
	 * @return the fitness score for the given gene
	 */
	public int fitness(int[] gene, int[][] dataset) {

		int fitness = 0;
		double currentDist;
		double min = Double.MAX_VALUE;
		int minPos = -1;
		int[] currentRow;

		for (int pos = 0; pos < 10; pos++) {

			currentRow = getRow(gene, pos);
			for (int datasetPos = 0; datasetPos < dataset.length; datasetPos += 10) {
				for (int somethign = datasetPos; somethign < datasetPos + 10; somethign++) {

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
					fitness++;

			}
		}
		return fitness;
	}

	/**
	 * Euclidean distance calculator, calculates distance
	 * between two rows of data from the dataset 
	 * 
	 * @param gene, the gene whose distance is to be calculated
	 * @param datasetRow, the row in the dataset to compare against
	 * @return double, the Euclidean distance between each row
	 */
	public double euclideanDistance(int[] gene, int[] datasetRow) {

		int sum = 0;

		/* sums the distance between each point in both arrays */
		for (int pos = 0; pos < gene.length - 1; pos++)
			sum += ((gene[pos] - datasetRow[pos]) * (gene[pos] - datasetRow[pos]));

		return Math.sqrt(sum);
	}

	/**
	 * Retrieves the two genes in the population with the highest fitness. This is
	 * a very simple selection function, but because of this simplicity it has proven
	 * particularly effective in generating populations with a high average fitness quickly
	 * 
	 * @param dataset, current dataset that is being trained on
	 * 
	 */
	public void bestGeneSelection(int[][] dataset) {

		int currentEvaluation; /* the fitness of the current gene */
		int firstHighest = 0, secondHighest = 0;
		int firstHighestPos = -1, secondHighestPos = -1;

		tempPopulation = new int[POPULATION_SIZE][GENE_LENGTH]; /* reset temporary population */

		/* loop through each gene in the population in order 
		 * to find the two genes with the highest fitnesses */
		for (int currentGene = 0; currentGene < population.length; currentGene++) {

			/* get fitness for the current gene */
			currentEvaluation = fitness(population[currentGene], dataset);

			if (currentEvaluation >= firstHighest) {
				secondHighest = firstHighest;
				secondHighestPos = firstHighestPos;

				firstHighest = currentEvaluation;
				firstHighestPos = currentGene;
			} else if (currentEvaluation >= secondHighest) {
				secondHighest = currentEvaluation;
				secondHighestPos = currentGene;
			}

		}

		int[] bestGene1 = population[firstHighestPos].clone(), bestGene2 = population[secondHighestPos].clone();

		/* populate the temporary population with the two best genes */
		for (int populationPos = 0; populationPos < population.length; populationPos++) {
			tempPopulation[populationPos] = bestGene1.clone();
			tempPopulation[++populationPos] = bestGene2.clone();
		}
	}

	/**
	 * Tournament selection method for selecting parent genes
	 * 
	 * @param dataset, current training set
	 */
	public void tournamentSelection(int[][] dataset) {

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

			bestGenes = findBestGenesTournament(tournament, dataset);

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
	public void fisherYatesShuffle() {

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
	* Retrieves the two genes in the population with the highest fitness
	* 
	* @param tournament
	* @param dataset, current dataset that is being trained on
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
			currentEval = fitness(tournament[currentGene], dataset);

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
	 * Uniform crossover function; for each category in the gene, there
	 * is a 50% chance for it to crossover.  
	 */
	public void uniformCrossover() {
		double crossoverChance = 50.0;
		double randomChance; /* random chance that the gene element will crossover */
		int tempElement;

		int[] newGene1, newGene2;

		/* loop through every two genes in the temporary population and generate two new genes from them */
		for (int populationPos = 0; populationPos < tempPopulation.length; populationPos++) {

			newGene1 = tempPopulation[populationPos];
			newGene2 = tempPopulation[populationPos + 1];

			/* loop through each element in the gene with a 50% chance for a crossover to occur */
			for (int pos = 0; pos < newGene1.length; pos++) {

				randomChance = Math.random() * 100;

				/* if the random chance is higher than the crossover chance, perform crossover */
				if (randomChance >= crossoverChance) {
					tempElement = newGene1[pos];
					newGene1[pos] = newGene2[pos];
					newGene2[pos] = tempElement;
				}

				/* call mutate on the current gene elements */
				newGene1[pos] = mutate(newGene1[pos]);
				newGene2[pos] = mutate(newGene2[pos]);

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
	public void twoPointCrossover() {

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
	public void multiPointCrossover() {

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
	public void crossoverGenesAndAddToPopulation(int populationPos, int startPos, int crossPoint1, int crossPoint2,
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
	public int mutate(int element) {

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