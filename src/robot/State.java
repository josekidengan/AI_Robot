package robot;

import java.util.Arrays;

import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

public class State {
	double state[];
        private boolean dimReduction;
        
    public State(int size,boolean dimensionReduction) {
		this.state= new double[size];
	    this.dimReduction=dimensionReduction;
	    Arrays.fill(this.state, 0.0);
    }
	public double[] getState() {
		return state;
	}

	public void setState(double[] state) {
		this.state = state;
	}
	 public void setState(ScannedRobotEvent event, NNBot bot){

			double enemy_Distance=event.getDistance();
			double enemy_Energy=event.getEnergy();
			double sin_Enemy=Math.sin(event.getBearing())*100;
			double cos_Enemy=Math.cos(event.getBearing())*100;
			double my_Bearing_X=Math.sin(bot.getHeadingRadians());			
			double myX=Math.sin(event.getHeading())*100;
			double myY=Math.cos(event.getHeading())*100;
			double myEnergy=bot.getEnergy();
			if(dimReduction) {
				state[0]=dimReduce_enemy_Distance(enemy_Distance);
				state[1]=dimReduce_Energy(enemy_Energy);
				state[2]=dimReduce_Angle(sin_Enemy);
				state[3]=dimReduce_Angle(cos_Enemy);
				state[4]=dimReduce_Angle(myX);
				state[5]=dimReduce_Angle(myY);
				state[6]=dimReduce_Energy(myEnergy);
			}else {
				state[0]=enemy_Distance;
				state[1]=enemy_Energy;
				state[2]=sin_Enemy;
				state[3]=cos_Enemy;
				state[4]=myX;
				state[5]=myY;
				state[6]=myEnergy;
				//state[4]=myEnergy;
			}
			
             
     }
    public void setState(HitWallEvent event, NNBot bot){
            double X=bot.getX();
            double Y=bot.getY();
            double energy=bot.getEnergy();
            
            if(dimReduction){
                double myX=dimReduce_Axes(X);
                double myY=dimReduce_Axes(Y);
                double myEnergy=dimReduce_Energy(energy);
                this.state[6]=myEnergy;
            }else{
                this.state[6]=energy;
            }
                
        }
    public void setState(HitRobotEvent event, NNBot bot){
	    	double X=bot.getX();
	        double Y=bot.getY();
	        double energy=bot.getEnergy();
    	if(dimReduction) {
			double enemy_Energy=dimReduce_Energy(event.getEnergy());
			double sin_Enemy=dimReduce_Angle(Math.sin(event.getBearing())*100);
			double cos_Enemy=dimReduce_Angle(Math.cos(event.getBearing())*100);
			double myX=dimReduce_Axes(X);
			double myY=dimReduce_Axes(Y);				
			double myEnergy=dimReduce_Energy(energy);
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
			state[1]=enemy_Energy;
			state[2]=sin_Enemy;
			state[3]=cos_Enemy;
    	}else {
    		//TODO: Add the conditions
    	}
    	
    }
    
    public void setState(HitByBulletEvent event, NNBot bot){
    	double X=bot.getX();
        double Y=bot.getY();
        double energy=bot.getEnergy();
        if(dimReduction) {
        	double sin_Enemy=dimReduce_Angle(Math.sin(event.getBearing())*100);
			double cos_Enemy=dimReduce_Angle(Math.cos(event.getBearing())*100);
			/*double myX=dimReduce_Axes(getX());
			double myY=dimReduce_Axes(getY());*/
			double myX=dimReduce_Angle(Math.sin(event.getHeading())*100);
			double myY=dimReduce_Angle(Math.cos(event.getHeading())*100);
			double myEnergy=dimReduce_Energy(energy);
			state[2]=sin_Enemy;
			state[3]=cos_Enemy;
			state[4]=myX;
			state[5]=myY;
			state[6]=myEnergy;
        }else {
//        	TODO: Adjust the evets
        }
    	
    }
    
    public void setState(BulletMissedEvent event, NNBot bot){
    	double X=bot.getX();
        double Y=bot.getY();
        double energy=bot.getEnergy();
        if(dimReduction) {
			double myX=dimReduce_Axes(X);
			double myY=dimReduce_Axes(Y);
			double myEnergy=dimReduce_Energy(energy);
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
        }else {
//        	TODO: need toc check
        }
    }
    
    public void setState(BulletHitBulletEvent event, NNBot bot){
    	double X=bot.getX();
        double Y=bot.getY();
        double energy=bot.getEnergy();
        if(dimReduction) {
        	double myX=dimReduce_Axes(X);
			double myY=dimReduce_Axes(Y);
			double myEnergy=dimReduce_Energy(energy);
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
        }else {
//        	TODO:  do it
        }
    }
    
    public void setState(BulletHitEvent event, NNBot bot){
    	double X=bot.getX();
        double Y=bot.getY();
        double energy=bot.getEnergy();
        if(dimReduction) {
        	double myX=dimReduce_Axes(X);
			double myY=dimReduce_Axes(Y);
			double myEnergy=dimReduce_Energy(energy);
			double enemy_Energy=dimReduce_Energy(event.getEnergy());
			state[1]=enemy_Energy;
			/*state[4]=myX;
			state[5]=myY;*/
			state[6]=myEnergy;
        }else {
//        	TODO: check
        }
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
		if(Energy<25)//Total dimensions of 3
			return 12.5;
		else if (Energy<50)
			return 37.5;
		else if (Energy<75)
			return 62.5;
		else
			return 87.5;
	}
	private double dimReduce_Axes(double Axes )
	{
		return (Math.floor(Axes/100)*10); //Total dimensions : 10
	}
	private double dimReduce_Angle(double Anngle )
	{
		return Math.floor(Anngle/10); //Total dimensions of : 10
	}
public void reduceStateDimentions(ScannedRobotEvent e, NNBot bot) {
		

	}
}
