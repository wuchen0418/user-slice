import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class Predictor {
	ArrayList<Map> upccList;
	ArrayList<Map> ipccList;
	float[] URR_L1AVG;
	float[] URR_L2AVG;
	float[] URR_Cluster_AVG;
	//float[] URR_imean;
	//float[] URR_umean;
	ArrayList<Integer> unreliableUserList;
	HashMap userLocationMap; 
	Vector<String> country = new Vector<String>();

	
	public double[][] run8Methods(float[][] originalMatrix, float[][] randomedMatrix, float random, int topK, float density, float factord){
		float[][] originalMatrixT = UtilityFunctions.matrixTransfer(originalMatrix);
		float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
		int methodNumber = 4;
		double[][] mae_rmse = new double[methodNumber][2];
		double mae = 0;
		double allnmae = 0;
		double nmae = 0;
		
		//UMEAN
		//calculate the mean failure rate of services that each user called
		float[] umean = UtilityFunctions.getUMean(randomedMatrix);
		
		// UMEAN use user's mean failure rate of called services to predict the missing ones.
		/*float[][] predictedMatrix = UMEAN(originalMatrix, randomedMatrix, umean);
		double mae = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrix);
		double rmse = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrix);
		System.out.println("UMEAN:\t" + mae + "\t" + rmse);
		mae_rmse[0][0] = mae;
		mae_rmse[0][1] = rmse;*/
		
		//IMEAN
		//calculate the mean failure rate of users that called the same service
		float[] imean = UtilityFunctions.getUMean(randomedMatrixT);
		
		// IMEAN 
		/*predictedMatrix = UMEAN(originalMatrixT, randomedMatrixT, imean);
		mae = UtilityFunctions.MAE(originalMatrixT, randomedMatrixT, predictedMatrix);
		rmse = UtilityFunctions.RMSE(originalMatrixT, randomedMatrixT, predictedMatrix);
		System.out.println("IMEAN:\t" + mae + "\t" + rmse);
		mae_rmse[1][0] = mae;
		mae_rmse[1][1] = rmse;*/
		
		// UPCC
		//System.out.println("calculating UPCC: " + new Time(System.currentTimeMillis()));
//		System.out.println("calculating UIPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixUPCC = UPCC(originalMatrix, randomedMatrix, 
				umean, topK, true);
		mae = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixUPCC);
		allnmae = UtilityFunctions.allNMAE(originalMatrix, randomedMatrix, predictedMatrixUPCC);
		nmae = UtilityFunctions.NMAE(mae,allnmae);
//		rmse = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixUPCC);
//		System.out.println("UPCC:\t" + mae + "\t" + rmse);
		mae_rmse[0][0] = mae;
		mae_rmse[0][1] = nmae;
		
		// IPCC
		//ipccList = calculatePccList(originalMatrixT, randomedMatrixT, imean, true, false, 20, 100);
		//System.out.println("calculating IPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixIPCC = UPCC(originalMatrixT, randomedMatrixT, 
				imean, topK, false);
		mae = UtilityFunctions.MAE(originalMatrixT, randomedMatrixT, predictedMatrixIPCC);
		allnmae = UtilityFunctions.allNMAE(originalMatrixT, randomedMatrixT, predictedMatrixIPCC);
		nmae = UtilityFunctions.NMAE(mae,allnmae);
//		rmse = UtilityFunctions.RMSE(originalMatrixT, randomedMatrixT, predictedMatrixIPCC);
//		System.out.println("IPCC:\t" + mae + "\t" + rmse);
		mae_rmse[1][0] = mae;
		mae_rmse[1][1] = nmae;
		
		// UIPCC
		double[] mae2 = new double[11];
		double[] allnmae2 = new double[11]; 
		double[] nmae2 = new double[11]; 
//		double[] rmse2 = new double[11]; 
		double smallMAE = 100;
		double smallNMAE =100;
//		double smallRMSE = 100;
		for (int i = 0; i < 11; i++) {
			double lambda = (double)i/10.0;
			float[][] predictedMatrixUIPCC = UIPCC(predictedMatrixUPCC, predictedMatrixIPCC, lambda);
			mae2[i] =   UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixUIPCC);
			allnmae2[i] =   UtilityFunctions.allNMAE(originalMatrix, randomedMatrix, predictedMatrixUIPCC);
			nmae2[i] = UtilityFunctions.NMAE(mae2[i],allnmae2[i]);
			//rmse2[i] = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixUIPCC);
		}
		for (int i = 0; i < mae2.length; i++) {
			if(mae2[i] < smallMAE) smallMAE = mae2[i];
			if(nmae2[i] < smallNMAE) smallNMAE = nmae2[i];
		}
		//UtilityFunctions.writeFile("result.txt", "UIPCC:\t" + smallMAE + "\t" + smallRMSE + "\r\n");
		mae_rmse[2][0] = smallMAE;
		mae_rmse[2][1] = smallNMAE;
    	
		//RAP
//		System.out.println("calculating RAP: " + new Time(System.currentTimeMillis()));
		//System.out.println("Identifying Unreliable User: " + new Time(System.currentTimeMillis()));
		getURR_L1AVG_Before(randomedMatrix, factord, 1000, imean); 
    	//for (int i=0 ;i <URR_L1AVG.length;i++)
    	//{
    	//	UtilityFunctions.writeFile("URR/URRBefore_"+density+"_"+random+".txt", URR_L1AVG[i]+"\t");
    	//}
    	//UtilityFunctions.writeFile("URR/URRBefore_"+density+"_"+random+".txt", "\r\n");
    	getUnreliableUserList(URR_L1AVG,15);
    	
    	
    	float[][] purifiedData = purifyDataset(randomedMatrix);
    	
    	
    	
    	float[] URR_umean = UtilityFunctions.getUMean(purifiedData);// getURR_umean(purifiedData,URR_L1AVG);
		
    	float[][] purifiedMatrrixT = UtilityFunctions.matrixTransfer(purifiedData);
    	float[] URR_imean = UtilityFunctions.getUMean(purifiedMatrrixT);//getURR_imean(purifiedData,URR_L1AVG);
    	//System.out.println("calculating UPCC on purified Dataset: " + new Time(System.currentTimeMillis()));
    	
    	
		float[][] purifiedUPCC = UPCC(originalMatrix, purifiedData, 
				URR_umean, topK, true);
		//System.out.println("calculating IPCC on purified Dataset: " + new Time(System.currentTimeMillis()));
		float[][] purifiedIPCC = UPCC(originalMatrixT, purifiedMatrrixT, 
				URR_imean, topK, false);

		double[] mae_urr_uipcc = new double[11];
		double[] allnmae_urr_uipcc = new double[11]; 
		double[] nmae_urr_uipcc = new double[11]; 
//		double[] rmse_urr_uipcc = new double[11]; 
		for (int i = 0; i < 11; i++) {
			double lambda = (double)i/10.0;
			float[][] purifiedUIPCC = UIPCC(purifiedUPCC, purifiedIPCC, lambda);
			mae_urr_uipcc[i] =   UtilityFunctions.MAE(originalMatrix, randomedMatrix, purifiedUIPCC);
			allnmae_urr_uipcc[i] =   UtilityFunctions.allNMAE(originalMatrix, randomedMatrix, purifiedUIPCC);
			nmae_urr_uipcc[i] =UtilityFunctions.NMAE(mae_urr_uipcc[i],allnmae_urr_uipcc[i]);
//			rmse_urr_uipcc[i] = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, purifiedUIPCC);
			//smallMAE =   UtilityFunctions.MAE(originalMatrix, randomedMatrix, purifiedUIPCC);
			//smallRMSE = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, purifiedUIPCC);
		//	System.out.println("UIPCC:" + i + "\t" + mae2[i] + "\t" + rmse2[i]);
		}
		
		smallMAE = 100;
		smallNMAE =100;
//		smallRMSE = 100;
		for (int i = 0; i < mae_urr_uipcc.length; i++) {
			if(mae_urr_uipcc[i] < smallMAE) smallMAE = mae_urr_uipcc[i];
			if(nmae_urr_uipcc[i] < smallNMAE) smallNMAE = nmae_urr_uipcc[i];
		}
		
		//UtilityFunctions.writeFile("result.txt", "RAP:\t" + smallMAE + "\t" + smallRMSE + "\r\n");
		mae_rmse[3][0] = smallMAE;
		mae_rmse[3][1] = smallNMAE;
		
		
//		System.out.println("calculating RAPC: " + new Time(System.currentTimeMillis()));
		
		/*
		getURR_L1AVG_After(randomedMatrix, factord, 1000, imean); 
    	//for (int i=0 ;i <URR_Cluster_AVG.length;i++)
    	//{
    	//	UtilityFunctions.writeFile("URR/URRCluster_"+density+"_"+random+".txt", URR_Cluster_AVG[i]+"\t");
    	//}
    	//UtilityFunctions.writeFile("URR/URRCluster_"+density+"_"+random+".txt", "\r\n");
    	
    	getUnreliableUserList(URR_Cluster_AVG);
    	purifiedData = purifyDataset(randomedMatrix);
    	
    	purifiedMatrrixT = UtilityFunctions.matrixTransfer(purifiedData);
    	
		//Test if reputation affects prediction result
		URR_umean = getURR_umean(purifiedData,URR_Cluster_AVG);
		URR_imean = getURR_imean(purifiedData,URR_Cluster_AVG);
		//URR_umean = UtilityFunctions.getUMean(purifiedData);
		//URR_imean = UtilityFunctions.getUMean(purifiedMatrrixT);
		purifiedUPCC = UPCC(originalMatrix, purifiedData, 
				URR_umean, topK, true);
		purifiedIPCC = UPCC(originalMatrixT, purifiedMatrrixT, 
				URR_imean, topK, false);
		
		for (int i = 0; i < 11; i++) {
			double lambda = (double)i/10.0;
			float[][] predictedMatrixURR_UIPCC = UIPCC(purifiedUPCC, purifiedIPCC, lambda);
			mae_urr_uipcc[i] =   UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
//			rmse_urr_uipcc[i] = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
			//smallMAE =   UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
			//smallRMSE = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
			//System.out.println("UIPCC:" + i + "\t" + mae2[i] + "\t" + rmse2[i]);
		}
		
		smallMAE = 100;
//		smallRMSE = 100;
		for (int i = 0; i < mae_urr_uipcc.length; i++) {
			if(mae_urr_uipcc[i] < smallMAE) smallMAE = mae_urr_uipcc[i];
//			if(rmse_urr_uipcc[i] < smallRMSE) smallRMSE = rmse_urr_uipcc[i];
		}
		
		//UtilityFunctions.writeFile("result.txt", "RAPC:\t" + smallMAE + "\t" + smallRMSE + "\r\n");
		mae_rmse[1][0] = smallMAE;
//		mae_rmse[2][1] = smallRMSE;
		
		*/
		unreliableUserList.clear();
		return mae_rmse;
	}
	
	public float[][] purifyDataset(float[][]randomedMatrix) {
		int userNumber = randomedMatrix.length;
		int serviceNumber = randomedMatrix[0].length;
		float[][] purifiedData = new float[userNumber][serviceNumber];
		
		UtilityFunctions.copyMatrix(randomedMatrix, purifiedData);
		for (int i = 0; i < unreliableUserList.size(); i ++) {
			int userIndex = (int) unreliableUserList.get(i);
			for (int j = 0; j < serviceNumber; j++) {
				purifiedData[userIndex][j] = -2;
			}
		}		
		return purifiedData;
	}

	public float[][] UIPCC(float[][] predictedMatrixUPCC, float[][] predictedMatrixIPCC, double lambda){
		float[][]result = new float[predictedMatrixUPCC.length][predictedMatrixUPCC[0].length];
		for (int i = 0; i < predictedMatrixUPCC.length; i++) {
			for (int j = 0; j < predictedMatrixUPCC[0].length; j++) {
				result[i][j] = (float)(lambda * predictedMatrixUPCC[i][j] + (1 - lambda) * predictedMatrixIPCC[j][i]);
			}
		}
		return result;
	}
	
	public float[][] URR_UPCC(float[][] originalMatrix, float[][] randomedMatrix, float[] umean, int topK) {
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
	
		for (int i = 0; i < originalMatrix.length; i++) {
//			System.out.println(i);
			// get the pcc values of the current user with all other users. 
			HashMap pcc = new HashMap();
			
			for (int j = 0; j < originalMatrix.length; j++) {
				// the same user. 
				if(i == j) continue;
				
				// the user has no ratings, no similarity computation. 
				if(umean[i] == -2 || umean[j] == -2) continue; 
				
				if (unreliableUserList.contains(j))
					continue;
				
				double pccValue = 0;
				pccValue = getPCC(randomedMatrix[i], randomedMatrix[j], umean[i], umean[j]);

				
				// find similar users, 
				if(pccValue>0)	pcc.put(j, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			// predict values for each items in the line. 
			for (int j = 0; j < originalMatrix[0].length; j++) {
				// not removed entry, no need to predict. 
				if(randomedMatrix[i][j] != -2) continue; 
				
				// no original value for making evaluation, no need to predict. 
				if(originalMatrix[i][j] < 0) continue;
				
				int k = 0;
				double pccAll = 0; 
				double predictedValue = 0;
				Iterator it = sortedPcc.entrySet().iterator();
				while(k < topK && it.hasNext()){
					
					Map.Entry en = (Map.Entry)it.next();
					int userID = (Integer)en.getKey();
					
					if(unreliableUserList.contains(userID))
						continue;
					// if the similar user does not use this item previously, can not be used. 
					if(randomedMatrix[userID][j] == -2 || randomedMatrix[userID][j] == -1) continue;
										
					double userPCCValue = (Double)en.getValue();
					pccAll += userPCCValue;
					k++;
					
					predictedValue += (userPCCValue) * (originalMatrix[userID][j] - umean[userID]);
				}
				
				// no similar users, use umean. 
				if(pccAll == 0) {
					predictedValue = umean[i];
				} else{ 
					predictedValue = predictedValue/pccAll + umean[i];
				}
				
				
				// will become worst, no need. 
				if(predictedValue <= 0) predictedValue = 0;
				
				predictedMatrix[i][j] = (float)predictedValue;
			}
		}
	
		return predictedMatrix;
	}	
	
	public float[][] URR_IPCC(float[][] originalMatrix, float[][] randomedMatrix, float[] imean, int topK) {
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
		float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
		
		for (int j = 0; j < originalMatrix[0].length; j++) {
 
			HashMap pcc = new HashMap();
			//get similar services of service j
			for (int i = 0; i < originalMatrix[0].length; i++) {
				// the same user. 
				if(j == i) continue;
				
				// the user has no ratings, no similarity computation. 
				if(imean[i] == -2 || imean[j] == -2) continue; 
				
				double pccValue = 0;
				
				pccValue = getURR_IPCC(randomedMatrixT[j], randomedMatrixT[i], imean[j], imean[i]);

				
				// find similar users, 
				if(pccValue>0)	pcc.put(i, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			
			 
			for (int i = 0; i < originalMatrix.length; i++) {
				// not removed entry, no need to predict. 
				if(randomedMatrix[i][j] != -2) continue; 
				
				// no original value for making evaluation, no need to predict. 
				if(originalMatrix[i][j] < 0) continue;
				
								
				int k = 0;
				double pccAll = 0; 
				double predictedValue = 0;
				Iterator it = sortedPcc.entrySet().iterator();
				while(it.hasNext()){
					
					Map.Entry en = (Map.Entry)it.next();
					int serviceID = (Integer)en.getKey();
					
					// if the similar user does not use this item previously, can not be used. 
					if(randomedMatrix[i][serviceID] == -2 || randomedMatrix[i][serviceID] == -1) continue;
					
										
					double userPCCValue = (Double)en.getValue();
					pccAll += userPCCValue;
					
					predictedValue += (userPCCValue) * (originalMatrix[i][serviceID] - imean[serviceID]);
				}
				
				// no similar users, use umean. 
				if(pccAll == 0) {
					predictedValue = imean[j];
				} else{ 
					predictedValue = predictedValue/pccAll + imean[j];
				}
				
				
				// will become worst, no need. 
				if(predictedValue <= 0) predictedValue = 0;
				
				predictedMatrix[i][j] = (float)predictedValue;
			}
				
		}
		
		
		return predictedMatrix;
	}
	
	
	public ArrayList<Map> calculateURRupccList(float[][] originalMatrix, float[][] removedMatrix, float[] umean, boolean isPCC, 
			boolean isSW, int swThreshold, int topK) {
		
		ArrayList<Map> pccList = new ArrayList<Map>();
		for (int i = 0; i < originalMatrix.length; i++) {
//			System.out.println(i);
			// get the pcc values of the current user with all other users. 
			HashMap pcc = new HashMap();
			
			for (int j = 0; j < originalMatrix.length; j++) {
				// the same user. 
				if(i == j) continue;
				
				// the user has no ratings, no similarity computation. 
				if(umean[i] == -2 || umean[j] == -2) continue; 
				
				if (unreliableUserList.contains(j))
					continue;
				
				double pccValue = 0;
				pccValue = getPCC(removedMatrix[i], removedMatrix[j], umean[i], umean[j]);
				
				// find similar users, 
				if(pccValue>0)	pcc.put(j, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			pccList.add(sortedPcc);
		}
			
		return pccList;	
	}
	
	/**
	 * 
	 * @param originalMatrix
	 * @param removedMatrix
	 * @param isSW
	 * @param swThreshold
	 */
	public float[][] UPCC(float[][] originalMatrix, float[][] removedMatrix, float[] umean, int topK, boolean isUPCC){
		
		float[][] predictedMatrix = new float[originalMatrix.length][originalMatrix[0].length];
		
		for (int i = 0; i < originalMatrix.length; i++) {
//			System.out.println(i);
			// get the pcc values of the current user with all other users. 
			HashMap pcc = new HashMap();
			
			for (int j = 0; j < originalMatrix.length; j++) {
				// the same user. 
				if(i == j) continue;
				
				// the user has no ratings, no similarity computation. 
				if(umean[i] == -2 || umean[j] == -2) continue; 
				
				double pccValue = 0;
				
					pccValue = getPCC(removedMatrix[i], removedMatrix[j], umean[i], umean[j]);

				
				// find similar users, 
				if(pccValue>0)	pcc.put(j, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			// predict values for each items in the line. 
			for (int j = 0; j < originalMatrix[0].length; j++) {
				// not removed entry, no need to predict. 
				if(removedMatrix[i][j] != -2) continue; 
				
				// no original value for making evaluation, no need to predict. 
				if(originalMatrix[i][j] < 0) continue;
				
				int k = 0;
				double pccAll = 0; 
				double predictedValue = 0;
				Iterator it = sortedPcc.entrySet().iterator();
				while(k < topK && it.hasNext()){
					
					Map.Entry en = (Map.Entry)it.next();
					int userID = (Integer)en.getKey();
					
					// if the similar user does not use this item previously, can not be used. 
					if(removedMatrix[userID][j] == -2 || removedMatrix[userID][j] == -1) continue;
					
					double userPCCValue = (Double)en.getValue();
					pccAll += userPCCValue;
					k++;
					
					predictedValue += (userPCCValue) * (originalMatrix[userID][j] - umean[userID]);
				}
				
				// no similar users, use umean. 
				if(pccAll == 0) {
					predictedValue = umean[i];
				} else{ 
					predictedValue = predictedValue/pccAll + umean[i];
				}
				
				
				// will become worst, no need. 
				if(predictedValue <= 0) predictedValue = 0;
				
				
//				if(predictedValue >= 1) predictedValue = 1;
				
//				System.out.println(predictedValue + "\t" + valueMatrix[i][j]);
				
				predictedMatrix[i][j] = (float)predictedValue;
			}
		}
		return predictedMatrix;
	}
	
	
	
	public Map sortByValue(Map map, int topK) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Collections.reverse(list);
		// logger.info(list);
		Map result = new LinkedHashMap();
		Iterator it = list.iterator();
		while (it.hasNext() && result.size() < topK) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	
	
	
	public float[][] UMEAN(float[][] originalMatrix, float[][] removedMatrix, float[] umean) {
		float[][] predictedMatrix = new float[originalMatrix.length][originalMatrix[0].length];
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				// predict the remove entry and the original entry is not null. 
				if(removedMatrix[i][j] == -2 && originalMatrix[i][j] != -1) {
					if(umean[i] == -2) predictedMatrix[i][j] = -2;
					else predictedMatrix[i][j] = umean[i];
				}
			}
		}
		return predictedMatrix;
	}
	
	
	/**
	 * two vectors, and two means of the vectors.
	 * isWS: whether enable the significant weight. 
	 * swPercent: the threshold of the significant weight.  
	 *  
	 * @return
	 * -2 indicates this pcc value has problem and can't be used.
	 */
	public double getPCC(float[] u1, float[] u2, double mean1, double mean2){
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
	
	public double getURR_IPCC(float[] u1, float[] u2, double mean1, double mean2){
		// get the index of the common rated items.
		Vector<Integer> commonRatedKey = new Vector<Integer>();
		
		//remove unreliable user
		for (int i = 0; i < u1.length; i++) {
			if (unreliableUserList.contains(i))
				continue;
			if(u1[i] >= 0 && u2[i] >= 0 ) {
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
	
	
	public double getPCC(double[] u1, double[] u2, double mean1, double mean2, boolean isSW, int swThreshold){
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
		if(isSW && commonRatedKey.size() < swThreshold) {
			pcc = pcc * commonRatedKey.size() / swThreshold;
		}
		return pcc;
	}
	
	public float[] getURR_L1AVG_Before(float[][] randomedMatrix, double d, int loopNum, float[] imean) {
		int userNumber = randomedMatrix.length;
		int serviceNumber = randomedMatrix[0].length;
		
		if(URR_L1AVG == null) {
			URR_L1AVG = new float[userNumber];  //user's reputation
		}
		
		float[] rating = new float[serviceNumber];
		float[] last_rating = new float[serviceNumber];
		int iteration = 0;
		
		for (int i = 0; i < imean.length; i++) {
			rating[i] = imean[i];
		}
		
		Vector<Integer> unpredictedList = new Vector<Integer>();
		
		for (int i = 0; i < userNumber; i++) {
			//URR[i] = random.nextFloat();
			URR_L1AVG[i] = (float)1.0;
		}
		
		for (int i = 0; i < serviceNumber; i++) {
			unpredictedList.add(i);
			last_rating[i] = -3;
		}
			
		do {
			
			//calculating URR
			for (int i = 0; i < userNumber; i++) {
				float tmpURR = 0;
				int Oj = 0;
				Vector<Integer> ratedServiceKey = new Vector<Integer>();
				
				for (int j =0; j < serviceNumber; j++) {
					if (randomedMatrix[i][j] > 0) {
						ratedServiceKey.add(j);
					}
				}
				Oj = ratedServiceKey.size();
				for (int k = 0; k < Oj; k++) {
					tmpURR += Math.abs(randomedMatrix[i][k] - rating[k]);
				}
				if (Oj > 0) {
					tmpURR = (float)1 - (float)d * tmpURR / (float)Oj;
					URR_L1AVG[i] = tmpURR;
				}			
			}
					
			//calculating ratings
			for (int i = 0; i < serviceNumber; i++) {
				float tmprating = 0;
				int Mj = 0;
				Vector<Integer> commonRatedKey = new Vector<Integer>();
							
				for (int j = 0; j < userNumber; j++) {
					if (randomedMatrix[j][i] > 0) {
						commonRatedKey.add(j);
					}
				}
				Mj = commonRatedKey.size();
				
				for (int k = 0; k < Mj; k++) {
					int index = commonRatedKey.get(k);
					tmprating += randomedMatrix[index][i] * URR_L1AVG[index];
				}
				
				if (Mj > 0) {
					rating[i] =  tmprating / Mj;
				}
				else {
					rating[i] = -2;
				}		
			}
		
			for (int i = 0; i < unpredictedList.size(); i++) {
				int index = unpredictedList.get(i);
				if (rating[index] == -2){
					unpredictedList.remove(i);
					continue;
				}				
				if (Math.abs(rating[index] - last_rating[index]) < 0.0001) {
					unpredictedList.remove(i);
				} else {
					last_rating[index] = rating[index];
				}
			}
			

			iteration ++;
		} while ((iteration < loopNum) && unpredictedList.size() > 0);
	
		/*float totalURR = 0;
		for(int i = 0; i < userNumber; i++) {
			totalURR += URR_L1AVG[i];
		}
		for(int i = 0; i < userNumber; i++) {
			URR_L1AVG[i] /= totalURR;
		}*/
		
		return rating;
	}
	
	
	public void initUserLocationMap(String fileName) {
		userLocationMap = UtilityFunctions.getUserLocationMap(fileName);//users in the country
		Iterator it = userLocationMap.entrySet().iterator();
		
		while(it.hasNext()){
			Map.Entry en = (Entry) it.next();
			country.add((String) en.getKey());
		}
	}
	public float[] getURR_L1AVG_After(float[][] randomedMatrix, double d, int loopNum, float[] imean) {				

		int userNumber = randomedMatrix.length;
		if (URR_Cluster_AVG == null) {
			URR_Cluster_AVG = new float[userNumber];  //user's reputation
		}		
		int serviceNumber = randomedMatrix[0].length;
		int countryNumber = country.size();
		float[][] rating = new float[countryNumber][serviceNumber]; //service's avg rank
		float[][] last_rating = new float[countryNumber][serviceNumber];
		int iteration = 0;
		//System.out.println("begin: "+ new Time (System.currentTimeMillis()));
		for (int j = 0; j < countryNumber; j++) {
			for(int i = 0; i < imean.length; i++){
				rating[j][i] = imean[i];
			}
		}
		
		Vector<Integer> unpredictedList = new Vector<Integer>();
		
		for (int i = 0; i < userNumber; i++) {
			//URR[i] = random.nextFloat();
			URR_Cluster_AVG[i] = (float)1.0;
		}
		
		for (int j = 0; j<countryNumber; j++) {
			for(int i = 0; i < serviceNumber; i++){
			unpredictedList.add(i);
			last_rating[j][i] = -3;
		}
		}
	
		do {
		
		//System.out.println("do begin: "+ new Time (System.currentTimeMillis()));
			//calculating user's reputation
		for( int c = 0; c < countryNumber; c++){
			//System.out.println("1");			
			String coun = country.get(c);
			ArrayList<Integer> userInCountry = (ArrayList<Integer>) userLocationMap.get(coun);		
			if(userInCountry.size() < 2) {
				//int userIndex = userInCountry.get(0);
				//URR_Cluster_AVG[userIndex]= URR_L1AVG[userIndex];
				continue;
			}
			for (int i = 0; i < userInCountry.size(); i++) {
				float tmpURR = 0;
				int Oj = 0;
				Vector<Integer> ratedServiceKey = new Vector<Integer>();
				
				for (int j =0; j < serviceNumber; j++) {
					if (randomedMatrix[userInCountry.get(i)][j] > 0) {
						ratedServiceKey.add(j);
					}
				}
				Oj = ratedServiceKey.size();
				for (int k = 0; k < Oj; k++) {
					tmpURR += Math.abs(randomedMatrix[userInCountry.get(i)][ratedServiceKey.get(k)] - rating[c][ratedServiceKey.get(k)]);
				}
				if (Oj > 0) {
					tmpURR = (float)1 - (float)d * tmpURR / (float)Oj;
					URR_Cluster_AVG[userInCountry.get(i)] = tmpURR;
				}			
			}
			//System.out.println("2");
		}
			//System.out.println("3");
								
			//calculating service's avg rank
		for( int c = 0; c < country.size(); c++){
			//System.out.println("4");
			String coun = country.get(c);
			ArrayList<Integer> userInCountry = (ArrayList<Integer>) userLocationMap.get(coun);
			//System.out.println("��country"+c+"�е��û���"+userInCountry.size());
			for (int i = 0; i < serviceNumber; i++) {
				//System.out.println("5.1");
				float tmprating = 0;
				int Mj = 0;
				//Vector<Integer> commonRatedKey = new Vector<Integer>();
				ArrayList<Integer> userInvokeService = new ArrayList<Integer>(); //users which invoke the certain service in the country			

				for (int j = 0; j < userInCountry.size(); j++) {
				//	System.out.println("5.2");
					if (randomedMatrix[userInCountry.get(j)][i] > 0) {
						userInvokeService.add(userInCountry.get(j));
					}
				}
				//System.out.println("5.3");
				Mj = userInvokeService.size();
				//System.out.println("��country"+c+"�У�����service"+i+"���û���"+Mj);
				for (int k = 0; k < Mj; k++) {
				//	System.out.println("5.4");
					int index = userInvokeService.get(k);
					tmprating += randomedMatrix[index][i] * URR_Cluster_AVG[index];
				}
				
				//System.out.println("5.5");
				if (Mj > 0) {
					rating[c][i] =  tmprating / Mj;
				}
				else {
					rating[c][i] = -2;
				}		
				userInvokeService.clear();//��������������һ��serviceѭ�����������б�
			}
			//System.out.println("5");
			}
			//System.out.println("6");
		
			for (int j = 0; j < countryNumber; j++) {
				//System.out.println("7");
				for(int i = 0; i < unpredictedList.size(); i++){
				    int index = unpredictedList.get(i);
				    if (rating[j][index] == -2){
					unpredictedList.remove(i);
					continue;
				}				
				    if (Math.abs(rating[j][index] - last_rating[j][index]) < 0.0001) {
				    	unpredictedList.remove(i);
				    } else {
				    	last_rating[j][index] = rating[j][index];
				    }
		    	}
				//System.out.println("8");
			}
			

			iteration ++;
			//System.out.println("9");
		} while ((iteration < loopNum) && unpredictedList.size() > 0);
		//System.out.println("do after: "+ new Time (System.currentTimeMillis()));
		
		//Normalize
		/*for(int c = 0; c < countryNumber; c++) {
			String coun = country.get(c);
			ArrayList<Integer> userInCountry = (ArrayList<Integer>) userLocationMap.get(coun);
			int usersInCountry = userInCountry.size();
			if(usersInCountry < 2) {
				//int userIndex = userInCountry.get(0);
				//URR_Cluster_AVG[userIndex]= URR_L1AVG[userIndex];
				continue;
			} else {
				float totalURRinCountry = 0;
				for (int i = 0; i < usersInCountry; i++) {
					int userIndex = userInCountry.get(i);
					totalURRinCountry += URR_Cluster_AVG[userIndex];					
				}
				
				for (int i = 0; i < usersInCountry; i++) {
					int userIndex = userInCountry.get(i);
					URR_Cluster_AVG[userIndex] /= totalURRinCountry;					
				}
			}
		}*/
		return URR_Cluster_AVG;
	}
	
	public float[] getURR_L2AVG(float[][] randomedMatrix, double d, int loopNum, float[] imean) {
		int userNumber = randomedMatrix.length;
		URR_L2AVG = new float[userNumber]; 
		int serviceNumber = randomedMatrix[0].length; 
		float[] rating = new float[serviceNumber];
		float[] last_rating = new float[serviceNumber];
		int iteration = 0;
		
		for (int i = 0; i < imean.length; i++) {
			rating[i] = imean[i];
		}
		
		Vector<Integer> unpredictedList = new Vector<Integer>();
		
		for (int i = 0; i < userNumber; i++) {
			//URR[i] = random.nextFloat();
			URR_L2AVG[i] = (float)1.0;
		}
		
		for (int i = 0; i < serviceNumber; i++) {
			unpredictedList.add(i);
			last_rating[i] = -3;
		}
			
		do {
			
			//calculating URR
			for (int i = 0; i < userNumber; i++) {
				float tmpURR = 0;
				int Oj = 0;
				Vector<Integer> ratedServiceKey = new Vector<Integer>();
				
				for (int j =0; j < serviceNumber; j++) {
					if (randomedMatrix[i][j] > 0) {
						ratedServiceKey.add(j);
					}
				}
				Oj = ratedServiceKey.size();
				float diff = 0;
				for (int k = 0; k < Oj; k++) {
					diff = randomedMatrix[i][k] - rating[k];
					tmpURR += diff * diff;
				}
				if (Oj > 0) {
					tmpURR = (float)1 - (float)d * tmpURR / ((float)2.0 *(float)Oj);
					URR_L2AVG[i] = tmpURR;
				}			
			}
					
			//calculating ratings
			for (int i = 0; i < serviceNumber; i++) {
				float tmprating = 0;
				int Mj = 0;
				Vector<Integer> commonRatedKey = new Vector<Integer>();
							
				for (int j = 0; j < userNumber; j++) {
					if (randomedMatrix[j][i] > 0) {
						commonRatedKey.add(j);
					}
				}
				Mj = commonRatedKey.size();
				
				for (int k = 0; k < Mj; k++) {
					int index = commonRatedKey.get(k);
					tmprating += randomedMatrix[index][i] * URR_L2AVG[index];
				}
				
				if (Mj > 0) {
					rating[i] =  tmprating / Mj;
				}
				else {
					rating[i] = -2;
				}		
			}
		
			for (int i = 0; i < unpredictedList.size(); i++) {
				int index = unpredictedList.get(i);
				if (rating[index] == -2) {
					unpredictedList.remove(i);
					continue;
				}
				if (Math.abs(rating[index] - last_rating[index]) < 0.0001) {
					unpredictedList.remove(i);
				} else {
					last_rating[index] = rating[index];
				}
			}
			

			iteration ++;
		} while ((iteration < loopNum) && unpredictedList.size() > 0);
				
		return URR_L2AVG;
	}
	
	public void getUnreliableUserList(float[] URR, float unreliableNum) {
		//Test if the URR algorithm can identify unreliable users. (Yes)
		ArrayList<Float> list = new ArrayList<Float>();
		HashMap map = new HashMap();
		for (int i = 0; i < URR.length; i++) {
			list.add(new Float(URR[i]));	
			map.put(i, URR[i]);
		}
		Collections.sort(list);
		//Collections.reverse(list);
		
		unreliableUserList = new ArrayList();
		for (int i = 0; i < unreliableNum; i++) {
			unreliableUserList.add(UtilityFunctions.getMapKeyByValue(map, list.get(i)));
		}
	}
	
//	public void getUnreliableUserList(float[] URR) {
//		//Test if the URR algorithm can identify unreliable users. (Yes)
//		KMeans kMeans = new KMeans(URR,2);
//		kMeans.cluster();
//		unreliableUserList=kMeans.getUnreliableUserList();
//		System.out.println(unreliableUserList.toString());
//	}
	
	public float[] getURR_umean(float[][] removedMatrix, float[] URR) {
		float[] umean = new float[removedMatrix.length];
		int[] uNumber = new int[removedMatrix.length]; 
		for (int i = 0; i < removedMatrix.length; i++) {
			//if (unreliableUserList.contains(i))
			//	continue;
			for (int j = 0; j < removedMatrix[0].length; j++) {
				// exclude the null entries (-1) and the removed entries (-2). 
				if(removedMatrix[i][j] < 0) 
					continue;
				
				umean[i] += removedMatrix[i][j] ;
				uNumber[i]++;
			}
		}
		
		for (int i = 0; i < umean.length; i++) {
			if(uNumber[i] == 0)
				umean[i] = -2;
			else 
				umean[i] = umean[i] * URR[i] / uNumber[i];
		}
		
		return umean;
	}
	public float[] getURR_imean(float[][] removedMatrix, float[] URR) {
		int serviceNum = removedMatrix[0].length;
		float[] imean = new float[serviceNum];
		int[] iNumber = new int[serviceNum];
		
		/*for (int i = 0; i < removedMatrix.length; i++) {
			if (unreliableUserList.contains(i))
				continue;
			for (int j = 0; j < removedMatrix[0].length; j++) {
				imean[j] += removedMatrix[i][j] * URR[i];
				imean[j] ++;
			}
		}*/
		for (int j = 0; j < serviceNum; j ++) {
			for (int i = 0; i < removedMatrix.length; i ++) {
				if(removedMatrix[i][j] < 0) 
					continue;
				//if (unreliableUserList.contains(i))
				//	continue;
				imean[j] += removedMatrix[i][j]* URR[i];
				iNumber[j]++;
			}
		}
		
		for (int i = 0; i < imean.length; i++) {
			if(iNumber[i] ==0) imean[i] = -2;
			else imean[i] /= iNumber[i];
		}
		
		return imean;
	}
	
}
