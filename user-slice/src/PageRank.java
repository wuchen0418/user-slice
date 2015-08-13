import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PageRank {
	private static final double d = 0.85;
	private static final double DISTANCE = 0.0000001;
	private double [][] Wu;
	private double[][] Wu_normal;
	private double[][] simMatrix;
	private int userNum;
	private double[] rank;

	public PageRank (List<List<Double>> List) {
		userNum=List.size();
		Wu=transListToArray(List);
		Wu_normal=normalMatrix(Wu);
		simMatrix=new double[userNum][userNum];
	}
	
	public PageRank (double[][] List) {
		userNum=List.length;
		Wu=List;
		Wu_normal=normalMatrix(Wu);
		simMatrix=new double[userNum][userNum];
	}
	
	public double [][] transListToArray(List<List<Double>> List){
		double [][] matrix = new double [userNum][userNum];
		for(int i=0; i<userNum; i++){
			for(int j=0; j<userNum; j++){
				matrix[i][j]=List.get(i).get(j).doubleValue();
			}
		}
		return matrix;
	}
	
	public void calPageRank () {
		double [] p = new double[userNum];
		
		for(int j=0; j<userNum; j++){
			p[j]=(double)1/userNum;
		}
//		p[i]=1; //only ith entry of p is 1
		
		// init r and r_old
		double [] r = new double[userNum];
		double [] r_old = new double[userNum];
		
		for(int t=0; t<userNum; t++){
			r[t]=(double)1/userNum;
		}

//		System.out.println("Wu_normal");
//		printMatrix(Wu_normal);
		
		double[] dp= new double[p.length];
		for(int j=0; j<userNum; j++){
			dp[j]=(1-d)*p[j];
		}
		
		while (calDistance(r,r_old)>=DISTANCE){
			r_old=r;
			double[] Wur = new double[userNum];
			Wur=vectorMulMatrix(Wu_normal,r_old);
			double[] Wurd= new double[userNum];
			Wurd = numberMulMatrix(Wur,d);
			r=addMatrix(Wurd,dp);
		}
		printVec(r);
		this.rank=r;
	}
	
	public void calSimilarity () {
		double[][] simM = new double[userNum][userNum];
		for(int k=0; k<userNum; k++){
			double sum=0;
			List<Integer> N = new ArrayList<Integer>();
			for(int j=0; j<userNum; j++){
				if (Wu[k][j]>0&&j!=k){
					N.add(j);
					sum = sum +Wu[k][j]/rank[j];
				}
			}
			double [] suk = new double[userNum];
			if(N.size()>0){
				suk=numberMulMatrix(numberMulMatrix(rank,sum),(double)1/N.size());	
				suk[k]=0;
				for(int t=0; t<userNum; t++){
					simM[k][t]=suk[t];
				}
			}
			else{
				for(int t=0; t<userNum; t++){
					simM[k][t]=0;
				}
			}
		}
		simMatrix=simM;
	}
	
	public double[][] normalMatrix(double[][] m){
		double[][] nm=new double[m.length][m[0].length];
		for (int j=0; j<m.length; j++){
			double colsum=0;
			
			for (int t=0; t<m.length; t++){
				colsum=colsum+m[t][j];
			}
			for(int k=0; k<m.length; k++){
				if(colsum==0) nm[k][j]=0;
				else nm[k][j]=m[k][j]/colsum;
			}
		}
		return nm;
	}
	
	public static double[] vectorMulMatrix(double [][] m,
			double[] v) {
		
		if (m == null || v == null || m.length <= 0
				|| m[0].length != v.length) {
			return null;
		}
		double[] list = new double[v.length];
		for(int i=0; i<m.length;i++){
			double temp=0;
			for(int j=0; j<m[i].length; j++){
				temp=temp+m[i][j]*v[j];
			}
			list[i]=temp;
		}
		return list;
		
	}
	
	/**
	 * ����һ�������Ծ���
	 * 
	 * @param s
	 *            ����s
	 * @param a
	 *            double���͵���
	 * @return һ���µľ���
	 */
	public double [] numberMulMatrix(double [] s,
			double a) {
		double[] list = new double[s.length];
			for (int j = 0; j < s.length; j++) {
				double temp = a * s[j];
				list[j]=temp;
			}
		return list;
	}



	/**
	 * ��ӡ���һ������
	 * 
	 * @param m
	 */
	public void printMatrix(double[][] m) {
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				System.out.print(m[i][j] + ", ");
			}
			System.out.println();
		}
	}

	/**
	 * ��ӡ���һ������
	 * 
	 * @param v
	 */
	public void printVec(double[] v) {
		for (int i = 0; i < v.length; i++) {
			System.out.print(v[i] + ", ");
		}
		System.out.println();
	}


	/**
	 * �������������ľ���
	 * 
	 * @param q1
	 *            ��һ������
	 * @param q2
	 *            �ڶ�������
	 * @return ���ǵľ���
	 */
	public static double calDistance(double[] q1, double[] q2) {
		double sum = 0;

		if (q1.length != q2.length) {
			return -1;
		}

		for (int i = 0; i < q1.length; i++) {
			sum += Math.pow(q1[i] - q2[i],
					2);
		}
		return Math.sqrt(sum);
	}


	/**
	 * ������������ĺ�
	 * 
	 * @param list1
	 *            ��һ������
	 * @param list2
	 *            �ڶ�������
	 * @return ��������ĺ�
	 */
	public static double[] addMatrix(double[] v1,
			double[] v2) {
		double[] v= new double[v1.length];
		List<List<Double>> list = new ArrayList<List<Double>>();
		if (v1.length != v2.length || v1.length <= 0
				|| v2.length <= 0) {
			return null;
		}
		for (int i = 0; i <v1.length; i++) {
			v[i]=v1[i]+v2[2];
		}
		return v;
	}
	
	public static void main(String[] args) {
	
		List<Double> row1 = new ArrayList<Double>();
		row1.add(0.0);
		row1.add(1.0);
		row1.add(1.0);
		row1.add(0.0);
		List<Double> row2 = new ArrayList<Double>();
		row2.add(1.0);
		row2.add(0.0);
		row2.add(3.0);
		row2.add(0.0);
		List<Double> row3 = new ArrayList<Double>();
		row3.add(1.0);
		row3.add(3.0);
		row3.add(0.0);
		row3.add(4.0);
		List<Double> row4 = new ArrayList<Double>();
		row4.add(0.0);
		row4.add(0.0);
		row4.add(4.0);
		row4.add(0.0);

		List<List<Double>> s = new ArrayList<List<Double>>();
		s.add(row1);
		s.add(row2);
		s.add(row3);
		s.add(row4);

		System.out.println("��ʼ�ľ���WuΪ:");
		PageRank testPageRank = new PageRank(s);		
		testPageRank.calPageRank();
		testPageRank.calSimilarity();
	}
	
	public double[] getRank(){
		return rank;
	}
	
	public double[][] getSimMatrix(){
		return simMatrix;
	}


}
