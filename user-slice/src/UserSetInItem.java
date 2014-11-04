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
	
	public UserSet getUserSet(int userNo){
		UserSet aUserSet=null;
		for(int i=0; i<userSets.size();i++){
			aUserSet = userSets.get(i);
			ArrayList<Integer> userNoList = aUserSet.getUser();
			if(userNoList.contains(userNo)){
				break;
			}
		}
		return aUserSet; 
	}
}
