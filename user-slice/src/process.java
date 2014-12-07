import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


public class process {

	private ArrayList<UserSetInUser> userSetInUserList = new ArrayList<UserSetInUser>();
	private ArrayList<UserSetInItem> userSetInItemList = new ArrayList<UserSetInItem>();
	private ArrayList<SimUserSet> simUserSetList = new ArrayList<SimUserSet>();
	private int userNumber = 339;
	private int itemNumber = 5825;

	public void preProcess() {
		String prefix = "WSDream-QoSDataset2/";
		String matrix = "rtMatrix"; 
//		int userNumber = 339; 
//		int itemNumber = 5825;
		float[][] removedMatrix;
		float[][] randomedMatrix;
		
		float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", this.userNumber, this.itemNumber);
		float density = (float)0.1;
		float random = (float)0.03;
		
		removedMatrix = UtilityFunctions.removeEntry(originalMatrix, density, "randomed//" + matrix + "30");			
		randomedMatrix = UtilityFunctions.randomEntry(removedMatrix, random);
		UtilityFunctions.writeMatrix(randomedMatrix, "randomed/"+ matrix + density + "_" + random);
	}
	
	public float[] getItemRtList(int itemNo, float[][] randomedMatrix, int userNumber){
		float[] itemRtList = new float[userNumber];
		for(int i=0; i<userNumber; i++){
			itemRtList[i]=randomedMatrix[i][itemNo];
		}
		return itemRtList;
	}
	
	public int[] sortIndex(int[] userCount){
		int[] indexSorted = new int[this.userNumber];
		int temp=0;
		for(int i=0; i<this.userNumber; i++){
			indexSorted[i]=i;
		}
		
		for(int i=0; i<this.userNumber; i++)
			for(int j=0; j<this.userNumber-1; j++){
				if(userCount[j]<userCount[j+1]){
					temp=userCount[j];
					userCount[j]=userCount[j+1];
					userCount[j+1]=temp;
					
					temp=indexSorted[j];
					indexSorted[j]=indexSorted[j+1];
					indexSorted[j+1]=temp;
				}
			}
		return indexSorted;
	}
	
	public void buildUserSetInUserList(int userNum){
		for(int i=0; i<userNum; i++){
			userSetInUserList.add(i, new UserSetInUser(i));
		}
	}
	
	public void buildUserSetInItemList(int itemNum){
		for(int i=0; i<itemNum; i++){
			userSetInItemList.add(i, new UserSetInItem(i));
		}
	}
	
	public void printUserClustered(){
		for(int i = 0;i < this.userNumber;i++)
		{
			UserSetInUser temUser = userSetInUserList.get(i);
			ArrayList<UserSet> tempUserSet = temUser.getUserSetInUserList();
			System.out.println("user "+ i +"has "+tempUserSet.size() +" userSets:"+tempUserSet.toString());
		}
	}
	
	public void writeUser(String fileName){
		try {
            File file = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String line = "";
            
    		for(int i = 0;i < this.userNumber;i++)
    		{
    			UserSetInUser temUser = userSetInUserList.get(i);
    			ArrayList<UserSet> tempUserSet = temUser.getUserSetInUserList();
    			line += "user "+ i +" has "+tempUserSet.size() +" userSets:"+tempUserSet.toString();
    			writer.write(line);
    			writer.newLine();
    			line = "";
    		}
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
	
	public void bulidSimUserSet (ArrayList<UserSetInUser> userList, ArrayList<Integer> unreliablelist){
		for(int i=0; i<this.itemNumber; i++){
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
	}
	
	public void printclustedUserList(){
		for(int i = 0;i < 1;i++)
//		for(int i = 0;i < simUserSetList.size();i++)
		{
			SimUserSet tempUser = simUserSetList.get(i);
			ArrayList<SimUser> tempSimUserlist = tempUser.getSimUserList();
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
	
	public void printUserSetInItems(){
		for(int i = 0;i < 1;i++)
//		for(int i = 0;i < simUserSetList.size();i++)
		{
			UserSetInItem tempUserSetInItem = userSetInItemList.get(i);
			ArrayList<UserSet> tempUserSets = tempUserSetInItem.getUserSets();
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
	
	public static void main(String[] args) {
		double mae_rmse_4method[][] = new double[21][12];
		int loopNum = 1;
		for(int count=0; count<loopNum; count++){
			String prefix = "WSDream-QoSDataset2/";
			String matrix = "rtMatrix";
			process tester= new process();
			tester.preProcess();
			int userNumber = 339; 
			int itemNumber = 5825;
			int K=7;
			int K2=20;
			float[][] randomedMatrix;
			float[] itemRtList;
			ArrayList<Integer> unreliableUser = new ArrayList<Integer>();
			int[] userCount=new int[userNumber];
			int[] indexSorted = new int[userNumber];
			
			float density = (float)0.1;
			float random = (float)0.03;
			
			randomedMatrix = UtilityFunctions.readMatrix("randomed/" + matrix + density + "_" + random, userNumber, itemNumber);

			
			for(int itemNo=0; itemNo<itemNumber; itemNo++){			
				itemRtList=tester.getItemRtList(itemNo, randomedMatrix, userNumber);
				if(tester.userGreaterK(itemRtList,K)){
					KMeans kMeans = new KMeans(itemRtList,K);
					kMeans.cluster();
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
			}
	
			KMeans kMeans2 = new KMeans(userCount);
			kMeans2.cluster();
			ArrayList<Integer> unRUL=kMeans2.getUnreliableUserList();
	//		kMeans2.printPoints();
	//		kMeans2.printClusters();
	//		kMeans2.printBelongs();
			System.out.println(unRUL.toString());
			
			int userNumberT = 5825; 
			int itemNumberT = 339;
			
			float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
			tester.buildUserSetInUserList(userNumberT);
			tester.buildUserSetInItemList(itemNumberT);
			
			for(int itemNo=0; itemNo<itemNumberT; itemNo++){			
				ArrayList<UserSet> userSetsInOneItem = new ArrayList<UserSet>();
				itemRtList=tester.getItemRtList(itemNo, randomedMatrixT, userNumberT);
				if(tester.userGreaterK(itemRtList,K2)){
					KMeans kMeans = new KMeans(itemRtList,K2);
					kMeans.cluster();
					userSetsInOneItem = kMeans.buildUserSet(itemNo);
					
					tester.userSetInItemList.set(itemNo, new UserSetInItem(itemNo,userSetsInOneItem));				
					for(int i=0; i<userSetsInOneItem.size(); i++){
						UserSet aUserSet = userSetsInOneItem.get(i);
						ArrayList<Integer> usersInUserSet = new ArrayList<Integer>(); //store the userno in userSet
						usersInUserSet = aUserSet.getUserNoList();
						for(int u=0; u<usersInUserSet.size(); u++){
							int userno = usersInUserSet.get(u);
							UserSetInUser temUser = tester.userSetInUserList.get(userno);
							temUser.addUserSetInUserList(aUserSet);
							tester.userSetInUserList.set(userno, temUser);
						}
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
							
							UserSetInUser temUser = tester.userSetInUserList.get(t);
							temUser.addUserSetInUserList(aUserSet);
							tester.userSetInUserList.set(t, temUser);
							
						}
					}
					tester.userSetInItemList.set(itemNo, new UserSetInItem(itemNo,userSetsInOneItem));
				}
			}
			
	//		System.out.println();
	//		System.out.println("sorted");
			
//			indexSorted = tester.sortIndex(userCount);
	//		for(int i=0; i<userNumber; i++){
	//			System.out.println("No."+i+" index="+indexSorted[i]+" countNum="+userCount[i]+"\t");
	//		}
	//		tester.printUserSetInItems();
	//		tester.writeUser("userClusted.txt");
			tester.bulidSimUserSet(tester.userSetInUserList, unRUL);
			for(int i=0; i<tester.simUserSetList.size(); i++){
				tester.simUserSetList.get(i).sortSimUser();
				
			}
//			simUserSetList.get(163).printSimUser();
	//		tester.printclustedUserList();
			
			float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", userNumber, itemNumber);
			float[][] originalMatrixT = UtilityFunctions.matrixTransfer(originalMatrix);
			
			
			Prediction prediction = new Prediction();
//			Predictor predictor = new Predictor();

			double[] mae_rmse_cluster = prediction.cluserMean(originalMatrixT, randomedMatrixT, density, random, userNumberT, itemNumberT, unRUL, tester.simUserSetList, tester.userSetInItemList);
//			double[] mae_rmse_3method = prediction.runUIPCC(originalMatrix, randomedMatrix, density, 34);
//			double[][] mae_rmse_rap = predictor.run8Methods(originalMatrix, randomedMatrix, random, 34, density, (float)0.1);
//			double mae_rmse_rap2[] = new double[4];
			
//			mae_rmse_rap2[0] = mae_rmse_rap[0][0];
//			mae_rmse_rap2[1] = mae_rmse_rap[0][1];
//			mae_rmse_rap2[2] = mae_rmse_rap[1][0];
//			mae_rmse_rap2[3] = mae_rmse_rap[1][1];
//			
//			System.arraycopy(mae_rmse_3method, 0, mae_rmse_4method[count], 0, 3);
//			System.arraycopy(mae_rmse_3method, 3, mae_rmse_4method[count], 4, 3);
			System.arraycopy(mae_rmse_cluster, 0, mae_rmse_4method[count], 3, 1);
			System.arraycopy(mae_rmse_cluster, 1, mae_rmse_4method[count], 7, 1);
//			System.arraycopy(mae_rmse_rap2, 0, mae_rmse_4method[count], 8, 2);
//			System.arraycopy(mae_rmse_rap2, 2, mae_rmse_4method[count], 10, 2);
			
			System.out.println(count+": "+"mae__rmse_4method = \t"+mae_rmse_4method[count][0]+"\t"+mae_rmse_4method[count][1]+"\t"+mae_rmse_4method[count][2]+"\t"+mae_rmse_4method[count][3]
					+"\t"+mae_rmse_4method[count][4]+"\t"+mae_rmse_4method[count][5]+"\t"+mae_rmse_4method[count][6]+"\t"+mae_rmse_4method[count][7]+"\t"+mae_rmse_4method[count][8]+"\t"+mae_rmse_4method[count][9]+"\t"+mae_rmse_4method[count][10]+"\t"+mae_rmse_4method[count][11]);
		}
		double mae_upcc_mean = 0;
		double mae_ipcc_mean = 0;
		double mae_uipcc_mean = 0;
		double mae_cluster_mean = 0;
		double mae_uipcc2_mean=0;
		double mae_rap_mean=0;
		
		double rmse_upcc_mean = 0;
		double rmse_ipcc_mean = 0;
		double rmse_uipcc_mean = 0;
		double rmse_cluster_mean = 0;
		double rmse_uipcc2_mean = 0;
		double rmse_rap_mean = 0;
		
		for(int t=0; t<20; t++){
			mae_upcc_mean += mae_rmse_4method[t][0];
			mae_ipcc_mean += mae_rmse_4method[t][1];
			mae_uipcc_mean += mae_rmse_4method[t][2];
			mae_cluster_mean += mae_rmse_4method[t][3];
			
			rmse_upcc_mean += mae_rmse_4method[t][4];
			rmse_ipcc_mean += mae_rmse_4method[t][5];
			rmse_uipcc_mean += mae_rmse_4method[t][6];
			rmse_cluster_mean += mae_rmse_4method[t][7];
			
			mae_uipcc2_mean += mae_rmse_4method[t][8];
			mae_rap_mean += mae_rmse_4method[t][10];
			
			rmse_uipcc2_mean += mae_rmse_4method[t][9];
			rmse_rap_mean += mae_rmse_4method[t][11];
		}
		mae_upcc_mean = mae_upcc_mean/loopNum;
		mae_ipcc_mean = mae_ipcc_mean/loopNum;
		mae_uipcc_mean = mae_uipcc_mean/loopNum;
		mae_cluster_mean = mae_cluster_mean/loopNum;
		
		rmse_upcc_mean = rmse_upcc_mean/loopNum;
		rmse_ipcc_mean = rmse_ipcc_mean/loopNum;
		rmse_uipcc_mean = rmse_uipcc_mean/loopNum;
		rmse_cluster_mean = rmse_cluster_mean/loopNum;
		
		mae_uipcc2_mean = mae_uipcc2_mean/loopNum;
		mae_rap_mean = mae_rap_mean/loopNum;
		rmse_uipcc2_mean = rmse_uipcc2_mean/loopNum;
		rmse_rap_mean = rmse_rap_mean/loopNum;
		
		mae_rmse_4method[20][0]=mae_upcc_mean;
		mae_rmse_4method[20][1]=mae_ipcc_mean;
		mae_rmse_4method[20][2]=mae_uipcc_mean;
		mae_rmse_4method[20][3]=mae_cluster_mean;
		
		mae_rmse_4method[20][4] = rmse_upcc_mean;
		mae_rmse_4method[20][5] = rmse_ipcc_mean;
		mae_rmse_4method[20][6] = rmse_uipcc_mean;
		mae_rmse_4method[20][7] = rmse_cluster_mean;
		
		mae_rmse_4method[20][8]=mae_uipcc2_mean;
		mae_rmse_4method[20][10]=mae_rap_mean;
		
		mae_rmse_4method[20][9] = rmse_uipcc2_mean;
		mae_rmse_4method[20][11] = rmse_rap_mean;
		
		
		
		System.out.println("mae__rmse_4method(mean) = \t"+mae_rmse_4method[20][0]+"\t"+mae_rmse_4method[20][1]+"\t"+mae_rmse_4method[20][2]+"\t"+mae_rmse_4method[20][3]
				+"\t"+mae_rmse_4method[20][4]+"\t"+mae_rmse_4method[20][5]+"\t"+mae_rmse_4method[20][6]+"\t"+mae_rmse_4method[20][7]+"\t"+mae_rmse_4method[20][8]+"\t"+mae_rmse_4method[20][9]+"\t"+mae_rmse_4method[20][10]+"\t"+mae_rmse_4method[20][11]);
	}
}
