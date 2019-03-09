import java.util.List;

public abstract class ClassificationMethod {
	
	int[] legalLabels;
	
	public ClassificationMethod (int[] legalLabels) {
		this.legalLabels = legalLabels;
	}
	
	public abstract void train (List<Feature> trainingData, List<Integer> trainingLabels, List<Feature> validationData, List<Integer> validationLabels);
	
	public abstract List<Integer> classify (List<Feature> data);

}
