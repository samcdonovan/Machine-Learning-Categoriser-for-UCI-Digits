import java.util.List;

public class Euclidean {

	public static int categorise2(List<int[]> dataset1, List<int[]> dataset2) {
		double min = Float.MAX_VALUE;
		double currentDist;
		int minPos = 0;
		int numCorrect = 0;
		int dataset1Size = dataset1.size();
		int dataset2Size = dataset2.size();
		int lastIndex = dataset1.get(0).length - 1;

		for (int pos = 0; pos < dataset1Size; pos++) {

			min = Float.MAX_VALUE;

			for (int pos2 = 0; pos2 < dataset2Size; pos2++) {

				currentDist = euclidean(dataset1.get(pos), dataset2.get(pos2));

				if (currentDist < min) {
					min = currentDist;
					minPos = pos2;
				}
			}

			System.out.println(pos + " : " + dataset1.get(pos)[lastIndex] + ", closest = " + minPos + " : "
					+ dataset2.get(minPos)[lastIndex]);

			if (dataset1.get(pos)[lastIndex] == dataset2.get(minPos)[lastIndex])
				numCorrect++;
		}

		return numCorrect;
	}

	public static int categorise(int current, List<int[]> dataset) {
		double min = Float.MAX_VALUE;
		double currentDist;
		int minPos = 0;
		int numCorrect = 0;
		int datasetSize = dataset.size();
		int lastIndex = dataset.get(0).length - 1;

		for (int pos = current * 10; pos < (current * 10) + 10 && pos < datasetSize; pos++) {

			min = Float.MAX_VALUE;

			for (int pos2 = 0; pos2 < datasetSize; pos2++) {

				if (current * 10 == pos2) {
					pos2 += 10;

					if (pos2 >= datasetSize - 1)
						continue;
				}

				currentDist = euclidean(dataset.get(pos), dataset.get(pos2));

				if (currentDist < min) {
					min = currentDist;
					minPos = pos2;
				}
			}

			System.out.println(pos + " : " + dataset.get(pos)[lastIndex] + ", closest = " + minPos + " : "
					+ dataset.get(minPos)[lastIndex]);

			if (dataset.get(pos)[lastIndex] == dataset.get(minPos)[lastIndex])
				numCorrect++;
		}

		return numCorrect;
	}

	public static double euclidean(int[] digit1, int[] digit2) {

		int sum = 0;

		for (int pos = 0; pos < digit1.length - 1; pos++)
			sum += ((digit1[pos] - digit2[pos]) * (digit1[pos] - digit2[pos]));

		return Math.sqrt(sum);
	}
}