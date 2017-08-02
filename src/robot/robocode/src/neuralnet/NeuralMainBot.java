package robot.robocode.src.neuralnet;




public class NeuralMainBot {
	static NeuralStruct bb;
	public NeuralMainBot()
	{
		boolean reptype=true;
		NeuralStruct ns=new NeuralStruct();
		ns.setReptype(reptype);
		ns.initializeWeights();
		ns.load();
		this.bb=ns;
	}
	
	public double getQforStateAction(double[] StateAction)
	{
		return bb.GetQfromNN(StateAction);
		
	}
	public void trainNewQ(double Q)
	{
		bb.LearnQtoNN(Q);
	}
	public void SaveNN()
	{
		bb.save();
	}


	}

