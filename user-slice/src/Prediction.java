import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class Prediction {
	private ArrayList<Map> upccList;
	private ArrayList<Map> ipccList;
	private float[] URR_L1AVG;
	private float[] URR_L2AVG;
	private float[] URR_Cluster_AVG;
	private float[] URR_imean;
	private float[] URR_umean;
	private ArrayList<Integer> unreliableUserList;
	private HashMap userLocationMap; 
	private Vector<String> country = new Vector<String>();
	private float[][] originalMatrix;
	private float[][] randomedMatrix;
	private float[][] predictedMatrix;
	private int userNumber = 339;
	private int itemNumber = 5825;
	
	public void predictorOnCluster(float[][] originalMatrix, float[][] randomedMatrix, float random, float density,ArrayList<Integer> unreliableUserList, ArrayList<User> userClustered){
		ArrayList<Integer> clustedUser = new ArrayList<Integer>();
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];

		for(int i=0; i<randomedMatrix.length; i++){
			User aUser = userClustered.get(i);
			if(aUser.getUserNo()!=i)
				continue;
			for(int j=0; j<randomedMatrix[0].length; j++){
				if(randomedMatrix[i][j]!=-2&&randomedMatrix[i][j]!=-1) // copy
					predictedMatrix[i][j]=randomedMatrix[i][j];
				if(originalMatrix[i][j]<0)
					predictedMatrix[i][j]=originalMatrix[i][j]; //no need to predict
			}
		}
	}
	public void cluserMean(float[][] originalMatrix, float[][] randomedMatrix, float random, 
			float density,ArrayList<Integer> unreliableUserList, ArrayList<ClustedUser> clustedUserList, ArrayList<UserSetInItem> userSetInItems){
		//outlier has been removed
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
		for(int i=0; i<userNumber; i++){
			ClustedUser aClustedUser = clustedUserList.get(i);
			for(int j=0; j<itemNumber; j++){
				ArrayList<Integer> userNoInItem = new ArrayList<Integer>(); //get the users who invoke the service
				for(int index=0;index<userNumber;index++){
					if(randomedMatrix[index][j]!=-1&&randomedMatrix[index][j]!=-2&&randomedMatrix[index][j]!=-3){
						userNoInItem.add(index);
					}
				}
				//no value in originalMatrix
				UserSetInItem aUserSetInItem = userSetInItems.get(j);
				int simFlag =0; //flag for simuser
				if(randomedMatrix[i][j]==-1){
					predictedMatrix[i][j]=-1;
					continue;
				}
				if(randomedMatrix[i][j]==-2){
					for(int u=0;u<aClustedUser.getSimUserList().size();u++){
						SimUser aSimUser = aClustedUser.getSimUser(u);
						if(userNoInItem.contains(aSimUser.getUserNo())){ //simuser invoke the item
							simFlag=1;
							UserSet simUserSet = aUserSetInItem.getUserSet(aSimUser.getUserNo());
							ArrayList<Integer> simUserNo = simUserSet.getUser();
							int clusterMean=0;
							int count=0;
							for(int a=0; a<simUserNo.size(); a++){
								if(!unreliableUserList.contains(simUserNo.get(a))){
									clusterMean += randomedMatrix[simUserNo.get(a)][j];
									count++;
								}
							}
							if(count!=0){
								clusterMean=clusterMean/count; //get the cluster Mean of the SimUser
							}
							else{
								clusterMean=0; // all simUsers are outliers
							}
						}
						else{ //simuser dose not invoke the item
							continue; 
						}
					}
					continue;
				}
				if(randomedMatrix[i][j]==-3||simFlag==0){ //no simuser
					//predictedMatrix[i][j] = UMean;
					continue;
				}
			}
		}
		UtilityFunctions.writeMatrix(predictedMatrix, "predicted/d"+density+"r"+"random.txt");
	}
	
	public void removeOutlier(){
		//mark the unreliable user from the randomedMatrix to -3
		for(int i=0; i<unreliableUserList.size(); i++){
			int userno=unreliableUserList.get(i);
			for(int j=0; j<randomedMatrix[userno].length;j++)
			if(randomedMatrix[userno][j]!=-1&&randomedMatrix[userno][j]!=-2){
				randomedMatrix[userno][j]=-3; // outlier
			}
		}
	}


}
