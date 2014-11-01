import java.util.ArrayList;


public class ClustedUser 
{
	private int userNo;
	private ArrayList <SimUser> simUserList;
	
	public ClustedUser(User aUser, ArrayList<Integer> unreliableuser)
	{
		ArrayList <Integer> userlistInUserSet = new ArrayList<Integer>();
		if(!unreliableuser.contains(aUser.getUserNo())){
			this.userNo = aUser.getUserNo();
			this.simUserList = new ArrayList<SimUser>();
			ArrayList<UserSet> userListCluseted = aUser.getClusters();
			for(int i=0; i<userListCluseted.size(); i++){
				UserSet aUserSet = userListCluseted.get(i);
				userlistInUserSet = aUserSet.getUser();
				this.addSimUser(userlistInUserSet, unreliableuser);
			}
		}
	}

	public int getUserNo()
	{
		return this.userNo;
	}
	
	public ArrayList<SimUser> getSimUserList()
	{
		return this.simUserList;
	}
	
	public void addSimUser(ArrayList<Integer> userlistInUserSet, ArrayList<Integer> unreliableuser){
		ArrayList<Integer> simUserNo = new ArrayList<Integer>();
		for(int i=0; i<this.simUserList.size();i++){
			simUserNo.add(this.simUserList.get(i).getUserNo());
		}
		for(int i=0; i<userlistInUserSet.size();i++){
			int usernoInUserSet = userlistInUserSet.get(i);
			if(this.userNo==usernoInUserSet)
				continue;
			if(unreliableuser.contains(usernoInUserSet))
				continue;
			if(!simUserNo.contains(usernoInUserSet)){
				this.simUserList.add(new SimUser(userNo));
			}
			else{
				for(int j=0; j<this.simUserList.size(); j++){
					SimUser aSimUser = this.simUserList.get(j);
					int index = aSimUser.getUserNo();
					if(index==this.simUserList.get(j).getUserNo()){
						this.simUserList.get(j).addCount();
					}
				}
			}
		}
	}
}
