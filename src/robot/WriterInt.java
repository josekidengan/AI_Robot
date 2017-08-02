package robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class WriterInt {

	private ArrayList<Integer> Data=new ArrayList<Integer>();
	
	public WriterInt(ArrayList<Integer> Data)
	{
		this.Data=Data;
		this.Writeit();
		
	}
	
	
	private void Writeit()
	{
		try {
			FileWriter fw=new FileWriter(new File("C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\Win.dat"));
			
			
			
			for(int d=0;d<Data.size();d++)
		    {	
				//lUT.get(d)[lUT.get(d).length-1]=(Math.round(lUT.get(d)[lUT.get(d).length-1])*100)/100;
				
			fw.write(d+"|"+Integer.toString(Data.get(d))+'\n');
			//fw.write("fkswnf");
				
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
