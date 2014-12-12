import java.math.BigDecimal;
import java.sql.Time;
import java.text.DecimalFormat;
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
	private ArrayList<Integer> unRUL = new ArrayList<Integer>();
	
	public double[] runUICluster(float[][] originalMatrix, float[][] randomedMatrix,
			float density, float random,int userNumber, int itemNumber, int K1){		
		
		double mae_rmse_3method[] = new double[2];	
		float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
		float[][] predictedMatrixUCluster = UserCluser(originalMatrix, randomedMatrix, K1);
//		UtilityFunctions.writeMatrix(predictedMatrixUCluster, "RMSEResult/predicted/predictedMatrixUCluster.txt");

		double mae_ucluster = MAE(originalMatrix, randomedMatrix, predictedMatrixUCluster);
		double allnmae_ucluster = allNMAE(originalMatrix, randomedMatrix, predictedMatrixUCluster);
		double nmae_ucluster = NMAE(mae_ucluster, allnmae_ucluster);

		mae_rmse_3method[0] = mae_ucluster;
		mae_rmse_3method[1] = nmae_ucluster;	
		return mae_rmse_3method;
	}
	
	public float[][] UserCluser(float[][] originalMatrix, float[][] randomedMatrix, int K){
		
		int userNumber = originalMatrix.length;
		int itemNumber = originalMatrix[0].length;
		float[] itemRtList;
		ArrayList<Integer> unreliableUser = new ArrayList<Integer>();
		int[] userCount=new int[userNumber];
		
		
		ArrayList<UserSetInUser> userSetInUserList = new ArrayList<UserSetInUser>();
		ArrayList<UserSetInItem> userSetInItemList = new ArrayList<UserSetInItem>();
		ArrayList<SimUserSet> simUserSetList = new ArrayList<SimUserSet>();
		
		for(int i=0; i<userNumber; i++){
			userSetInUserList.add(i, new UserSetInUser(i));
		}
		
		for(int i=0; i<itemNumber; i++){
			userSetInItemList.add(i, new UserSetInItem(i));
		}
		
		
		for(int itemNo=0; itemNo<itemNumber; itemNo++){			
			ArrayList<UserSet> userSetsInOneItem = new ArrayList<UserSet>();
			itemRtList=getItemRtList(itemNo, randomedMatrix, userNumber);
			if(userGreaterK(itemRtList,K)){
				KMeans kMeans = new KMeans(itemRtList,K);
				kMeans.cluster();
				userSetsInOneItem = kMeans.buildUserSet(itemNo);
				
				userSetInItemList.set(itemNo, new UserSetInItem(itemNo,userSetsInOneItem));				
				for(int i=0; i<userSetsInOneItem.size(); i++){
					UserSet aUserSet = userSetsInOneItem.get(i);
					ArrayList<Integer> usersInUserSet = new ArrayList<Integer>(); //store the userno in userSet
					usersInUserSet = aUserSet.getUserNoList();
					for(int u=0; u<usersInUserSet.size(); u++){
						int userno = usersInUserSet.get(u);
						UserSetInUser temUser = userSetInUserList.get(userno);
						temUser.addUserSetInUserList(aUserSet);
						userSetInUserList.set(userno, temUser);
					}
				}

				if(kMeans.getClustersNum()!=0){
					unreliableUser = kMeans.getUnreliableUserList();
//					if(unreliableUser.size()>0){
						for(int i=0; i<unreliableUser.size();i++){
							int userNo = unreliableUser.get(i);
							userCount[userNo]++;
						}
//						System.out.println(unreliableUser.toString());
						unreliableUser.clear();
//					}
				}
	
			}
			else{
				int c=0;
				for(int t=0; t<itemRtList.length; t++){
					float x = itemRtList[t];
					if(x!=-2&&x!=-1){
						UserSet aUserSet = new UserSet(itemNo,c);
						aUserSet.addUser(t);
						userSetsInOneItem.add(aUserSet);
						c++;
						
						UserSetInUser temUser = userSetInUserList.get(t);
						temUser.addUserSetInUserList(aUserSet);
						userSetInUserList.set(t, temUser);
						
					}
				}
				userSetInItemList.set(itemNo, new UserSetInItem(itemNo,userSetsInOneItem));
			}
		}
		
		buildunRUL(userCount);
		simUserSetList = bulidSimUserSet(userSetInUserList, this.unRUL, userNumber);
		for(int i=0; i<simUserSetList.size(); i++){
			simUserSetList.get(i).sortSimUser();
		}
		
//		 printclustedUserList(simUserSetList);
		
//		 printUserSetInItems(userSetInItemList);
		//outlier has been removed
		randomedMatrix = removeOutlierUser(unRUL,randomedMatrix);
		float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
		float[] umean = UtilityFunctions.getUMean(randomedMatrix);
		float[] imean = UtilityFunctions.getUMean(randomedMatrixT);
		
//		System.out.println("Caculating begin: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];

		for(int i=0; i<userNumber; i++){
			SimUserSet aSimUserSet = simUserSetList.get(i);
			for(int j=0; j<itemNumber; j++){
				ArrayList<Integer> userNoInItem = new ArrayList<Integer>(); //get the users who invoke the service
				for(int index=0;index<userNumber;index++){
					if(randomedMatrix[index][j]!=-1&&randomedMatrix[index][j]!=-2&&randomedMatrix[index][j]!=-3){
						userNoInItem.add(index);
					}
				}
				UserSetInItem aUserSetInItem = userSetInItemList.get(j); //get userSets in item j 
				int simFlag =0; //flag for simuser
				if(originalMatrix[i][j]==-1){  //no value in originalMatrix
					predictedMatrix[i][j]=-1;
					continue;
				}
			
				else if(randomedMatrix[i][j]==-3){ //outlier
					predictedMatrix[i][j] = imean[j];
					continue;
				}
				//original
				else if(randomedMatrix[i][j]==-2){
					int topK=1;
					int simUserClusterCount=0;
					float allClusterMean=0;
					for(int u=0;u<aSimUserSet.getSimUserList().size();u++){
						SimUser aSimUser = aSimUserSet.getSimUser(u);
						if(userNoInItem.contains(aSimUser.getUserNo())){ //simuser invoke the item
							UserSet aUserSet = aUserSetInItem.getUserSet(aSimUser.getUserNo());
							ArrayList<Integer> clustedUserNoList = aUserSet.getUserNoList();
							float clusterMean=0;
							int simUserInAClusterCount=0;
							for(int a=0; a<clustedUserNoList.size(); a++){
								if(!unRUL.contains(clustedUserNoList.get(a))){
									clusterMean += randomedMatrix[clustedUserNoList.get(a)][j];
									simUserInAClusterCount++;
								}
							}
							if(simUserInAClusterCount!=0){
								simUserClusterCount++;
								simFlag=1;
								allClusterMean+=clusterMean/simUserInAClusterCount; //get the cluster Mean of the cluster
							}
						}
						
						if(simUserClusterCount==topK){
							predictedMatrix[i][j] = allClusterMean/topK;
							break;
						}
						
						if(u==aSimUserSet.getSimUserList().size()-1&&simUserClusterCount<topK&&simUserClusterCount!=0){
							predictedMatrix[i][j] = allClusterMean/simUserClusterCount;
						}
					}
					if(simUserClusterCount==0){
						if(umean[i]!=-2){
							predictedMatrix[i][j] = umean[i]; //no simUser, use UMEAN
						}
						else if(imean[j]!=-2){
							predictedMatrix[i][j] = imean[j];
						}
						else
							predictedMatrix[i][j] = -2;
						
					}
					continue;
				}
				else{
					predictedMatrix[i][j]=randomedMatrix[i][j];
				}
			}
		}
		return  predictedMatrix;
		
	}
	
	public float[] getItemRtList(int itemNo, float[][] randomedMatrix, int userNumber){
		float[] itemRtList = new float[userNumber];
		for(int i=0; i<userNumber; i++){
			itemRtList[i]=randomedMatrix[i][itemNo];
		}
		return itemRtList;
	}
	
	public Boolean userGreaterK(float[] URR, int K){
		int count = 0;
		for (int t=0;t<URR.length;t++){
			double x = URR[t];
			if(x!=-2&&x!=-1){
				count++;
			}
		}
		if(count>=K){
			return true;
		}
		else{
			return false;
		}
			
	}
	
	public ArrayList<SimUserSet> bulidSimUserSet (ArrayList<UserSetInUser> userList, ArrayList<Integer> unreliablelist,int userNumber){
		ArrayList<SimUserSet> simUserSetList = new ArrayList<SimUserSet>();
		for(int i=0; i<userNumber; i++){
			UserSetInUser aUser = userList.get(i);
			SimUserSet newClustedUser;
			//for outlier
			if(unreliablelist.contains(aUser.getUserNo())){
				newClustedUser = new SimUserSet(aUser.getUserNo(),1);
			}
			else{
				newClustedUser = new SimUserSet(aUser,unreliablelist);
			}
			simUserSetList.add(newClustedUser);
		}
		return simUserSetList;
	}
	
	public void buildunRUL(int[] userCount){
		KMeans kMeans2 = new KMeans(userCount);
		kMeans2.cluster();
		this.unRUL=kMeans2.getUnreliableUserList();
		System.out.println(unRUL.toString());
	}
	

	public float[][] removeOutlierUser(ArrayList<Integer> unreliableUserList,float[][] randomedMatrix){
		//mark the unreliable user from the randomedMatrix to -3
		float[][] resultMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
		UtilityFunctions.copyMatrix(randomedMatrix, resultMatrix);
		for(int i=0; i<unreliableUserList.size(); i++){
			int userno=unreliableUserList.get(i);
			for(int j=0; j<resultMatrix[userno].length;j++)
				resultMatrix[userno][j]=-3; // outlier
		}
		return resultMatrix;
	}
	
	public float[][] removeOutlierItem(ArrayList<Integer> unreliableUserList,float[][] randomedMatrix){
		//mark the unreliable user from the randomedMatrix to -3
		float[][] resultMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
		UtilityFunctions.copyMatrix(randomedMatrix, resultMatrix);
		for(int i=0; i<unreliableUserList.size(); i++){
			int userno=unreliableUserList.get(i);
			for(int j=0; j<resultMatrix.length;j++)
				resultMatrix[j][userno]=-3; // outlier
		}
		return resultMatrix;
	}
	
	public void printUserSetInItems(ArrayList<UserSetInItem> userSetInItemList){
		for(int i = 0;i < 1;i++)
//		for(int i = 0;i < simUserSetList.size();i++)
		{
			UserSetInItem tempUserSetInItem = userSetInItemList.get(i);
			ArrayList<UserSet> tempUserSets = tempUserSetInItem.getUserSets();
			System.out.println("new:");
			if(tempUserSets.isEmpty()){
				System.out.println("item "+ i +" is an outliser");
			}
			else{
				System.out.println("item "+i+" has "+tempUserSets.size() +" simuers");

			}
			for(int s =0; s<tempUserSets.size(); s++){
				UserSet aUserSet = tempUserSets.get(s);
				System.out.println("UserSet:"+ aUserSet.getClusterNo()+ " has users:"+aUserSet.getUserNoList());
			}
		}
	}
	
	public void printclustedUserList(ArrayList<SimUserSet> simUserSetList){
		for(int i = 0;i < 1;i++)
//		for(int i = 0;i < simUserSetList.size();i++)
		{
			SimUserSet tempUser = simUserSetList.get(i);
			ArrayList<SimUser> tempSimUserlist = tempUser.getSimUserList();
			System.out.println("new:");
			if(tempSimUserlist.isEmpty()){
				System.out.println("user "+ tempUser.getUserNo() +" is an outliser");
			}
			else{
				System.out.println("user "+ tempUser.getUserNo() +" has "+tempSimUserlist.size() +" simuers");

			}
			for(int s =0; s<tempSimUserlist.size(); s++){
				SimUser aSimUser = tempSimUserlist.get(s);
				System.out.println("simuser:"+ aSimUser.getUserNo() + " has "+aSimUser.getCount()+" times");
			}
		}
	}
	
	
	public double MAE(float[][] originalMatrix, float[][] randomedMatrix ,float[][] predictedMatrix){
		double allMAE = 0;
		float allMAEMatrix[][] = new float[originalMatrix.length][originalMatrix[0].length];
		double number = 0;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if((randomedMatrix[i][j] == -2 && originalMatrix[i][j] != -1 && predictedMatrix[i][j] != -2)||(randomedMatrix[i][j] == -3 && originalMatrix[i][j] != -1 && predictedMatrix[i][j] != -2)) {
					allMAEMatrix[i][j] = Math.abs(predictedMatrix[i][j] - originalMatrix[i][j]);
					allMAE += allMAEMatrix[i][j];
					number ++;
				}
			}
		}
		UtilityFunctions.writeMatrix(allMAEMatrix, "RMSEResult/mae_ucluster.txt");
		return allMAE/number;
	}
	
	public double RMSE(float[][] originalMatrix, float[][] randomedMatrix ,float[][] predictedMatrix){
		float allRMSE = 0;
		float allRMSEMatrix[][] = new float[originalMatrix.length][originalMatrix[0].length];
		double number = 0;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if((randomedMatrix[i][j] == -2 && originalMatrix[i][j] != -1 && predictedMatrix[i][j] != -2)||(randomedMatrix[i][j] == -3 && originalMatrix[i][j] != -1 && predictedMatrix[i][j] != -2)) {
					float f =(predictedMatrix[i][j] - originalMatrix[i][j])*(predictedMatrix[i][j] - originalMatrix[i][j]);
					allRMSEMatrix[i][j] = f;
					allRMSE += allRMSEMatrix[i][j];
					number ++;
				}
			}
		}
//		UtilityFunctions.writeMatrix(allRMSEMatrix, "RMSEResult/rmse_ucluster.txt");
		return Math.sqrt(allRMSE/number);
	}
	
	public double allNMAE(float[][] originalMatrix, float[][] randomedMatrix ,float[][] predictedMatrix){
		double number = 0;
		double allNMAE = 0;
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				if(originalMatrix[i][j] > 0 ) {
					allNMAE += originalMatrix[i][j];
					number ++;
				}
			}
		}
		allNMAE=allNMAE/number;
		System.out.println("allNMAE:"+allNMAE);

		return allNMAE;
	}
	
	public double NMAE(double mae, double allnmae){
		return mae/allnmae;
	}
	
	
	
	
	
	
	
	
	
	public double[] runUIPCC(float[][] originalMatrix, float[][] randomedMatrix, float density, int topK){
		double mae_rmse_3method[] = new double[6];	
		float[][] originalMatrixT = UtilityFunctions.matrixTransfer(originalMatrix);
		float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
		
		float[] umean = UtilityFunctions.getUMean(randomedMatrix);
		float[] imean = UtilityFunctions.getUMean(randomedMatrixT);
		
		double[] mae_uipcc = new double[11]; 
//		double[] nmae_uipcc = new double[11]; 
		double[] rmse_uipcc = new double[11];
		
		float[][] predictedMatrixUPCC = UPCC(originalMatrix, randomedMatrix, umean, topK);
		float[][] predictedMatrixIPCC = IPCC(originalMatrix, randomedMatrix, imean, topK);
		
//		UtilityFunctions.writeMatrix(predictedMatrixUPCC, "RMSEResult/predicted/d"+density+"upcc.txt");
//		UtilityFunctions.writeMatrix(predictedMatrixIPCC, "RMSEResult/predicted/d"+density+"ipcc.txt");
		
		float[][] predictedMatrixIPCCT = UtilityFunctions.matrixTransfer(predictedMatrixIPCC);
		double mae_upcc = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixUPCC);
		double mae_ipcc = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixIPCC);
//		double allNMAE = UtilityFunctions.allNMAE(originalMatrix, randomedMatrix, predictedMatrixIPCC);
//		
//		double nmae_upcc = UtilityFunctions.NMAE(mae_upcc,allNMAE);
//		double nmae_ipcc = UtilityFunctions.NMAE(mae_ipcc,allNMAE);
		
		double rmse_upcc = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixUPCC);
		double rmse_ipcc = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixIPCC);
		mae_uipcc = new double[11]; 
//		nmae_uipcc = new double[11]; 
		rmse_uipcc = new double[11]; 
		for (int i = 0; i < 11; i++) {
			double mae =0;
			double nmae =0;
			//对lambda值从0到1进行尝试，选择效果最好的作为最终结果
			double lambda2 = (double)i/10.0;
			float[][] predictedMatrixURR_UIPCC = UIPCC(predictedMatrixUPCC, predictedMatrixIPCCT, lambda2);
//			UtilityFunctions.writeMatrix(predictedMatrixURR_UIPCC, "RMSEResult/predicted/UIPCC"+i+".txt");
			mae = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
//			nmae = UtilityFunctions.NMAE(mae,allNMAE);
			mae_uipcc[i] =  mae;
//			nmae_uipcc[i] =  nmae;
			rmse_uipcc[i] = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
		}

		double smallMAE = 100;
		double smallRMSE = 100;
//		double smallNMAE = 100;
		for (int i = 0; i < 11; i++) {
			if(mae_uipcc[i] < smallMAE) smallMAE = mae_uipcc[i];
//			if(nmae_uipcc[i] < smallNMAE) smallNMAE = nmae_uipcc[i];
			if(rmse_uipcc[i] < smallRMSE){
				smallRMSE = rmse_uipcc[i];
//				System.out.println(i + " is better");
			}
		}		
//		UtilityFunctions.writeFile("UIPCCresult.txt", "UIPCC:\t" + smallMAE + "\t" + smallRMSE + "\r\n");
		mae_rmse_3method[0] = mae_upcc;
		mae_rmse_3method[1] = mae_ipcc;
		mae_rmse_3method[2] = smallMAE;
//		
//		mae_rmse_3method[3] = nmae_upcc;
//		mae_rmse_3method[4] = nmae_ipcc;
//		mae_rmse_3method[5] = smallNMAE;
		
		mae_rmse_3method[3] = rmse_upcc;
		mae_rmse_3method[4] = rmse_ipcc;
		mae_rmse_3method[5] = smallRMSE;
		
		return mae_rmse_3method;
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
	
	public float[][] UPCC(float[][] originalMatrix, float[][] randomedMatrix, float[] umean, int topK){
		
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
				
				
//				if(predictedValue >= 1) predictedValue = 1;
				
//				System.out.println(predictedValue + "\t" + valueMatrix[i][j]);
				
				predictedMatrix[i][j] = (float)predictedValue;
			}
		}
		return predictedMatrix;
	}
	
	
	public float[][] IPCC(float[][] originalMatrix, float[][] randomedMatrix, float[] imean, int topK) {
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
				
				pccValue = getPCC(randomedMatrixT[j], randomedMatrixT[i], imean[j], imean[i]);

				
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
