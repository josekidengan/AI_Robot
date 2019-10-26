package robot;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class LUT {
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

	private enum STATE{
		fire,
		forwaard,
		leftforward,
		rightforward,
		back
	}
	randEnum<STATE> randSTATE= new randEnum<STATE>(STATE.class);
	HashMap<STATE,double[]> actionList =new HashMap<>();
	HashMap<double[],HashMap<double[],Double>> SAP = new HashMap<>();
	int stateSize;
	int actionSize;
	public LUT()
	{
		this.stateSize=7;
		this.actionSize=5;
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

		
	}
	
	public double[] Lookup(double[] state)
	{   

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

	}
	public double getQfromLUTforState_And_Action(double[] state,double[] action)
	{

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
	private double[] loadRandomStateAction(double[] state){
		HashMap <double[],Double> d =new HashMap<>();
		double[] randAction=this.RandomAction();
		d.put(randAction,0.0);
		this.SAP.put(state,d);
		return randAction;
	}

	public double[] RandomAction()
	{
		return this.actionList.get(randSTATE.random());

	}
	public void WriteLUT(){
		try
		{
			FileOutputStream fos =
					new FileOutputStream("hashmap.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.SAP);
			oos.close();
			fos.close();
			System.out.printf("Serialized HashMap data is saved in hashmap.ser");
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
