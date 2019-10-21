package robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class LUT {
//	ArrayList<double[]> ActionList;
	class randEnum<E extends Enum<STATE>>{
		Random RND= new Random();
		E[] values;

		public randEnum(Class<E> token){
			values=token.getEnumConstants();
		}
		public E random(){
			return values[RND.nextInt(values.length)];
		}
}
//	ArrayList<double[]> LUT=new ArrayList<double[]>();
//	double[] stateActionAndProbablity = new double[13];
	private enum STATE{
		fire,
		forwaard,
		leftforward,
		rightforward,
		back
	}
	randEnum<STATE> randSTATE= new randEnum<STATE>(STATE.class);
	HashMap<STATE,double[]> actionList =new HashMap<>();
	HashMap<double[],HashMap<double[],Double>> SAP = new HashMap<double[], HashMap<double[],Double>>();
	int stateSize;
	int actionSize;
	public LUT()
	{
		this.stateSize=7;
		this.actionSize=5;
//		this.ActionList=new ArrayList<double[]>();
		double fire[]={1,0,0,0,0};
		double forward[]={0,1,0,0,0};
		double leftforward[]={0,0,1,0,0};
		double rightforward[]={0,0,0,1,0};
		double back[]={0,0,0,0,1};

		this.actionList.put(STATE.fire,fire);
		this.actionList.put(STATE.forwaard,forward);
		this.actionList.put(STATE.leftforward,leftforward);
		this.actionList.put(STATE.rightforward,rightforward);
		this.actionList.put(STATE.back,back);

//		this.ActionList.add(fire);
//		this.ActionList.add(forward);
//		this.ActionList.add(leftforward);
//		this.ActionList.add(rightforward);
//		this.ActionList.add(back);
//		this.Load();
		
	}
	
	public double[] Lookup(double[] state)
	{   
//		if(!isState_in_LUT(state))
//		{	double[] randomActionforFill=new double[5];
//			System.arraycopy(RandomAction(), 0, randomActionforFill, 0, randomActionforFill.length-1);
//			initialize_StateActions_LUT(state,randomActionforFill);
//		}
//		//double fire[]={1,1,1,0,0};
//		double action[]=new double[5];
//		//actonSelection(state);
//		System.arraycopy(actonSelection(state), 0, action, 0, action.length-1);
//		return 	action;

		if(this.SAP.containsKey(state)){
			return this.bestAction(state);
		}else{
			return this.loadRandomStateAction(state);
		}
	}
	public void train(double[] state,double[] action,Double reward)
	{
		if (this.SAP.containsKey(state)){
				this.SAP.get(state).put(action,reward);
		}else{
			HashMap<double[],Double> d =new HashMap<>();
			d.put(action,reward);
			this.SAP.put(state,d);
		}

//		double[] stateAction=MergeArray(state,action);
//		if(isActionState_in_LUT(stateAction))
//		{
//			int c=getIndex_of_State_Action_FromLUT(stateAction);
//			LUT.get(c)[LUT.get(c).length-1]=reward;
//		}else{
//			initialize_StateActions_LUT(state,action);
//			if(!isActionState_in_LUT(stateAction))
//			{
//				System.out.print("Its not working, i dont know why is this");
//			}
//			int c=getIndex_of_State_Action_FromLUT(stateAction);
//			LUT.get(c)[LUT.get(c).length-1]=reward;
//		}
		
	}
	public double getQfromLUTforState_And_Action(double[] state,double[] action)
	{
		//ActionCheck(state);
//		double[] State_Action = MergeArray(state,Action);
//		if(!isActionState_in_LUT(State_Action))
//		{
//			initialize_StateActions_LUT(state,Action);
//		}
//
//		int c=getIndex_of_State_Action_FromLUT(State_Action);
//		double Q=LUT.get(c)[LUT.get(c).length-1];
//		return Q;

		if (this.SAP.containsKey(state)){
			if (this.SAP.get(state).containsKey(action)){
				return this.SAP.get(state).get(action);
			}else{

				this.SAP.get(state).put(action,0.0);
				return 0.0;
			}
		}else{
			HashMap<double[],Double> d= new HashMap<>();
			d.put(action,0.0);
			this.SAP.put(state,d);
			return 0.0;
		}
		
	}

	private double[] bestAction(double[] state){
		HashMap<double[],Double> actionQ= this.SAP.get(state);
		Double Q=Double.MIN_VALUE;
		double[] action=new double[this.actionSize];
		Arrays.fill(action,0.0);
		for(Map.Entry<double[],Double> entry : actionQ.entrySet()){
			if(entry.getValue()>Q){
				Q=entry.getValue();
				action=entry.getKey();
			}
		}
		return action;

	}
//	private double[] actonSelection(double[] state)
//	{
//		double[] tempAction = null;
//		boolean firstcompare=true;
//		int intexofProbablity=0;
//		int intexofOldProbablity=0;
//		double probablity=0.0;
//		double Oldprobablity=0.0;
//		double largestProbablity=0;
//		int largeprobbalityIndex=0;
//		boolean StateActionMatch=false;
//		//Checking if LUT have probablities initialied to 0 then just randomly initialiing
//		//int statActionvisitCheck=0;
//		for(int y=0;y<ActionList.size();y++)
//		{
//			double[] state_Action = MergeArray(state,ActionList.get(y));
//			if(isActionState_in_LUT(state_Action))
//			{
//				for(int m=0;m<LUT.size();m++)
//				{
//						double[] tempState_Action_FromLUT=new double[state_Action.length];
//						System.arraycopy( LUT.get(m), 0, tempState_Action_FromLUT, 0, tempState_Action_FromLUT.length);
//						//The greedy move
//						if(Arrays.equals(tempState_Action_FromLUT, state_Action))
//						{
//								if(firstcompare)
//								{
//									Oldprobablity=LUT.get(m)[LUT.get(m).length-1];
//									intexofOldProbablity=m;
//									firstcompare=false;
//									StateActionMatch=true;
//									System.out.println(Arrays.toString(LUT.get(m)));
//								}else
//								{
//										probablity=LUT.get(m)[LUT.get(m).length-1];
//										intexofProbablity=m;
//										StateActionMatch=true;
//										if(probablity>Oldprobablity)
//										{
//											Oldprobablity=probablity;
//											intexofOldProbablity=intexofProbablity;
//
//										}
//										System.out.println(Arrays.toString(LUT.get(m)));
//								}
//						}
//				}
//			}
//		}
//		largestProbablity=Oldprobablity;
//		largeprobbalityIndex=intexofOldProbablity;
//		System.out.println("The LUT selected for Action is "+Arrays.toString(LUT.get(intexofOldProbablity)));
//			if(!StateActionMatch)
//			{
//				System.out.println("best action is not selected form the LUT");
//			}
//
//		tempAction=Arrays.copyOf(returnActionFromLUT_stateAction(LUT.get(largeprobbalityIndex)), returnActionFromLUT_stateAction(LUT.get(largeprobbalityIndex)).length);
//		return tempAction;
//	}
//	private double[] returnActionFromLUT_stateAction(double[] stateActionAndProbablity )
//	{
//
//		double[] tempz = new double[5];
//	    System.arraycopy( stateActionAndProbablity,7 , tempz, 0, 5 );
//		return tempz;

//	}
//	private boolean isState_in_LUT(double[] state)
//	{
//		boolean statePresence=false;
//		//checking state in LUT
//		for(int lut=0;lut<LUT.size();lut++)
//		{
//			double[] tempstate=new double[state.length];
//			System.arraycopy(LUT.get(lut), 0, tempstate, 0, state.length-1);
//			if(Arrays.equals(tempstate, state))
//			{
//				statePresence=true;
//				break;
//			}
//		}
//
//		/*for(int actions=0;actions<ActionList.size();actions++)
//		{
//			lookAndADDinLUT(state,ActionList.get(actions));
//		}*/
//		return statePresence;
//
//
//	}
	private double[] loadRandomStateAction(double[] state){
		HashMap <double[],Double> d =new HashMap<>();
		double[] randAction=this.RandomAction();
		d.put(randAction,0.0);
		this.SAP.put(state,d);
		return randAction;
	}
//	private void initialize_StateActions_LUT(double[] state,double[] action)
//	{
//		double[] temp0 = MergeArray(state,action);
//	    boolean flag=false;
//	    for(int j=0;j<LUT.size();j++)
//	    {
//	    	if(checkActionStateEquality(LUT.get(j), temp0))
//	    	{
//	    		flag=true;
//	    		break;
//	    	}
//	    }
//	    if(!flag)
//	    {
//	    	double Qtemp[]={0};
//	    	LUT.add(MergeArray(temp0,Qtemp));
//	    }
//
//
//
//	}
//	private int getIndex_of_State_Action_FromLUT(double[] StateAction)
//	{
//		int IndexforStateActioninLUT =0;
//		boolean breakflg=false;
//		for(int g=0;g<LUT.size();g++)
//		{
//				//boolean flag=true;
//				double le=StateAction.length;
//				double tempStateActionFromLUt[]=new double[StateAction.length];
//				System.arraycopy( LUT.get(g), 0, tempStateActionFromLUt, 0, LUT.get(g).length-1 );
//				if(Arrays.equals(tempStateActionFromLUt, StateAction))
//				{
//					IndexforStateActioninLUT=g;
//					breakflg=true;
//					break;
//				}
//		}
//		if(!breakflg)
//		{
//			System.out.print("Its not working");
//			return 0;
//		}else
//		{
//			return IndexforStateActioninLUT;
//		}
//
//
//	}
//	private boolean checkActionStateEquality (double[] stateAction_With_Probablity,double[] stateActionInput_toCheck)
//	{
//
//		boolean flag=false;
//		double tempStateActionFromLUt[]=new double[stateActionInput_toCheck.length];
//		System.arraycopy( stateAction_With_Probablity, 0, tempStateActionFromLUt, 0, stateActionInput_toCheck.length );
//		if(Arrays.equals(tempStateActionFromLUt, stateActionInput_toCheck))
//		{
//			flag=true;
//		}
//
//		return flag;
//
//	}
//	private double[] MergeArray(double[] Arr1,double[] Arr2)
//	{
//
//		double[] temp = new double[ Arr1.length + Arr2.length ];
//	    System.arraycopy( Arr1, 0, temp, 0, Arr1.length );
//	    System.arraycopy( Arr2, 0, temp, Arr1.length, Arr2.length );
//		return temp;
//
//	}
//	private boolean isActionState_in_LUT(double[] StateAction)
//	{
//		boolean found=false;
//		for(int count=0;count<LUT.size();count++)
//		{
//			double tempStateActionFromLUt[]=new double[StateAction.length];
//			System.arraycopy( LUT.get(count), 0, tempStateActionFromLUt, 0, tempStateActionFromLUt.length);
//			if(Arrays.equals(tempStateActionFromLUt, StateAction))
//			{
//				found=true;
//				break;
//			}
//
//		}
//
//		return found;
//
//	}
//	public ArrayList<double[]> getLUT()
//	{
//		return LUT;
//	}
	public double[] RandomAction()
	{
//		Random myrand=new Random();
//		STATE.values()
//		int ActualRand=myrand.nextInt(ActionList.size());
//		return ActionList.get(ActualRand);
		return this.actionList.get(randSTATE.random());
	}
//	public void WriteLUT()
//	{
//		WriteData u=new WriteData(LUT);
//	}
//	private void Load()
//	{
//		String file ="C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\FINALLUT.dat";
//		double[] robotval=new double[13];
//		ArrayList<double[]> stateActionQ=new ArrayList<double[]>();
//		try {
//
//		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//		    String line;
//		    // Uncomment the line below if you want to skip the fist line (e.g if headers)
//		    // line = br.readLine();
//
//		    while ((line = br.readLine()) != null) {
//		    	String Filtered=line.substring(1, line.length()-1);
//
//		    	for (int c=0;c<Filtered.split(",").length;c++)
//		    	{
//		    		robotval[c]=Double.parseDouble(Filtered.split(",")[c]);
//		    		/*if(c==0 || c==1)
//		    		{
//		    			robotval[c]=(Double.parseDouble(Filtered.split(",")[c]))/10;
//		    		}else
//		    		{
//		    			robotval[c]=Double.parseDouble(Filtered.split(",")[c]);
//		    		}*/
//
//
//		    	}
//		    	stateActionQ.add(Arrays.copyOf(robotval, robotval.length));
//
//		    	//line.split(",");
//
//		        // do something with line
//
//		    }
//		    br.close();
//
//		} catch (IOException e) {
//		    System.out.println("ERROR: unable to read file " + file);
//		    e.printStackTrace();
//		}
//
//		this.LUT.addAll(stateActionQ);
//		System.out.println("loading from lut");
//	}
}
