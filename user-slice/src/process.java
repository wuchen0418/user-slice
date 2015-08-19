import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;


public class process {

	private ArrayList<UserSetInUser> userSetInUserList = new ArrayList<UserSetInUser>();
	private ArrayList<UserSetInItem> userSetInItemList = new ArrayList<UserSetInItem>();
	private ArrayList<SimUserSet> simUserSetList = new ArrayList<SimUserSet>();
	private int userNumber = 339;
	private int itemNumber = 5825;
	
	private int[][] MatrixB = new int[userNumber][userNumber];

	public void preProcess(float density, float random) {
		String prefix = "WSDream-QoSDataset2/";
		String matrix = "tpMatrix"; 
		float[][] removedMatrix;
		float[][] randomedMatrix;
		
		float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", this.userNumber, this.itemNumber);
//		float density = (float)0.1;
//		float random = (float)0.03;
		
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
	
	public void main_ori(int count,float density, float random, int K1, int TK1, int TK2) {
		double mae_rmse_4method[][] = new double[1][12];
//		int loopNum = 20;
//		for(int count=0; count<loopNum; count++){
			String prefix = "WSDream-QoSDataset2/";
			String matrix = "tpMatrix";
			
			int userNumber = 339; 
			int itemNumber = 5825;
			float[][] randomedMatrix;

//			float density = (float)0.1;
//			float random = (float)0.03;
			
			randomedMatrix = UtilityFunctions.readMatrix("randomed/" + matrix + density + "_" + random, userNumber, itemNumber);
			
			float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", userNumber, itemNumber);		
			Prediction prediction = new Prediction();
			Predictor predictor = new Predictor();
//			String userLoactionFileName = "userlist.txt";
//			predictor.initUserLocationMap(prefix + userLoactionFileName);
//			double[] mae_rmse_uicluster = prediction.runUICluster(originalMatrix, randomedMatrix, density, random, userNumber, itemNumber, K1, TK1, TK2);
			double[][] mae_rmse_rap = predictor.run8Methods(originalMatrix, randomedMatrix, random, 34, density, (float)0.1);
			mae_rmse_rap = UtilityFunctions.matrixTransfer(mae_rmse_rap);

			System.arraycopy(mae_rmse_rap[1], 0, mae_rmse_4method[0], 0, 4);
//			System.arraycopy(mae_rmse_uicluster, 0, mae_rmse_4method[0], 5, 1);
//			System.arraycopy(mae_rmse_uicluster, 1, mae_rmse_4method[0], 6, 1);
//			System.arraycopy(mae_rmse_uicluster, 2, mae_rmse_4method[0], 7, 1);

			
			System.out.println(count+": "+"mae__rmse_4method = \t"+mae_rmse_4method[0][0]+"\t"+mae_rmse_4method[0][1]+"\t"+mae_rmse_4method[0][2]+"\t"+mae_rmse_4method[0][3]
					+"\t"+mae_rmse_4method[0][4]+"\t"+mae_rmse_4method[0][5]+"\t"+mae_rmse_4method[0][6]+"\t"+mae_rmse_4method[0][7]+"\t"+mae_rmse_4method[0][8]+"\t"+mae_rmse_4method[0][9]);
			
			String str = count+": "+"mae__rmse_4method = \t"+mae_rmse_4method[0][0]+"\t"+mae_rmse_4method[0][1]+"\t"+mae_rmse_4method[0][2]+"\t"+mae_rmse_4method[0][3]
					+"\t"+mae_rmse_4method[0][4]+"\t"+mae_rmse_4method[0][5]+"\t"+mae_rmse_4method[0][6]+"\t"+mae_rmse_4method[0][7]+"\t"+mae_rmse_4method[0][8]+"\t"+mae_rmse_4method[0][9];
			UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+str+"\r\n");
		
//		}
//		double mae_upcc_mean = 0;
//		double mae_ipcc_mean = 0;
//		double mae_uipcc_mean = 0;
//		double mae_rap_mean=0;
//		double mae_ucluster_mean = 0;
//		
//		double rmse_upcc_mean = 0;
//		double rmse_ipcc_mean = 0;
//		double rmse_uipcc_mean = 0;
//		double rmse_rap_mean = 0;
//		double rmse_ucluster_mean = 0;
//		
//		for(int t=0; t<20; t++){
//			mae_upcc_mean += mae_rmse_4method[t][0];
//			mae_ipcc_mean += mae_rmse_4method[t][1];
//			mae_uipcc_mean += mae_rmse_4method[t][2];
//			mae_rap_mean += mae_rmse_4method[t][3];
//			mae_ucluster_mean += mae_rmse_4method[t][4];
//			
//			rmse_upcc_mean += mae_rmse_4method[t][5];
//			rmse_ipcc_mean += mae_rmse_4method[t][6];
//			rmse_uipcc_mean += mae_rmse_4method[t][7];
//			rmse_rap_mean += mae_rmse_4method[t][8];
//			rmse_ucluster_mean += mae_rmse_4method[t][9];
//		}
//		mae_upcc_mean = mae_upcc_mean/loopNum;
//		mae_ipcc_mean = mae_ipcc_mean/loopNum;
//		mae_uipcc_mean = mae_uipcc_mean/loopNum;
//		mae_rap_mean = mae_rap_mean/loopNum;
//		mae_ucluster_mean = mae_ucluster_mean/loopNum;
//		
//		rmse_upcc_mean = rmse_upcc_mean/loopNum;
//		rmse_ipcc_mean = rmse_ipcc_mean/loopNum;
//		rmse_uipcc_mean = rmse_uipcc_mean/loopNum;
//		rmse_rap_mean = rmse_rap_mean/loopNum;
//		rmse_ucluster_mean = rmse_ucluster_mean/loopNum;
//		
//		mae_rmse_4method[20][0]=mae_upcc_mean;
//		mae_rmse_4method[20][1]=mae_ipcc_mean;
//		mae_rmse_4method[20][2]=mae_uipcc_mean;
//		mae_rmse_4method[20][3]=mae_rap_mean;
//		mae_rmse_4method[20][4]=mae_ucluster_mean;
//		
//		mae_rmse_4method[20][5] = rmse_upcc_mean;
//		mae_rmse_4method[20][6] = rmse_ipcc_mean;
//		mae_rmse_4method[20][7] = rmse_uipcc_mean;
//		mae_rmse_4method[20][8] = rmse_rap_mean;
//		mae_rmse_4method[20][9] = rmse_ucluster_mean;
//
//		System.out.println("mae__rmse_4method(mean) = \t"+mae_rmse_4method[20][0]+"\t"+mae_rmse_4method[20][1]+"\t"+mae_rmse_4method[20][2]+"\t"+mae_rmse_4method[20][3]
//				+"\t"+mae_rmse_4method[20][4]+"\t"+mae_rmse_4method[20][5]+"\t"+mae_rmse_4method[20][6]+"\t"+mae_rmse_4method[20][7]+"\t"+mae_rmse_4method[20][8]+"\t"+mae_rmse_4method[20][9]);
//		
//		String str = "mae__rmse_4method(mean) = \t"+mae_rmse_4method[20][0]+"\t"+mae_rmse_4method[20][1]+"\t"+mae_rmse_4method[20][2]+"\t"+mae_rmse_4method[20][3]
//				+"\t"+mae_rmse_4method[20][4]+"\t"+mae_rmse_4method[20][5]+"\t"+mae_rmse_4method[20][6]+"\t"+mae_rmse_4method[20][7]+"\t"+mae_rmse_4method[20][8]+"\t"+mae_rmse_4method[20][9];
		
//		UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+str+"\r\n");
		UtilityFunctions.writeFile("logtp/d"+density+"r"+random+"TK2"+TK2+".txt", new Date()+"\t"+str+"\r\n");
	}
	
	public static void main(String[] args) {
		
		ArrayList<Float> densityList= new ArrayList<Float>();
		ArrayList<Float> randomList= new ArrayList<Float>();
		ArrayList<Integer> KList= new ArrayList<Integer>();
		ArrayList<Integer> TK1List= new ArrayList<Integer>();
		ArrayList<Integer> TK2List= new ArrayList<Integer>();
		
		densityList.add((float)0.05);
		densityList.add((float)0.1);
		densityList.add((float)0.15);
		densityList.add((float)0.2);
		densityList.add((float)0.25);
		densityList.add((float)0.3);
		
		randomList.add((float)0.01);
		randomList.add((float)0.015);
		randomList.add((float)0.02);
		randomList.add((float)0.025);
		randomList.add((float)0.03);
		randomList.add((float)0.035);
		randomList.add((float)0.04);
		randomList.add((float)0.045);
		randomList.add((float)0.05);
		
		KList.add(2);
		KList.add(3);
		KList.add(4);
		KList.add(5);
		KList.add(6);
		KList.add(7);
		KList.add(8);
		KList.add(9);
		KList.add(10);
		
		TK1List.add(1);
		TK1List.add(2);
		TK1List.add(3);
		TK1List.add(4);
		TK1List.add(5);
		TK1List.add(6);
		TK1List.add(7);
		
		
		TK2List.add(5);
		TK2List.add(10);
		TK2List.add(15);
		TK2List.add(20);
		TK2List.add(25);
		TK2List.add(30);
		TK2List.add(35);
		
//		for(int i=0; i<10; i++)
//			UtilityFunctions.writeFile("log/log.txt", new Date()+"\t"+"Loop End\r\n");
		
		UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+"Project Begin\r\n");

		process tester= new process();
		
		//for density
//		for(int i=0; i<densityList.size();i++){
//			UtilityFunctions.writeFile("logtp/log.txt", "Density="+densityList.get(i).floatValue()+"\t"+"random=0.03"+"\r\n");
//			for(int loopindex=0; loopindex<20; loopindex++){
//				tester.preProcess(densityList.get(i).floatValue(), (float)0.03);
//				tester.main_ori(loopindex,densityList.get(i).floatValue(), (float)0.03, 7, 2,10);
//			}
//			UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+"Loop End\r\n");
//			UtilityFunctions.writeFile("logtp/log.txt", "\r\n");
//		}
		
		//for random
		for(int i=0; i<randomList.size();i++){
			UtilityFunctions.writeFile("logtp/log.txt", "Density=0.1"+"\t"+"random="+randomList.get(i).floatValue()+"\r\n");
			for(int loopindex=0; loopindex<20; loopindex++){
				tester.preProcess((float)0.1, randomList.get(i).floatValue());
				tester.main_ori(loopindex,(float)0.1, randomList.get(i).floatValue(),7, 2,10);
			}
			UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+"Loop End\r\n");
			UtilityFunctions.writeFile("logtp/log.txt", "\r\n");
		}
		
		
		//for K
//		for(int i=0; i<KList.size();i++){
//			UtilityFunctions.writeFile("logtp/log.txt", "K="+KList.get(i).intValue()+"\t"+"Density=0.1"+"\t"+"random=0.03"+"\r\n");
//			for(int loopindex=0; loopindex<20; loopindex++){
//				tester.preProcess((float)0.1, (float)0.03);
//				tester.main_ori(loopindex,(float)0.1, (float)0.03, KList.get(i).intValue(),2,10);
//			}
//			UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+"Loop End\r\n");
//			UtilityFunctions.writeFile("logtp/log.txt", "\r\n");
//			
//		}
		
		//for TK1
//		for(int i=0; i<TK1List.size();i++){
//			UtilityFunctions.writeFile("logtp/log.txt", "TK1="+TK1List.get(i).intValue()+"\t"+"Density=0.1"+"\t"+"random=0.03"+"\r\n");
//			for(int loopindex=0; loopindex<20; loopindex++){
//				tester.preProcess((float)0.1, (float)0.03);
//				tester.main_ori(loopindex,(float)0.1, (float)0.03, 7, TK1List.get(i),10);	
//			}
//			UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+"Loop End\r\n");
//			UtilityFunctions.writeFile("logtp/log.txt", "\r\n");
//		}
		
//		for TK2
//		for(int i=0; i<TK2List.size();i++){
//			UtilityFunctions.writeFile("logtp/log.txt", "TK2="+TK2List.get(i).intValue()+"\t"+"Density=0.1"+"\t"+"random=0.03"+"\r\n");
//			for(int loopindex=0; loopindex<20; loopindex++){
//				tester.preProcess((float)0.1, (float)0.03);
//				tester.main_ori(loopindex,(float)0.1, (float)0.03, 7, 2,TK2List.get(i));
//			}
//			UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+"Loop End\r\n");
//		UtilityFunctions.writeFile("logtp/log.txt", "\r\n");
//			
//		}

		UtilityFunctions.writeFile("logtp/log.txt", new Date()+"\t"+"Project End\r\n");
	}
}
