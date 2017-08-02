/*package neuralnet;

import java.util.ArrayList;

public class NeuralStructure {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		

		//Layer1  Creation 				
				//Neural net layer1 neuron count(layer1)		
						 int layer1nodenum=4;
				// Neural net layer1 node input count	
						 int L1inputnum=2;
						 
						
	
				//Neural net input arrayList
						 ArrayList<Double> Input=new ArrayList<Double>();
						
					//public void Setinput(double in1,double in2)
					//{
						//inputs
						//Bias
						Input.add(1.00);
						//Other inputs
						Input.add(1.00);
						Input.add(1.00);
					///}
					

				//INput weights		
						ArrayList<Double> weights=new ArrayList<Double>();
						weights.add(0.020);
						weights.add(0.050);
						weights.add(0.0560);
				
				//Creating layer 1 neuron structure
					 ArrayList<Neuralnode> layer1=new ArrayList<Neuralnode>();
					
							
							for (int i1=0;i1<=layer1nodenum-1;i1++)
								{
									layer1.add(new Neuralnode(Input,L1inputnum));
								}
					//Adding weights to layer 1		
							for (int i2=0;i2<=layer1nodenum-1;i2++)
								{
									layer1.get(i2).SetWeight();
								}
					
							for (int i3=0;i3<=layer1nodenum-1;i3++)
							{
								layer1.get(i3).Activate();
							}
							
					//Testing the output for layer1
							for (int i4=0;i4<=layer1nodenum-1;i4++)
								{
									System.out.println("The output of the "+i4+" th node is "+layer1.get(i4).GetOutPut());
								}
					
						
		// LAYER 2 NEURON NODE CREATION
					
							//Neural net layer2 neuron count(layer2)		
									int layer2nodenum=1;
							//Neural net layer2 neuron input count(layer2)		
									int l2inputnum=4;
							//Creating neuron Array for level 2 
							ArrayList<Neuralnode> layer2=new ArrayList<Neuralnode>();
							//Layer 2 input values Array
							ArrayList<Double> Input2=new ArrayList<Double>();
							//layer 2 weights Array
							ArrayList<Double> Weights2=new ArrayList<Double>();
							Weights2.add(0.04);
							Weights2.add(0.04);
							Weights2.add(0.04);
							Weights2.add(0.04);
							Weights2.add(0.04);
							//Adding Bias
									Input2.add(1.00);
							//ADDING INPUT OF LAYER1 TO LAYER2 input Array
							
									for (int x=0;x<=layer1nodenum-1;x++)
										{
											//System.out.println("the value of loop variable is "+x);
											Input2.add(layer1.get(x).GetOutPut());
										}
							//ADDING Neuron to Layer 2
							for (int i=0;i<=layer2nodenum-1;i++)
							{			
								layer2.add(new Neuralnode(Input2,l2inputnum));
							}
						//Adding Weights to layer 2
						layer2.get(0).SetWeight();
						layer2.get(0).Activate();
							//Getting output to layer 2
							System.out.println("The out value of the neural net "+layer2.get(0).GetOutPut());
						
					

				
				for(int c=0;c<layer1nodenum;c++)
				{
					System.out.println("Weight on "+c+" th node are"+layer1.get(c).GetWeights());
					
					
				}
				for(int c=0;c<layer1nodenum;c++)
				{
					
					System.out.println("output on "+c+" th node is"+layer1.get(c).GetOutPut());
					
				}

			
		
		
	}

}
*/