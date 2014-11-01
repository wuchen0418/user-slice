import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class sliceInit {
	
	public static void main(String[] args) {
		String prefix = "WSDream-QoSDataset3/";
		String matrix = "frMatrix"; 
		int userNumber = 142; 
		int itemNumber = 4532;
		int timeNumber = 64;
		String fileName = "WSDream-QoSDataset3/rtRate";
		try {
			
			for(int timeCount=0; timeCount<timeNumber; timeCount++){
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileName)));
				float[][] result = new float[userNumber][itemNumber];
				for(int i=0; i<userNumber; i++)
					for(int j=0; j<itemNumber; j++)
						result[i][j]=-1;
				String line;
				while ((line = br.readLine()) != null) {		
					String[] temp = line.split(" ");
					if(Integer.parseInt(temp[2])==timeCount){
						result[Integer.parseInt(temp[0])][Integer.parseInt(temp[1])] = Float.parseFloat(temp[3]);
						System.out.println("timeInter="+timeCount+"_user="+Integer.parseInt(temp[0])+"_item="+Integer.parseInt(temp[1])+"_timeInter="+Float.parseFloat(temp[3]));
					}
				}
				writeMatrix(result,timeCount);
				br.close();
			} 
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeMatrix(float[][] matrix, int SliceNum){
		try {
            File file = new File("WSDream-QoSDataset3/Slice/Slice"+SliceNum+".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String line = "";
            for(int i =0; i < matrix.length; i++) {
            	for(int j=0; j < matrix[0].length; j++) {
            		line += matrix[i][j] + "\t";
            	}
            	writer.write(line);
            	if (i < matrix.length -1) {
            		writer.newLine();
            	}
            	line = "";
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
