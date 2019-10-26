package robot.robocode.src.neuralnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class NeuralMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Activation type
		boolean reptype=true;
		NeuralStruct ns=new NeuralStruct();
		ns.setReptype(reptype);
		ns.initializeWeights();		
		double error=0.0;
		ArrayList<Double> totalerrorlist=new ArrayList<Double>();
		ArrayList<Double> totalRMSerrorlist=new ArrayList<Double>();
		double totalerror=0.0;
		double FORRMSerror=0.0;
		double RMSError=0.0;
		int epochno=0;
		boolean flag=true;
		boolean log=true;
		double[] robotval=new double[13];
		ArrayList<double[]> stateActionQ=new ArrayList<double[]>();
		//ArrayList<double[]> SquaredError=new ArrayList<double[]>();
		
		
		//String file = "C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\MyBot.data\\FINALLUT.dat";
		String file ="C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\old data\\FINALLUT.dat";

		try {

		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		    String line;
		    // Uncomment the line below if you want to skip the fist line (e.g if headers)
		    // line = br.readLine();

		    while ((line = br.readLine()) != null) {
		    	String Filtered=line.substring(1, line.length()-1);
		    	
		    	for (int c=0;c<Filtered.split(",").length;c++)
		    	{
		    		robotval[c]=Double.parseDouble(Filtered.split(",")[c]);
		    		/*if(c==0 || c==1)
		    		{
		    			robotval[c]=(Double.parseDouble(Filtered.split(",")[c]))/10;
		    		}else 
		    		{
		    			robotval[c]=Double.parseDouble(Filtered.split(",")[c]);
		    		}*/
		    		
		    		
		    	}
		    	stateActionQ.add(Arrays.copyOf(robotval, robotval.length));
		    	
		    	//line.split(",");

		        // do something with line

		    }
		    br.close();

		} catch (IOException e) {
		    System.out.println("ERROR: unable to read file " + file);
		    e.printStackTrace();   
		}
		int counter=0;
		int counterstop=0;
		do
		{
			counter=counter+1;
			error=0;
			FORRMSerror=0;
		for (int v=0;v<stateActionQ.size();v++)
		{
			ns.train(Arrays.copyOf(stateActionQ.get(v), stateActionQ.get(v).length-1), stateActionQ.get(v)[stateActionQ.get(v).length-1]);
			error=error+ns.GetOutputError();
			FORRMSerror=FORRMSerror+ ns.getOutputErrorForRMS();
		}
		totalerror=error;
		RMSError=Math.sqrt(FORRMSerror/(stateActionQ.size()));
		//System.out.println("Total Error is :"+totalerror);
		//System.out.println("Total RMS Error is :"+RMSError);
		if(counter==100)
		{
			
		System.out.println("Total Error is :"+totalerror);
		totalerrorlist.add(totalerror);
		System.out.println("Total RMS Error is :"+RMSError);
		totalRMSerrorlist.add(RMSError);
		counter=0;
		counterstop=counterstop+1;
		}
		
		
		}while(counterstop!=100);
		//!(totalerror>-3.536902070221013 && totalerror<3.536902070221013)
		//3.536902070221013
		ns.save();
		ns.load();
		
		try {
			FileWriter fw=new FileWriter(new File("C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\old data\\Error.dat"));
			
			
			for(int d=0;d<totalerrorlist.size();d++)
		    {	
			fw.write(d+"|"+(totalerrorlist.get(d))+'\n');	
		    }
			
			fw.close();
		} 

		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally
		{
			
		}
		
		try {
			FileWriter fw=new FileWriter(new File("C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\old data\\RMSError.dat"));
			
			
			for(int d=0;d<totalRMSerrorlist.size();d++)
		    {	
			fw.write(d+"|"+(totalRMSerrorlist.get(d))+'\n');	
		    }
			
			fw.close();
		} 

		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally
		{
			
		}
		
		

	

	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*		int converged=0;
		
		ArrayList<Integer> noofexecs=new ArrayList<Integer>();
		ArrayList<Integer> epochnos=new ArrayList<Integer>();
		
		
		for(int noofexec = 1;noofexec<100;noofexec++)
		{
			noofexecs.add(noofexec);
			
		boolean reptype=false;
		
		NeuralStruct ns=new NeuralStruct();
		ns.setReptype(reptype);
		ns.initializeWeights();		
		double error=0.0;
		int epochno=0;
		boolean flag=true;
		boolean log=true;
		
		
		// 0 for binary, 1 for bipolar
		
		
		
		
		error=0.0;
		double a[]={1.0,1.0};
		//System.out.println(ns.train(a,1.0));
		ns.train(a,1.0);
		error=error+ns.GetOutputError();
		
			
		
		
		ArrayList<Integer> epochArr=new ArrayList<Integer>();
		ArrayList<Double> epochErrArr=new ArrayList<Double>();
		while (flag)
		{
			error=0.0;
			if (!reptype)
			{
				double d[]={0.0,0.0};
				//System.out.println(ns.train(d,1.0));
				ns.train(d,1.0);
			}
			if(reptype)
			{
				double d[]={-1.0,-1.0};
				//System.out.println(ns.train(d,1.0));
				ns.train(d,1.0);
			}
			
			error=error+ns.GetOutputError();
			//System.out.println("Training error : "+error);
			//System.out.println("#########################################End of  training a pattern#########################################");
			
			if (!reptype)
			{
			double b[]={1.0,0.0};
			ns.train(b,1.0);
			}
			if(reptype)
			{
				double d[]={1.0,-1.0};
				//System.out.println(ns.train(d,1.0));
				ns.train(d,1.0);
			}
			//System.out.println(ns.train(b,1.0));
			
			error=error+ns.GetOutputError();
			//System.out.println("Training error : "+error);
			//System.out.println("#########################################End of  training a pattern#########################################");
			
			if (!reptype)
			{
			double c[]={0.0,1.0};
			//System.out.println(ns.train(c,1.0));
			ns.train(c,1.0);
			}
			if(reptype)
			{
				double d[]={-1.0,1.0};
				//System.out.println(ns.train(d,1.0));
				ns.train(d,1.0);
			}
			error=error+ns.GetOutputError();
			//System.out.println("Training error : "+error);
			//System.out.println("#########################################End of  training a pattern#########################################");
			
			if (!reptype)
			{
			double a[]={1.0,1.0};
			//System.out.println(ns.train(a,1.0));
			ns.train(a,1.0);
			}
			if(reptype)
			{
				double d[]={1.0,1.0};
				//System.out.println(ns.train(d,1.0));
				ns.train(d,1.0);
			}
			error=error+ns.GetOutputError();
			//System.out.println("Training error : "+error);
			//System.out.println("#########################################End of  training a pattern#########################################"); 
			
			
			
			
			
			//System.out.println("Training error : "+error);
			epochErrArr.add(error);
			//System.out.println("Epoch error : "+error+"\n" );
			if ((error<0.05 && error>-0.05)||epochno==5000)
			{
				flag=false;	
				
				
				
				if(error<0.05)
				{
					converged=converged+1;
				}
					
				
				
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet=workbook.createSheet("data");
				ArrayList<XSSFRow> rowlist=new ArrayList<XSSFRow>();
				for (int j=0;j<epochArr.size();j++)
				{
					XSSFCell l=sheet.getRow(j).getCell(0);
					l.setCellValue(j);
					rowlist.add(sheet.createRow(j));
					sheet.createRow(epochArr.get(j)).createCell(1).setCellValue(epochArr.get(j));
					sheet.createRow(epochArr.get(j)).createCell(2).setCellValue(epochErrArr.get(j));
					rowlist.get(j).createCell(1).setCellValue(epochArr.get(j));
					rowlist.get(j).createCell(2).setCellValue(epochErrArr.get(j));
				}
				
				for(int b=0;b<epochArr.size();b++)
				{
					rowlist.get(b).getCell(0).setCellValue(b);
					rowlist.get(b).getCell(noofexec).setCellValue(epochArr.get(b));
				}
				
				try {
					FileOutputStream out =new FileOutputStream( new File("C:\\robocode\\data.xlsx"));
					workbook.write(out);
					out.close();
					workbook.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			epochArr.add(epochno);
			epochno=epochno+1;
			
			
			
*/			
			
			
/*		}
		
		
		
		
		System.out.println("Epoch number is :"+epochno+" error is "+error);
		epochnos.add(epochno);
		
	}
	
		System.out.println("converged number is :"+converged);
		
		XSSFWorkbook workbook1 = new XSSFWorkbook();
		XSSFSheet sheet=workbook1.createSheet("data2");
		ArrayList<XSSFRow> rowlist2=new ArrayList<XSSFRow>();
		for (int j=0;j<noofexecs.size();j++)
		{
			XSSFCell l=sheet.getRow(j).getCell(0);
			l.setCellValue(j);
			rowlist2.add(sheet.createRow(j));
			sheet.createRow(epochArr.get(j)).createCell(1).setCellValue(epochArr.get(j));
			sheet.createRow(epochArr.get(j)).createCell(2).setCellValue(epochErrArr.get(j));
			rowlist2.get(j).createCell(1).setCellValue(noofexecs.get(j));
			rowlist2.get(j).createCell(2).setCellValue(epochnos.get(j));
			
		}
		
		try {
			FileOutputStream out =new FileOutputStream( new File("C:\\robocode\\dataepoch.xlsx"));
			workbook1.write(out);
			out.close();
			workbook1.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}



	}

