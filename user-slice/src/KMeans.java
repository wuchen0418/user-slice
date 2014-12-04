import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;


public class KMeans 
{
	private int itemnum; 
	private int K;
	private  ArrayList<Point> points = new ArrayList<Point>();
	private  ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private static double lastE = Double.MAX_VALUE;
	private static double curE = 0.0;
	private ArrayList<Integer> user = new ArrayList<Integer>() ;
	private ArrayList<UserSetInUser> userClustered = new ArrayList<UserSetInUser>();
	
	public Boolean userGreaterK(float[] URR){
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
		else 
			return false;
	}
	
	
	public KMeans(float[] URR, int K){
		try
		{
			this.itemnum = URR.length;
			this.K = K;

			for (int t=0;t<URR.length;t++){
				double x = URR[t];
				if(x!=-2&&x!=-1){
					Point apoint = new Point(x,t);			//construct points array
					points.add(apoint);
				}
			}
			
			for(int i = 0;i < K;i++)					//initialize K clusters and centroid
			{
				Cluster acluster = new Cluster(i, points.get(i));
				clusters.add(acluster);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	public KMeans(int[] URR){
		try
		{
			itemnum = URR.length;
			K = 2;

			for (int t=0;t<URR.length;t++){
				double x = URR[t];
				if(x!=-2&&x!=-1){
					Point apoint = new Point(x,t);			//construct points array
					points.add(apoint);
				}
			}
			
			for(int i = 0;i < K;i++)					//initialize K clusters and centroid
			{
				Cluster acluster = new Cluster(i, points.get(i));
				clusters.add(acluster);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	/*
	 * calculate which cluster each of the items belongs to
	 */
	public void calBelongs()
	{
		curE = 0;
		for(int i = 0;i < points.size();i++)
		{
				double distance = Double.MAX_VALUE;
				int clusternum = 0;
				for(int j = 0;j < clusters.size();j++)
				{
					if(distance > clusters.get(j).calDistance(points.get(i)))
					{
						distance = clusters.get(j).calDistance(points.get(i));
						clusternum = j;
					}
				}
				curE += distance;
				int oldcluster = points.get(i).getClusternum();
				points.get(i).setClusternum(clusternum);
				clusters.get(oldcluster).removePoint(points.get(i));
				clusters.get(clusternum).addPoint(points.get(i));
		}
	}
	
	/*
	 * recalculate the centroid of each cluster
	 */
	public  void calAllCentroids()
	{
		for(int i = 0;i < clusters.size();i++)
		{
			clusters.get(i).calcentroid();
		}
	}
	
	public  void printPoints()
	{
		for(int i = 0;i < points.size();i++)
		{
			System.out.println(points.get(i).toString());
		}
	}
	
	public  void printClusters()
	{
		for(int i = 0;i < clusters.size();i++)
		{
			System.out.println(clusters.get(i).toString());
		}
	}
	
	public  void printBelongs()
	{
		for(int i = 0;i < clusters.size();i++){
			clusters.get(i).printElements();
			System.out.println();
		}
			
	}
	
	public  void cluster(){
		while(true){
			calBelongs();
			calAllCentroids();
			if(curE == lastE)
				break;
			lastE = curE;
		}
	}
	
	public int getSmallerClusterNum(){
		int min = clusters.get(0).getClusterSize();
		int index=0;
		for(int i=0;i<clusters.size();i++){
			if (min>clusters.get(i).getClusterSize()){
				min = clusters.get(i).getClusterSize();
				index = i;
			}
		}
		return index;
	}
	
	public ArrayList<Integer> getUnreliableUserList(){
		if(clusters.size()==0){
			System.out.println("clusters size = 0");
		}
		int min = clusters.get(0).getClusterSize();
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		for(int i=0;i<clusters.size();i++){
			if (clusters.get(i).getClusterSize()!=0&&min>clusters.get(i).getClusterSize()){
				min = clusters.get(i).getClusterSize();
			}
		}

		for(int i=0;i<clusters.size();i++){
			if (min==clusters.get(i).getClusterSize()){
				indexList.add(i);
			}
		}
		ArrayList<Integer> unreliableUser = new ArrayList<Integer>() ;
		for(int i=0; i<indexList.size();i++){
			for (int j=0; j<min; j++){
				unreliableUser.add(clusters.get(indexList.get(i)).getUsernum(j));
			}
		}
		
		return unreliableUser;
	}
	
	public void getUserCluBelong(){
		for(int i = 0;i < user.size();i++)
		{
			System.out.println(user.get(i).toString());
		}
	}
	
	public int getClustersNum(){
		return clusters.size();
	}
	
	public void buildUser(int userNum){
		for(int i=0; i<userNum; i++){
			userClustered.add(i, new UserSetInUser(i));
		}
	}
	
	public ArrayList<UserSet> buildUserSet(int itemNo){
		ArrayList<UserSet> userSetsInOneItem =new ArrayList<UserSet>();
		for(int i = 0;i < clusters.size();i++)
		{
			UserSet userSet = new UserSet(itemNo, i);
			ArrayList<Integer> usersInUserSet = new ArrayList<Integer>(); //store the userno in userSet
			for(int j=0; j<clusters.get(i).getClusterSize(); j++){
				int userno = clusters.get(i).getUsernum(j);
				usersInUserSet.add(userno);
			}
			userSet.addUser(usersInUserSet);
			userSetsInOneItem.add(userSet);
//			for(int u=0; u<usersInUserSet.size(); u++){
//				int userno = usersInUserSet.get(u);
//				User temUser = userClustered.get(userno);
//				temUser.addCluster(userSet);
//				userClustered.set(userno, temUser);
//			}
		}
		return userSetsInOneItem;
	}
	
//	public void printUserClustered(){
//		for(int i = 0;i < userClustered.size();i++)
//		{
//			User temUser = userClustered.get(i);
//			ArrayList<UserSet> tempUserSet = temUser.getClusters();
//			System.out.println("user "+ i +"has "+tempUserSet.size() +" userSets:"+tempUserSet.toString());
//		}
//	}
		
	
	/*public static void main(String[] args)
	{
		float[] URR = new float[339] ;
		int u=0;
		String fileroute = "src/URRCluster_d0.1r0.03.txt";
		try{
			FileReader fr = new FileReader(fileroute);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null){
				URR[u]=Float.parseFloat(line);
				u++;
				line = br.readLine();
			}
			fr.close();
			br.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		KMeans kMeans = new KMeans(URR);
		kMeans.cluster();
		System.out.println(kMeans.getUnreliableUserList());

	}*/

}
