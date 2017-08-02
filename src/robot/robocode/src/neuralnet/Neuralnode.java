package robot.robocode.src.neuralnet;
import java.util.ArrayList;

public class Neuralnode {
	
	//private double weights[];
	private ArrayList<Double> weightsArr=new ArrayList<Double>();
	private ArrayList<Double> weightsArrFirstPrevious=new ArrayList<Double>();
	private ArrayList<Double> weightsArrSecondPrevious=new ArrayList<Double>();
	private double biasInput=1.00;
	private double biasweight;
	private double firstprevbiasweight=0;
	private double secondprevbiasweight=0;
	private boolean log=false;
	
	//private double[] inarr;
	private ArrayList<Double> inpArr=new ArrayList<Double>();
	private double WeightedSum;
	private double Output;
	private int numofin;
	private double ErrorInfoTerm;
	private double DeltaWeight;
	private double LearningRate=0.002;
	
	boolean reptype;
	
	
	public Neuralnode(int numberofinput){
		this.numofin=numberofinput;
			}
	
	
	
	public void SetInput(ArrayList<Double> inarr)
	{
		this.inpArr.clear();
		this.inpArr.addAll(inarr);
		if (this.numofin==this.inpArr.size())
		{
			//System.out.println("The input values are ="+this.inpArr.toString());
					
		} else if (this.numofin!=this.inpArr.size())
		{
			if(log)
			{
			System.out.print("Input array size is "+this.inpArr.size()+" not matcing with the no of input to neurons which is : "+this.numofin);
			}
		}
		
	}
	
		
	public ArrayList<Double> Getinput()
	{
		return inpArr;
		
	}
		
		
	
	public void SetWeight(ArrayList<Double> weightsArr,double biasweight)
	{
		this.biasweight=biasweight;
		this.weightsArr.clear();
		this.weightsArr.addAll(weightsArr);
		initPrevWeightarray();
			
	}
	
	public ArrayList<Double>  GetWeights(){
		return weightsArr;
		
	}
	
	public double  GetBiasWeight(){
		return biasweight;
		
	}
	
	
	private void initPrevWeightarray()
	{
		for(int s=0;s<weightsArr.size();s++)
		{
			weightsArrFirstPrevious.add(0.0);
			weightsArrSecondPrevious.add(0.0);
		}
	}
	
	public void Activate()
	{
		double z =0;
		try{
			for (int m=0;m<inpArr.size();m++)
			{
				z=z+this.inpArr.get(m)*this.weightsArr.get(m);
			}
		}catch(IndexOutOfBoundsException e){
			System.out.println("Check if weights and input array matches: input array Size is"+inpArr.size()+" and weights array size is "+weightsArr.size());
		}	
		
		z=z+biasInput*biasweight;
		this.WeightedSum=z;
		if (!reptype)
		{
		this.Output=sigmoout(z);
		}
		if (reptype)
		{
		this.Output=bipolar(z);
		}
		
	}
	
	private double sigmoout(double a)
	{
		double sigma;
		sigma=1/(1+Math.pow(Math.E,-a));
		return  sigma;
		
	}
	
	private double bipolar(double a)
	
	{		
		double bipolar;
		bipolar=(2/(1+Math.pow(Math.E,-a)))-1;
		return  bipolar;
		
		
	}
	
	public double GetOutPut()
	{		
		return  Output;		
	}
	
	
	public double getErrorInfoTerm() {
		return ErrorInfoTerm;
	}


	public void setErrorInfoTerm(double ErrorInfoTerm) {
		this.ErrorInfoTerm = ErrorInfoTerm;
	}
	
	public double GetWeightedSum()
	{
		return this.WeightedSum;
	}
	
	public void ComputeNewWeight()
	{
		double weightdiff=0;
		
		for (int z=0;z<weightsArr.size();z++)
			
		{
			
			weightdiff=weightsArrFirstPrevious.get(z)-weightsArrSecondPrevious.get(z);
			double newweight=weightsArr.get(z)+weightdiff*0.5+LearningRate*ErrorInfoTerm*inpArr.get(z);
			/*weightsArrSecondPrevious.set(z, weightsArrFirstPrevious.get(z));
			weightsArrFirstPrevious.set(z, newweight);*/
			weightsArr.set(z, newweight);
			
		}
		weightsArrSecondPrevious.clear();
		weightsArrSecondPrevious.addAll(weightsArrFirstPrevious);
		weightsArrFirstPrevious.clear();
		weightsArrFirstPrevious.addAll(weightsArr);
		
		
		//System.out.println("The new weight of the neuron is "+weightsArr.toString());
		double biaswtdiff;
		biaswtdiff=firstprevbiasweight-secondprevbiasweight;
		biasweight=biasweight+LearningRate*ErrorInfoTerm+biaswtdiff*0;
		secondprevbiasweight=firstprevbiasweight;
		firstprevbiasweight=biasweight;
	}

	public double getDeltaWeight() {
		return DeltaWeight;
	}

	public void setDeltaWeight(double deltaWeight) {
		DeltaWeight = deltaWeight;
	}

	public void setReptype(boolean reptype) {
		this.reptype = reptype;
	}



	/**
	 * @return the weightsArr
	 */
	public ArrayList<Double> getWeightsArr() {
		return weightsArr;
	}

	
}
