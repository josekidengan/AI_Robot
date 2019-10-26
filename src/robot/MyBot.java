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


public class MyBot extends AdvancedRobot {
	
	
	static NeuralMainBot Mb;
	Random rr=new Random();
	static LUT lut=new LUT();
	double[] state = new double[7];
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
	boolean exploration=true;
	//double fire[]={1,0,0,0,0};
	
// The switch for NN and LUT
	boolean NN=false;
	boolean statereductionNN=false;
	
/*	double HitWall=-5;
	double Death=-10;;
	double Win=0;
	double onHitRobot=-4;
	double onBulletMissed=-5;
	double onHitByBullet=-7;
	double onBulletHitBullet=-3;
	double onBulletHit=-1;
	*/
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
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		while (true) {
				//setTurnRight(30);
				//setAhead(200);				
				setTurnRadarRight(360);
				execute();
				

		}
		
	}
	

	/**
	 * Fire when we see a robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		if(NN)
		{
			if(statereductionNN)
			{
				double enemy_Distance=dimReduce_enemy_Distance(e.getDistance());
				double enemy_Energy=dimReduce_Energy(e.getEnergy());
				double sin_Enemy=dimReduce_Angle(Math.sin(e.getBearing())*100);
				double cos_Enemy=dimReduce_Angle(Math.cos(e.getBearing())*100);
				double my_Bearing_X=Math.sin(getHeadingRadians());
				/*double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());*/
				
				double myX=dimReduce_Angle(Math.sin(e.getHeading())*100);
				double myY=dimReduce_Angle(Math.cos(e.getHeading())*100);
				double myEnergy=dimReduce_Energy(getEnergy());
				
				state[0]=enemy_Distance;
				state[1]=enemy_Energy;
				state[2]=sin_Enemy;
				state[3]=cos_Enemy;
				state[4]=myX;
				state[5]=myY;
				state[6]=myEnergy;
				//state[4]=myEnergy;
			}else
			{
				double enemy_Distance=e.getDistance();
				double enemy_Energy=e.getEnergy();
				double sin_Enemy=Math.sin(e.getBearing());
				double cos_Enemy=Math.cos(e.getBearing());
				double my_Bearing_X=Math.sin(getHeadingRadians());
				/*double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());*/
				
				double myX=Math.sin(e.getHeading());
				double myY=Math.cos(e.getHeading());
				double myEnergy=getEnergy();
				
				state[0]=enemy_Distance;
				state[1]=enemy_Energy;
				state[2]=sin_Enemy;
				state[3]=cos_Enemy;
				state[4]=myX;
				state[5]=myY;
				state[6]=myEnergy;
				//state[4]=myEnergy;
			}
			
			Prevstate=Arrays.copyOf(state,state.length);
			Action=Arrays.copyOf(GetStatewithHighQFromNN(state), 5);
			ActionRandom=Arrays.copyOf(lut.RandomAction(), 5);
			ActionCount=ActionCount+1;	
			if(exploration)
			{	
					if((ActionCount%10)<=epsilonRemider)
					{
						takeAction(ActionRandom,e);
						
					}else
					{
						takeAction(Action,e);
					}
			}else{
				takeAction(Action,e);
			}
			
			
		}else{
			
			double enemy_Distance=dimReduce_enemy_Distance(e.getDistance());
			double enemy_Energy=dimReduce_Energy(e.getEnergy());
			double sin_Enemy=dimReduce_Angle(Math.sin(e.getBearing())*100);
			double cos_Enemy=dimReduce_Angle(Math.cos(e.getBearing())*100);
			double my_Bearing_X=Math.sin(getHeadingRadians());
			/*double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());*/
			
			double myX=dimReduce_Angle(Math.sin(e.getHeading())*100);
			double myY=dimReduce_Angle(Math.cos(e.getHeading())*100);
			double myEnergy=dimReduce_Energy(getEnergy());
			
			state[0]=enemy_Distance;
			state[1]=enemy_Energy;
			state[2]=sin_Enemy;
			state[3]=cos_Enemy;
			state[4]=myX;
			state[5]=myY;
			state[6]=myEnergy;
			Prevstate=Arrays.copyOf(state,state.length);
			Action=Arrays.copyOf(lut.Lookup(state), 5);
			ActionRandom=Arrays.copyOf(lut.RandomAction(), 5);
			ActionCount=ActionCount+1;	
			
			
			
		}
		
		if(exploration)
		{
			
				if((ActionCount%10)<=epsilonRemider)
				{
					takeAction(ActionRandom,e);
				}else
				{
					takeAction(Action,e);
				}
		}else{
			takeAction(Action,e);
		}
		
	}
		
	
	public void onHitWall(HitWallEvent event)
	{
		//event.getBearing();
		if(NN)
		{
			if(statereductionNN)
			{
				double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());
				double myEnergy=dimReduce_Energy(getEnergy());
				/*state[4]=myX;
				state[5]=myY;*/
			}else
			{
				double myX=getX();
				double myY=getY();
				double myEnergy=getEnergy();
				/*state[4]=myX;
				state[5]=myY;*/
				
				state[6]=myEnergy;
			}
			
			

				if(!onpolicy)
				{
					double[] bestActioninFuture=Arrays.copyOf(GetStatewithHighQFromNN(state),5);
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
		}else 
		{
			double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());
			double myEnergy=dimReduce_Energy(getEnergy());
			/*state[4]=myX;
			state[5]=myY;*/
			
			state[6]=myEnergy;

				if(!onpolicy)
				{
					double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
					double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
					double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
					double NewQcurrentRandom=Qcurrentrandom+alpha*(HitWall+gamma*QMaxRandomFuture-Qcurrentrandom);
					lut.train(Prevstate, Action, NewQcurrentRandom);
					
					
				}else{
					double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
					double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
					double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
					double NewQcurrentRandom=Qcurrentrandom+alpha*(HitWall+gamma*QMaxRandomFuture-Qcurrentrandom);
					lut.train(Prevstate, ActionRandom, NewQcurrentRandom);
					
					}	
				reward=reward+HitWall;
		}
		
		setTurnRadarRight(360);
		execute();
		
	}
	
	public void onDeath(DeathEvent event)
	{
		if(NN)
		{
			if(!onpolicy)
			{
				double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
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
		}else
		{
			if(!onpolicy)
			{
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(Death);
				lut.train(Prevstate, Action, NewQcurrentRandom);
				
			}else{
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(Death);
				lut.train(Prevstate, Action, NewQcurrentRandom);
				
			}		
			fail=fail+1;
			reward=reward+Death;
			Q_Epizodes.add(reward);
			//out.print("The reward is to ADD on fail is :"+reward+'\n');
			
			//out.print("The reward is :"+reward+'\n');
		}
		
	
		
		
	}
	public void onWin(WinEvent event)
	{
		if(NN)
		{
			if(!onpolicy)
			{
				double Qcurrentrandom=GetQfromNN(MergeArray(Prevstate, Action));
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
		}else
		{
			if(!onpolicy)
			{
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
				double NewQcurrentRandom=Qcurrentrandom+Win;
				lut.train(Prevstate, Action, NewQcurrentRandom);
			
			}else{
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
				double NewQcurrentRandom=Qcurrentrandom+Win;
				lut.train(Prevstate, Action, NewQcurrentRandom);
				
			}
			win=win+1;
			reward=reward+Win;
			//out.print("The reward is to ADD on win is :"+reward+'\n');
			Q_Epizodes.add(reward);
			//out.print("The reward is :"+reward+'\n');
			
		}		
	}
	public void onRoundEnded(RoundEndedEvent event)
	{
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
		if(NN)
		{
			if(statereductionNN)
			{
				double enemy_Energy=dimReduce_Energy(event.getEnergy());
				double sin_Enemy=dimReduce_Angle(Math.sin(event.getBearing())*100);
				double cos_Enemy=dimReduce_Angle(Math.cos(event.getBearing())*100);
				double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());				
				double myEnergy=dimReduce_Energy(getEnergy());
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
				state[1]=enemy_Energy;
				state[2]=sin_Enemy;
				state[3]=cos_Enemy;
				
			}else
			{
				double enemy_Energy=event.getEnergy();
				double sin_Enemy=Math.sin(event.getBearing());
				double cos_Enemy=Math.cos(event.getBearing());
				double myX=getX();
				double myY=getY();
				double myEnergy=getEnergy();
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
				state[1]=enemy_Energy;
				state[2]=sin_Enemy;
				state[3]=cos_Enemy;
			}
			
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
		}else
		{
			double enemy_Energy=dimReduce_Energy(event.getEnergy());
			double sin_Enemy=dimReduce_Angle(Math.sin(event.getBearing())*100);
			double cos_Enemy=dimReduce_Angle(Math.cos(event.getBearing())*100);
			double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());

			
			double myEnergy=dimReduce_Energy(getEnergy());
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
			state[1]=enemy_Energy;
			state[2]=sin_Enemy;
			state[3]=cos_Enemy;
			if(!onpolicy)
			{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitRobot+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, Action, NewQcurrentRandom);
				reward=reward+onHitRobot;
			}else{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitRobot+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, ActionRandom, NewQcurrentRandom);
				reward=reward+onHitRobot;
			}
		}
		setTurnRadarRight(360);
		execute();
	}
	public void onHitByBullet(HitByBulletEvent event)
	{
		if(NN)
		{
			if(statereductionNN)
			{
				double sin_Enemy=dimReduce_Angle(Math.sin(event.getBearing())*100);
				double cos_Enemy=dimReduce_Angle(Math.cos(event.getBearing())*100);
				/*double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());*/
				double myX=dimReduce_Angle(Math.sin(event.getHeading())*100);
				double myY=dimReduce_Angle(Math.cos(event.getHeading())*100);
				double myEnergy=dimReduce_Energy(getEnergy());
				state[2]=sin_Enemy;
				state[3]=cos_Enemy;
				state[4]=myX;
				state[5]=myY;
				state[6]=myEnergy;
			}else
			{
				double sin_Enemy=Math.sin(event.getBearing());
				double cos_Enemy=Math.cos(event.getBearing());
				/*double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());*/
				double myX=Math.sin(event.getHeading());
				double myY=Math.cos(event.getHeading());
				double myEnergy=getEnergy();
				state[2]=sin_Enemy;
				state[3]=cos_Enemy;
				state[4]=myX;
				state[5]=myY;
				state[6]=myEnergy;
			}
			
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
		}else
		{
			double sin_Enemy=dimReduce_Angle(Math.sin(event.getBearing())*100);
			double cos_Enemy=dimReduce_Angle(Math.cos(event.getBearing())*100);
			/*double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());*/
			double myX=dimReduce_Angle(Math.sin(event.getHeading())*100);
			double myY=dimReduce_Angle(Math.cos(event.getHeading())*100);
			double myEnergy=dimReduce_Energy(getEnergy());
			state[2]=sin_Enemy;
			state[3]=cos_Enemy;
			state[4]=myX;
			state[5]=myY;
			state[6]=myEnergy;
			if(!onpolicy)
			{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitByBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, Action, NewQcurrentRandom);
				reward=reward+onHitByBullet;
			}else{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onHitByBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, ActionRandom, NewQcurrentRandom);
				reward=reward+onHitByBullet;
			}
		}
		
		setTurnRadarRight(360);
		execute();
	}
	
	public void onBulletMissed(BulletMissedEvent event)
	{
		if(NN)
		{
			if(statereductionNN)
			{
				double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());
				double myEnergy=dimReduce_Energy(getEnergy());
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
			}else
			{
				double myX=getX();
				double myY=getY();
				double myEnergy=getEnergy();
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
			}
			
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
		}else
		{
			double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());
			double myEnergy=dimReduce_Energy(getEnergy());
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
			if(!onpolicy)
			{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletMissed+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, Action, NewQcurrentRandom);
				reward=reward+onBulletMissed;
			}else{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletMissed+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, ActionRandom, NewQcurrentRandom);
				reward=reward+onBulletMissed;
			}
		}
		
		setTurnRadarRight(360);
		execute();
	}
	public void onBulletHitBullet(BulletHitBulletEvent event)
	{
	
		if(NN)
		{
			if(statereductionNN)
			{
				double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());
				double myEnergy=dimReduce_Energy(getEnergy());
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
			}else
			{
				double myX=getX();
				double myY=getY();
				double myEnergy=getEnergy();
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
			}
			
		
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
		}else
		{
			
			double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());
			double myEnergy=dimReduce_Energy(getEnergy());
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
			if(!onpolicy)
			{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHitBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, Action, NewQcurrentRandom);
				reward=reward+onBulletHitBullet;
			}else{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHitBullet+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, ActionRandom, NewQcurrentRandom);
				reward=reward+onBulletHitBullet;
			}
		}
		setTurnRadarRight(360);
		execute();
	}
	public void onBulletHit(BulletHitEvent event)
	{
		if(NN)
		{
			if(statereductionNN)
			{
				double myX=dimReduce_Axes(getX());
				double myY=dimReduce_Axes(getY());
				double myEnergy=dimReduce_Energy(getEnergy());
				double enemy_Energy=dimReduce_Energy(event.getEnergy());
				state[1]=enemy_Energy;
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
			}else
			{
				double myX=getX();
				double myY=getY();
				double myEnergy=getEnergy();
				double enemy_Energy=event.getEnergy();
				state[1]=enemy_Energy;
				/*state[4]=myX;
				state[5]=myY;*/
				state[6]=myEnergy;
			}
			
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
		}else
		{
			double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());
			double myEnergy=dimReduce_Energy(getEnergy());
			double enemy_Energy=dimReduce_Energy(event.getEnergy());
			state[1]=enemy_Energy;
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
			if(!onpolicy)
			{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, Action);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHit+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, Action, NewQcurrentRandom);
				reward=reward+onBulletHit;
			}else{
				double[] bestActioninFuture=Arrays.copyOf(lut.Lookup(state),5);
				double QMaxRandomFuture=lut.getQfromLUTforState_And_Action(state,bestActioninFuture);
				double Qcurrentrandom=lut.getQfromLUTforState_And_Action(Prevstate, ActionRandom);
				double NewQcurrentRandom=Qcurrentrandom+alpha*(onBulletHit+gamma*QMaxRandomFuture-Qcurrentrandom);
				lut.train(Prevstate, ActionRandom, NewQcurrentRandom);
				reward=reward+onBulletHit;
			}
		}
		
		setTurnRadarRight(360);
		execute();		
	}
	
	public void onBattleEnded(BattleEndedEvent event)
	{
//		lut.WriteLUT();

		getDataDirectory();

		try {
			RobocodeFileWriter fw=new RobocodeFileWriter(getDataFile("res.dat"));
//			RobocodeFileWriter fw=new RobocodeFileWriter("C:\\Users\\josek\\OneDrive\\Robocode\\592%20project\\bin\\robot\\MyBot.data\\Win.dat");



			//RobocodeFileWriter fw=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";myBot.dat"));
			for(double[] key :lut.SAP.keySet()){
				fw.write(Arrays.toString(key)+"|"+lut.SAP.get(key)+"\n");
			}

//			for(int u=0;u<plotbattle_Sucess_Count.size();u++)
//			{
//				fw.write((u+1)+" | "+plotbattle_Sucess_Count.get(u)+"\n");
//			}
			fw.close();

		}

		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		//out.print( "Total Data left:"+getDataQuotaAvailable());
/*		try {
			RobocodeFileWriter fcw=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";QEpizode.dat"));
			
			for(int u=0;u<Q_Epizodes.size();u++)
			{
				fcw.write((u+1)+" | "+Q_Epizodes.get(u)+"\n");
				
			}
			fcw.close();
			
			
		} 

		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally
		{
			
		}*/			
		/*out.print( "Total Data left:"+getDataQuotaAvailable());
		PrintStream w = null;
		try {
			RobocodeFileWriter fwh=new RobocodeFileWriter(getDataFile("Explore="+exploration+";Onpolicy="+onpolicy+";LUT.dat"));
			
			
			for(int d=0;d<lut.getLUT().size();d++)
		    {	    	
				fwh.write(Arrays.toString(lut.getLUT().get(d))+'\n');		    	
		    }
			
			fwh.close();
			
		} 

		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally
		{
			
		}	*/
//		WriterDouble db=new WriterDouble(Q_Epizodes);
//		WriterInt in=new WriterInt(plotbattle_Sucess_Count);
//		lut.WriteLUT();
		//out.print( "Total Data left:"+getDataQuotaAvailable());
	}
	private void takeAction(double [] action,ScannedRobotEvent e)
	{
		
		
		double fire[]={1,0,0,0,0};
		double forward[]={0,1,0,0,0};
		double leftforward[]={0,0,1,0,0};
		double rightforward[]={0,0,0,1,0};
		double back[]={0,0,0,0,1};
		
		if (Arrays.equals(fire, action))
		{
			turnGunRight(getHeading()-getGunHeading()+e.getBearing());
			fire(2);
			
			/*if (e.getDistance() > 200) {
			//stop();
			//gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(getHeading()-getGunHeading()+e.getBearing());
			//turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			fire(1);
			//resume();
		} else if (e.getDistance() > 50) {
			//stop();
			turnGunRight(getHeading()-getGunHeading()+e.getBearing());
			//turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			fire(2);
			//resume();
		} else {
			//stop();
			turnGunRight(getHeading()-getGunHeading()+e.getBearing());
			//turnGunRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			fire(3);
		}
			*/
		}
		if (Arrays.equals(forward, action))
		{
			//stop();
			//turnRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			//setAhead(-e.getDistance()-200);
			//setAhead(e.getDistance());
			setAhead(100);
			//resume();
		}
			
		if (Arrays.equals(leftforward, action))
		{
			//stop();
			setTurnLeft(30);
			//setAhead(e.getDistance()-400);
			//setAhead(100);
			//resume();
		}
		if (Arrays.equals(rightforward, action))
		{
			//stop();
			setTurnRight(30);
			//setAhead(e.getDistance()-100);
			//setAhead(100);
			//resume();
		}
			
		if (Arrays.equals(back, action))
		{
			//stop();
			//setTurnRight(normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading())));
			//back(e.getDistance()-600);
			back(100);
			//resume();
		}
		
		setTurnRadarRight(360);
		execute();
		
	}
	
	
	
	private double dimReduce_enemy_Distance(double enemy_Distance )
	{
		//Total dimensions of 3
		if(enemy_Distance<100)
			return 50;
		else if (enemy_Distance<200)
			return 150;
		else 
			return 300;		
		//return Math.floor(enemy_Distance/100)*10;	
		
	}
	
	private double dimReduce_Energy(double Energy )
	{
		//Total dimensions of 3
		if(Energy<25)
			return 12.5;
		else if (Energy<50)
			return 37.5;
		else if (Energy<75)
			return 62.5;
		else
			return 87.5;
		//return Math.floor(Energy/100)*10;
	}
	
	private double dimReduce_Axes(double Axes )
	{
		//Total dimensions of 3
		
			return (Math.floor(Axes/100)*10);
	}
	
	
	private double dimReduce_Angle(double Anngle )
	{
		//Total dimensions of 3
		
		return Math.floor(Anngle/10);
	}
	
	private double[] GetStatewithHighQFromNN(double[] state)
	{
		ArrayList<double[]> localStAct=new ArrayList<double[]>();
		double fire[]={1,0,0,0,0};
		double forward[]={1,1,0,0,0};
		double leftforward[]={1,1,1,0,0};
		double rightforward[]={1,1,1,1,0};
		double back[]={1,1,1,1,1};
		localStAct.add(MergeArray(state,fire));
		localStAct.add(MergeArray(state,forward));
		localStAct.add(MergeArray(state,leftforward));
		localStAct.add(MergeArray(state,rightforward));
		localStAct.add(MergeArray(state,back));
		int index=0;
		double q=Mb.getQforStateAction(localStAct.get(0));
		for(int y=1;y<localStAct.size();y++)
		{
			if(q<Mb.getQforStateAction(localStAct.get(y)))
			{
				index=y;
				q=Mb.getQforStateAction(localStAct.get(y));
			}
			
		}
		double[] temp=Arrays.copyOfRange(localStAct.get(index), state.length, localStAct.get(index).length);
		return temp;
		
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
