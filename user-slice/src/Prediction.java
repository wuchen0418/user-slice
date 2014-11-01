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
	
	public void removeOutlier(){
		//remove the unreliable user from the randomedMatrix
		for(int i=0; i<unreliableUserList.size(); i++){
			int userno=unreliableUserList.get(i);
			for(int j=0; j<randomedMatrix[userno].length;j++)
			if(randomedMatrix[userno][j]!=-1&&randomedMatrix[userno][j]!=-2){
				randomedMatrix[userno][j]=-2; //here is the question: -1 or -2?
			}
		}
	}


}
