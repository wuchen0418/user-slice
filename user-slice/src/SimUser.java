import java.util.ArrayList;


public class SimUser 
{
	private int userNo;
	private int count;
	
	public SimUser(int usernum)
	{
		this.userNo = usernum;
		this.count = 1;
	}

	public int getUserNo()
	{
		return this.userNo;
	}
	
	public int getCount()
	{
		return this.count;
	}
	public void addCount(){
		this.count++;
	}
	
	public void addCount(int userNo){
		this.count++;
	}
}
