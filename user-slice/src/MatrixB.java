import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MatrixB {
	int userNum;
	double[][] matrix;
	List<List<Double>> compressedList = new ArrayList<List<Double>>();
	double[][] uncompressedMatrix = new double[userNum][userNum];
	private Map userIndexMap = new HashMap();
	private ArrayList<Integer> unRUL = new ArrayList<Integer>();
	
	
	//construction method
	public MatrixB(ArrayList<SimUserSet> simUserSetList, ArrayList<Integer> unRUL){
		this.unRUL=unRUL;
		userNum=simUserSetList.size();
		matrix = new double[userNum][userNum];
		int currUserNo;
		int currSimUserNo;
		ArrayList <SimUser> currSimUserList;
		SimUser currSimUser;
		
		
		for(int i=0; i<simUserSetList.size(); i++){
			SimUserSet aSimUserSet= simUserSetList.get(i);
			currUserNo=aSimUserSet.getUserNo();
			
			if(aSimUserSet.getOutlierFlag()==1||this.unRUL.contains(currUserNo)){  //for outlier
				continue;
			}
			else{ // for other simUsers
				currSimUserList=aSimUserSet.getSimUserList();
				for(int j=0; j<currSimUserList.size(); j++){
					currSimUser = currSimUserList.get(j);
					currSimUserNo=currSimUser.getUserNo();
					int count=currSimUser.getCount();
					matrix[currUserNo][currSimUserNo]=count;
				}
			}
		}		
		compressedList=compressMatrix(matrix);	
	}
	
	public MatrixB(double[][] matrix, ArrayList<Integer> unRUL){
		userNum=matrix.length;
		this.unRUL=unRUL;
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix.length; j++){
				if(matrix[i][j]==-2)
					matrix[i][j]=0;
			}
		}
		
		this.matrix=matrix;
		compressedList=compressMatrix(matrix);	
		
	}
	public Map getMap(){
		return userIndexMap;
	}
	
	public List<List<Double>> getList(){
		return compressedList;
	}
	
	public double [][] getUncompressedMatrix(){
		return uncompressedMatrix;
	}
	
	public List<List<Double>> compressMatrix(double[][] originalMatrix){ //delete the row of outlier and put the value in the map
		List<List<Double>> list = new ArrayList<List<Double>>();
		int newi=0;
		for(int i=0; i<originalMatrix.length; i++){
			if(!this.unRUL.contains(i)){ //for outlier
				list.add(new ArrayList<Double>());
				for(int j=0; j<originalMatrix.length; j++){
					if(!this.unRUL.contains(j)){
						list.get(newi).add((double)originalMatrix[i][j]);
					}
				}
				userIndexMap.put(newi, i);  //
				newi++;	
			}			
		}	
		return list;
	}
	
//	public List<List<Double>> compressMatrix(double[][] originalMatrix){ //delete the row of outlier and put the value in the map
//		List<List<Double>> list = new ArrayList<List<Double>>();
//		int newi=0;
//		for(int i=0; i<originalMatrix.length; i++){
//			if(!this.unRUL.contains(i)){ //for outlier
//				list.add(new ArrayList<Double>());
//				for(int j=0; j<originalMatrix.length; j++){
//					if(!this.unRUL.contains(j)){
//						list.get(newi).add(originalMatrix[i][j]);
//					}
//				}
//				userIndexMap.put(newi, i);
//				newi++;	
//			}			
//		}	
//		return list;
//	}
	
	public double[][] uncompressMatrix(List<List<Double>> originalList){
		double[][] newMatrix = new double[userNum][userNum];
		for(int i=0; i<originalList.size(); i++){
			List<Double> list =originalList.get(i);
			for(int j=0; j<originalList.size(); j++){
				newMatrix[(Integer) userIndexMap.get(i)][(Integer) userIndexMap.get(j)]=list.get(j).doubleValue();
			}
		}
		return newMatrix;
	}
	
	public double[][] uncompressMatrix(double[][] originalMatrix){
		double[][] nMatrix = new double[this.userNum][this.userNum];
		for(int i=0; i<originalMatrix.length; i++){
			for(int j=0; j<originalMatrix.length; j++){
				nMatrix[(Integer)userIndexMap.get(i)][(Integer)userIndexMap.get(j)]
						=originalMatrix[i][j];
			}
		}
		return nMatrix;
	}
}
