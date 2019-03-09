
public class Utils {
	
	// returns the key with the highest value
	public int argMax (double[] maxArray) {
		int maxIndex = 0;
		
		for (int i = 1; i < maxArray.length; i++) {
			if (maxArray[i] > maxArray[maxIndex]) {
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
