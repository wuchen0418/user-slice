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
		float[][] removedMatrix;
		float[][] randomedMatrix;
		
		float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", this.userNumber, this.itemNumber);
		float density = (float)0.1;
		float random = (float)0.015;
		
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
		for(int i=0; i<this.userNumber; i++){
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
			System.out.println("original:");
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
			System.out.println("original:");
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
		int loopNum = 20;
		for(int count=0; count<loopNum; count++){
			String prefix = "WSDream-QoSDataset2/";
			String matrix = "rtMatrix";
			process tester= new process();
			tester.preProcess();
			int userNumber = 339; 
			int itemNumber = 5825;
			int K1=7;
			float[][] randomedMatrix;

			float density = (float)0.1;
			float random = (float)0.015;
			
			randomedMatrix = UtilityFunctions.readMatrix("randomed/" + matrix + density + "_" + random, userNumber, itemNumber);
			
			float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", userNumber, itemNumber);		
			Prediction prediction = new Prediction();
			Predictor predictor = new Predictor();
//			String userLoactionFileName = "userlist.txt";
//			predictor.initUserLocationMap(prefix + userLoactionFileName);
			double[] mae_rmse_uicluster = prediction.runUICluster(originalMatrix, randomedMatrix, density, random, userNumber, itemNumber, K1);
//			double[] mae_rmse_3method = prediction.runUIPCC(originalMatrix, randomedMatrix, density, 34);
			double[][] mae_rmse_rap = predictor.run8Methods(originalMatrix, randomedMatrix, random, 34, density, (float)0.1);
			mae_rmse_rap = UtilityFunctions.matrixTransfer(mae_rmse_rap);
			
			System.arraycopy(mae_rmse_rap[0], 0, mae_rmse_4method[count], 0, 4);
			System.arraycopy(mae_rmse_rap[1], 0, mae_rmse_4method[count], 5, 4);
			System.arraycopy(mae_rmse_uicluster, 0, mae_rmse_4method[count], 4, 1);
			System.arraycopy(mae_rmse_uicluster, 1, mae_rmse_4method[count], 9, 1);

			
			System.out.println(count+": "+"mae__rmse_4method = \t"+mae_rmse_4method[count][0]+"\t"+mae_rmse_4method[count][1]+"\t"+mae_rmse_4method[count][2]+"\t"+mae_rmse_4method[count][3]
					+"\t"+mae_rmse_4method[count][4]+"\t"+mae_rmse_4method[count][5]+"\t"+mae_rmse_4method[count][6]+"\t"+mae_rmse_4method[count][7]+"\t"+mae_rmse_4method[count][8]+"\t"+mae_rmse_4method[count][9]);
		}
		double mae_upcc_mean = 0;
		double mae_ipcc_mean = 0;
		double mae_uipcc_mean = 0;
		double mae_rap_mean=0;
		double mae_ucluster_mean = 0;
		
		double rmse_upcc_mean = 0;
		double rmse_ipcc_mean = 0;
		double rmse_uipcc_mean = 0;
		double rmse_rap_mean = 0;
		double rmse_ucluster_mean = 0;
		
		for(int t=0; t<20; t++){
			mae_upcc_mean += mae_rmse_4method[t][0];
			mae_ipcc_mean += mae_rmse_4method[t][1];
			mae_uipcc_mean += mae_rmse_4method[t][2];
			mae_rap_mean += mae_rmse_4method[t][3];
			mae_ucluster_mean += mae_rmse_4method[t][4];
			
			rmse_upcc_mean += mae_rmse_4method[t][5];
			rmse_ipcc_mean += mae_rmse_4method[t][6];
			rmse_uipcc_mean += mae_rmse_4method[t][7];
			rmse_rap_mean += mae_rmse_4method[t][8];
			rmse_ucluster_mean += mae_rmse_4method[t][9];
		}
		mae_upcc_mean = mae_upcc_mean/loopNum;
		mae_ipcc_mean = mae_ipcc_mean/loopNum;
		mae_uipcc_mean = mae_uipcc_mean/loopNum;
		mae_rap_mean = mae_rap_mean/loopNum;
		mae_ucluster_mean = mae_ucluster_mean/loopNum;
		
		rmse_upcc_mean = rmse_upcc_mean/loopNum;
		rmse_ipcc_mean = rmse_ipcc_mean/loopNum;
		rmse_uipcc_mean = rmse_uipcc_mean/loopNum;
		rmse_rap_mean = rmse_rap_mean/loopNum;
		rmse_ucluster_mean = rmse_ucluster_mean/loopNum;
		
		mae_rmse_4method[20][0]=mae_upcc_mean;
		mae_rmse_4method[20][1]=mae_ipcc_mean;
		mae_rmse_4method[20][2]=mae_uipcc_mean;
		mae_rmse_4method[20][3]=mae_rap_mean;
		mae_rmse_4method[20][4]=mae_ucluster_mean;
		
		mae_rmse_4method[20][5] = rmse_upcc_mean;
		mae_rmse_4method[20][6] = rmse_ipcc_mean;
		mae_rmse_4method[20][7] = rmse_uipcc_mean;
		mae_rmse_4method[20][8] = rmse_rap_mean;
		mae_rmse_4method[20][9] = rmse_ucluster_mean;

		System.out.println("mae__rmse_4method(mean) = \t"+mae_rmse_4method[20][0]+"\t"+mae_rmse_4method[20][1]+"\t"+mae_rmse_4method[20][2]+"\t"+mae_rmse_4method[20][3]
				+"\t"+mae_rmse_4method[20][4]+"\t"+mae_rmse_4method[20][5]+"\t"+mae_rmse_4method[20][6]+"\t"+mae_rmse_4method[20][7]+"\t"+mae_rmse_4method[20][8]+"\t"+mae_rmse_4method[20][9]);
	}
}
