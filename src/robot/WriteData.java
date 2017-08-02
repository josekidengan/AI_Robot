package robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class WriteData {
	private ArrayList<double[]> LUT=new ArrayList<double[]>();

	/**
	 * @param lUT
	 */
	public WriteData(ArrayList<double[]> lUT) {
		super();
		LUT = lUT;
		SendtoStore(LUT);
	}
	
	private void SendtoStore(ArrayList<double[]> lUT)
	{
		try {
			FileWriter fw=new FileWriter(new File("C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\FINALLUT.dat"));
			
			for(int d=0;d<lUT.size();d++)
		    {	
				//lUT.get(d)[lUT.get(d).length-1]=(Math.round(lUT.get(d)[lUT.get(d).length-1])*100)/100;
				/*if(lUT.get(d)[lUT.get(d).length-1]!=0.0)
				{*/
					
				//System.out.println(Arrays.toString(lUT.get(d)));
			fw.write(Arrays.toString(lUT.get(d))+'\n');
			//fw.write("fkswnf");
				//}
		    }
			
			fw.close();
		} 

		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally
		{
			
		}
	}
}
