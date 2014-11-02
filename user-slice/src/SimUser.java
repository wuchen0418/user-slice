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
	public SimUser (int usernum, int outlierflag){
		this.userNo=usernum;
		this.count=0;
	}

	public int getUserNo()
	{
		return this.userNo;
	}
	
	public void setCount(int count)
	{
		this.count=count;
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
