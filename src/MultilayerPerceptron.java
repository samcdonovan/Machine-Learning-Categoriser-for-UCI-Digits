
/**
 * MultilayerPerceptron.java
 * 
 * @author Samuel C. Donovan
 * Created: 14/02/22
 * Updated: 03/03/22
 *
 * 
 */
public class MultilayerPerceptron {

	static final int NUM_INPUTS = 2810; /* number of input features */
	static final int NUM_HIDDEN = 10; /* number of hidden nodes */
	static final int NUM_OUTPUT = 10; /* number of output nodes */

	static final int ITERATIONS = 300; /* maximum number of training iterations */
	static final double ERROR_THRESHOLD = 0.01; /* threshold for training error */

	static final double LEARNING_RATE = 0.05; /* low learning rate to avoid convergence */

	double[] hiddenWeights = new double[NUM_HIDDEN]; /* weights for the hidden nodes */
	double[][] hiddenLayer = new double[NUM_INPUTS][NUM_HIDDEN]; /* hidden layer which stores the weighted sums from the inputs */
	double[] hiddenBias = new double[NUM_HIDDEN]; /* the bias for each hidden node */

	double[] outputWeights = new double[NUM_OUTPUT]; /* weights for the output nodes */
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
		train(dataset1); /* train on dataset1 */
		int firstFoldTotal = test(dataset2); /* test on dataset2 */

		initialise(); /* reset weights and biases */
		train(dataset2); /* train on dataset2 */
		int secondFoldTotal = test(dataset1); /* test on dataset1 */

		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		int totalCorrect = firstFoldTotal + secondFoldTotal;
		double percentageCorrect = ((double) totalCorrect / (double) (dataset1.length + dataset2.length)) * 100;
		System.out.println("Total correct: " + totalCorrect + "/" + (dataset1.length + dataset2.length) + " = "
				+ Math.round(percentageCorrect * 10.0) / 10.0 + "% (" + percentageCorrect + "%)");
	}

	/**
	* Helper function to initialise all weights and biases in the MLP
	*/
	public void initialise() {

		/* maximum and minimum range for the random weights */
		int max = 1;
		int min = -1;

		/* initialise all hidden weights randomly and hidden biases to 0 */
		for (int pos = 0; pos < NUM_HIDDEN; pos++) {
			hiddenWeights[pos] = -min + (int) (Math.random() * ((max - (-min)) + 1));
			hiddenBias[pos] = 0.0;
		}

		/* initialise all output weights randomly and output biases to 0 */
		for (int pos = 0; pos < NUM_OUTPUT; pos++) {
			outputWeights[pos] = -min + (int) (Math.random() * ((max - (-min)) + 1));
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
	public void forwardPassthrough(int[][] dataset, int currentInput) {

		double weightedInput = 0.0;
		double sigmoidVal;

		/* loop through every feature value in the current row of the dataset */
		for (int dataPoint = 0; dataPoint < dataset[currentInput].length; dataPoint++) {
			/* loop through every hidden node in the MLP */
			for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {

				/* multiply current feature value by the hidden nodes weight, and add it to the weighted
				   sum (stored in the hidden layer) */
				weightedInput = dataset[currentInput][dataPoint] * hiddenWeights[hiddenNode];
				hiddenLayer[currentInput][hiddenNode] += weightedInput;
			}
		}

		/* loop through every every hidden node */
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {
			/* loop through every output node */
			for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {

				/* calculate the sigmoid value of the weighted sum of all inputs (plus bias) for the current hidden node */
				sigmoidVal = sigmoidTransfer(hiddenLayer[currentInput][hiddenNode] + hiddenBias[hiddenNode], false);

				/* multiply the sigmoid value by the weight for the current output node, and add this 
				   value to the weighted sum for that output node */
				outputLayer[currentInput][outputNode] += sigmoidVal * outputWeights[outputNode];
			}
		}

		/* loop through every output node */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {

			/* pass the weighted sum from the hidden nodes into the sigmoid transfer function and
			   store this value in the output node */
			outputLayer[currentInput][outputNode] = sigmoidTransfer(
					outputLayer[currentInput][outputNode] + outputBias[outputNode], false);
		}
	}

	/**
	 * Runs the MLP on the test set and counts the number of correct categorisations
	 * 
	 * @param testSet, the current test dataset
	 * @return the number of correct categorisations
	 */
	public int test(int[][] testSet) {

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

	/**
	 * 
	 * @param trainingSet
	 */
	public void train(int[][] trainingSet) {

		int actualCategory = -1;
		double totalCrossEntropy;

		int numIterations = 0;
		boolean correctCategory;
		int outputNode;
		do {
			numIterations++;
			totalCrossEntropy = 0.0;
			for (int currentInput = 0; currentInput < trainingSet.length; currentInput++) {
				actualCategory = trainingSet[currentInput][trainingSet[currentInput].length - 1];

				forwardPassthrough(trainingSet, currentInput);

				outputNode = findHighestInOutput(currentInput);
				correctCategory = outputNode == actualCategory;

				if (!correctCategory) {

					outputWeights[outputNode] -= gradientDescent(outputLayer[currentInput][outputNode]);

					totalCrossEntropy += crossEntropy(outputLayer[currentInput][outputNode], correctCategory);
				}
			}
			/* loop while there are still a reasonable amount of erros and while 
			   the specified number of iterations has not yet passed */
		} while (totalCrossEntropy > ERROR_THRESHOLD && numIterations < ITERATIONS);
	}

	/**
	 * Sigmoid transfer function that also handles the Sigmoid derivative
	 * 
	 * @param dotProduct to be passed into the sigmoid transfer 
	 * @param derivative, boolean check to determine whether or not the derivative 
	 * of the sigmoid is to be used
	 * @return new value after being passed through the sigmoid function
	 */
	public double sigmoidTransfer(double dotProduct, boolean derivative) {

		/* if the derivative needs to be used, the value of the sigmoid of the dot product
		   is retrieved, and then passed into the derived sigmoid function */
		if (derivative) {
			double sigmoid = sigmoidTransfer(dotProduct, false);
			return sigmoid * (1 - sigmoid);
		}

		/* return the value of the sigmoid of the dot product */
		return 1 / (1 + Math.exp(-dotProduct));
	}

	/**
	 * 
	 * @param value
	 * @param correctCategory
	 * @return
	 */
	public double crossEntropy(double value, boolean correctCategory) {
		if (!correctCategory)
			return -Math.log(value);

		return -Math.log(1 - value);
	}

	public double gradientDescent(double value) {

		return value * LEARNING_RATE;
	}

	/**
	 * Helper function to find the highest probability in the current output layer
	 * 
	 * @param currentInput, the index of the current input layer
	 * @return the index of the output node with the highest probability
	 */
	public int findHighestInOutput(int currentInput) {

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
