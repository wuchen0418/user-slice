import java.util.ArrayList;


public class User 
{
	private int userNo;
	private ArrayList <UserSet> userListClusted;
	
	public User(int usernum)
	{
		this.userNo = usernum;
		this.userListClusted = new ArrayList<UserSet>();
	}

	public int getUserNo()
	{
		return this.userNo;
	}
	
	public ArrayList<UserSet> getClusters()
	{
		return this.userListClusted;
	}
	
	public void addCluster(UserSet user){
		this.userListClusted.add(user);
	}
}
