
public class Point 
{
	private double coordinatex;
	private int clusternum;
	private int usernum;
	
	public Point(double x)
	{
		this.coordinatex = x;
	}
	
	public Point(double x, int user){
		this.coordinatex = x;
		this.usernum=user;
	}
	public Point(double x, int clusternum, int user)
	{
		this.coordinatex = x;
		this.clusternum = clusternum;
		this.usernum=user;
	}
	
	public double getX()
	{
		return this.coordinatex;
	}
	
	public int getUsernum()
	{
		return this.usernum;
	}
	
	public int getClusternum()
	{
		return this.clusternum;
	}
	
	public void setClusternum(int clusternum)
	{
		this.clusternum = clusternum;
	}
	
	public String toString()
	{
		return "Point" + "(" + coordinatex + ") belongs to cluster " + clusternum;
	}
	
}
