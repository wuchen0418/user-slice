import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


public class process {

	private static ArrayList<User> userClustered = new ArrayList<User>();
	private static ArrayList<ClustedUser> clustedUserList = new ArrayList<ClustedUser>();
	private int userNumber = 339;
	private int itemNumber = 5825;
	
	public void preProcess() {
		String prefix = "WSDream-QoSDataset2/";
		String matrix = "rtMatrix"; 
//		int userNumber = 339; 
//		int itemNumber = 5825;
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
	
	public void buildUser(int userNum){
		for(int i=0; i<userNum; i++){
			userClustered.add(i, new User(i));
		}
	}
	
	public void printUserClustered(){
		for(int i = 0;i < this.userNumber;i++)
		{
			User temUser = userClustered.get(i);
			ArrayList<UserSet> tempUserSet = temUser.getClusters();
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
    			User temUser = userClustered.get(i);
    			ArrayList<UserSet> tempUserSet = temUser.getClusters();
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
	
	public Boolean userGreaterK(float[] URR){
		int count = 0;
		for (int t=0;t<URR.length;t++){
			double x = URR[t];
			if(x!=-2&&x!=-1){
				count++;
			}
		}
		if(count>=7){
			return true;
		}
		else 
			return false;
	}
	
	public void buildClustedUserList (ArrayList<User> userList, ArrayList<Integer> unreliablelist){
		for(int i=0; i<this.userNumber; i++){
			User aUser = userList.get(i);
			if(unreliablelist.contains(aUser.getUserNo()))
				continue;
			ClustedUser newClustedUser = new ClustedUser(aUser,unreliablelist);
			clustedUserList.add(newClustedUser);
		}
	}
	
	public void printclustedUserList(){
		for(int i = 0;i < clustedUserList.size();i++)
		{
			User temUser = userClustered.get(i);
			ArrayList<UserSet> tempUserSet = temUser.getClusters();
			System.out.println("user "+ i +"has "+tempUserSet.size() +" userSets:"+tempUserSet.toString());
		}
	}
	
	public static void main(String[] args) {
		String matrix = "rtMatrix";
		process tester= new process();
		tester.preProcess();
		int userNumber = 339; 
		int itemNumber = 5825;
		float[][] randomedMatrix;
		float[] itemRtList;
		ArrayList<Integer> unreliableUser = new ArrayList<Integer>();
		int[] userCount=new int[userNumber];
		int[] indexSorted = new int[itemNumber];
				
		float density = (float)0.1;
		float random = (float)0.03;
		
		randomedMatrix = UtilityFunctions.readMatrix("randomed/" + matrix + density + "_" + random, userNumber, itemNumber);
		tester.buildUser(userNumber);
		
		for(int itemNo=0; itemNo<itemNumber; itemNo++){			
			ArrayList<UserSet> userSetsInOneItem = new ArrayList<UserSet>();
			itemRtList=tester.getItemRtList(itemNo, randomedMatrix, userNumber);
			if(tester.userGreaterK(itemRtList)){
				KMeans kMeans = new KMeans(itemRtList);
				kMeans.cluster();
				userSetsInOneItem = kMeans.buildUserSet(itemNo);
				
				for(int i=0; i<userSetsInOneItem.size(); i++){
					UserSet aUserSet = userSetsInOneItem.get(i);
					ArrayList<Integer> usersInUserSet = new ArrayList<Integer>(); //store the userno in userSet
					usersInUserSet = aUserSet.getUser();
					for(int u=0; u<usersInUserSet.size(); u++){
						int userno = usersInUserSet.get(u);
						User temUser = userClustered.get(userno);
						temUser.addCluster(aUserSet);
						userClustered.set(userno, temUser);
					}
				}
//				kMeans.printPoints();
//				kMeans.printClusters();
//				kMeans.printBelongs();
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
		
		tester.writeUser("userClusted.txt");
		tester.buildClustedUserList(userClustered, unreliableUser);
		System.out.println(clustedUserList.toString());
/*		
		KMeans kMeans2 = new KMeans(userCount);
		kMeans2.cluster();
		ArrayList<Integer> unRUL=kMeans2.getUnreliableUserList();
//		kMeans2.printPoints();
//		kMeans2.printClusters();
//		kMeans2.printBelongs();
		System.out.println(unRUL.toString());
		
		System.out.println();
		System.out.println("sorted");
		
		indexSorted = tester.sortIndex(userCount);
		for(int i=0; i<userNumber; i++){
			System.out.println("No."+i+" index="+indexSorted[i]+" countNum="+userCount[i]+"\t");
		}
			*/
	}
}
