public class Feature {
	int[][] feature;
	
	public Feature(int x, int y) {
		feature = new int[x][y];
	}
	
	public int getHeight() {
		return feature.length;
	}
	
	public int getWidth() {
		return feature[0].length;
	}
	
	public int get(int x, int y) {
		return feature[x][y];
	}
	
	public void set(int x, int y, int val) {
		feature[x][y] = val;
	}
}
