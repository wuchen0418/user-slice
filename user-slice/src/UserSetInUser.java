import java.util.ArrayList;


public class UserSetInUser 
{
	private int userNo;
	private ArrayList <UserSet> userSetInUserList;
	
	public UserSetInUser(int usernum)
	{
		this.userNo = usernum;
		this.userSetInUserList = new ArrayList<UserSet>();
	}

	public int getUserNo()
	{
		return this.userNo;
	}
	
	public ArrayList<UserSet> getUserSetInUserList()
	{
		return this.userSetInUserList;
	}
	
	public void addUserSetInUserList(UserSet userSet){
		this.userSetInUserList.add(userSet);
	}
}
