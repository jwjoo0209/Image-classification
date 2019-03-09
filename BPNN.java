import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class BPNN extends ClassificationMethod {
	int inputCount;
	int hiddenCount;
	int outputCount;
	double[] inputLayer;		// input layers
	double[] hiddenLayer;		// hidden layers
	double[] outputLayer;		// output layers
	double[][] weightInput;		// weight between input and hidden
	double[][] weightHidden;	// weight between hidden and output
	double[] target;			// target (label)
	double ratio;				// learning rate

	Utils utils = new Utils();
	
	public BPNN (int[] legalLabels, int inputCount, int hiddenCount, int outputCount, double ratio) {
		super(legalLabels);
		this.inputCount = inputCount;
		this.hiddenCount = hiddenCount;
		this.outputCount = outputCount;
		inputLayer = new double[inputCount];
		hiddenLayer = new double[hiddenCount];
		outputLayer = new double[outputCount];
		target = new double[outputCount];
		weightInput = new double[inputCount][hiddenCount];
		weightHidden = new double[hiddenCount][outputCount];
		this.ratio = ratio;
		initialWeight();		// initialize the weight with random value.
	}

	public void train(List<Feature> trainingData, List<Integer> trainingLabels, List<Feature> validationData, List<Integer> validationLabels) {
		int iteration = 0;
		int error = 0;
		double errorRate = 1.0;
		
		while (errorRate > 0.02 && iteration < 1000) {
			error = 0;
			for (int i = 0; i < trainingData.size(); i++) {
				input(trainingData.get(i));
				target(trainingLabels.get(i));
				forwardPass();
				backPropagation();
				
				int trueVal = trainingLabels.get(i);
				int prediction = 0;
				if (outputCount > 1) {
					prediction = utils.argMax(outputLayer);
				}
				else {
					if (outputLayer[0] > 0.5) {
						prediction = 1;
					}
					else {
						prediction = 0;
					}
				}
				if (prediction != trueVal) {
					error++;
				}
			}
			errorRate = (double) error / trainingLabels.size();
			iteration++;
		}
	}

	public List<Integer> classify(List<Feature> testdata) {
		List<Integer> guesses = new ArrayList<Integer>();
		for (Feature data : testdata) {
			input(data);
			forwardPass();
			
			int prediction = 0;
			if (outputCount > 1) {
				prediction = utils.argMax(outputLayer);
			}
			else {
				if (outputLayer[0] > 0.5) {
					prediction = 1;
				}
				else {
					prediction = 0;
				}
			}
			guesses.add(prediction);
		}
		return guesses;
	}
	
	// initialize both weights (input-hidden, hidden-output)
	public void initialWeight() {
		Random rand = new Random();
		
		// weights between input and hidden
		for (int i = 0; i < inputCount; i++) {
			for (int j = 0; j < hiddenCount; j++) {
				weightInput[i][j] = 2 * rand.nextDouble() - 1;
			}
		}
		// weights between hidden and output
		for (int i = 0; i < hiddenCount; i++) {
			for (int j = 0; j < outputCount; j++) {
				weightHidden[i][j] = 2 * rand.nextDouble() - 1;
			}
		}
	}
	
	// sigmoid function
	public double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
	
	public void forwardPass() {
		// update hidden layer
		for (int j = 0; j < hiddenCount; j++) {
			double sum = 0.0;
			for (int i = 0; i < inputCount; i++) {
				sum += inputLayer[i] * weightInput[i][j];
			}
			hiddenLayer[j] = sigmoid(sum);
		}
		// update output layer
		for (int j = 0; j < outputCount; j++) {
			double sum = 0.0;
			for (int i = 0; i < hiddenCount; i++) {
				sum += hiddenLayer[i] * weightHidden[i][j];
			}
			outputLayer[j] = sigmoid(sum);
		}
	}
	
	// weight update
	public void backPropagation() {
		double[] deltaHidden = new double[hiddenCount];
		double[] deltaOutput = new double[outputCount];
		
		// calculate errors of output layer
		for (int i = 0; i < outputCount; i++) {
			double transferDerivative = outputLayer[i] * (1 - outputLayer[i]);
			deltaOutput[i] = transferDerivative * (target[i] - outputLayer[i]);
		}
		// update weight between hidden layer and output layer
		for (int i = 0; i < hiddenCount; i++) {
			for (int j = 0; j < outputCount; j++) {
				weightHidden[i][j] += ratio * deltaOutput[j] * hiddenLayer[i];
			}
		}
		// calculate errors of hidden layer
		for (int i = 0; i < hiddenCount; i++) {
			for (int j = 0; j < outputCount; j++) {
				double transferDerivative  = hiddenLayer[i] * (1 - hiddenLayer[i]);
				deltaHidden[i] += transferDerivative * deltaOutput[j] * weightHidden[i][j];
			}
		}
		// update weight between input layer and hidden layer
		for (int i = 0; i < inputCount; i++) {
			for (int j = 0; j < hiddenCount; j++) {
				weightInput[i][j] += ratio * deltaHidden[j] * inputLayer[i];
			}
		}
	}
	
	public void input(Feature feature) {
		int height = feature.getHeight();
		int width = feature.getWidth();
		int index = 0;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (feature.get(i, j) == 0) {
					inputLayer[index] = 0;
				}
				else if (feature.get(i, j) == 1) {
					inputLayer[index] = 1;
				}
				else if (feature.get(i, j) == 2) {
					inputLayer[index] = 1;
				}
				index++;
			}
		}
	}
	
	public void target(int label) {
		Arrays.fill(target, 0);
		if (outputCount > 1) {
			target[label] = 1;
		}
		else {
			target[0] = label;
		}
	}
}