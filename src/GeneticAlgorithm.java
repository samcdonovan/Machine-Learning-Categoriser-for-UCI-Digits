
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

	private final int popSize = 20;
	private final int geneLength = 2810;
	private final int generations = 3;

	int[][] population;

	/**
	 * 
	 * @param dataset1
	 * @param dataset2
	 */
	public void run(int[][] dataset1, int[][] dataset2) {

		generateInitialPopulation(dataset1);
		
		for (int i = 0; i < generations; i++) {
			printPopAndEval(dataset2);
			uniformCrossover(dataset2);

		}
	}

	public void printPopAndEval(int[][] dataset) {

		for (int i = 0; i < popSize; i++) {

			System.out.print(i + " " + fitness(population[i], dataset));
			System.out.println();
		}
	}

	public int[] generateRandomGene() {

		int[] gene = new int[geneLength];

		for (int genePos = 0; genePos < geneLength; genePos++)
			gene[genePos] = (int) (Math.random() * 10);

		return gene;
	}

	/**
	 * Generates an initial population using the categories 
	 * from the current dataset
	 * 
	 * @param dataset (int[][]), current dataset for 2-fold test
	 */
	public void generateInitialPopulation(int[][] dataset) {

		int[] datasetGene = new int[geneLength];

		/* create a gene from the categories of each row in the current dataset */
		for (int row = 0; row < datasetGene.length; row++)
			datasetGene[row] = dataset[row][dataset[row].length - 1];

		/* set the class variable "population" to a new 2D array */
		this.population = new int[popSize][geneLength];

		/* fill population with the gene created from the dataset */
		for (int currentGene = 0; currentGene < popSize; currentGene++)
			this.population[currentGene] = datasetGene.clone();
/*
		population[0] = datasetGene;
		
		for (int currentGene = 1; currentGene < popSize; currentGene++)
			//this.population[currentGene] = datasetGene;
			this.population[currentGene] = generateRandomGene();
	*/	 
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

	public void mutation(int[] gene) {

		double mutationRate = 1.5;
		double rand;

		for (int pos = 0; pos < gene.length; pos++) {

			rand = Math.random() * 100;

			if (mutationRate >= rand)
				gene[pos] = (int) (Math.random() * 10);

		}
	}

	/**
	 * Retrieves the two genes in the population with the highest fitness
	 * 
	 * @param dataset
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
			currentEval = fitness(population[currentGene], dataset);
			System.out.println(currentGene + " : " + currentEval);

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
		System.out.println("Best: " + firstHighestPos + " " + secondHighestPos);
		return highestArr;
	}

	/**
	 *
	 * 
	 * @param dataset
	 */
	public void uniformCrossover(int[][] dataset) {
		double crossoverChance = 50.0;
		double randomChance = Math.random();

		int[] bestGenes = findBestGenes(dataset);

		int[] gene1 = population[bestGenes[0]], gene2 = population[bestGenes[1]];
		int[] newGene = gene1;

		for (int newGenePos = 0; newGenePos < popSize; newGenePos++) {
		
			newGene = gene1.clone();

			for (int pos = 0; pos < gene1.length; pos++) {

				if (gene1[pos] != dataset[pos][64]) {

					randomChance = Math.random() * 100;
					if (randomChance >= crossoverChance) {

						newGene[pos] = gene2[pos];

					}

					newGene[pos] = mutate(newGene[pos]);
				}
			}

			population[newGenePos] = newGene.clone();

		}
		//print2D(population);
	}

	/**
	 * Gene mutation function, 1.5% chance to change current bit 
	 * in the gene to a random category (0-9)
	 * 
	 * @param geneBit (int), the gene bit to perform mutation on
	 * @return (int) the gene bit that is either mutated or unchanged
	 */
	public int mutate(int geneBit) {

		double mutationRate = 1.5;
		double rand = Math.random() * 100;

		/* if the random number is less than or equal to the mutation rate,
		 * the gene bit is mutated */
		if (mutationRate >= rand)
			geneBit = (int) (Math.random() * 10);

		return geneBit;
	}

	public static void print2D(int[][] list) {
		for (int pos = 0; pos < list.length; pos++) {
			for (int pos2 = 0; pos2 < list[0].length; pos2++) {
				System.out.print(list[pos][pos2]);
			}
			System.out.println();
		}
	}
}
