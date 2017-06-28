package domain.nao;


import java.util.ArrayList;
import java.util.List;

import com.aldebaran.qi.helper.proxies.*;


/**
 * Class containing all methods for Nao's legs
 * @author WeiHao
 */
public class Leg_Functionality {

	
	List<Float> angles; 
	static boolean pause; 
	NAO_Handler naoHandler;
	
	Leg_Functionality(NAO_Handler naoHandler){
		this.naoHandler = naoHandler;
		angles = new ArrayList<Float>();
		pause = false;
		
	}
	
	/**
	 * This method brings the Nao into a helpful position state for the other leg methods and it
	 * also takes into account whether the Nao has a token in his hand or not
	 */
	 void wakeUpPosition(){
	
		try{
		
		ALMotion alMotion = new ALMotion(naoHandler.getSession());
		angles = alMotion.getAngles("Body", false);

		
		 //if Nao has a token in his right hand, he should not change the angles of his right arm
		 
		if((0<alMotion.getAngles("RHand", false).get(0))&&(alMotion.getAngles("RHand", false).get(0)<=0.6f)){
			
			angles.set(0, 8.311904E-10f);
	        angles.set(1, -6.3481565E-7f);
	        angles.set(2, 1.2892275f);
	        angles.set(3, 0.27708617f);
	        angles.set(4, -1.279054f);
	        angles.set(5, -0.93232155f);
	        angles.set(6, 1.4932917E-7f);
	        //hand is not important
	        angles.set(8, -3.5731286E-5f);
	        angles.set(9, 8.194672E-6f);
	        angles.set(10, -0.4283372f);
	        angles.set(11, 0.6662958f);
	        angles.set(12, -0.33315217f);
	        angles.set(13, 1.0763653E-5f);
	        angles.set(14, -3.5731286E-5f);
	        angles.set(15, -1.9743735E-5f);
	        angles.set(16, -0.42833722f);
	        angles.set(17, 0.6662958f);
	        angles.set(18, -0.3331523f);
	        angles.set(19, -2.3311555E-5f);
	        alMotion.angleInterpolationWithSpeed("Body", angles,0.1f);
	        
		}
		else{
		
      	angles.set(0, 8.311904E-10f);
        angles.set(1, -6.3481565E-7f);
        angles.set(2, 1.2892275f);
        angles.set(3, 0.27708617f);
        angles.set(4, -1.279054f);
        angles.set(5, -0.93232155f);
        angles.set(6, 1.4932917E-7f);
        //hand is not important
        angles.set(8, -3.5731286E-5f);
        angles.set(9, 8.194672E-6f);
        angles.set(10, -0.4283372f);
        angles.set(11, 0.6662958f);
        angles.set(12, -0.33315217f);
        angles.set(13, 1.0763653E-5f);
        angles.set(14, -3.5731286E-5f);
        angles.set(15, -1.9743735E-5f);
        angles.set(16, -0.42833722f);
        angles.set(17, 0.6662958f);
        angles.set(18, -0.3331523f);
        angles.set(19, -2.3311555E-5f);
        angles.set(20, 1.2891109f);
        angles.set(21, -0.2770861f);
        angles.set(22, 1.2790532f);
        angles.set(23, 0.9323195f);
        angles.set(24, -1.3267025E-7f);
        //hand is not important
		
       alMotion.angleInterpolationWithSpeed("Body", angles,0.1f);
		}
       
        
		}
		catch(Exception e){
			
		}
		
	 }
	 
	

	/**
	 *Own moveTo-method implementation with blocking move-method from ALMotion,  which takes into account whether Nao has a token in his hand or not.
	 * Adding of the boolean "pause" in order to have the possibility to interrupt the move properly.
	 * Following parameter documentation is taken from Aldebaran's documentation.
	 * @param x Distance along the X axis in meters.
	 * @param y	Distance along the Y axis in meters.
	 * @param z	Rotation around the Z axis in radians [-3.1415 to 3.1415]
	 */
	void moveTo_blockingCall(float x, float y, float z){
			
		try{		
			
			ALMotion alMotion = new ALMotion(naoHandler.getSession());
			float path = (float) Math.sqrt(x*x+y*y); // right path length after turning to the target
			
			List<Float> headPosition = new ArrayList<Float>();
			
//			if Nao has a token in his right hand, he should not move his right arm during his walk in order to prevent losing the token
			
		if((0.4<alMotion.getAngles("RHand", false).get(0))&&(alMotion.getAngles("RHand", false).get(0)<=0.6f)){
			alMotion.setMoveArmsEnabled(true, false);
			}
			else{
				alMotion.setMoveArmsEnabled(true, true);
			} 
	
			alMotion.wbEnable(true);
			alMotion.moveInit();
			
			alMotion.moveTo(0f, 0f, z);
			
			
			headPosition.add(0f);
			headPosition.add(0.5f);
			alMotion.angleInterpolationWithSpeed("Head", headPosition, 0.2f); // looking towards walking direction
			
			alMotion.moveTo(path, 0f, 0f);
			
			
			
		}
		catch(Exception e){
			
		}		
		
	}
	/**
	 * @deprecated only used for "AvoidToken Class"
	 * @param stop
	 */
	 void setPause(boolean stop){
		 
		pause = stop;
		
	 }
	/**
	 * Own moveTo-method implementation with non-blocking move-method from ALMotion, which takes into account whether Nao has a token in his hand or not.
	 * Adding of the boolean "pause" in order to have the possibility to interrupt the move properly.
	 * Following parameter documentation is taken from Aldebaran's documentation.
	 * @param x Distance along the X axis in meters.
	 * @param y	Distance along the Y axis in meters.
	 * @param z	Rotation around the Z axis in radians [-3.1415 to 3.1415]
	 * @throws Exception
	 */
	void moveTo(float x, float y, float z) throws Exception{
		
				ALMotion alMotion = new ALMotion(naoHandler.getSession());
				
				float path = (float) Math.sqrt(x*x+y*y);// right path length after turning to the target
				
				List<Float> headPosition = new ArrayList<Float>();
				
				
//				 if Nao has a token in his right hand, he should not move his right arm during his walk in order to prevent losing the token
				 
			if((0.4<alMotion.getAngles("RHand", false).get(0))&&(alMotion.getAngles("RHand", false).get(0)<=0.6f)){
				alMotion.setMoveArmsEnabled(true, false);
				}
				else{
					alMotion.setMoveArmsEnabled(true, true);
				} 
		
				alMotion.wbEnable(true);
				alMotion.moveInit();
				
				
				pause = naoHandler.getPause();	
				
				if(!pause)
				alMotion.moveTo(0f, 0f, z);
				
			while((path>0.05)&&(!pause)){
				
						
						headPosition.add(0f);
						headPosition.add(0.5f);
						
					alMotion.angleInterpolationWithSpeed("Head", headPosition, 0.2f); // looking towards walking direction
				
					pause = naoHandler.getPause();
					
					if((path>0.3f)&&(!pause)){
						
						
						alMotion.move(0.3f,  0f, 0f); 
						Thread.sleep(4500); // 0.3 meters are approximately 9 steps which are carried out in approximately 4.5 seconds
						alMotion.stopMove();
						
						angles = alMotion.getAngles("Body", false);
						angles.set(8, -6.4651465E-11f);
						angles.set(9, 0.025297988f);
						angles.set(10, -0.34423277f);
						angles.set(11, 0.8475033f);
						angles.set(12, -0.5032705f);
						angles.set(13, -0.025298016f);
						angles.set(14, -6.4651465E-11f);
						angles.set(15, 0.025297988f);
						angles.set(16, -0.34423277f);
						angles.set(17, 0.8475033f);
						angles.set(18, -0.5032705f);
						angles.set(19, -0.025298016f);
						
						alMotion.angleInterpolationWithSpeed("Body", angles, 0.2f);
						
						path = path-0.3f;
			}
					else if(!pause){
						
						path = path-0.3f;
						moveTo_blockingCall(path, 0f, 0f);
					
					}
			}
		}
	
	/**
	 * Initiating the right height for the task of grabbing a token.
	 * 
	 */
		void torsoDown(){
		
			pause = naoHandler.getPause();
			
			while(pause){
				try{
				Thread.sleep(1000); pause = naoHandler.getPause();
				}
				catch(Exception e){
					
				}
			}
			
		try{
			ALMotion alMotion = new ALMotion(naoHandler.getSession());
			
			wakeUpPosition();
	
			angles = alMotion.getAngles("Body", false);
			
		
				
					angles.set(10, -1f); 	//Leg
					angles.set(16, -1f);
					angles.set(11, 3f);		//Knee
					angles.set(17, 3f);
					angles.set(12, -1.2f);	//Foot
					angles.set(18, -1.2f);
					
					
			alMotion.angleInterpolationWithSpeed("Body", angles, 0.1f);
			
					angles.set(10, -1.4f);
					angles.set(16, -1.4f);
	   
	
			alMotion.angleInterpolationWithSpeed("Body", angles,0.05f);
			
		}
	catch(Exception e){
		
		
	}
		
		
	}
	
	/**
	 * Stand up method.
	 */
	 void torsoUp(){
			
		 pause = naoHandler.getPause();
		 
			while(pause){
				try{
				Thread.sleep(1000); pause = naoHandler.getPause();
				}
				catch(Exception e){
					
				}
			}
			
			try{
				
			ALMotion alMotion = new ALMotion(naoHandler.getSession());
//			alMotion.setFallManagerEnabled(false);
			angles = alMotion.getAngles("Body", false);
	
	        angles.set(10, -1f);
	        angles.set(16, -1f);
	   
	
			  alMotion.angleInterpolationWithSpeed("Body", angles,0.1f);
			  
	        angles.set(8, -3.5731286E-5f);
	        angles.set(9, 8.194672E-6f);
	        angles.set(10, -0.4283372f);
	        angles.set(11, 0.6662958f);
	        angles.set(12, -0.33315217f);
	        angles.set(13, 1.0763653E-5f);
	        angles.set(14, -3.5731286E-5f);
	        angles.set(15, -1.9743735E-5f);
	        angles.set(16, -0.42833722f);
	        angles.set(17, 0.6662958f);
	        angles.set(18, -0.3331523f);
	        angles.set(19, -2.3311555E-5f);
	
	        
	       alMotion.angleInterpolationWithSpeed("Body", angles,0.1f);
	      
	        
			}
			catch(Exception e){
				
			}
			
		}
	 
	 
	 void stopMove(){
		 
		 try{
			 ALMotion alMotion = new ALMotion(naoHandler.getSession());
			 alMotion.stopMove();
		 }
		 catch(Exception e){
			 
		 }
	 }
}