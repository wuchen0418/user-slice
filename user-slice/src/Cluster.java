import java.util.ArrayList;


public class Cluster 
{
	private int id;
	private ArrayList<Point> items;
	private Point centroid;
	
	public Cluster(int id, Point centroid)
	{
		this.id = id;
		this.centroid = centroid; 
		this.items = new ArrayList<Point>();
	}
	
	public double calDistance(Point apoint)
	{
		double distance = Math.abs(centroid.getX() - apoint.getX());
		return distance;
	}
	
	public void addPoint(Point apoint)
	{
		items.add(apoint);
	}
	
	public String toString()
	{
		return "Cluster " + id + " has " + items.size() + " element(s). Centroid: " + centroid.toString();
	}
	
	public void calcentroid()
	{
		double sumx = 0.0;
		double itemnum = items.size();
		for(int i = 0;i < itemnum;i++)
		{
			sumx += items.get(i).getX();
		}
		centroid = new Point(sumx / itemnum,id);		
	}
	
	public void removePoint(Point apoint)
	{
		items.remove(apoint);
	}
	
	public void printElements()
	{
		for(int i = 0;i < items.size();i++)
		{
			System.out.print(items.get(i).getX()+",");
		}
		System.out.println();
	}
	
	public int getClusterSize(){
		return items.size();
	}
	
	public double getElement(int i){
		return items.get(i).getX();
	}
	
	public int getUsernum(int i){
		return items.get(i).getUsernum();
	}
}
