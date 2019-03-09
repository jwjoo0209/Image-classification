import java.util.ArrayList;
import java.util.List;

public class NaiveBayes extends ClassificationMethod {
	
	double k = 1;
	double[] kGrid = {0.01, 0.05, 0.1, 0.5, 1, 5, 10, 20, 50};
	int trainingSize = 0;
	int[] count;				// counts for label
	int[][][] countFeature1;	// counts the label that has feature value 1
	int[][][] countFeature2;	// counts the label that has feature value 2
	boolean automaticTuning = false;
	
	Utils utils = new Utils();
	
	public NaiveBayes (int[] legalLabels) {
		super (legalLabels);
		count = new int[legalLabels.length];
	}
	
	// Laplace smoothing factor
	public void setSmoothing (double k) {
		this.k = k;
	}
	
	public void setAutomaticTuning (boolean autoTune) {
		automaticTuning = autoTune;
	}
	
	public void train (List<Feature> trainingData, List<Integer> trainingLabels, List<Feature> validationData, List<Integer> validationLabels) {
		trainingSize = trainingLabels.size();
		
		for (int i : trainingLabels) {
			count[i]++;
		}
		Feature sampleData = trainingData.get(0);
		int height = sampleData.getHeight();
		int width = sampleData.getWidth();
		countFeature1 = new int[height][width][legalLabels.length];
		countFeature2 = new int[height][width][legalLabels.length];
		
		for (int i = 0; i < trainingData.size(); i++) {
			Feature feature = trainingData.get(i);
			int label = trainingLabels.get(i);
			for (int x = 0; x < feature.getHeight(); x++) {
				for (int y = 0; y < feature.getWidth(); y++) {
					if (feature.get(x, y) == 1) {
						countFeature1[x][y][label]++;
					}
					else if (feature.get(x, y) == 2) {
						countFeature2[x][y][label]++;
					}
				}
			}
		}
		
		validate(validationData, validationLabels);
	}
	
	public List<Integer> classify (List<Feature> testdata) {
		List<Integer> labels = new ArrayList<Integer>();
		
		for (Feature data : testdata) {
			double[] posterior = calculateLogJointProbabilities(data);
			labels.add(utils.argMax(posterior));
		}
		return labels;
	}
	
	// Calculates the log-joint distribution over legal labels given the feature of data.
	public double[] calculateLogJointProbabilities (Feature feature) {
		double[] prob = new double[legalLabels.length];
		
		for (int i = 0; i < prob.length; i++) {
			prob[i] = Math.log((double) count[i] / trainingSize);
		}
		int height = feature.getHeight();
		int width = feature.getWidth();
		
		for (int y = 0; y < prob.length; y++) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (feature.get(i, j) == 1) {
						prob[y] += Math.log((double) (countFeature1[i][j][y] + k) / (count[y] + 3 * k));
					}
					else if (feature.get(i, j) == 2) {
						prob[y] += Math.log((double) (countFeature2[i][j][y] + k) / (count[y] + 3 * k));
					}
					else {
						prob[y] += Math.log((double) (count[y] - countFeature1[i][j][y] - countFeature2[i][j][y] + k) / (count[y] + 3 * k));
					}
				}
			}
		}
		return prob;
	}
	
	// use validation data to set k by choosing the best result
	public void validate (List<Feature> validationData, List<Integer> validationLabels) {
		if (automaticTuning == true) {
			double[] accuracy = new double[kGrid.length];
			for (int i = 0; i < kGrid.length; i++) {
				k = kGrid[i];
				List<Integer> guesses = classify(validationData);
				int correct = 0;
				
				// compare guesses and validation labels
				for (int j = 0; j < validationData.size(); j++) {
					if (validationLabels.get(j) == guesses.get(j)) {
						correct++;
					}
				}
				accuracy[i] = (double) correct / validationData.size();
			}
			
			int maxIndex = utils.argMax(accuracy);
			k = kGrid[maxIndex];
			System.out.println("The smoothing value k after Automatic tuning: " + k);
		}
		else {
			System.out.println("Mannually choose the smoothing value k: " + k);
		}
	}
	
}
