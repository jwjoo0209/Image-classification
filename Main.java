import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	static int DIGIT_DATUM_WIDTH = 28;
	static int DIGIT_DATUM_HEIGHT = 28;
	static int FACE_DATUM_WIDTH = 60;
	static int FACE_DATUM_HEIGHT = 70;
	static int DIGIT = 0;
	static int FACE = 1;
	static int MAX_ITERATION = 20;
	static int DIGIT_TRAINING_SIZE = 5000;
	static int DIGIT_VALIDATION_SIZE = 1000;
	static int DIGIT_TEST_SIZE = 1000;
	static int FACE_TRAINING_SIZE = 451;
	static int FACE_VALIDATION_SIZE = 301;
	static int FACE_TEST_SIZE = 150;
	
	public static void main (String[] args) {
		int trainingNum = 0;
		int offsetNum = 0;
		int[] legalLabels;
		long startTime;
		long endTime;
		double trainTime;
		
		List<Data> trainingData = null;
		List<Integer> trainingLabels = null;
		List<Data> validationData = null;
		List<Integer> validationLabels = null;
		List<Data> testData = null;
		List<Integer> testLabels = null;
		List<Feature> trainingDataFeature = null;
		List<Feature> validationDataFeature = null;
		List<Feature> testDataFeature = null;
				
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter \'digits\' or \'faces\': ");
		String option = sc.nextLine();
		System.out.print("Enter the part of the training data (percent 0 - 100): ");
		int percent = sc.nextInt();
		
		while (percent < 0 || percent > 100) {
			System.out.println("Input the correct percent!");
			percent = sc.nextInt();
		}
		
		System.out.print("Enter the offset of the training data (percent 0 - 100): ");
		int offset = sc.nextInt();
		if (percent == 100) {
			offset = 0;
			System.out.println("100% data used");
		}
		sc.close();
		
		if (option.equals("digits")) {
			trainingNum = (int) ((double) percent / 100 * DIGIT_TRAINING_SIZE);
			offsetNum = (int) ((double) offset / 100 * DIGIT_TRAINING_SIZE);
			trainingData = Reader.loadData("data/digitdata/trainingimages", trainingNum, DIGIT_DATUM_WIDTH, DIGIT_DATUM_HEIGHT, offsetNum);
			trainingLabels = Reader.loadLabels("data/digitdata/traininglabels", trainingNum, offsetNum);
			validationData = Reader.loadData("data/digitdata/validationimages", DIGIT_VALIDATION_SIZE, DIGIT_DATUM_WIDTH, DIGIT_DATUM_HEIGHT, 0);
			validationLabels = Reader.loadLabels("data/digitdata/validationLabels", DIGIT_VALIDATION_SIZE, 0);
			testData = Reader.loadData("data/digitdata/testimages", DIGIT_TEST_SIZE, DIGIT_DATUM_WIDTH, DIGIT_DATUM_HEIGHT, 0);
			testLabels = Reader.loadLabels("data/digitdata/testlabels", DIGIT_TEST_SIZE, 0);
			trainingDataFeature = extractFeatures(trainingData, DIGIT);
			validationDataFeature = extractFeatures(validationData, DIGIT);
			testDataFeature = extractFeatures(testData, DIGIT);
			legalLabels = new int[10];
			for (int i = 0; i < 10; i++) {
				legalLabels[i] = i;
			}
		}
		else if (option.equals("faces")) {
			trainingNum = (int) ((double) percent / 100 * FACE_TRAINING_SIZE);
			offsetNum = (int) ((double) offset / 100 * FACE_TRAINING_SIZE);
			trainingData = Reader.loadData("data/facedata/facedatatrain", trainingNum, FACE_DATUM_WIDTH, FACE_DATUM_HEIGHT, offsetNum);
			trainingLabels = Reader.loadLabels("data/facedata/facedatatrainlabels", trainingNum, offsetNum);
			validationData = Reader.loadData("data/facedata/facedatavalidation", FACE_VALIDATION_SIZE, FACE_DATUM_WIDTH, FACE_DATUM_HEIGHT, 0);
			validationLabels = Reader.loadLabels("data/facedata/facedatavalidationLabels", FACE_VALIDATION_SIZE, 0);
			testData = Reader.loadData("data/facedata/facedatatest", FACE_TEST_SIZE, FACE_DATUM_WIDTH, FACE_DATUM_HEIGHT, 0);
			testLabels = Reader.loadLabels("data/facedata/facedatatestlabels", FACE_TEST_SIZE, 0);
			trainingDataFeature = extractFeatures(trainingData, FACE);
			validationDataFeature = extractFeatures(validationData, FACE);
			testDataFeature = extractFeatures(testData, FACE);
			legalLabels = new int[2];
			for (int i = 0; i < 2; i++) {
				legalLabels[i] = i;
			}
		}
		else {
			legalLabels = new int[1];
			System.out.println("Wrong data type!");
			System.exit(1);
		}
		
		System.out.println("Data Type: " + option);
		
		// naive bayes
		NaiveBayes naivebayes = new NaiveBayes(legalLabels);
		naivebayes.setAutomaticTuning(true);
		System.out.println("------------------------------");
		System.out.println("Running Naive Bayes");
		System.out.println("Training Data");
		startTime = System.currentTimeMillis();
		naivebayes.train(trainingDataFeature, trainingLabels, validationDataFeature, validationLabels);
		endTime = System.currentTimeMillis();
		trainTime = (endTime - startTime) / 1000.0;
		System.out.println("Testing Data");
		List<Integer> guessesNaiveBayes = naivebayes.classify(testDataFeature);
		analysis(guessesNaiveBayes, testLabels, testData);
		System.out.println("Trainning time: " + trainTime + " s");
		
		// perceptron
		Perceptron perceptron = new Perceptron(legalLabels, MAX_ITERATION);
		System.out.println("");
		System.out.println("------------------------------");
		System.out.println("Running Perceptron");
		System.out.println("Training Data");
		startTime = System.currentTimeMillis();
		perceptron.train(trainingDataFeature, trainingLabels, validationDataFeature, validationLabels);
		endTime = System.currentTimeMillis();
		trainTime = (endTime - startTime) / 1000.0;
		System.out.println("Testing Data");
		List<Integer> guessesPerceptron = perceptron.classify(testDataFeature);
		analysis(guessesPerceptron, testLabels, testData);
		System.out.println("Training time: " + trainTime + " s");
		
		// back propagation neural network
		BPNN bpnn = null;
		if (option.equals("digits")) {
			bpnn = new BPNN(legalLabels, DIGIT_DATUM_WIDTH * DIGIT_DATUM_HEIGHT, 70, 10, 1.0);
		}
		else if (option.equals("faces")) {
			bpnn = new BPNN(legalLabels, FACE_DATUM_WIDTH * FACE_DATUM_HEIGHT, 100, 1, 1.0);
		}
		System.out.println("");
		System.out.println("------------------------------");
		System.out.println("Running BPNN");
		System.out.println("Training Data");
		startTime = System.currentTimeMillis();
		bpnn.train(trainingDataFeature, trainingLabels, validationDataFeature, validationLabels);
		endTime = System.currentTimeMillis();
		trainTime = (endTime - startTime) / 1000.0;
		System.out.println("Testing Data");
		List<Integer> guessesBPNN = bpnn.classify(testDataFeature);
		analysis(guessesBPNN, testLabels, testData);
		System.out.println("Training time: " + trainTime + " s");
	}
	
	// calculate for error rate, accuracy, print image of errors
	public static void analysis (List<Integer> guesses, List<Integer> testLabels, List<Data> testData) {
		int error = 0;
		int total = testLabels.size();
		double errorRate;
		double accuracy;
		
		for (int i = 0; i <guesses.size(); i++) {
			int prediction = guesses.get(i);
			int trueVal = testLabels.get(i);
			
			if (prediction != trueVal) {
				error++;
				//System.out.println("------------------------------");
				//System.out.println("Error index: " + i);
				//System.out.println("Predict Val: " + prediction + ", True Val : " + trueVal);
				//System.out.println("Image: ");
				//printImage(testData.get(i));
			}
		}
		errorRate = (double) error / total;
		accuracy = (double) (total - error) / total;
		System.out.println("========= Results =========");
		System.out.println("Number of errors: " + error + ", Total: " + total);
		System.out.println("Error Rate: " + errorRate);
		System.out.println("Accuracy: " + accuracy);
		
	}
	
	// print image of data
	public static void printImage(Data data) {
		char[][] pixels = data.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j <pixels[0].length; j++) {
				System.out.print(pixels[i][j]);
			}
			System.out.println();
		}
	}
	
	// Obtain feature from original data for 3 algorithms
	public static List<Feature> extractFeatures (List<Data> data, int type) {
		List<Feature> feature = new ArrayList<Feature>();
		
		for (Data datum : data) {
			if (type == DIGIT) {
				feature.add(datum.getDigitFeature());
			}
			else if (type == FACE) {
				feature.add(datum.getFaceFeature());
			}
		}
		return feature;
		
	}

}
