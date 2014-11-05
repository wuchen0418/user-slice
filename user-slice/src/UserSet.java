import java.util.ArrayList;


public class UserSet 
{
	private int itemNo;
	private int clusterNo;
	private ArrayList<Integer> userNoList;
	
	public UserSet(int itemNo, int clusterNo)
	{
		this.itemNo = itemNo;
		this.clusterNo = clusterNo; 
		this.userNoList = new ArrayList<Integer>();
	}
	
	public int getClusterNo()
	{
		return this.clusterNo;
	}	
	public void addUser(Integer userNo)
	{
		userNoList.add(userNo);
	}
	
	public void addUser(ArrayList<Integer> userlist)
	{
		for(int i=0; i<userlist.size(); i++){
			userNoList.add(userlist.get(i));
		}
	}
	
	
	
	public void removeUser(Integer userNo)
	{
		userNoList.remove(userNo);
	}
	
	public void printElements()
	{
		for(int i = 0;i < userNoList.size();i++)
		{
			System.out.print(userNoList.get(i).toString()+",");
		}
		System.out.println();
	}
	
	public int getUserNoListSize(){
		return userNoList.size();
	}
	
	public ArrayList<Integer> getUserNoList(){
		return this.userNoList;
	}
	
	public String toString()
	{
		return "clusters " + itemNo+","+clusterNo + " has " + userNoList.size() + " element(s).";
	}
}
