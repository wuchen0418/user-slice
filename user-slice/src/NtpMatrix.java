
public class NtpMatrix {
	
	public static void main(String[] args) {
		
		int userNumber = 339;
		int itemNumber = 5825;
		
		String prefix = "WSDream-QoSDataset2/";
		String matrix = "tpMatrix"; 
		
		float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", userNumber, itemNumber);
		float[][] normalizedMatrix = new float[originalMatrix.length][originalMatrix[0].length];
		
		int number = 0;
		float allNMAE = 0;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if(originalMatrix[i][j] > 0 ) {
					allNMAE += originalMatrix[i][j];
					number ++;
				}
			}
		}
		
		allNMAE=allNMAE/number;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if(originalMatrix[i][j] > 0 )
					normalizedMatrix[i][j] = originalMatrix[i][j]/allNMAE;
				else
					normalizedMatrix[i][j] = originalMatrix[i][j];
			}
		}
		UtilityFunctions.writeMatrix(normalizedMatrix, prefix + "NormalizedtpMatrix.txt");
	}
}
