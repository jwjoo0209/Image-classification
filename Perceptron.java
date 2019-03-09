import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class Perceptron extends ClassificationMethod {
	int maxIteration;
	double[] scores;
	double[][][] weights;
	
	Utils utils = new Utils();
	
	public Perceptron (int[] legalLabels, int maxIteration) {
		super(legalLabels);
		this.maxIteration = maxIteration;
		scores = new double[legalLabels.length];
	}
	
	public void train (List<Feature> trainingData, List<Integer> trainingLabels, List<Feature> validationData, List<Integer> validationLabels) {
		int height = trainingData.get(0).getHeight();
		int width = trainingData.get(0).getWidth();
		int length = legalLabels.length;
		initialWeight(height, width, length);
		
		for (int i = 0; i < maxIteration; i++) {
			for (int j = 0; j < trainingData.size(); j++) {
				calculateScores(trainingData.get(j));
				int prediction = utils.argMax(scores);
				int trueVal = trainingLabels.get(j);
				
				if (prediction != trueVal) {
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							weights[y][x][trueVal] += trainingData.get(j).get(y, x);
							weights[y][x][prediction] -= trainingData.get(j).get(y, x);
						}
					}
				}
			}
		}
	}
	
	public List<Integer> classify (List<Feature> testdata) {
		List<Integer> guesses = new ArrayList<Integer>();
		
		for (Feature data : testdata) {
			calculateScores(data);
			guesses.add(utils.argMax(scores));
		}
		return guesses;
	}
	
	// initialize the weight
	public void initialWeight (int height, int width, int length) {
		weights = new double[height][width][length];
		Random rand = new Random();
		
		for (int y = 0; y < length; y++) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					weights[i][j][y] = rand.nextDouble();
				}
			}
		}
	}
	
	public void calculateScores (Feature feature) {
		Arrays.fill(scores, 0);
		
		for (int y = 0; y < legalLabels.length; y++) {
			for (int i = 0; i < feature.getHeight(); i++) {
				for (int j = 0; j <feature.getWidth(); j++) {
					scores[y] += weights[i][j][y] * feature.get(i, j);
				}
			}
		}
	}
	
}
