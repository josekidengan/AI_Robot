package robot;


import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robot.robocode.src.neuralnet.NeuralMainBot;
import robot.robocode.src.neuralnet.NeuralStruct;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileOutputStream;
import robocode.RobocodeFileWriter;

import static robocode.util.Utils.normalRelativeAngleDegrees;


public class NNBot extends AdvancedRobot {
	
	
    static NeuralMainBot Mb;
    Random rr=new Random();
    //static LUT lut=new LUT();

    State currState= new State(7,true);
    State prevState= new State(7,true);
    HashMap<double[],Actions> act = new HashMap<>();
    //double[] stateAction=new double[12];
    
    //double[] state = new double[7];
    double[] stateErrorCheck = new double[7];
    double[] Prevstate = new double[7];
    double[] Action = new double[5];
    double[] previousAction = new double[5];
    double[] bestpreviousAction = new double[5];
    double[] ActionRandom=new double[5];
    static double error=0;
    double alpha=0.02;
    double gamma=0.6;
    boolean movingForward;
    int gunTurnAmt=20;
    int count=0;
    static int win=0;
    static int fail=0;
    static int hundreds=0;
    double reward=0;
    static ArrayList<Integer> plotbattle_nos_in_hundreds=new ArrayList<Integer>();
    static ArrayList<Integer> plotbattle_Sucess_Count=new ArrayList<Integer>();
    static ArrayList<Double> Q_Epizodes=new ArrayList<Double>();
    static ArrayList<Double> PredictionError=new ArrayList<Double>();
    static int ActionCount=0;
    //This is the number to select if random discovery is needed based on epsilon
    int epsilonRemider=4;
    boolean onpolicy=true;
    //RandomAction is to enable discovery| if this turned off it will be epsilon greedy always
    boolean exploration=false;
    //double fire[]={1,0,0,0,0};
	
// The switch for NN and LUT
	boolean NN=true;
	boolean statereductionNN=false;
	
	double HitWall=1;
	double Death=0;
	double Win=6;
	double onHitRobot=2;
	double onBulletMissed=2;
	double onHitByBullet=1;
	double onBulletHitBullet=3;
	double onBulletHit=4;
	
	/**
	 * MyFirstRobot's run method - Seesaw
	 */
	
public void run() {

		NNInitial();
        this.loadActions();
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
        while (true) {

        setTurnRadarRight(360);
        execute();
        }
		
	}

    /**
     * Fire when we see a robot
     */
    public void onScannedRobot(ScannedRobotEvent e) 
    {
    	out.print("ok");
    	prevState.setState(currState.getState());
        currState.setState(e, this);
        //out.println(Arrays.toString(currState.getState()));
        double Action[]=GetStatewithHighQFromNN(currState.getState());
        out.println("Action : "+Arrays.toString(Action));
        //Action=Arrays.copyOf(stateAction, 5);
        //ActionRandom=Arrays.copyOf(lut.RandomAction(), 5);
        ActionCount+=1;	
        if(exploration)
        {	
//		if((ActionCount%10)<=epsilonRemider) 
//			takeAction(ActionRandom,e);
//		else 
//			takeAction(Action,e);
        }else{
        		out.println(act.get(Action));
                takeAction(act.get(Action),e);
        }	
    }
		
	
public void onHitWall(HitWallEvent event)
{
    currState.setState(event, this); // Passing state to object
    double state[]=GetStatewithHighQFromNN(currState.getState());
    if(!onpolicy)
    {
            double[] bestActioninFuture=Arrays.copyOf(state,5);
            double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
            double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
            double NewQcurrentRandom=Qcurrentrandom+alpha*(HitWall+gamma*QMaxRandomFuture-Qcurrentrandom);
            trainQ(NewQcurrentRandom);
            error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);

    }else{
            double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
            double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
            double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
            double NewQcurrentRandom=Qcurrentrandom+alpha*(HitWall+gamma*QMaxRandomFuture-Qcurrentrandom);
            trainQ(NewQcurrentRandom);
            error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
            }	
    reward=reward+HitWall;
		
    setTurnRadarRight(360);
    execute();
		
    }
	
	public void onDeath(DeathEvent event)
	{
		
		double prevS[]=prevState.getState();
		if(!onpolicy)
		{
			double Qcurrentrandom=GetQfromNN(MergeArray(prevS, Action));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(Death);
			trainQ(NewQcurrentRandom);
			
		}else{
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(Death);
			trainQ(NewQcurrentRandom);
			
		}		
		fail=fail+1;
		reward=reward+Death;
		Q_Epizodes.add(reward);
		//out.print("The reward is to ADD on fail is :"+reward+'\n');
		
		//out.print("The reward is :"+reward+'\n');
		
		
	}
	public void onWin(WinEvent event)
	{
		
		double previousState[]=prevState.getState();
		if(!onpolicy)
		{
			double Qcurrentrandom=GetQfromNN(MergeArray(previousState, Action));
			double NewQcurrentRandom=Qcurrentrandom+Win;
			trainQ(NewQcurrentRandom);
		
		}else{
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
			double NewQcurrentRandom=Qcurrentrandom+Win;
			trainQ(NewQcurrentRandom);
			
		}
		win=win+1;
		reward=reward+Win;
		//out.print("The reward is to ADD on win is :"+reward+'\n');
		Q_Epizodes.add(reward);
		//out.print("The reward is :"+reward+'\n');
				
	}
	public void onRoundEnded(RoundEndedEvent event)
	{	
		//TODO: Update the value update
		
		//out.println("At round no "+getRoundNum()+"| total win "+win+" | failed no "+fail);
		//out.println("LUT size is: "+lut.getLUT().size());
		if(getRoundNum()>1)
		{
		if((getRoundNum()%100)==0)
		{
			plotbattle_Sucess_Count.add(win);
			win=0;
			
		}
		}
		
		PredictionError.add(error);
		error=0;
		
		
		//out.print("The reward is to ADD is :"+reward+'\n');
		out.print("the count of win :"+win+'\n');
		//Q_Epizodes.add(reward);
		/*for(int i=0;i<Q_Epizodes.size();i++)
		{
			out.print("Q_Epizodes "+i+" | "+Q_Epizodes.get(i)+'\n');
		}*/
		//out.print("The reward is added to ArrayList"+'\n');
		
	}
	public void onHitRobot(HitRobotEvent event)
	{	
		currState.setState(event, this); // Passing state to object
		currState.setState(event, this); //Passing state to State object
		double state[]=currState.getState();
		if(!onpolicy)
		{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitRobot+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			reward=reward+onHitRobot;
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
		}else{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitRobot+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onHitRobot;
		}
		
		setTurnRadarRight(360);
		execute();
	}
	public void onHitByBullet(HitByBulletEvent event)
	{
		currState.setState(event, this); // Passing state to object
		double state[]=currState.getState();
		if(!onpolicy)
		{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitByBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onHitByBullet;
		}else{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitByBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			/*if(Arrays.equals(Prevstate, stateErrorCheck)&& Arrays.equals(Action, fire))*/
				error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onHitByBullet;
		}			
		setTurnRadarRight(360);
		execute();
	}
	
	public void onBulletMissed(BulletMissedEvent event)
	{
		currState.setState(event, this); // Passing state to object
		double state[]=currState.getState();
		if(!onpolicy)
		{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletMissed+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onBulletMissed;
		}else{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletMissed+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onBulletMissed;
		}
		setTurnRadarRight(360);
		execute();
	}
	public void onBulletHitBullet(BulletHitBulletEvent event)
	{
		currState.setState(event, this); // Passing state to object
		double state[]=currState.getState();
		if(!onpolicy)
		{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHitBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onBulletHitBullet;
		}else{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHitBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onBulletHitBullet;
		}
		setTurnRadarRight(360);
		execute();
	}
	public void onBulletHit(BulletHitEvent event)
	{
		currState.setState(event, this); // Passing state to object
		double state[]=currState.getState();
		if(!onpolicy)
		{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHit+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onBulletHit;
		}else{
			double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
			double QMaxRandomFuture=GetQfromNN(MergeArray(state,bestActioninFuture));
			double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, ActionRandom));
			double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHit+gamma*QMaxRandomFuture-Qcurrentrandom);
			trainQ(NewQcurrentRandom);
			error=error+Math.abs(QMaxRandomFuture-Qcurrentrandom);
			reward=reward+onBulletHit;
		}
		
		setTurnRadarRight(360);
		execute();		
	}
	
	public void onBattleEnded(BattleEndedEvent event)
	{
//
//		getDataDirectory();
//		
//		try {
//			RobocodeFileWriter fw=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";myBot.dat"));
//			//RobocodeFileWriter fw=new RobocodeFileWriter("C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\MyBot.data\\Win.dat");
//			
//			
//		
//			//RobocodeFileWriter fw=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";myBot.dat"));
//			for(int u=0;u<plotbattle_Sucess_Count.size();u++)
//			{
//				fw.write((u+1)+" | "+plotbattle_Sucess_Count.get(u)+"\n");				
//			}
//			fw.close();	
//			
//		} 
//
//		catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}finally
//		{
//			
//		}		
//		
//		//out.print( "Total Data left:"+getDataQuotaAvailable());
//				try {
//					RobocodeFileWriter fcw=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";PredictionError.dat"));
//					
//					for(int u=0;u<PredictionError.size();u++)
//					{
//						fcw.write((u+1)+" | "+PredictionError.get(u)+"\n");
//						
//					}
//					fcw.close();
//					
//					
//				} 
//
//				catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}finally
//				{
//					
//				}			
//		//out.print( "Total Data left:"+getDataQuotaAvailable());
///*		try {
//			RobocodeFileWriter fcw=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";QEpizode.dat"));
//			
//			for(int u=0;u<Q_Epizodes.size();u++)
//			{
//				fcw.write((u+1)+" | "+Q_Epizodes.get(u)+"\n");
//				
//			}
//			fcw.close();
//			
//			
//		} 
//
//		catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}finally
//		{
//			
//		}*/			
//		/*out.print( "Total Data left:"+getDataQuotaAvailable());
//		PrintStream w = null;
//		try {
//			RobocodeFileWriter fwh=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";LUT.dat"));
//			
//			
//			for(int d=0;d<lut.getLUT().size();d++)
//		    {	    	
//				fwh.write(Arrays.toString(lut.getLUT().get(d))+'\n');		    	
//		    }
//			
//			fwh.close();
//			
//		} 
//
//		catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}finally
//		{
//			
//		}	*/
//		WriterDouble db=new WriterDouble(Q_Epizodes);
//		WriterInt in=new WriterInt(plotbattle_Sucess_Count);
//		lut.WriteLUT();
//		//out.print( "Total Data left:"+getDataQuotaAvailable());
	}
	private void loadActions() {
		act.put(new double[]{1,0,0,0,0},Actions.FIRE);
		act.put(new double[]{0,1,0,0,0},Actions.FORWARD);
		act.put(new double[]{0,0,1,0,0},Actions.LEFT_FORWARD);
		act.put(new double[]{0,0,0,1,0},Actions.RIGHT_FORWARD);
		act.put(new double[]{0,0,0,0,1},Actions.BACK);
	}
	private void takeAction(Actions action,ScannedRobotEvent e)
	{
		out.print("The enum inside  "+action);
		switch(action) {
		case FIRE:
			turnGunRight(getHeading()-getGunHeading()+e.getBearing());
			fire(2);
			break;
		case FORWARD:
			setAhead(100);
			break;
		case LEFT_FORWARD:
			setTurnLeft(30);
			break;
		case RIGHT_FORWARD:
			setTurnRight(30);
			break;
		case BACK:
			back(100);
			break;
		default:
			System.out.println("Error wrong input");
		}
		
//		setTurnRadarRight(360);
		execute();
		
	}
	
	
	private double[] GetStatewithHighQFromNN(double[] state)
	{
		
		double q=Double.MIN_VALUE;
		double action[]= new double[5];
		for (double[] actions : act.keySet()) {
			double StateAction[]=MergeArray(state, actions);
			double qNN=Mb.getQforStateAction(StateAction);
			if(qNN>q) {
				q=qNN;
				action=actions;
			}
		}
		return action;
		
	}
	private void trainQ(double Q)
	{
		Mb.trainNewQ(Q);
	}
	
	private double GetQfromNN(double[] Q)
	{
		double q=Mb.getQforStateAction(Q);
		return q;
	}
	
	private void NNInitial()
	{
		NeuralMainBot nb=new NeuralMainBot();
		this.Mb=nb;
	}
	
	private double[] MergeArray(double[] Arr1,double[] Arr2)
	{
		
		double[] temp = new double[ Arr1.length + Arr2.length ];
	    System.arraycopy( Arr1, 0, temp, 0, Arr1.length );
	    System.arraycopy( Arr2, 0, temp, Arr1.length, Arr2.length );
		return temp;
		
	}
	

}
