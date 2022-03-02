
/**
 * MultilayerPerceptron.java
 * 
 * @author Samuel C. Donovan
 * @created: 14/02/22
 * @updated: 03/03/22
 *
 * 
 */
public class MultilayerPerceptron {

	static final int NUM_INPUTS = 2810; /* number of input features */
	static final int NUM_FEATURE_VALS = 64; /* number of feature values for each input */
	static final int NUM_HIDDEN = 10; /* number of hidden nodes */
	static final int NUM_OUTPUT = 10; /* number of output nodes */

	static final int ITERATIONS = 100; /* maximum number of training iterations */
	static final double ERROR_THRESHOLD = 0.0001; /* threshold for training error */

	static final double LEARNING_RATE = 0.01; /* low learning rate to avoid convergence */

	double[][] hiddenWeights = new double[64][NUM_HIDDEN]; /* weights for the hidden nodes */
	double[][] hiddenLayer = new double[NUM_INPUTS][NUM_HIDDEN]; /* hidden layer which stores the weighted sums from the inputs */
	double[] hiddenBias = new double[NUM_HIDDEN]; /* the bias for each hidden node */

	double[][] outputWeights = new double[NUM_HIDDEN][NUM_OUTPUT]; /* weights for the output nodes */
	double[][] outputLayer = new double[NUM_INPUTS][NUM_OUTPUT]; /* output layer which stores the weighted sums from the hidden nodes */
	double[] outputBias = new double[NUM_OUTPUT]; /* bias for the output nodes */

	/**
	 * Runs a two fold test on both of the datasets. Trains the 
	 * MLP on dataset1, then tests it on dataset2, and then does the
	 * reverse. Prints the total correct number of categorisations and a percentage
	 * 
	 * @param dataset1, the first dataset
	 * @param dataset2, the second dataset
	 */
	public void twoFold(int[][] dataset1, int[][] dataset2) {

		initialise(); /* set weights and biases */
		train(dataset1);
		int firstFoldTotal = test(dataset2); /* test on dataset2 */
		System.out.println("First fold : " + firstFoldTotal + "/" + dataset1.length);
		
		initialise();
		train(dataset2);
		int secondFoldTotal = test(dataset1); /* test on dataset1 */
		System.out.println("Second fold : " + secondFoldTotal + "/" + dataset2.length);
		
		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		int totalCorrect = firstFoldTotal + secondFoldTotal;
		double percentageCorrect = ((double) totalCorrect / (double) (dataset1.length + dataset2.length)) * 100;
		System.out.println("\nTotal correct: " + totalCorrect + "/" + (dataset1.length + dataset2.length) + " = "
				+ Math.round(percentageCorrect * 100.0) / 100.0 + "% (" + percentageCorrect + "%)");
	}

	/**
	* Helper function to initialise all weights and biases in the MLP
	*/
	private void initialise() {

		hiddenWeights = new double[64][NUM_HIDDEN]; /* weights for the hidden nodes */
		hiddenLayer = new double[NUM_INPUTS][NUM_HIDDEN]; /* hidden layer which stores the weighted sums from the inputs */
		hiddenBias = new double[NUM_HIDDEN]; /* the bias for each hidden node */

		outputWeights = new double[NUM_HIDDEN][NUM_OUTPUT]; /* weights for the output nodes */
		outputLayer = new double[NUM_INPUTS][NUM_OUTPUT]; /* output layer which stores the weighted sums from the hidden nodes */
		outputBias = new double[NUM_OUTPUT]; /* bias for the output nodes */

		/* maximum and minimum range for the random weights */

		/* initialise all hidden weights randomly and hidden biases to 0 */
		for (int pos = 0; pos < hiddenWeights.length; pos++)
			for (int pos2 = 0; pos2 < hiddenWeights[0].length; pos2++) {

				hiddenWeights[pos][pos2] = (Math.random() * 2) - 1;

			}
		for (int pos = 0; pos < NUM_HIDDEN; pos++) {

			hiddenBias[pos] = 0.0;
		}

		/* initialise all output weights randomly and output biases to 0 */
		for (int pos = 0; pos < outputWeights.length; pos++)
			for (int pos2 = 0; pos2 < outputWeights[0].length; pos2++)
				outputWeights[pos][pos2] = (Math.random() * 2) - 1;

		for (int pos = 0; pos < NUM_OUTPUT; pos++) {

			outputBias[pos] = 0.0;
		}

	}

	/**
	 * Main function that carries forward passes through the MLP. Passes the input layer through
	 * the hidden layer, and then through the output layer.
	 * 
	 * @param dataset, the current dataset (train or test)
	 * @param currentInput, the index of the current row in the dataset
	 */
	private void forwardPassthrough(int[][] dataset, int currentInput) {

		double weightedSum = 0.0;

		boolean derivative = true;
		/* loop through every hidden node in the MLP */
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {
			weightedSum = 0.0;
			/* loop through every feature value in the current row of the dataset */
			for (int dataPoint = 0; dataPoint < dataset[currentInput].length - 1; dataPoint++) {

				/* multiply current feature value by the hidden nodes weight, and add it to the weighted
				   sum (stored in the hidden layer) */
				weightedSum += dataset[currentInput][dataPoint] * hiddenWeights[dataPoint][hiddenNode];

			
			}
		

			hiddenLayer[currentInput][hiddenNode] = sigmoidFunction(weightedSum + hiddenBias[hiddenNode], !derivative);
		
		}

		
		/* loop through every output node */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {
			weightedSum = 0.0;
			for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {
			
				/* multiply the sigmoid value by the weight for the current output node, and add this 
				   value to the weighted sum for that output node */
				weightedSum += (hiddenLayer[currentInput][hiddenNode] * outputWeights[hiddenNode][outputNode]);
				
			}

			outputLayer[currentInput][outputNode] = sigmoidFunction(weightedSum + outputBias[outputNode], !derivative);
	
		}

	}

	/**
	 * Runs the MLP on the test set and counts the number of correct categorisations
	 * 
	 * @param testSet, the current test dataset
	 * @return the number of correct categorisations
	 */
	private int test(int[][] testSet) {

		int currentBestIndex = -1, correctCount = 0;

		int actualCategory;

		/* loop through every row of the test dataset */
		for (int currentInput = 0; currentInput < testSet.length; currentInput++) {

			/* get the category for the current row (last element of the row */
			actualCategory = testSet[currentInput][testSet[currentInput].length - 1];

			/* pass the current row through the layers */
			forwardPassthrough(testSet, currentInput);

			/* find the highest probability in the output layer, after the forward pass through */
			currentBestIndex = findHighestInOutput(currentInput);

			/* if the index of the highest probability matches the actual category of the row,
			 * the categorisation was correct */
			if (currentBestIndex == actualCategory)
				correctCount++;
		}

		return correctCount;
	}
	/*
		private double lossFunction(int predictedCategory, int actualCategory) {
			double squaredError = Math.pow(predictedCategory - actualCategory, 2);
	
		}
	*/

	private void train(int[][] trainingSet) {
		double meanSquaredError = 0.0;
		int numIterations = 0;
		int target, actual;
		double[][] errorGradients = new double[2][];

		do {
			meanSquaredError = 0.0;
			numIterations++;

			for (int currentInput = 0; currentInput < trainingSet.length; currentInput++) {
				forwardPassthrough(trainingSet, currentInput);
				target = trainingSet[currentInput][64];
				actual = findHighestInOutput(currentInput);
				meanSquaredError += Math.pow(target - actual, 2);

				errorGradients = error(trainingSet, currentInput);

				weightUpdate(trainingSet, currentInput, errorGradients);
			}
			// System.out.println(test(trainingSet));
			meanSquaredError = meanSquaredError / NUM_INPUTS * 2;
			// System.out.println(meanSquaredError);
		} while (meanSquaredError > ERROR_THRESHOLD && numIterations < ITERATIONS);

	}

	private double[][] error(int[][] trainingSet, int currentInput) {
		double[] outputErrors = new double[NUM_OUTPUT];
		double[] hiddenErrors = new double[NUM_HIDDEN];

		int category = trainingSet[currentInput][trainingSet[currentInput].length - 1];
		int target;
		double sum = 0.0;

		boolean derivative = true;

		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {
			target = outputNode == category ? 1 : 0;

			outputErrors[outputNode] = (target - outputLayer[currentInput][outputNode])
					* sigmoidFunction(outputLayer[currentInput][outputNode], derivative);
		}

		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {
			sum = 0;
			for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {
				sum += outputWeights[hiddenNode][outputNode] * outputErrors[outputNode];
			}

			hiddenErrors[hiddenNode] = sum * sigmoidFunction(hiddenLayer[currentInput][hiddenNode], derivative);

		}

		return new double[][] { outputErrors, hiddenErrors };
	}

	private void weightUpdate(int[][] trainingSet, int currentInput, double[][] gradientErrors) {
		double currentWeightChange;

		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {
			currentWeightChange = LEARNING_RATE * gradientErrors[0][outputNode];

			for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {
				outputWeights[hiddenNode][outputNode] += currentWeightChange * hiddenLayer[currentInput][outputNode];
			}
			outputBias[outputNode] += currentWeightChange;
		}
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {
			currentWeightChange = LEARNING_RATE * gradientErrors[1][hiddenNode];

			for (int dataPoint = 0; dataPoint < NUM_FEATURE_VALS; dataPoint++) {
				hiddenWeights[dataPoint][hiddenNode] += currentWeightChange * trainingSet[currentInput][dataPoint];

			}
			hiddenBias[hiddenNode] += currentWeightChange;

		}
	}

	/**
	 * Sigmoid function that handles both the Sigmoid transfer and the Sigmoid derivative
	 * 
	 * @param dotProduct to be passed into the sigmoid transfer 
	 * @param derivative, boolean check to determine whether or not the derivative 
	 * of the sigmoid is to be used
	 * @return new value after being passed through the sigmoid function
	 */
	private double sigmoidFunction(double dotProduct, boolean derivative) {

		/* if the derivative needs to be used, the value of the sigmoid of the dot product
		   is retrieved, and then passed into the derived sigmoid function */
		return derivative ? dotProduct * (1 - dotProduct) : (1 / (1 + Math.exp(-dotProduct)));

	}

	/**
	 * 
	 * @param value
	 * @param correctCategory
	 * @return
	 */
	private double crossEntropy(double value, boolean correctCategory) {
		if (!correctCategory)
			return -Math.log(value);

		return -Math.log(1 - value);
	}

	private double gradientDescent(double value) {

		return value * LEARNING_RATE;
	}

	/**
	 * Helper function to find the highest probability in the current output layer
	 * 
	 * @param currentInput, the index of the current input layer
	 * @return the index of the output node with the highest probability
	 */
	private int findHighestInOutput(int currentInput) {

		double currentHighest = -1.0;
		double currentOutputVal;
		int outputIndex = -1;

		/* loop through every node in the output layer */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {

			currentOutputVal = outputLayer[currentInput][outputNode];

			/* find the node with the highest probability */
			if (currentOutputVal > currentHighest) {
				currentHighest = currentOutputVal;
				outputIndex = outputNode;
			}
		}

		/* return the index of the node with the highest probability */
		return outputIndex;
	}
}
