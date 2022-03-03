
/**
 * MultilayerPerceptron.java:
 * MLP that uses a Sigmoid transfer/activation function and 
 * an MSE loss function. Achieves variable accuracy but
 * the best recorded is ~93.59%.
 * 
 * @author Samuel C. Donovan
 * @created 14/02/22
 * @updated 03/03/22
 */
public class MultilayerPerceptron {

	static final int NUM_INPUTS = 2810; /* number of input features */
	static final int NUM_FEATURE_VALS = 64; /* number of feature values for each input */
	static final int NUM_HIDDEN = 50; /* number of hidden nodes */
	static final int NUM_OUTPUT = 10; /* number of output nodes */

	static final int ITERATIONS = 500; /* maximum number of training iterations */
	static final double ERROR_THRESHOLD = 0.001; /* threshold for training error */

	static final double LEARNING_RATE = 0.1; /* relatively low learning rate to avoid convergence */

	double[][] inputToHiddenWeights = new double[NUM_FEATURE_VALS][NUM_HIDDEN]; /* weights for the hidden nodes */
	double[][] hiddenLayer = new double[NUM_INPUTS][NUM_HIDDEN]; /* hidden layer which stores the weighted sums from the inputs */
	double[] hiddenBias = new double[NUM_HIDDEN]; /* the bias for each hidden node */

	double[][] hiddenToOutputWeights = new double[NUM_HIDDEN][NUM_OUTPUT]; /* weights for the output nodes */
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

		/* get total correct categorisations from first fold */
		int firstFoldTotal = trainAndTestMLP(dataset1, dataset2);

		/* get total correct categorisations from second fold */
		int secondFoldTotal = trainAndTestMLP(dataset2, dataset1);

		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		Utility.calculatePercentage(firstFoldTotal, secondFoldTotal, dataset1.length, dataset2.length);

	}

	/**
	 * Trains the MLP on a training set, then tests the MLP on a test set
	 * 
	 * @param trainSet, the dataset to train with
	 * @param testSet, the set to test the MLP against
	 * @return the total number of correct categorisations
	 */
	private int trainAndTestMLP(int[][] trainSet, int[][] testSet) {

		initialise(); /* randomise weights and set biases to 0 */

		train(trainSet); /* train MLP weights on trainSet */

		int totalCorrect = test(testSet); /* test on testSet */

		return totalCorrect;
	}

	/**
	* Helper function to initialise all weights and biases in the MLP
	*/
	private void initialise() {

		inputToHiddenWeights = new double[NUM_FEATURE_VALS][NUM_HIDDEN]; /* weights for the hidden nodes */
		hiddenLayer = new double[NUM_INPUTS][NUM_HIDDEN]; /* hidden layer which stores the weighted sums from the inputs */
		hiddenBias = new double[NUM_HIDDEN]; /* the bias for each hidden node */

		hiddenToOutputWeights = new double[NUM_HIDDEN][NUM_OUTPUT]; /* weights for the output nodes */
		outputLayer = new double[NUM_INPUTS][NUM_OUTPUT]; /* output layer which stores the weighted sums from the hidden nodes */
		outputBias = new double[NUM_OUTPUT]; /* bias for the output nodes */

		/* maximum and minimum range for the random weights */
		int max = 1;
		int min = -1;

		/* initialise all hidden weights randomly between -1 and 1 */
		for (int inputNode = 0; inputNode < inputToHiddenWeights.length; inputNode++)
			for (int hiddenNode = 0; hiddenNode < inputToHiddenWeights[0].length; hiddenNode++)
				inputToHiddenWeights[inputNode][hiddenNode] = (Math.random() * (max + max)) + min;

		/* set all hidden biases to 0 */
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++)
			hiddenBias[hiddenNode] = 0.0;

		/* initialise all output weights randomly randomly between -1 and 1 */
		for (int hiddenNode = 0; hiddenNode < hiddenToOutputWeights.length; hiddenNode++)
			for (int outputNode = 0; outputNode < hiddenToOutputWeights[0].length; outputNode++)
				hiddenToOutputWeights[hiddenNode][outputNode] = (Math.random() * (max + max)) + min;

		/* set all output biases to 0 */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++)
			outputBias[outputNode] = 0.0;
	}

	/**
	 * Main function for forward propagation through the MLP. Passes the input layer through
	 * the hidden layer, and then through the output layer.
	 * 
	 * @param dataset, the current dataset (train or test)
	 * @param currentInput, the index of the current row in the dataset
	 */
	private void forwardPropagation(int[][] dataset, int currentInput) {

		double weightedSum = 0.0;

		boolean derivative = true; /*  */
		boolean forwardPropagate = true;

		/* loop through every hidden node in the MLP */
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {

			/* get the weighted sum to passed into the sigmoid function at the current hidden node */
			weightedSum = getWeightedSum(inputToHiddenWeights, hiddenNode, dataset[currentInput].length - 1,
					dataset[currentInput]);

			weightedSum += hiddenBias[hiddenNode]; /* add hidden bias to the weighted sum */

			/* pass the weighted sum into the sigmoid transfer function (not derivative) */
			hiddenLayer[currentInput][hiddenNode] = sigmoidFunction(weightedSum, !derivative);

		}

		/* loop through every output node */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {

			/* get the weighted sum to passed into the sigmoid function at the current output node */
			weightedSum = getWeightedSum(hiddenToOutputWeights, outputNode, NUM_HIDDEN, hiddenLayer[currentInput],
					forwardPropagate);

			weightedSum += outputBias[outputNode]; /* add output bias to the weighted sum */

			/* pass the weighted sum into the sigmoid transfer function (not derivative) */
			outputLayer[currentInput][outputNode] = sigmoidFunction(weightedSum, !derivative);

		}
	}

	/**
	 * Trains the MLP by passing the training set through the MLP, and updating
	 * the weights with each iteration.
	 * 
	 * @param trainingSet, dataset to train the MLP on
	 */
	private void train(int[][] trainingSet) {
		double meanSquaredError = 0.0;
		int numIterations = 0;
		int actualOutput;
		double[][] errorGradients = new double[2][];
		int target;

		do {
			meanSquaredError = 0.0;
			numIterations++;

			/* loop through every row of the training set and train the weights of the MLP */
			for (int currentInput = 0; currentInput < trainingSet.length; currentInput++) {

				forwardPropagation(trainingSet, currentInput); /* pass the current row forward through the layers */

				actualOutput = findHighestInOutput(
						currentInput); /* get the output category after forward propagation */

				/* calculate error gradients for output and hidden layers, used for updating weights */
				errorGradients = calculateErrorGradients(trainingSet, currentInput);

				weightUpdate(trainingSet, currentInput,
						errorGradients); /* update weights and biases using error gradients */

				target = actualOutput == trainingSet[currentInput][trainingSet[currentInput].length - 1] ? 1 : 0;
				/* update mean squared error */
				meanSquaredError += Math.pow(target - outputLayer[currentInput][actualOutput], 2);
			}

			meanSquaredError = meanSquaredError / NUM_INPUTS * 2;

		} while (meanSquaredError > ERROR_THRESHOLD && numIterations < ITERATIONS);

	}

	/**
	 * Calculates error gradients for output and hidden layers
	 * 
	 * @param trainingSet, the current training set
	 * @param currentInput, the index for the current row of the training set
	 * @return a 2D array containing all of the output and hidden error gradients
	 */
	private double[][] calculateErrorGradients(int[][] trainingSet, int currentInput) {
		double[] outputErrors = new double[NUM_OUTPUT];
		double[] hiddenErrors = new double[NUM_HIDDEN];

		int category = trainingSet[currentInput][trainingSet[currentInput].length - 1];
		int target;
		double weightedSum = 0.0;

		int actual = findHighestInOutput(currentInput);
		int error = actual != category ? 1 : 0;
		boolean derivative = true;
		boolean forwardPropagate = true;

		//if (actual != category) {
			/* loop through every node in the output layer */
			for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {
				/* if the current node is equal to the category for this row of data,  */
				target = outputNode == category ? 1 : 0;

				/* calculate error gradient for the current output node using the sigmoid derivative */
				outputErrors[outputNode] = (target - outputLayer[currentInput][outputNode]) * error
						* sigmoidFunction(outputLayer[currentInput][outputNode], derivative);
			}

			/* loop through every node in the hidden layer */
			for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {

				/* calculate weighted sum of output error gradients multiplied by 
				 * the weight of the current hidden node (connected to the corresponding output node) */
				weightedSum = getWeightedSum(hiddenToOutputWeights, hiddenNode, NUM_OUTPUT, outputErrors,
						!forwardPropagate);

				/* calculate hidden error gradient by multiplying weighted sum by the sigmoid derivative */
				hiddenErrors[hiddenNode] = weightedSum * error
						* sigmoidFunction(hiddenLayer[currentInput][hiddenNode], derivative);

			}
		//}
		/* return arrays containing the error gradients */
		return new double[][] { outputErrors, hiddenErrors };
	}

	/**
	 * Update the weights in the MLP using pre-calculated error gradients for 
	 *  
	 * @param trainingSet, current training set
	 * @param currentInput, index of current row in training set
	 * @param gradientErrors, error gradients, calculated prior to updating weights
	 */
	private void weightUpdate(int[][] trainingSet, int currentInput, double[][] gradientErrors) {
		double currentWeightChange;
		int outputErrors = 0, hiddenErrors = 1;

		/* update hidden to output weights and output bias */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {

			/* calculate weight change for the current output node; learning rate multiplied
			 * by the gradient error for this node */
			currentWeightChange = LEARNING_RATE * gradientErrors[outputErrors][outputNode];

			for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++)
				/* update hidden to output weights by adding the current weight change multiplied by
				   the value at the hidden node (this will be sigmoid value) */
				hiddenToOutputWeights[hiddenNode][outputNode] += hiddenLayer[currentInput][outputNode]
						* currentWeightChange;

			/* update output bias at current output node */
			outputBias[outputNode] += currentWeightChange;
		}

		/* update input to hidden weights and hidden bias */
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {
			/* calculate weight change */
			currentWeightChange = LEARNING_RATE * gradientErrors[hiddenErrors][hiddenNode];

			for (int dataPoint = 0; dataPoint < NUM_FEATURE_VALS; dataPoint++)
				/* update input to hidden weight by adding current weight change multiplied
				 * by the feature value at the current position in the dataset row */
				inputToHiddenWeights[dataPoint][hiddenNode] += trainingSet[currentInput][dataPoint]
						* currentWeightChange;

			/* update hidden bias at current node */
			hiddenBias[hiddenNode] += currentWeightChange;
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

			/* forward feed the current row through the MLP layers */
			forwardPropagation(testSet, currentInput);

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
	 * Helper function for calculating the weighted sum during forward or back propagation
	 * 
	 * @param weights, the weights to be used for the weighted sum
	 * @param currentNode, current node in the layer
	 * @param numNodes, number of nodes in either the previous or next layer (depending on if it is 
	 * forward or back propagation)
	 * @param inputs, double array of inputs to be multiplied by the weights and summed
	 * @param forward, boolean to check if forward or back propagation is used
	 * @return the weighted sum
	 */
	private double getWeightedSum(double[][] weights, int currentNode, int numNodes, double[] inputs, boolean forward) {
		double weightedSum = 0.0;

		/* if it is forward propagation, use the nodes in the previous layer */
		if (forward)
			for (int nodeInPrevLayer = 0; nodeInPrevLayer < numNodes; nodeInPrevLayer++)
				weightedSum += weights[nodeInPrevLayer][currentNode] * inputs[nodeInPrevLayer];

		/* otherwise if it is backpropagation, use the nodes in the next layer */
		else
			for (int nodeInNextLayer = 0; nodeInNextLayer < numNodes; nodeInNextLayer++)
				weightedSum += weights[currentNode][nodeInNextLayer] * inputs[nodeInNextLayer];

		return weightedSum;
	}

	/**
	 * Calculates the weighted sum for inputs that are int (as opposed to double)
	 * 
	 * @param weights, the weights to be used for the weighted sum
	 * @param currentNode, current node in the layer
	 * @param numNodes, number of nodes in the next layer 
	 * @param inputs, int array of inputs to be multiplied by the weights and summed
	 * @return the weighted sum
	 */
	private double getWeightedSum(double[][] weights, int currentNode, int numNodes, int[] inputs) {
		double weightedSum = 0.0;

		/* for every node in the next layer, multiple its value by the weight from 
		   that layer to the current node */
		for (int nodeInNextLayer = 0; nodeInNextLayer < numNodes; nodeInNextLayer++)
			weightedSum += weights[nodeInNextLayer][currentNode] * inputs[nodeInNextLayer];

		return weightedSum;
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
	 * Finds the node with the highest probability in the current output layer;
	 * the index of this node is the MLP's prediction for the current input
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
