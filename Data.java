public class Data {
	int WIDTH;
	int HEIGHT;
	char[][] data;
	
	public Data(char[][] data, int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		
		if (data == null) {
			this.data = new char[height][width];
		}
		else {
			this.data = data;
		}
	}
	
	public char[][] getPixels() {
		return data;
	}
	
	public char getPixel(int x, int y) {
		return data[x][y];
	}
	
	// extract feature of digits data
	public Feature getDigitFeature() {
		Feature feature = new Feature(HEIGHT, WIDTH);
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				if (data[i][j] == '+') {
					feature.set(i, j, 1);
				}
				else if (data[i][j] == '#') {
					feature.set(i, j, 2);
				}
				else {
					feature.set(i, j, 0);
				}
			}
		}
		return feature;
	}
	
	// extract feature of faces data
	public Feature getFaceFeature() {
		Feature feature = new Feature(HEIGHT, WIDTH);
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				if (data[i][j] == '#') {
					feature.set(i, j, 1);
				}
				else {
					feature.set(i, j, 0);
				}
			}
		}
		return feature;
	}
}
