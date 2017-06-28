package domain.nao;

import com.aldebaran.qi.helper.proxies.*;

import java.util.ArrayList;
import java.util.List;

/**
 * class containing all methods for Nao's arms
 * @author WeiHao
 */
public class Arm_Functionality {

	List<Float> angles; 
	boolean token;
	boolean pause;
	ALMemory mem;
	NAO_Handler naoHandler;
	
	Arm_Functionality(NAO_Handler naoHandler){
		this.naoHandler = naoHandler;
		angles = new ArrayList<Float>();
		token = false;
		pause = false;
		
	
	}
	
	/**
	 * Grabbing a token in front of the Nao.
	 * When the lower camera detects a token within 10 seconds, you will have 7 seconds time to place the token correctly into the Nao's open hand, otherwise the Nao will just continue with his actions
	 * @Param color of the token (true = blue, false = red)
	 */
	void grab(boolean color){
		
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
		mem = new ALMemory(naoHandler.getSession());
		
		angles = alMotion.getAngles("Body", false);
		
		angles.set(2, 1.2892275f);	//left arm
		angles.set(3, 0.27708617f);
		angles.set(4, -1.279054f);
		angles.set(5, -0.93232155f);
	
		angles.set(20, 1.1f);	//right arm
		angles.set(21, -1f);
		angles.set(22, 1f);
		angles.set(23, 1.5f);
		angles.set(24, 0.1f);
		angles.set(25, 1f);
		
		alMotion.angleInterpolationWithSpeed("Body", angles, 0.2f);
		
		angles.set(21, 1f); //right arm
		angles.set(22, 0.8f);	
			
		alMotion.angleInterpolationWithSpeed("Body", angles, 0.1f);
		
		angles = new ArrayList<Float>();
		angles.add(-0.1f);
		angles.add(0.2f);
	
		alMotion.angleInterpolationWithSpeed("Head", angles, 0.2f);
	
		token = false;
		
		int counter = 0;
		
		while(!token && (counter<10)){ // waiting for the token to appear in the lower camera
			
			try{
						
				Thread.sleep(1000);
				ALTracker alt = new ALTracker(naoHandler.getSession());
				ALColorBlobDetection blob = new ALColorBlobDetection(naoHandler.getSession());
				ALMotion alM = new ALMotion(naoHandler.getSession());
				if(color){	
				blob.setColor(57, 86, 170, 10);
				blob.setObjectProperties(5, 0.0018f, "Circle");
				}
				else if(!color){
				blob.setColor(255, 89, 89, 10);
				blob.setObjectProperties(5, 0.0018f, "Circle");
				}
				else{
					System.out.println("Wrong color!!!");
					return;
				}
				
				blob.setActiveCamera(1);
				alt.setMode("Head");
				
				alt.trackEvent("ALTracker/ColorBlobDetected");
						
				if(alt.getTargetPosition(2).size()>0){
					
					alM.stopMove();
					Thread.sleep(7000);
					token = true;
				
				}
				alt.stopTracker();
				mem.unsubscribeAllEvents();
				counter++;
				}
				catch(Exception e){
					
				}
		}
		
		alMotion.angleInterpolationWithSpeed("RHand", 0.6f, 0.1f);
			
		}
		
		catch(Exception e){
			
		}
		
	
		
	}
	
	/**
	 * Nao will let go of the token and try not to knock it over while doing so
	 */
	void letGo(){
		
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
			angles = alMotion.getAngles("RArm", false);
			
			alMotion.openHand("RHand");
			
			angles.set(1, -0.6f);
			alMotion.angleInterpolationWithSpeed("RArm", angles, 0.1f);
			
			
			angles.set(2, 1f);
			alMotion.angleInterpolationWithSpeed("RArm", angles, 0.1f);
			
		}
		catch(Exception e){
			
		}
	}
	 

}
