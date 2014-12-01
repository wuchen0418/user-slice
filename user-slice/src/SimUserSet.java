import java.util.ArrayList;


public class SimUserSet 
{
	private int userNo;
	private ArrayList <SimUser> simUserList = new ArrayList <SimUser>();
	private int outlierFlag;
	
	public SimUserSet(UserSetInUser aUser, ArrayList<Integer> unreliableuser)
	{
		ArrayList <Integer> userlistInUserSet = new ArrayList<Integer>();
		if(!unreliableuser.contains(aUser.getUserNo())){
			this.userNo = aUser.getUserNo();
			this.outlierFlag = 0;
			this.simUserList = new ArrayList<SimUser>();
			ArrayList<UserSet> userListCluseted = aUser.getUserSetInUserList();
			for(int i=0; i<userListCluseted.size(); i++){
				UserSet aUserSet = userListCluseted.get(i);
				userlistInUserSet = aUserSet.getUserNoList();
				this.addSimUser(userlistInUserSet, unreliableuser);
			}
		}
	}
	
	public SimUserSet(int outlierUserNo, int outlierflag){
		this.userNo=outlierUserNo;
		this.outlierFlag=1;
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
		if(outlierFlag==0){
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
	public void printSimUser(int userNo){
		System.out.println("user "+userNo +" has simUser: ");
		for(int i=0; i<this.simUserList.size(); i++){
			System.out.println("user "+userNo +" has simUser: " +simUserList.get(i).getUserNo() );
		}
		
	}
	
}
