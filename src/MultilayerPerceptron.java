
/**
 * MultilayerPerceptron.java:
 * MLP that uses a Sigmoid transfer/activation function and 
 * an MSE loss function. Achieves accuracy in the range of 90% to 93% but
 * the best recorded is ~93.59%.
 * 
 * @author Samuel C. Donovan
 * @created 14/02/22
 * @updated 03/03/22
 */
public class MultilayerPerceptron {

	static final int NUM_INPUTS = 2810; /* number of input features */
	static final int NUM_FEATURE_VALS = 64; /* number of feature values for each input */
	static final int NUM_HIDDEN = 60; /* number of hidden nodes */
	static final int NUM_OUTPUT = 10; /* number of output nodes */

	static final int MAX_EPOCHS = 400; /* maximum number of training epochs */
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
	 * @return the percentage of correct categorisations
	 */
	public double twoFold(int[][] dataset1, int[][] dataset2) {

		/* if the paramaters are currently being tested/experimented with, 
		   start a timer for the algorithm. This is to measure running time */
		long startTime;
		if (Utility.MLP_PARAMATER_TESTING) {
			startTime = System.nanoTime(); /* start the timer */
		}

		/* get total correct categorisations from first fold */
		int firstFoldTotal = trainAndTestMLP(dataset1, dataset2);

		/* get total correct categorisations from second fold */
		int secondFoldTotal = trainAndTestMLP(dataset2, dataset1);

		/* print the total number of correct categorisations and its percentage (the full percentage and to 2 d.p.) */
		double percentCorrect = Utility.calculatePercentage(firstFoldTotal, secondFoldTotal, dataset1.length,
				dataset2.length);

		/* if the MLP parameters are currently being tested, calculate running time of the algorithm */
		if (Utility.MLP_PARAMATER_TESTING) {
			long endTime = System.nanoTime();
			long totalTime = endTime - startTime;

			System.out.println("Running time = " + totalTime + " nano seconds");
		}

		return percentCorrect;
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
	 * Trains the MLP by passing the training set through the MLP, and updating
	 * the weights with each epoch.
	 * 
	 * @param trainingSet, dataset to train the MLP on
	 */
	private void train(int[][] trainingSet) {

		/* values to determine when to stop backpropagating */
		double meanSquaredError = 0.0;
		int numEpochs = 0;

		double[][] errorGradients; /* 2D array to hold error gradients for hidden and output nodes */
		int target, actualOutput;

		/* do while loop to ensure it runs at least once */
		do {
			meanSquaredError = 0.0;
			numEpochs++;

			/* loop through every row of the training set and train the weights of the MLP */
			for (int currentRow = 0; currentRow < trainingSet.length; currentRow++) {

				forwardPropagation(trainingSet, currentRow); /* pass the current row forward through the layers */

				/* calculate error gradients for output and hidden layers, used for updating weights */
				errorGradients = calculateErrorGradients(trainingSet, currentRow);

				/* update weights and biases using error gradients */
				weightUpdate(trainingSet, currentRow, errorGradients);

				actualOutput = getPredictedOutput(currentRow); /* get the predicted output after forward propagation */

				/* if the actual output is correct, target is 1, otherwise its 0 */
				target = actualOutput == trainingSet[currentRow][NUM_FEATURE_VALS] ? 1 : 0;

				/* update mean squared error */
				meanSquaredError += Math.pow(target - outputLayer[currentRow][actualOutput], 2);
			}

			/* take the mean of the squared error */
			meanSquaredError = meanSquaredError / NUM_INPUTS * 2;

			/* loop while squared error is above the threshold and max epochs hasn't been reached yet */
		} while (meanSquaredError > ERROR_THRESHOLD && numEpochs < MAX_EPOCHS);
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
		for (int currentRow = 0; currentRow < testSet.length; currentRow++) {

			/* get the category for the current row (last element of the row */
			actualCategory = testSet[currentRow][testSet[currentRow].length - 1];

			/* forward feed the current row through the MLP layers */
			forwardPropagation(testSet, currentRow);

			/* find the highest probability in the output layer, after the forward pass through */
			currentBestIndex = getPredictedOutput(currentRow);

			/* if the index of the highest probability matches the actual category of the row,
			 * the categorisation was correct */
			if (currentBestIndex == actualCategory)
				correctCount++;
		}

		return correctCount;
	}

	/**
	 * Main function for forward propagation through the MLP. Passes the input layer through
	 * the hidden layer, and then through the output layer.
	 * 
	 * @param dataset, the current dataset (train or test)
	 * @param currentRow, the index of the current row in the dataset
	 */
	private void forwardPropagation(int[][] dataset, int currentRow) {

		double weightedSum = 0.0;

		boolean transfer = false; /* boolean for sigmoid function, false = transfer, true = derivative */
		boolean forwardPropagate = true; /* boolean for weighted sum function */

		/* loop through every hidden node in the MLP */
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {

			/* get the weighted sum for all feature values in the current row of data multiplied
			 * by the weight connecting that input with the current hidden node  */
			weightedSum = getWeightedSum(inputToHiddenWeights, hiddenNode, NUM_FEATURE_VALS, dataset[currentRow]);

			weightedSum += hiddenBias[hiddenNode]; /* add hidden bias to the weighted sum */

			/* pass the weighted sum into the sigmoid transfer function (not derivative) */
			hiddenLayer[currentRow][hiddenNode] = sigmoidFunction(weightedSum, transfer);

		}

		/* loop through every output node */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {

			/* get the weighted sum for all hidden node outputs in the hidden layer multiplied 
			 * by the weight connecting that node with the current output node  */
			weightedSum = getWeightedSum(hiddenToOutputWeights, outputNode, NUM_HIDDEN, hiddenLayer[currentRow],
					forwardPropagate);

			weightedSum += outputBias[outputNode]; /* add output bias to the weighted sum */

			/* pass the weighted sum into the sigmoid transfer function (not derivative) */
			outputLayer[currentRow][outputNode] = sigmoidFunction(weightedSum, transfer);

		}
	}

	/**
	 * Calculates error gradients for output and hidden layers
	 * 
	 * @param trainingSet, the current training set
	 * @param currentRow, the index for the current row of the training set
	 * @return a 2D array containing all of the output and hidden error gradients
	 */
	private double[][] calculateErrorGradients(int[][] trainingSet, int currentRow) {
		double[] outputErrors = new double[NUM_OUTPUT];
		double[] hiddenErrors = new double[NUM_HIDDEN];

		/* get the category for the current row in the dataset */
		int category = trainingSet[currentRow][NUM_FEATURE_VALS];
		int predicted = getPredictedOutput(currentRow); /* get predicted output */
		int target;

		/* if the predicted category does not match the actual category, there is an error,
		 * so error = 1, otherwise it equals 0 */
		int error = predicted != category ? 1 : 0;
		double weightedSum = 0.0;

		boolean derivative = true; /* boolean for sigmoid function so that it returns the derivative */
		boolean backPropagate = false; /* boolean for weighted sum function, false = backpropagate, true = forward */

		/* loop through every node in the output layer */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {
			/* if the current node is equal to the category for this row of data,  */
			target = outputNode == category ? 1 : 0;

			/* calculate error gradient for the current output node using the sigmoid derivative */
			outputErrors[outputNode] = (target - outputLayer[currentRow][outputNode]) * error
					* sigmoidFunction(outputLayer[currentRow][outputNode], derivative);
		}

		/* loop through every node in the hidden layer */
		for (int hiddenNode = 0; hiddenNode < NUM_HIDDEN; hiddenNode++) {

			/* calculate weighted sum of output error gradients multiplied by 
			 * the weight of the current hidden node (connected to the corresponding output node) */
			weightedSum = getWeightedSum(hiddenToOutputWeights, hiddenNode, NUM_OUTPUT, outputErrors, backPropagate);

			/* calculate hidden error gradient by multiplying weighted sum by the sigmoid derivative */
			hiddenErrors[hiddenNode] = weightedSum * error
					* sigmoidFunction(hiddenLayer[currentRow][hiddenNode], derivative);

		}

		/* return arrays containing the error gradients */
		return new double[][] { outputErrors, hiddenErrors };
	}

	/**
	 * Update the weights in the MLP using pre-calculated error gradients for 
	 *  
	 * @param trainingSet, current training set
	 * @param currentRow, index of current row in training set
	 * @param gradientErrors, error gradients, calculated prior to updating weights
	 */
	private void weightUpdate(int[][] trainingSet, int currentRow, double[][] gradientErrors) {
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
				hiddenToOutputWeights[hiddenNode][outputNode] += hiddenLayer[currentRow][outputNode]
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
				inputToHiddenWeights[dataPoint][hiddenNode] += trainingSet[currentRow][dataPoint] * currentWeightChange;

			/* update hidden bias at current node */
			hiddenBias[hiddenNode] += currentWeightChange;
		}
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
	 * Calculates the weighted sum for inputs that are int (as opposed to double). This is used
	 * when dealing with the inputs from the dataset, as they are int arrays.
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
	 * @param input to be passed into the sigmoid transfer 
	 * @param derivative, boolean check to determine whether or not the derivative 
	 * of the sigmoid is to be used
	 * @return new value after being passed through the sigmoid function
	 */
	private double sigmoidFunction(double input, boolean derivative) {

		/* if the derivative is being called, return the sigmoid derivative of the dotProduct */
		if (derivative)
			return input * (1 - input);
		else
			/* if it's not the derivative, it's the sigmoid transfer function */
			return (1 / (1 + Math.exp(-input)));
	}

	/**
	 * Finds the node with the highest probability in the current output layer;
	 * the index of this node is the MLP's prediction for the current input
	 * 
	 * @param currentRow, the index of the current input layer
	 * @return the index of the output node with the highest probability
	 */
	private int getPredictedOutput(int currentRow) {

		double currentHighest = -1.0;
		double currentOutputVal;
		int outputIndex = -1;

		/* loop through every node in the output layer */
		for (int outputNode = 0; outputNode < NUM_OUTPUT; outputNode++) {

			currentOutputVal = outputLayer[currentRow][outputNode];

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
