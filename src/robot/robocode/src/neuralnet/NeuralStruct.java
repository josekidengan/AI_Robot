package robot.robocode.src.neuralnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class NeuralStruct implements NeuralNetInterface  {
	 
	private double inputArray[];
	private double OutputError;
	
	private double OutputErrorForRMS;
	/**
	 * @return the outputErrorForRMS
	 */
	public double getOutputErrorForRMS() {
		return OutputErrorForRMS;
	}
	
	private boolean log=false;
	private double ErrorInfoTerm;
	boolean reptype;
	private double ExpectedOutNeuralNet;

	
	
	

	
	
//Initialization of variables for Layer 1
	//Neural net layer1 neuron count(layer1)		
	 private int layer1nodenum=10;
	 // Neural net layer1 node input count	
	 private  int L1inputnum=12;
	//Creating neuron Array for level 1 
	 private ArrayList<Neuralnode> layer1=new ArrayList<Neuralnode>();
	 //Layer 1 input values Array
	 private  ArrayList<Double> Input=new ArrayList<Double>();
	 private  ArrayList<Double> Weights=new ArrayList<Double>();
	 //Random object for weight input
	 Random rr=new Random();
	 //Random weignt for bias
	 private double randbiasweight;
	
	 
	 
//Initialization of the variables for layer2
	//Neural net layer2 neuron count(layer2)		
		private int layer2nodenum=1;
	//Neural net layer2 neuron input count(layer2)		
		private int l2inputnum=10;
	//Creating neuron Array for level 2 
		private ArrayList<Neuralnode> layer2=new ArrayList<Neuralnode>();
	//Layer 2 input values Array
		private ArrayList<Double> Input2=new ArrayList<Double>();
	
	
	public NeuralStruct()
	{
	this.Start();
	}
// creating neural net structure
	private void Start()
	{	
	//creating neurons and connecting input to neurons for layer1
		for (int i1=0;i1<=layer1nodenum-1;i1++)
			{
				//System.out.println(Input.toString());
				layer1.add(new Neuralnode(L1inputnum));
				layer1.get(i1).setReptype(this.reptype);
				
					
			}
		
	//creating neurons and connecting input to neurons for layer2
		for (int i=0;i<=layer2nodenum-1;i++)
		{			
			layer2.add(new Neuralnode(l2inputnum));
			layer2.get(i).setReptype(this.reptype);
			
		}
		
	}
	
	public void SetReptypeneurones()
	{
		
		for (int i1=0;i1<=layer1nodenum-1;i1++)
		{
			
			layer1.get(i1).setReptype(this.reptype);
			
				
		}
	

	for (int i=0;i<=layer2nodenum-1;i++)
	{			
		
		layer2.get(i).setReptype(this.reptype);
		
	}
		
	}
	
	public double GetOutputError()
	{
		return this.OutputError;
	}
	
	public void setInput(double train[])
	{
		/*//adding bias layer 1
				Input.add(1.00);*/
			//adding training input variables to input array list
		this.inputArray=train;
		      Input.clear();	
				for (int j1=0;j1<train.length;j1++)
				{
					Input.add(train[j1]);
				}
				
				//setting input to neurons for layer1
				for (int i1=0;i1<=layer1nodenum-1;i1++)
					{
						//System.out.println(Input.toString());
						layer1.get(i1).SetInput(Input);
						if(log)
						{
						System.out.println("The input of the neuron no "+i1+" in the layer 1 is "+layer1.get(i1).Getinput().toString());
						}
					}
			
				
	}
	
//method to initialize weights	
	public void SetWeights(double v)
	{
		//Initializing weights for layer 1
		for (int i2=0;i2<layer1nodenum;i2++)
		{
			
		//generating weigts for layer1
			for (int b1=0;b1<L1inputnum;b1++)
			{
				Weights.add(v);
									
			}
			
				randbiasweight=v;
			
			
		//Assigning weights to the neuron in layer1
			layer1.get(i2).SetWeight(Weights,randbiasweight);
			//printing weights
			if (log)
			{
			System.out.println("Weights for "+i2+" th neuron layer 1 is "+layer1.get(i2).GetWeights().toString()+" the bias weight is "+layer1.get(i2).GetBiasWeight());
			}
			Weights.clear();
		}
		
	//Initializing weights for layer 2
		
		for (int i7=0;i7<layer2nodenum;i7++)
		{
			for (int b2=0;b2<l2inputnum;b2++)
			{
				Weights.add(v);
			}
			randbiasweight=v;
			
		    layer2.get(i7).SetWeight(Weights,randbiasweight);
		    if(log)
		    {
		    System.out.println("Weights for "+i7+" th neuron layer 2 is "+layer2.get(i7).GetWeights().toString()+" the bias weight is "+layer2.get(i7).GetBiasWeight());
		    }
		    Weights.clear();
		}
	}
	
	
	public void SetWeights()
	{
		//Initializing weights for layer 1
				for (int i2=0;i2<layer1nodenum;i2++)
				{
					
				//generating weigts for layer1
					for (int b1=0;b1<L1inputnum;b1++)
					{
						Weights.add(-0.5+rr.nextDouble());
											
					}
					
						randbiasweight=-0.5+rr.nextDouble();
					
					
				//Assigning weights to the neuron in layer1
					layer1.get(i2).SetWeight(Weights,randbiasweight);
					//printing weights
					if(log)
					{
					System.out.println("Weights for "+i2+" th neuron layer 1 is "+layer1.get(i2).GetWeights().toString()+" the bias weight is "+layer1.get(i2).GetBiasWeight());
					}
					Weights.clear();
				}
				
			//Initializing weights for layer 2
				
				for (int i7=0;i7<layer2nodenum;i7++)
				{
					for (int b2=0;b2<l2inputnum;b2++)
					{
						Weights.add(-0.5+rr.nextDouble());
					}
					randbiasweight=-0.5+rr.nextDouble();
					
				    layer2.get(i7).SetWeight(Weights,randbiasweight);
				    if (log)
				    {
				    System.out.println("Weights for "+i7+" th neuron layer 2 is "+layer2.get(i7).GetWeights().toString()+" the bias weight is "+layer2.get(i7).GetBiasWeight());
				    }
				    Weights.clear();
				}
	}
	
	
	
	
	
	public double GetoutputNet()
	{
		return layer2.get(0).GetOutPut();
		
	}
	
//Training for a specific value
	public void trainit()
	{	
		this.forwardprop();
		this.backprop();
		
	}
	
	
	
	private void forwardprop()
	{
		if (log)
		{
			System.out.println("Forward Propgating------------------------------------------------------------------------------------");	
		}
		
	//Printing Weights from Layer2
		
		for (int i7=0;i7<layer2nodenum;i7++)
		{
		if (log)
		    {
			System.out.println("WEIGHTS STATUS LAYER 2");
		    System.out.println("Weights for "+i7+" th neuron layer 2 is "+layer2.get(i7).GetWeights().toString()+" the bias weight is "+layer2.get(i7).GetBiasWeight());
		    System.out.println("*****************************************************");
		    }
		}
		
	//Printing layer1 Weight
		for (int i2=0;i2<layer1nodenum;i2++)
		{
		if (log)
		{
		System.out.println("WEIGHTS STATUS LAYER 1");
		System.out.println("Weights for "+i2+" th neuron layer 1 is "+layer1.get(i2).GetWeights().toString()+" the bias weight is "+layer1.get(i2).GetBiasWeight());
		 System.out.println("*****************************************************");
		}
		}
	//activating layer1 neurons for output
		for (int i3=0;i3<=layer1nodenum-1;i3++)
		{
			layer1.get(i3).Activate();
			if (log)
			{
			System.out.println("Layer 1 neuron no "+i3+" Activated");
			System.out.println("Layer 1 neuron no "+i3+" Weighted Sum is : "+layer1.get(i3).GetWeightedSum());
			}
		}
		
	//view Output for layer1
		for (int i4=0;i4<=layer1nodenum-1;i4++)
			{
			if (log)
			{
			System.out.println("The output of the "+i4+" th node in the layer 1 is "+layer1.get(i4).GetOutPut());
			}
			}
		

	//ADDING output OF LAYER1 TO LAYER2 input Array List
		for (int x=0;x<=layer1nodenum-1;x++)
			{
				Input2.add(layer1.get(x).GetOutPut());
			}
		
	//Setting input to layer 2
		for (int x=0;x<=layer2nodenum-1;x++)
		{
			layer2.get(x).SetInput(Input2);
			if (log)
			{
			System.out.println("The input of the neuron no "+x+" in the layer 2 is "+layer2.get(x).Getinput().toString());
			}
		}
	//Activating layer 2
		for (int x=0;x<=layer2nodenum-1;x++)
		{
			layer2.get(x).Activate();
			if (log)
			{
				
			System.out.println("Layer 2 neuron no "+x+" Activated");
			System.out.println("Layer 2 neuron no "+x+" Weighted Sum is : "+layer2.get(x).GetWeightedSum());
			}
		}

	//View output to layer 2
		if(log)
		{
		System.out.println("The out value of the neural net "+layer2.get(0).GetOutPut());
		}
		Input.clear();
		Input2.clear();
		
// calculating error
		if(log)
		{
		System.out.println("The expected output for input "+inputArray[0]+" and "+inputArray[1]+" is "+outputFor(inputArray));
		System.out.println("The Actual Neural net output  is "+layer2.get(0).GetOutPut());
		}
		
		OutputError=0.5*Math.pow(layer2.get(0).GetOutPut()-outputFor(inputArray), 2.0);
		//OutputError=layer2.get(0).GetOutPut()-outputFor(inputArray);
		OutputErrorForRMS=Math.pow(layer2.get(0).GetOutPut()-outputFor(inputArray), 2.0);
		if(log)
		{
			System.out.println("The Squared error for training pattern "+ inputArray[0]+" and "+inputArray[1] +" is "+OutputError);
		}
		
		
	}
	
	private void backprop()
	{

		if (log)
		{
		System.out.println("Backward Propgating-----------------------------------------------------------------------------------");
		}
		double deriv = 0;
		//Computing Layer 2 Error info term
		for (int x=0;x<=layer2nodenum-1;x++)
		{
			double u=layer2.get(0).GetOutPut();
			double C=outputFor(inputArray);
						
			if(!this.reptype)
			{
				deriv=u*(1-u);
			}
			
			if(this.reptype)
			{
				deriv=0.5*(1+u)*(1-u);
				//deriv=0.05*(10+u)*(10-u);
			}
			double errorinfo=(C-u)*deriv;
			ErrorInfoTerm=errorinfo;
			layer2.get(x).setErrorInfoTerm(ErrorInfoTerm);
			if(log)
			{
				System.out.println(" The u of  "+x+" neuron in Layer2 is  "+u);
				System.out.println(" The C of  "+x+" neuron in Layer2 is  "+C);
				System.out.println(" The u(1-u) "+x+" neuronin Layer2 is  "+deriv);
			System.out.println("Layer 2 neuron no "+x+" error info term set to "+ErrorInfoTerm);
			System.out.println("*********************************************************************");
			}
		}
		
		//Updating Layer2 weights

		if (log)
		{
		System.out.println(" UPDATING WEIGHTS LAYER 2---------------------------");
		}
		for (int x=0;x<=layer2nodenum-1;x++)
		{
			if(log)
			{
			System.out.println(" Error Info Term for "+x+" th neuron in layer2 : "+layer2.get(x).getErrorInfoTerm());
			System.out.println(" Output for "+x+" th neuron in layer2 : "+layer2.get(x).GetOutPut());
			System.out.println(" Current Weights for "+x+" th neuron in layer2 : "+layer2.get(x).GetWeights());
			}
			layer2.get(x).ComputeNewWeight();
			if(log)
			{
			System.out.println("New updated weights for Layer 2 neuron no "+x+" is "+layer2.get(x).GetWeights().toString()+" the bias weight is  "+layer2.get(x).GetBiasWeight());
			}
		}	
		
		
		
		//Computing Layer 1 Error info term
				
			for (int i3=0;i3<=layer1nodenum-1;i3++)
			{
				for (int x=0;x<=layer2nodenum-1;x++)
				{
					double u1=layer1.get(i3).GetOutPut();
					double deriv2 = 0;
					if(!this.reptype)
					{
						deriv2=u1*(1-u1);
					}
					
					if(this.reptype)
					{
						deriv2=0.5*(1+u1)*(1-u1);
						//deriv2=0.05*(10+u1)*(10-u1);
					}
					
					double dowh=layer2.get(x).getErrorInfoTerm();
					double wh=layer2.get(x).GetWeights().get(i3);
							
				layer1.get(i3).setErrorInfoTerm(deriv2*dowh*wh);
				if(log)
				{
					System.out.println(" The u of  "+i3+" neuron in Layer1 is  "+u1);
					System.out.println(" The Dowh for  "+i3+" neuron in Layer1 is  "+dowh);
					System.out.println(" The wh of  "+i3+" neuron in Layer1 is  "+wh);
					System.out.println(" The u(1-u) "+i3+" neuron in Layer1 is  "+deriv2);
				System.out.println("Error info term in the Layer 1 neuron no "+i3+" is "+layer1.get(i3).getErrorInfoTerm());
				System.out.println("*********************************************************************");
				}
			}
			
			
		}
		
			
		//Updating layer 1 Weights

			if (log)
			{
			System.out.println(" UPDATING WEIGHTS LAYER 1---------------------------");
			}
		for (int i3=0;i3<=layer1nodenum-1;i3++)
		{
			if(log)
			{
			System.out.println(" Error Info Term for "+i3+" th neuron in layer1 : "+layer1.get(i3).getErrorInfoTerm());
			System.out.println(" Output for "+i3+" th neuron in layer1 : "+layer1.get(i3).GetOutPut());
			System.out.println(" Current Weights for "+i3+" th neuron in layer2 : "+layer1.get(i3).GetWeights());
			}
			layer1.get(i3).ComputeNewWeight();
			if(log)
			{
			System.out.println("New updated weights for neuron no "+i3+"  inn the layer 1 are "+layer1.get(i3).GetWeights().toString() + " and bias weight is "+layer1.get(i3).GetBiasWeight());
			}
		}
		
				
	}
	
	public double outputFor(double[] X) {
		
		return ExpectedOutNeuralNet;	
		}
	
	
	public double train(double[] X, double argValue) {
		this.setInput(X);
		this.ExpectedOutNeuralNet=argValue;
		this.trainit();
		return this.GetoutputNet();
	}
	
	public double GetQfromNN(double[] X)
	{
		this.setInput(X);
		this.forwardprop();
		return this.GetoutputNet();
		
	}
	
	public void LearnQtoNN(double X)
	{
		this.ExpectedOutNeuralNet=X;
		this.backprop();
		
	}
	
	
	
	public void setReptype(boolean reptype) {
		this.reptype = reptype;
		SetReptypeneurones();
	}
	@Override
	public void save() {
		
		try {
			FileWriter fw=new FileWriter(new File("C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\MyBot.data\\NEURALDATA.dat"));
			
			for (int x=0;x<=layer2nodenum-1;x++)
			{
				
				fw.write("L2|N"+x+":"+layer2.get(x).GetWeights().toString()+layer2.get(x).GetBiasWeight()+'\n');
				
				
			}
			
			for (int i3=0;i3<=layer1nodenum-1;i3++)
			{
				fw.write("L1|N"+i3+":"+layer1.get(i3).GetWeights().toString()+layer1.get(i3).GetBiasWeight()+'\n');
				//fw.write("Layer 2 Weights are :"+'\n'+layer1.get(i3).GetWeights().toString());
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
	@Override
	public void load() {
		// TODO Auto-generated method stub
		String file = "C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\MyBot.data\\NEURALDATA.dat";

		try {

		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		    String line;
		    // Uncomment the line below if you want to skip the fist line (e.g if headers)
		    // line = br.readLine();
		    while ((line = br.readLine()) != null) {
		    	String Filtered=line.substring(line.indexOf("[")+1, line.indexOf("]"));
		    	double bias=Double.parseDouble(line.substring(line.indexOf("]")+1, line.length()-1));
		    	int Layer=Integer.parseInt(line.substring(line.indexOf("L")+1, line.indexOf("|")));
		    	int Neuron=Integer.parseInt(line.substring(line.indexOf("N")+1, line.indexOf(":")));
		    	
		    	if(Layer==2)
		    	{
		    			ArrayList<Double> tempWeights=new ArrayList<Double>();
		    			for (int c=0;c<Filtered.split(",").length;c++)
				    	{
		    				
		    				tempWeights.add(Double.parseDouble(Filtered.split(",")[c]));
				    	}
		    			layer2.get(Neuron).SetWeight(tempWeights, bias);					
		    	}else
		    	{
		    		ArrayList<Double> tempWeights=new ArrayList<Double>();
	    			for (int c=0;c<Filtered.split(",").length;c++)
			    	{
	    				tempWeights.add(Double.parseDouble(Filtered.split(",")[c]));
			    	}
	    			layer1.get(Neuron).SetWeight(tempWeights, bias);		    		
		    	}
		    }
		    br.close();

		} catch (IOException e) {
		    System.out.println("ERROR: unable to read file " + file);
		    e.printStackTrace();   
		}
	}
	@Override
	public double sigmoid(double x) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void initializeWeights() {
		this.SetWeights();
		
	}
	@Override
	public void zeroWeights() {
		// TODO Auto-generated method stub
		this.SetWeights(0.1);
	}
}
