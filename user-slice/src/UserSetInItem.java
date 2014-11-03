import java.util.ArrayList;


public class UserSetInItem 
{
	private int itemNo;
	private ArrayList <UserSet> userSets;
	
	public UserSetInItem(int itemNo)
	{
		this.itemNo = itemNo;
		this.userSets = new ArrayList<UserSet>();
	}

	public UserSetInItem(int itemNo, ArrayList <UserSet> userSets)
	{
		this.itemNo = itemNo;
		this.userSets = userSets;
	}
	
	public int getItemNo()
	{
		return this.itemNo;
	}
	
	public ArrayList<UserSet> getUserSets()
	{
		return this.userSets;
	}
	
	public void addCluster(UserSet aUserSet){
		this.userSets.add(aUserSet);
	}
}
