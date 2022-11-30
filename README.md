<h1 align="center">Machine learning categoriser for UCI digits</h1>

<div align="center">

  [![License](https://img.shields.io/badge/license-MIT-blue.svg)](/LICENSE)

</div>

---

## 📝 Table of Contents
- [About](#about)
- [Libraries/Frameworks/Services](#built_using)
- [Authors](#authors)

## ℹ️ About <a name = "about"></a>

For this project, I implemented 3 different algorithms with varying success: Nearest Neighbour (98.26% acc.), Genetic Algorithm (70.41% acc.) and MLP (93.59% acc.).

## Nearest Neighbour
Basic solution; works by finding the datapoint in the other dataset that most closely resembles the current datapoint. Achieved 98.26% categorisation accuracy.

## Genetic Algorithm (GA)
For this GA implementation, each gene in the population consists of 640 elements which are intended to match the 10 digits in the UCI task (0 to 9). Each row in the dataset has 64 feature values, so each gene in the GA contains 640 elements, 64 feature values for each digit. This means that each gene can be split into 10 sections to represent each digit, and this is how the fitness of each gene is measured. Each section is compared against every 10 rows in the training dataset and using Euclidean distance, the closest row in that 10 to the current gene section is found. If the category of that row matches the category section in the gene, the categorisation was correct, and the fitness value increases. 

With this solution, I carried out parameter exploration on the population size, mutation rate, number of generations, the gene crossover function and the parent gene selection. The best accuracy recorded was ~70.41% which occured with a population size of 40 genes, a mutation rate of 2.0%, 300 generations, uniform crossover, and a simple "best gene" parent selection.

## Multilayer Perceptron (MLP)
This MLP implementation contains 1 input layer, 1 hidden layer and 1 output layer. During forward propagation, every row in the dataset is passed into the MLP, with each feature value being passed as input nodes. These values are then multiplied by the weights between the input and hidden nodes, and these products are then summed to produce a weighted sum for each hidden node. A bias is then added to this sum, and this new value is then passed through a Sigmoid transfer function. The resulting values are then passed through to the output nodes, where they are multiplied by output weights, summed and then passed into the activation function at the output layer. The node with the highest probability is the predicted category for that row, and if it matches the category in the dataset, the prediction is correct. 

The transfer function used is the Sigmoid function and the loss function used is the Mean Squared Error loss function. This was used in tandem with the Sigmoid derivative in each epoch during backpropagation to update the weights. This implementation achieved an accuracy of 93.59%.

## ✍️ Authors <a name = "authors"></a>
- [@samcdonovan](https://github.com/samcdonovan)
