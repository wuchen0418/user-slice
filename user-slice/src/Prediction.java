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
import java.util.Vector;


public class Prediction {
	private float[] URR_imean;
	private float[] URR_umean;
	private float[][] originalMatrix;
	private float[][] removedMatrix;
	private float density;
	private int userNumber;
	private int itemNumber;
	
	private ArrayList<SimUserSet> simUserSetList;
	private ArrayList<UserSetInItem> userSetInItemList;
	
	public Prediction(float[][] originalMatrix, float[][] removedMatrix,
			float density,int userNum, int itemNum, ArrayList<SimUserSet> simUserSetList, ArrayList<UserSetInItem> userSetInItemList){
		this.originalMatrix=originalMatrix;
		this.removedMatrix=removedMatrix;
		this.density=density;
		this.userNumber=userNum;
		this.itemNumber=itemNum;
		this.simUserSetList=simUserSetList;
		this.userSetInItemList=userSetInItemList;		
	}
	
	public void cluserMean(){
		System.out.println("Caculating begin: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrix = new float[removedMatrix.length][removedMatrix[0].length];
		
		for(int i=0; i<userNumber; i++){
			SimUserSet aSimUserSet = simUserSetList.get(i);
			for(int j=0; j<itemNumber; j++){
				ArrayList<Integer> userNoInItem = new ArrayList<Integer>(); //get the users who invoke the service
				for(int index=0;index<userNumber;index++){
					if(removedMatrix[index][j]!=-1&&removedMatrix[index][j]!=-2&&removedMatrix[index][j]!=-3){
						userNoInItem.add(index);
					}
				}
				UserSetInItem aUserSetInItem = userSetInItemList.get(j); //get userSets in item j 
				int simFlag =0; //flag for simuser
				if(originalMatrix[i][j]==-1){  //no value in originalMatrix
					predictedMatrix[i][j]=-1;
					continue;
				}
				
				else if(removedMatrix[i][j]==-2){
					for(int u=0;u<aSimUserSet.getSimUserList().size();u++){
						SimUser aSimUser = aSimUserSet.getSimUser(u);
						if(userNoInItem.contains(aSimUser.getUserNo())){ //simuser invoke the item
							simFlag=1;
							UserSet aUserSet = aUserSetInItem.getUserSet(aSimUser.getUserNo());
							ArrayList<Integer> clustedUserNoList = aUserSet.getUserNoList();
							float clusterMean=0;
							int count=0;
							for(int a=0; a<clustedUserNoList.size(); a++){
									clusterMean += removedMatrix[clustedUserNoList.get(a)][j];
									count++;
							}
							if(count!=0){
								clusterMean=clusterMean/count; //get the cluster Mean of the cluster
							}
							else{
								clusterMean=0; // all simUsers are outliers
							}
							predictedMatrix[i][j] = clusterMean;
							break;
						}
						else{ //simuser dose not invoke the item
							continue; 
						}
					}
					continue;
				}
				else{
					predictedMatrix[i][j]=removedMatrix[i][j];
				}
			}
		}
		System.out.println("Caculating end: " + new Time(System.currentTimeMillis()));
		System.out.println("MAE=" + MAE(predictedMatrix));
		UtilityFunctions.writeMatrix(predictedMatrix, "predicted/d"+density+".txt");
	}
		
	public double MAE(float[][] predictedMatrix){
		double allMAE = 0;
		double number = 0;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if(removedMatrix[i][j] == -2 && originalMatrix[i][j] != -1) {
					allMAE += Math.abs(predictedMatrix[i][j] - originalMatrix[i][j]);
					number ++;
				}
			}
		}
		return allMAE/number;
	}
	
	public void runUIPCC(float[][] originalMatrix, float[][] removedMatrix,
			float density,int userNum, int itemNum, int topK, boolean isUPCC){
		
		float[] umean = UtilityFunctions.getUMean(removedMatrix);
		float[] imean = UtilityFunctions.getUMean(removedMatrix);
		
		double[] mae_uipcc = new double[11]; 
		double[] rmse_uipcc = new double[11];
		
		float[][] predictedMatrixUPCC = UPCC(originalMatrix, removedMatrix, umean, topK);
		float[][] predictedMatrixIPCC = IPCC(originalMatrix, removedMatrix, imean, topK);
		float[][] predictedMatrixIPCCT = UtilityFunctions.matrixTransfer(predictedMatrixIPCC);
		mae_uipcc = new double[11]; 
		rmse_uipcc = new double[11]; 
		for (int i = 0; i < 11; i++) {
			//对lambda值从0到1进行尝试，选择效果最好的作为最终结果
			double lambda2 = (double)i/10.0;
			float[][] predictedMatrixURR_UIPCC = UIPCC(predictedMatrixUPCC, predictedMatrixIPCCT, lambda2);
			mae_uipcc[i] =   UtilityFunctions.MAE(originalMatrix, removedMatrix, predictedMatrixURR_UIPCC);
			rmse_uipcc[i] = UtilityFunctions.RMSE(originalMatrix, removedMatrix, predictedMatrixURR_UIPCC);
//			System.out.println("UIPCC:" + i + "\t" + mae2[i] + "\t" + rmse2[i]);
		}

		double smallMAE = 100;
		double smallRMSE = 100;
		for (int i = 0; i < 11; i++) {
			if(mae_uipcc[i] < smallMAE) smallMAE = mae_uipcc[i];
			if(rmse_uipcc[i] < smallRMSE) smallRMSE = rmse_uipcc[i];
		}		
		UtilityFunctions.writeFile("result.txt", "L1_UIPCC:\t" + smallMAE + "\t" + smallRMSE + "\r\n");
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
	
	public float[][] UPCC(float[][] originalMatrix, float[][] removedMatrix, float[] umean, int topK){
		
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
	
	
	public float[][] IPCC(float[][] originalMatrix, float[][] removedMatrix, float[] imean, int topK) {
		float[][] predictedMatrix = new float[removedMatrix.length][removedMatrix[0].length];
		float[][] removedMatrixT = UtilityFunctions.matrixTransfer(removedMatrix);
		
		for (int j = 0; j < originalMatrix[0].length; j++) {
 
			HashMap pcc = new HashMap();
			//get similar services of service j
			for (int i = 0; i < originalMatrix[0].length; i++) {
				// the same user. 
				if(j == i) continue;
				
				// the user has no ratings, no similarity computation. 
				if(imean[i] == -2 || imean[j] == -2) continue; 
				
				double pccValue = 0;
				
				pccValue = getPCC(removedMatrixT[j], removedMatrixT[i], imean[j], imean[i]);

				
				// find similar users, 
				if(pccValue>0)	pcc.put(i, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			
			 
			for (int i = 0; i < originalMatrix.length; i++) {
				// not removed entry, no need to predict. 
				if(removedMatrix[i][j] != -2) continue; 
				
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
					if(removedMatrix[i][serviceID] == -2 || removedMatrix[i][serviceID] == -1) continue;
					
										
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


}
