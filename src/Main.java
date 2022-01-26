
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main.java
 * 
 * @author Samuel C. Donovan
 * 
 * INSTRUCTIONS:
*/
public class Main {

	public static void main(String[] args) {

		int totalCorrect = 0;

		String dataFile1 = System.getProperty("user.dir") + File.separator + "data" + File.separator
				+ "cw2DataSet1.csv";
		System.out.println("Loading from " + dataFile1);

		String dataFile2 = System.getProperty("user.dir") + File.separator + "data" + File.separator
				+ "cw2DataSet2.csv";
		System.out.println("Loading from " + dataFile2);

		ArrayList<int[]> dataset1 = readFile(dataFile1);
		ArrayList<int[]> dataset2 = readFile(dataFile2);
		int fullDatasetSize = dataset1.size() + dataset2.size();
		totalCorrect += Euclidean.categorise2(dataset1, dataset2) + Euclidean.categorise2(dataset2, dataset1);

		if (dataset1.size() > 0 && dataset2.size() > 0) {
			/*for (int currentNum = 0; currentNum < data.size(); currentNum++) {
				totalCorrect += Euclidean.categorise(currentNum, data);
				System.out.println(totalCorrect);
			}*/
			System.out.println("Total correct: " + totalCorrect + "/" + fullDatasetSize + " = "
					+ (((double) totalCorrect / (double) fullDatasetSize) * 100) + "%");
		}
	}

	public static ArrayList<int[]> readFile(String filePath) {
		File file = new File(filePath); /* create file object to use a scanner on */

		String line = "", delimiter = ",";
		String[] tempArray;
		int[] array = new int[65];
		ArrayList<int[]> list = new ArrayList<int[]>();

		try {

			FileReader fileReader = new FileReader(file);
			BufferedReader bufferReader = new BufferedReader(fileReader);

			while ((line = bufferReader.readLine()) != null) {
				array = new int[65];
				tempArray = line.split(delimiter);

				for (int pos = 0; pos < tempArray.length; pos++) {
					array[pos] = Integer.parseInt(tempArray[pos]);

				}

				list.add(array);
			}

			bufferReader.close();

		} catch (IOException fileNotFound) { /* if file is not found, stop the program */
			System.out.println("File not found at " + filePath);
			return list;
		} finally {
			return list;
		}
	}

	public static void print2D(List<int[]> list) {
		for (int pos = 0; pos < list.size(); pos++) {
			for (int pos2 = 0; pos2 < list.get(0).length; pos2++) {
				System.out.print(list.get(pos)[pos2] + ", ");
			}
			System.out.println();
		}
	}

}