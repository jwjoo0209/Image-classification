import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Reader {
	
	public static List<Data> loadData(String fileName, int n, int width, int height, int offset) {
		List <Data> result = new ArrayList<Data>();
		BufferedReader br = null;
		char[][] raw;
		
		try {
			File file = new File(fileName);
			br = new BufferedReader(new FileReader(file));
			
			for (int i = 0; i < offset * height; i++) {
				br.readLine();
			}
			
			String line = br.readLine();
			for (int i = 0; i < n && line != null; i++) {
				raw = new char[height][width];
				for (int j = 0; j < height; j++) {
					char[] array = line.toCharArray();
					raw[j] = array;
					line = br.readLine();
				}
				Data data = new Data(raw, width, height);
				result.add(data);
			}
			br.close();
		}
		catch (IOException e) {
			System.out.println("No Data file exists!");
		}
		return result;
	}
	
	public static List<Integer> loadLabels (String fileName, int n, int offset) {
		List<Integer> result = new ArrayList<Integer>();
		BufferedReader br = null;
		
		try {
			File file = new File(fileName);
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			
			for (int i = 0; i < offset; i++) {
				br.readLine();
			}
			while (line != null && n > 0) {
				if (Integer.parseInt(line) >= 0 && Integer.parseInt(line) <= 9) {
					result.add(Integer.parseInt(line));
				}
				line = br.readLine();
				n--;
			}
			br.close();
		}
		catch (IOException e) {
			System.out.println("No Label file exists!");
		}
		return result;
	}
}
