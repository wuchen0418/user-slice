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
			float density,ArrayList<Integer> unreliableUserList, ArrayList<ClustedUser> clustedUserList){
		//outlier has been removed
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
		for(int i=0; i<userNumber; i++){
			ClustedUser aClustedUser = clustedUserList.get(i);
			for(int j=0; j<itemNumber; j++){
				//no value in originalMatrix
				if(randomedMatrix[i][j]==-1) 
					predictedMatrix[i][j]=-1;
				if(randomedMatrix[i][j]==-2){
					ArrayList<SimUser> aSimUserList = aClustedUser.getSimUserList();
				}
				if(randomedMatrix[i][j]==-3){
					//predictedMatrix[i][j] = UMean;
				}
			}
		}
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
