import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;


public class UtilityFunctions {


	public static void main(String[] args) {
		String prefix = "WSDream-QoSDataset2/";
		String matrix = "frMatrix"; 
		int userNumber = 150; 
		int itemNumber = 100;
		
		/*
		float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix, userNumber, itemNumber);
		removeEntry(originalMatrix, 0.30, "removed/" + matrix + "30");
		originalMatrix = UtilityFunctions.readMatrix("removed/" + matrix + "30", userNumber, itemNumber);
		originalMatrix = randomEntry(originalMatrix, 0.05);
		writeMatrix(originalMatrix, "randomed/" + matrix + "0.05");
		*/
		
		HashMap<String, ArrayList<Integer>> userList, serviceList;
		
		userList = getUserLocationMap(prefix +"userlist.txt");
		serviceList = getServiceLocationMap(prefix + "wslist.txt");
		Set<String> keySet = userList.keySet();
		Iterator it = keySet.iterator();
	}
	
	
	
	public static void datasetDesp(){
		double[][] tp = readMatrixDouble("dataset1//tpMatrix", 150, 100);
		double tpStdSum = 0;
		double tpSum = 0;
		int tpNumber = 0;
		double max = 0;
		double min = 100000;
		
		for (int i = 0; i < tp.length; i++) {
			for (int j = 0; j < tp[0].length; j++) {
				if(tp[i][j] <= 0) continue; 
				
				tpSum += tp[i][j];
				tpStdSum += tp[i][j] * tp[i][j];
				tpNumber++;
				
				if(tp[i][j] > max) max = tp[i][j];
				if(tp[i][j] < min) min = tp[i][j];
			}
		}
		
		System.out.println(min);
		System.out.println(max);
		double mean = tpSum/tpNumber;
		System.out.println(mean);
		System.out.println(Math.sqrt(tpStdSum / tpNumber - mean * mean));
	}
	
	public static void genMatrixDensity(){
		double[][] rt = readMatrixDouble("dataset1//rtMatrix", 150, 100);
		removeEntry(rt, 0.05, "dataset1//removed//rtMatrix05");
		removeEntry(rt, 0.15, "dataset1//removed//rtMatrix15");
		removeEntry(rt, 0.25, "dataset1//removed//rtMatrix25");
		removeEntry(rt, 0.35, "dataset1//removed//rtMatrix35");
		removeEntry(rt, 0.45, "dataset1//removed//rtMatrix45");
		
		
		double[][] tp = readMatrixDouble("dataset1//tpMatrix", 150, 100);
		removeEntry(tp, 0.05, "dataset1//removed//tpMatrix05");
		removeEntry(tp, 0.15, "dataset1//removed//tpMatrix15");
		removeEntry(tp, 0.25, "dataset1//removed//tpMatrix25");
		removeEntry(tp, 0.35, "dataset1//removed//tpMatrix35");
		removeEntry(tp, 0.45, "dataset1//removed//tpMatrix45");	
	}
	
	
	
	public static void matrixUnitChange(){
		double[][] rtt = readMatrixDouble("dataset1//rtMatrix", 150, 100);
		String result = "";
		for (int i = 0; i < rtt.length; i++) {
			for (int j = 0; j < rtt[0].length; j++) {
				result += rtt[i][j]/1000;
				if(j == rtt[0].length - 1) result += "\r\n"; 
				else result += "\t"; 
			}
		}
		
		String path = "dataset1//rtMatrixSecond";
		File f = new File(path);
		if(f.exists()) f.delete();
		writeFile(path, result);
	}
	
	
	/**
	 * descending sorting. 
	 * @param array
	 * @return
	 */
/*	public static float[] sortArray(float[] array){
		int[] index = new int[array.length];
		float[] result = new float[array.length];
		double[] arrayTemp = new double[array.length];
		
		
		uti.InsertSort is=new uti.InsertSort();
		for (int i = 0; i < array.length; i++) {
			index[i] = i;
			arrayTemp[i] = array[i];
		}
		try {
			index = is.sort(arrayTemp, index);
			
			for (int i = 0; i < index.length; i++) {
				result[index.length-i-1] = (float)arrayTemp[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}*/
	
	
/*	public static int[] sortArray2(float[] array){
		int[] index = new int[array.length];
		int[] result = new int[array.length];
		double[] arrayTemp = new double[array.length];
		
		uti.InsertSort is=new uti.InsertSort();
		for (int i = 0; i < array.length; i++) {
			index[i] = i;
			arrayTemp[i] = array[i];
		}
		try {
			index = is.sort(arrayTemp, index);
			for (int i = 0; i < index.length; i++) {
				result[index.length-i-1] = index[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

	public static int[] sortArray2(double[] array){
		int[] index = new int[array.length];
		int[] result = new int[array.length];
		double[] arrayTemp = new double[array.length];
		
		uti.InsertSort is=new uti.InsertSort();
		for (int i = 0; i < array.length; i++) {
			index[i] = i;
			arrayTemp[i] = array[i];
		}
		try {
			index = is.sort(arrayTemp, index);
			for (int i = 0; i < index.length; i++) {
				result[index.length-i-1] = index[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}*/
	
	
	public static float[][] readMatrix(String fileName, int userNumber, int itemNumber) {
		float[][] result = new float[userNumber][itemNumber];
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			
			String line;
			int index = 0;
			while ((line = br.readLine()) != null) {
				if(index >= userNumber) break;
				String[] temp = line.split("\t");
				for (int j = 0; j < temp.length; j++) {
					result[index][j] = Float.parseFloat(temp[j]);
				}
				index++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static float[][] readMatrixTimeInterval(String fileName, int userNumber, int itemNumber, int timeInterval) {
		float[][] result = new float[userNumber][itemNumber];
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			
			String line;
			int index = 0;
			while ((line = br.readLine()) != null) {
				if(index >= userNumber) break;
				String[] temp = line.split("\t");
				for (int j = 0; j < temp.length; j++) {
					result[index][j] = Float.parseFloat(temp[j]);
				}
				index++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static double[][] readMatrixDouble(String fileName, int userNumber, int itemNumber) {
		double[][] result = new double[userNumber][itemNumber];
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			
			String line;
			int index = 0;
			while ((line = br.readLine()) != null) {
				if(index >= userNumber) break;
				String[] temp = line.split("\t");
				for (int j = 0; j < temp.length; j++) {
					result[index][j] = Float.parseFloat(temp[j]);
				}
				index++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void writeMatrix(float[][] matrix, String fileName) {
        try {
            File file = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String line = "";
            for(int i =0; i < matrix.length; i++) {
            	for(int j=0; j < matrix[0].length; j++) {
            		line += matrix[i][j] + "\t";
            	}
            	writer.write(line);
            	if (i < matrix.length -1) {
            		writer.newLine();
            	}
            	line = "";
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public static void writeMatrix(double[][] matrix, String fileName) {
        try {
            File file = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String line = "";
            for(int i =0; i < matrix.length; i++) {
            	for(int j=0; j < matrix[0].length; j++) {
            		line += matrix[i][j] + "\t";
            	}
            	writer.write(line);
            	if (i < matrix.length -1) {
            		writer.newLine();
            	}
            	line = "";
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void writeTestMatrix300x500(float[][] matrix, String fileName) {
		try {
            File file = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String line = "";
            for(int i =0; i < 300; i++) {
            	for(int j=0; j < 500; j++) {
            		line += matrix[i][j] + "\t";
            	}
            	writer.write(line);
            	if (i < matrix.length -1) {
            		writer.newLine();
            	}
            	line = "";
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void printMatrix(float[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
        	for (int j = 0; j < matrix[0].length; j++) {
        		System.out.print(matrix[i][j] + "\t");
			}
        	System.out.println();
		}
    }
	
	public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
        	for (int j = 0; j < matrix[0].length; j++) {
        		System.out.print(matrix[i][j] + "\t");
			}
        	System.out.println();
		}
    }
	
	public static void printVector(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
       		System.out.print(vector[i] + "\t");
		}
        System.out.println();
    }
	
	public static void printVector(boolean[] vector) {
        for (int i = 0; i < vector.length; i++) {
       		System.out.print(vector[i] + "\t");
		}
        System.out.println();
    }
	
	public static void printVector(float[] vector) {
        for (int i = 0; i < vector.length; i++) {
       		System.out.print(vector[i] + "\t");
		}
        System.out.println();
    }
	
	public static void printVector(int[] vector) {
        for (int i = 0; i < vector.length; i++) {
       		System.out.print(vector[i] + "\t");
		}
        System.out.println();
    }
	
	public static float[][] matrixTransfer(float[][]originalMatrix) {
		float[][] result = new float[originalMatrix[0].length][originalMatrix.length];
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				result[j][i] = originalMatrix[i][j];
			}
		}
		return result;
	}
	
	public static double[][] matrixTransfer(double[][]originalMatrix) {
		double[][] result = new double[originalMatrix[0].length][originalMatrix.length];
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				result[j][i] = originalMatrix[i][j];
			}
		}
		return result;
	}
	
	
	/**
	 *  outFile: removed entry use -2 to present.  
	 *  The entry number is not exactly the same as entryNumber*density, proximately the same.  
	 */
	public static float[][] removeEntry(float[][]originalMatrix, double density, String outFile){	
		/*File f = new File(outFile);
		if(f.exists()) f.delete();
		
		Random r = new Random();
		for (int i = 0; i < originalMatrix.length; i++) {
			String line = "";
			for (int j = 0; j < originalMatrix[0].length; j++) {
				double value = r.nextDouble();
				if(value <= density){
					line += originalMatrix[i][j];
				} else {
					line += "-2";
				}
				
				// avoid the last tab in the line. 
				if(j != originalMatrix[0].length - 1)
					line += "\t";
			}
			writeFile(outFile, line + "\r\n");
		}*/
		
		int userNumber = originalMatrix.length;
		int serviceNumber = originalMatrix[0].length;
		float[][] removedMatrix = new float[userNumber][serviceNumber];
		
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < userNumber; i++) {
			for (int j = 0; j < serviceNumber; j++) {
				double value = random.nextDouble();
				if (value > density) {
					removedMatrix[i][j] = -2;
				} else {
					removedMatrix[i][j] = originalMatrix[i][j];
				}
			}
		}
		
		return removedMatrix;
		
	}
	
	public static void copyMatrix(float[][] source, float[][] dest) {
		for (int i= 0; i < source.length; i++) {
			for (int j=0; j < source[0].length; j++) {
				dest[i][j] = source[i][j];
			}
		}
	}
	
	public static float[][] randomEntry(float[][]originalMatrix, double rate) {
		
		int userNumber = originalMatrix.length;
		int serviceNumber = originalMatrix[0].length;
		float[][] randomedMatrix = new float[userNumber][serviceNumber];
		float range = getMaxValue(originalMatrix);
		
		copyMatrix(originalMatrix, randomedMatrix);
		int randomUserNumber = (int)(userNumber * rate);
		ArrayList<Integer> list = new ArrayList<Integer>();
		int index;
		Random random = new Random(System. currentTimeMillis());
		for (int i = 0; i < randomUserNumber; i++) {
			do {
				index = random.nextInt(userNumber);
			} while(list.contains(new Integer(index)));
			list.add(new Integer(index));	
			for(int j = 0; j < originalMatrix[0].length; j++){
				if (originalMatrix[index][j] != -1 && originalMatrix[index][j] != -2) {
					randomedMatrix[index][j] = range * random.nextFloat();
				}				
			}
		}
		writeFile("unreliableUsers.txt", list.toString());
		return randomedMatrix;
	}
	
	public static float getMaxValue(float[][] matrix) {
		float max = 0;
		for(int i = 0; i < matrix.length; i++) {
			for (int j =0; j < matrix[0].length; j++) {
				if (matrix[i][j] > max) {
					max = matrix[i][j];
				}
			}
		}
		
		return max;
	}
	
	
	
	/**
	 *  outFile: removed entry use -2 to present.  
	 *  The entry number is not exactly the same as entryNumber*density, proximately the same.  
	 */
	public static void removeEntry(double [][]originalMatrix, double density, String outFile){	
		File f = new File(outFile);
		if(f.exists()) f.delete();
		
		Random r = new Random();
		for (int i = 0; i < originalMatrix.length; i++) {
			String line = "";
			for (int j = 0; j < originalMatrix[0].length; j++) {
				double value = r.nextDouble();
				if(value <= density){
					line += originalMatrix[i][j];
				} else {
					line += "-2";
				}
				
				// avoid the last tab in the line. 
				if(j != originalMatrix[0].length - 1)
					line += "\t";
			}
			writeFile(outFile, line + "\r\n");
		}
	}
	
	
	public static String[] readFile(String fileLocation) {
        Vector<String> tags = new Vector<String>();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(fileLocation)));

            String tag = "";
            while ((tag = br.readLine()) != null) {
                tags.add(tag);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] tagString = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            tagString[i] = (String) tags.get(i);
        }

        return tagString;
    }
	
	
	public static void writeFile(String fileLocation, String content) {
        try {
            File file = new File(fileLocation);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static int getMapKeyByValue(Map map, float value) {
		//Map<K, V>.Entry<K, V> entry = 
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			if (Math.abs(Float.parseFloat(entry.getValue().toString()) - value) < 0.00001) {
				return Integer.parseInt(entry.getKey().toString());
			}
		}
		return -1;

	}
	
	public static HashMap<String, ArrayList<Integer>> getUserLocationMap(String fileName) {
		HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			String[] values;
			while ((line = reader.readLine())!= null) {
				values = line.split("\t");
				if (values != null && values.length > 3) {
					String key = values[2];
					if (map.containsKey(key)) {
						map.get(key).add(new Integer(values[0]));
					} else {
						ArrayList<Integer> list = new ArrayList<Integer>();
						list.add(new Integer(values[0]));
						map.put(key, list);
					}
				}
			}
			reader.close();
			System.out.print(map.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	public static HashMap<String, ArrayList<Integer>> getServiceLocationMap(String fileName) {
		HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			String[] values;
			while ((line = reader.readLine())!= null) {
				values = line.split("\t");
				if (values != null && values.length > 3) {
					String key = values[3];
					if (map.containsKey(key)) {
						map.get(key).add(new Integer(values[0]));
					} else {
						ArrayList<Integer> list = new ArrayList<Integer>();
						list.add(new Integer(values[0]));
						map.put(key, list);
					}
				}
			}
			reader.close();
			System.out.print(map.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	public static double MAE(float[][] originalMatrix, float[][] removedMatrix, float[][] predictedMatrix){
		double allMAE = 0;
		double number = 0;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if(removedMatrix[i][j] == -2 && originalMatrix[i][j] != -1 && predictedMatrix[i][j] != -2) {
					allMAE += Math.abs(predictedMatrix[i][j] - originalMatrix[i][j]);
					number ++;
				}
			}
		}
		return allMAE/number;
	}
	
	public static double allNMAE(float[][] originalMatrix, float[][] removedMatrix, float[][] predictedMatrix){
		double number = 0;
		double allNMAE = 0;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if(originalMatrix[i][j] > 0) {
					allNMAE += originalMatrix[i][j];
					number++;
				}
			}
		}
		allNMAE = allNMAE/number;
		return allNMAE;
	}
	
	public static double NMAE(double mae, double allNMAE){
		return mae/allNMAE;
	}
	
	public static double RMSE(float[][] originalMatrix, float[][] removedMatrix, float[][] predictedMatrix){
		double allRMSE = 0;
		double number = 0;
		
		float allRMSEMatrix[][] = new float[originalMatrix.length][originalMatrix[0].length];
		
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if(removedMatrix[i][j] == -2 && originalMatrix[i][j] != -1 && predictedMatrix[i][j] != -2) {
//					allRMSE += (predictedMatrix[i][j] - originalMatrix[i][j])*(predictedMatrix[i][j] - originalMatrix[i][j]);
					
					float f =(predictedMatrix[i][j] - originalMatrix[i][j])*(predictedMatrix[i][j] - originalMatrix[i][j]);
					BigDecimal b = new BigDecimal(f);
					float f1 = b.setScale(4,BigDecimal.ROUND_HALF_UP).floatValue(); 
//					allRMSEMatrix[i][j] = (predictedMatrix[i][j] - originalMatrix[i][j])*(predictedMatrix[i][j] - originalMatrix[i][j]);
					allRMSEMatrix[i][j] = f1;
					allRMSE += allRMSEMatrix[i][j];
					
					number ++;
				}
			}
		}
		
//		UtilityFunctions.writeMatrix(allRMSEMatrix, fileName);
		
		return Math.sqrt(allRMSE/number);
	}
	
	
	public static float[] getUMean(float[][] removedMatrix){
		float[] umean = new float[removedMatrix.length];
		int[] uNumber = new int[removedMatrix.length];
		
		for (int i = 0; i < removedMatrix.length; i++) {
			for (int j = 0; j < removedMatrix[0].length; j++) {
				// exclude the null entries (-1) and the removed entries (-2). 
				if(removedMatrix[i][j] < 0) continue;
				
				umean[i] += removedMatrix[i][j];
				uNumber[i]++;
			}
		}
		
		for (int i = 0; i < umean.length; i++) {
			if(uNumber[i] ==0) umean[i] = -2;
			else umean[i] /= uNumber[i];
		}
		
		return umean;
	}
	
	
	public static double[] getUMean(double[][] removedMatrix){
		double[] umean = new double[removedMatrix.length];
		int[] uNumber = new int[removedMatrix.length];
		
		for (int i = 0; i < removedMatrix.length; i++) {
			for (int j = 0; j < removedMatrix[0].length; j++) {
				// exclude the null entries (-1) and the removed entries (-2). 
				if(removedMatrix[i][j] < 0) continue;
				
				umean[i] += removedMatrix[i][j];
				uNumber[i]++;
			}
		}
		
		for (int i = 0; i < umean.length; i++) {
			if(uNumber[i] ==0) umean[i] = -2;
			else umean[i] /= uNumber[i];
		}
		
		return umean;
	}
	
	public static float getMean(float[] vector) {
		float mean = 0;
		for (int i = 0; i < vector.length; i ++) {
			mean += vector[i];
		}
		
		return mean/(float) vector.length;		
	}
	
	public static double getPCC(float[] u1, float[] u2, double mean1, double mean2){
		// get the index of the common rated items.
		Vector<Integer> commonRatedKey = new Vector<Integer>();
		
		for (int i = 0; i < u1.length; i++) {
			if(u1[i] >= 0 && u2[i] >= 0) {
				commonRatedKey.add(i);
			}
		}
		
		// no common rate items. 
		if(commonRatedKey.size() == 0 || commonRatedKey.size() == 1) 
			return -2;
		
		
		double pcc = 0;
		double upperAll = 0;
		double downAll1 = 0;
		double downAll2 = 0;

		for (int i = 0; i < commonRatedKey.size(); i++) {
			int key = commonRatedKey.get(i);
			double value1 = u1[key];
			double value2 = u2[key];
			
			double temp1 = value1 - mean1;
			double temp2 = value2 - mean2;
			
			if(temp1 < 0.00001 && temp1 > 0) temp1 = 0.00001;
			if(temp2 < 0.00001 && temp2 > 0) temp2 = 0.00001;

			if(temp1 > -0.00001 && temp1 < 0) temp1 = -0.00001;
			if(temp2 > -0.00001 && temp2 < 0) temp2 = -0.00001;
			
			upperAll += temp1 * temp2;
			downAll1 += temp1 * temp1;
			downAll2 += temp2 * temp2;
		}
		
		double downValue = Math.sqrt(downAll1 * downAll2);
		
		if(downValue == 0) 
			return -2;
		
		pcc = upperAll / downValue;
		
		//use significant weight to avoid the over estimation problem.
		// 10 is a parameter, which can be set.
		/*if(isSW && commonRatedKey.size() < swThreshold) {
			pcc = pcc * commonRatedKey.size() / swThreshold;
		}*/
		return pcc;
	}
	


}
