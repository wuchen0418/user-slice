import java.util.ArrayList;


public class SimUserSet 
{
	private int userNo;
	private ArrayList <SimUser> simUserList = new ArrayList <SimUser>();
	
	public SimUserSet(UserSetInUser aUser)
	{
		ArrayList <Integer> userlistInUserSet = new ArrayList<Integer>();
		this.userNo = aUser.getUserNo();
		this.simUserList = new ArrayList<SimUser>();
		ArrayList<UserSet> userListCluseted = aUser.getUserSetInUserList();
		for(int i=0; i<userListCluseted.size(); i++){
			UserSet aUserSet = userListCluseted.get(i);
			userlistInUserSet = aUserSet.getUserNoList();
			this.addSimUser(userlistInUserSet);
		}
	}
	
	public SimUserSet(int outlierUserNo){
		this.userNo=outlierUserNo;
	}

	public int getUserNo()
	{
		return this.userNo;
	}
	
	public ArrayList<SimUser> getSimUserList()
	{
		return this.simUserList;
	}
	
	public SimUser getSimUser(int i){
		return this.simUserList.get(i);
	}
	
	public void addSimUser(ArrayList<Integer> userlistInUserSet){
		ArrayList<Integer> simUserNo = new ArrayList<Integer>();
		for(int i=0; i<this.simUserList.size();i++){
			simUserNo.add(this.simUserList.get(i).getUserNo());
		}
		for(int i=0; i<userlistInUserSet.size();i++){
			int usernoInUserSet = userlistInUserSet.get(i);
			if(this.userNo==usernoInUserSet)
				continue;
			if(!simUserNo.contains(usernoInUserSet)){
				this.simUserList.add(new SimUser(usernoInUserSet));
			}
			else{
				for(int j=0; j<this.simUserList.size(); j++){
					SimUser aSimUser = this.simUserList.get(j);
					int index = aSimUser.getUserNo();
					if(index==usernoInUserSet){
						this.simUserList.get(j).addCount();
					}
				}
			}
		}
	}
	public void sortSimUser(){
		for(int i=0; i<this.simUserList.size(); i++){
			for(int j=0; j<this.simUserList.size()-1; j++){
				SimUser aSimUser1= this.simUserList.get(j);
				SimUser aSimUser2= this.simUserList.get(j+1);
				if(aSimUser1.getCount()<aSimUser2.getCount()){
					this.simUserList.set(j, aSimUser2);
					this.simUserList.set(j+1, aSimUser1);
				}
			}
		}
	}
	
}
