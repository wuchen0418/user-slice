import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
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


}
