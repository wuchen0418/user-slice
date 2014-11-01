import java.util.ArrayList;


public class UserSet 
{
	private int itemNo;
	private int clusterNo;
	private ArrayList<Integer> user;
	
	public UserSet(int itemNo, int clusterNo)
	{
		this.itemNo = itemNo;
		this.clusterNo = clusterNo; 
		this.user = new ArrayList<Integer>();
	}
		
	public void addUser(Integer userNo)
	{
		user.add(userNo);
	}
	
	public void addUser(ArrayList<Integer> userlist)
	{
		for(int i=0; i<userlist.size(); i++){
			user.add(userlist.get(i));
		}
	}
	
	
	public void removeUser(Integer userNo)
	{
		user.remove(userNo);
	}
	
	public void printElements()
	{
		for(int i = 0;i < user.size();i++)
		{
			System.out.print(user.get(i).toString()+",");
		}
		System.out.println();
	}
	
	public int getClusterSize(){
		return user.size();
	}
	
	public ArrayList<Integer> getUser(){
		return this.user;
	}
	
	public String toString()
	{
		return "clusters " + itemNo+","+clusterNo + " has " + user.size() + " element(s).";
	}
}
