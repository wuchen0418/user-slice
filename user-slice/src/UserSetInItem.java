import java.util.ArrayList;


public class UserSetInItem 
{
	private int itemNo;
	private ArrayList <UserSet> userSetInItemList;
	
	public UserSetInItem(int itemNo)
	{
		this.itemNo = itemNo;
		this.userSetInItemList = new ArrayList<UserSet>();
	}

	public UserSetInItem(int itemNo, ArrayList <UserSet> userSetInItemList)
	{
		this.itemNo = itemNo;
		this.userSetInItemList = userSetInItemList;
	}
	
	public int getItemNo()
	{
		return this.itemNo;
	}
	
	public ArrayList<UserSet> getUserSets()
	{
		return this.userSetInItemList;
	}
	
	public void addUserSetInItemList(UserSet aUserSet){
		this.userSetInItemList.add(aUserSet);
	}
	
	public UserSet getUserSet(int userNo){
		UserSet aUserSet=null;
		System.out.println("userSetInItemList size:"+userSetInItemList.size()); 
		for(int i=0; i<userSetInItemList.size();i++){
			aUserSet = userSetInItemList.get(i);
			ArrayList<Integer> userNoList = aUserSet.getUserNoList();
			if(userNoList.contains(userNo)){
				break;
			}
		}
		return aUserSet; 
	}
}
