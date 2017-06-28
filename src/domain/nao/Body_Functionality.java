package domain.nao;



import java.util.ArrayList;
import java.util.List;

import application.ai.Dijkstra;

import com.aldebaran.qi.helper.proxies.ALColorBlobDetection;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALTracker;





import foundation.exception.InvalidInputException;

/**
 * Class combining the functionality of each movement and detections class
 * @author WeiHao, Aljoscha
 *
 *
 */

public class Body_Functionality {


	static private NAO_Handler nao_handler;
	Head_Functionality head_Functionality;
	Leg_Functionality leg_Functionality;
	Arm_Functionality arm_Functionality;
	boolean pause;
	ALMemory mem;
	static String currentCircle;
	
	Body_Functionality(NAO_Handler naoHandler) {
		
		nao_handler = naoHandler;
		pause = false;
		head_Functionality = new Head_Functionality(nao_handler);
		leg_Functionality = new Leg_Functionality(nao_handler);
		arm_Functionality = new Arm_Functionality(nao_handler);
		currentCircle = "orange";
		
	}
	
	/**	
 	 *Sending the Nao to a colored circle by updating the position of the target every 0.6 meters.
	 * @param color of the circle as a String (pink, orange, blue, purple, red, khaki, grey, green, beige)
	 */
	void goToCircle(String color){
		goToCircle(color, false);
	}
	
	/**	
 	*Sending the Nao to a colored circle by updating the position of the target every 0.6 meters.
	 * @param color of the circle as a String (pink, orange, blue, purple, red, khaki, grey, green, beige)
	 * @param look (True = looking for circle with wider range and turning right if circle is not found in the first attempt, False = looking for circle in a shorter range and turning left when circle is not found in the first attempt) 
	 */
	void goToCircle(String color,boolean look){
		
		pause = nao_handler.getPause();	
		
		while(pause){
			try{
			Thread.sleep(1000); pause = nao_handler.getPause();
			}
			catch(Exception e){
				
			}
		}
		try{
			
		if(lookForCircle(color,look,look)){
		
		ArrayList<Float> upperCameraPosition = head_Functionality.getCirclePosition(0,color), lowerCameraPosition = head_Functionality.getCirclePosition(1, color);
			
		if((upperCameraPosition.size()>0)&&(lowerCameraPosition.size()==0)){
			
			if(upperCameraPosition.get(0)>0.6f){ 
				
				leg_Functionality.moveTo(0.6f, 0f, upperCameraPosition.get(2));

				goToCircle(color);
				
			}
			else{
				
				leg_Functionality.moveTo(upperCameraPosition.get(0), upperCameraPosition.get(1), upperCameraPosition.get(2));

				goToCircle(color);
				
			}
			
		}
		else if((upperCameraPosition.size()==0)&&(lowerCameraPosition.size()>0)){
			
				if(lowerCameraPosition.get(0)>0.2f){
					
					leg_Functionality.moveTo(0.2f, 0f, lowerCameraPosition.get(2));

					goToCircle(color);
				}
				else{
					
					leg_Functionality.moveTo(lowerCameraPosition.get(0), lowerCameraPosition.get(1), lowerCameraPosition.get(2)); //ending of the recursion because this case should only occur when Nao is standing on top of the circle 
					if(lowerCameraPosition.get(0)>0.2f){
						goToCircle(color);
					}		
				}
		}
		
		else if((upperCameraPosition.size()>0)&&(lowerCameraPosition.size()>0)){
			// if both cameras get values, the smaller ones are the correct values
			
			if((upperCameraPosition.get(0)<lowerCameraPosition.get(0))&&(upperCameraPosition.get(0)>0.1)){
			
					leg_Functionality.moveTo(upperCameraPosition.get(0), upperCameraPosition.get(1), upperCameraPosition.get(2));
					goToCircle(color);
				
			}
			else if((upperCameraPosition.get(0)>lowerCameraPosition.get(0))&&(lowerCameraPosition.get(0)>0.1)){
			
				leg_Functionality.moveTo(0.2f, 0f, lowerCameraPosition.get(2));
				goToCircle(color);
				
				
			}
			else if((upperCameraPosition.get(0)>lowerCameraPosition.get(0))&&(lowerCameraPosition.get(0)<0.3)){
				
				leg_Functionality.moveTo(lowerCameraPosition.get(0), lowerCameraPosition.get(1), lowerCameraPosition.get(2));//ending of the recursion because this case should only occur when Nao is standing on top of the circle
				if(lowerCameraPosition.get(0)>0.2f){
					goToCircle(color);
				}
			}
			
		}
		currentCircle = color;
		}
		}
		
		catch(Exception e){
			
		}

	}

	/**
	 * Sending the Nao in front of a black circle/field
	 */
	void goInFrontOfBlackCircle(){
		
		
		pause = nao_handler.getPause();	
		
		while(pause){
			try{
			Thread.sleep(1000); pause = nao_handler.getPause();
			}
			catch(Exception e){
				
			}
		}
		
		try{
	
		if(lookForBlackCircle()){
		
		List<Float> blackCirclePosition = head_Functionality.getCirclePosition(1, "black");
		
		if(blackCirclePosition.get(0)>0.2f){
			
		blackCirclePosition = head_Functionality.getCirclePosition(1, "black");	
		leg_Functionality.moveTo(0.08f, 0, blackCirclePosition.get(2));
		goInFrontOfBlackCircle();
		
		}
		}
		}
		catch(Exception e){
			
		}
	}


	/**
	 * Checking if a black circle is in the field of vision or not (Only using the lower camera).
	 * @return true if there is a black circle
	 */
	boolean lookForBlackCircle() throws Exception{
		
		
		pause = nao_handler.getPause();	
		
		while(pause){
			try{
			Thread.sleep(1000); pause = nao_handler.getPause();
			}
			catch(Exception e){
				
			}
		}
		
		boolean found = false;
		ALMotion alM = new ALMotion(nao_handler.getSession());
		List<Float> position = new ArrayList<Float>();
		
		position = alM.getAngles("Head", false);
		
		List<Float> lowerCamera = head_Functionality.getCirclePosition(1, "black");
		
					if(lowerCamera.size()==0){
						
						position.set(0, 0f);
						position.set(1, 0.5f);
		
						alM.angleInterpolationWithSpeed("Head", position, 0.2f);
					}
					
					lowerCamera = head_Functionality.getCirclePosition(1, "black");
					
					if(lowerCamera.size()>0){
							
						found =  true;
					}
					else{
						
						found = false;
					}
					
						return found;
	}
	
	/**
	 * Searching for a given color on the playboard.
	 * @param color of the circle as a String (pink, orange, blue, purple, red, khaki, grey, green, beige)
	 * @return true when circle is found
	 * @throws Exception
	 */
	boolean lookForCircle(String color) throws Exception{
		
		boolean found;
		
		if(color.equals("grey")){
			found = lookForCircle(color, false, false);
		}
		else{
			found = lookForCircle(color, false, true);
		}
		
		return found;
	}
	
	/**
	 * Searching for a given color on the playboard.
	 * @param color of the circle as a String (pink, orange, blue, purple, red, khaki, grey, green, beige)
	 * @param rotation (true = turning right, false = turning left)
	 * @param length (true = looking for circles with a wider range , false = looking for circles in a shorter range)
	 * @return true when circle is found
	 * @throws Exception
	 */
	boolean lookForCircle(String color,boolean rotation, boolean length) throws Exception{
		
		
		pause = nao_handler.getPause();	
		
		while(pause){
			try{
			Thread.sleep(1000); pause = nao_handler.getPause();
			}
			catch(Exception e){
				
			}
		}
		
		boolean found = false;
		ALMotion alM = new ALMotion(nao_handler.getSession());
		List<Float> position = new ArrayList<Float>();

		position = alM.getAngles("Head", false);
		
		position.set(0, 0f);
		
		alM.angleInterpolationWithSpeed("Head", position, 0.2f);
		
		List<Float> upperCamera = head_Functionality.getCirclePosition(0,color), lowerCamera = head_Functionality.getCirclePosition(1,color);

		if((upperCamera.size()==0)&&(lowerCamera.size()==0)){
			
		if(length)
			position.set(0, 1f);//look left
		else
			position.set(0, 0.5f);//look left
		
		alM.angleInterpolationWithSpeed("Head", position, 0.2f);
		
		upperCamera = head_Functionality.getCirclePosition(0,color);
		lowerCamera = head_Functionality.getCirclePosition(1,color);
		
			if((upperCamera.size()==0)&&(lowerCamera.size()==0)){
			
				if(length)
					position.set(0, -1f);//look right
				else
					position.set(0, -0.5f);//look right	
				
				alM.angleInterpolationWithSpeed("Head", position, 0.2f);
		
				upperCamera = head_Functionality.getCirclePosition(0,color);
				lowerCamera = head_Functionality.getCirclePosition(1,color);
					
					
					 //if Nao has not found something with head movements then he shall try to look for the target with his whole body through rotations 
					
				int counter = 0;
				
				pause = nao_handler.getPause();
				
				while(((upperCamera.size()==0)&&(lowerCamera.size()==0))&&(counter<7)&&(!pause)){
					
						position.set(0, 0f);
						position.set(1, 0.2f);
						alM.angleInterpolationWithSpeed("Head", position, 0.2f);
						
						if(rotation)
							leg_Functionality.moveTo(0f,0f,-1f);
						
						else
							leg_Functionality.moveTo(0f, 0f, 1f);	
					
						upperCamera = head_Functionality.getCirclePosition(0,color);
						lowerCamera = head_Functionality.getCirclePosition(1,color);
						
						pause = nao_handler.getPause();
						
						counter++;
					}
				}
			}
		
					if((upperCamera.size()>0)||(lowerCamera.size()>0)){
						
						//the following movements are necessary to get precise values with the getCirclePosition-Method because the following cases only occur if the target was highly out of the field of vision
						if(alM.getAngles("Head", false).get(0)>0.8f){
							
								leg_Functionality.moveTo(0f, 0f, 1f);	
							
							lookForCircle(color);
							
						}
						else if(alM.getAngles("Head", false).get(0)<-0.8){
							
					
								leg_Functionality.moveTo(0f,0f,-1f);
							
							lookForCircle(color);
							
						}
						
						found =  true;
						
					}
					else{
						
						found = false;
						System.out.println("Could not find "+ color +" circle!");
						
					}
					
						return found;
	}
	
	
	/**
	 * Creating a Thread in order to avoid tokens
	 *	@deprecated not possible in combination with other methods that are using ColorBlobDetection at the same time
	 *	
	 */
	
	public static class AvoidToken extends Thread{
		
	 boolean active = true;
		
		Body_Functionality aM = new Body_Functionality(nao_handler);
		
		public void run() {
		
			while(active){
				
				System.out.println("Entered");
		try{
		
		sleep(1000);
		
		ALTracker alt=new ALTracker(nao_handler.getSession());
		ALMemory mem = new ALMemory(nao_handler.getSession());
		ALColorBlobDetection blob=new ALColorBlobDetection(nao_handler.getSession());
		blob.setColor(255, 89, 89, 10);
		
		blob.setObjectProperties(5, 0.0018f, "Circle");
		blob.setActiveCamera(1);
		alt.setMode("Head");
		
		alt.trackEvent("ALTracker/ColorBlobDetected");
						
					ALMotion alM = new ALMotion(nao_handler.getSession());	
					Leg_Functionality help = new Leg_Functionality(nao_handler);
					
					if(alt.getTargetPosition(2).size()>0){
						
					help.setPause(true);
					alM.stopMove();
					
					float tokenPosition = alt.getTargetPosition(2).get(2);
					if(tokenPosition>0f){
						
						System.out.println("avoiding");
						alt.stopTracker();
						alM.moveTo(0f,-0.4f,0f);
					
						
					}
					else{
						
						System.out.println("avoiding");
						alt.stopTracker();
						alM.moveTo(0f, 0.4f, 0f);
			
					}
					mem.unsubscribeAllEvents();
					alt.stopTracker();
					System.out.println("finished");
					
					}
					else{
						
						alt.stopTracker();
						mem.unsubscribeAllEvents();
						System.out.println("no obstacle");
					}
					
					}
		catch(Exception e){
			
		}
			}
	}
		
		
	public void finish(){
		
		active = false;
		System.out.println("finish avt");
	}

	}

/**
 * @param field desired field as int [0 to 23]
 * @return an Array of 2 colors as Strings from which the passed field parameter is reachable
 */
	String[] getCircles(int field){
	 
	 String [] color = new String[2];
	 
	 switch(field){
	 
	 	case 0: 
	 	case 3:
	 		color[0] = "green";
 			color[1] = "pink";
 			break;
 			
	 	case 1:
	 	case 4:
	 		color[0] = "pink";
	 		color[1] = "orange";
	 		break;
	 		
	 	case 2:
	 	case 5:
	 		color[0] = "orange";
	 		color[1] = "blue";
	 		break;
	 	
	 	case 14:
	 	case 13:
	 		color[0] = "blue";
	 		color[1] = "purple";
	 		break;
	 		
	 	case 23:
	 	case 20:
	 		color[0] = "purple";
	 		color[1] = "red";
	 		break;
	 		
	 	case 22:
	 	case 19:
	 		color[0] = "red";
	 		color[1] = "khaki";
	 		break;
	 		
	 	case 21:
	 	case 18:
	 		color[0] = "khaki";
	 		color[1] = "grey";
	 		break;
	 		
	 	case 9:
	 	case 10:
	 		color[0] = "grey";
	 		color[1] = "green";
	 		break;
	 	
	 	case 6:
	 	case 7:
	 	case 8:
	 	case 12:
	 	case 17:
	 	case 16:
	 	case 15:
	 	case 11:
	 		color[0] = "beige"; //only one color needed for those desired fields
	 		color[1] = "beige";
	 		break;
	 		
	 	default: System.out.println("Field does not exist!");
	 }
	 
	 return color;
 }


 
 /**
  * Going to the closest circle to a field by using a Dijkstra-Algorithm
  * @param field the desired field as Integer [0 to 23]
  */
	void goToClosestCircle(int field){
	 
	try{
		
	 String closestCircle = Dijkstra.getShorterPathColor(currentCircle, getCircles(field)); // getting the closest circle to the passed field
	 
	 String [] path = Dijkstra.getPath(currentCircle, closestCircle);// getting the path to the closest circle
	 if(!currentCircle.equals(closestCircle)){
		 for(int i = 1; i< path.length; i++ ){
		 
			 goToCircle(path[i]);
		 }
	 }
	}
	catch(InvalidInputException iie){
		
	}
	 
 }
 
 /**
  * Sending the Nao to a a desired field by using goToClosestCircle first and distinguishing between individual cases.
  * @param field desired field as Integer [0 to 23]
  */
	void goToField(int field){
		
		pause = nao_handler.getPause();	
		
		while(pause){
			try{
			Thread.sleep(1000); pause = nao_handler.getPause(); 
			}
			catch(Exception e){
				
			}
		}
		
	 try{
	 goToClosestCircle(field);

	 String pathType = getCircles(field)[0] + " and " + getCircles(field)[1];
	 ALMotion alM = new ALMotion(nao_handler.getSession());
	 List<Float> angles = new ArrayList<Float>();
	 
	 switch(pathType){
	 
	 //pathType 1 (cutting the playboard)
	 case "green and pink":
	 case "orange and blue":
	 case "purple and red":
	 case "khaki and grey":
		
		 // inner corner fields
		 switch(field){
		 case 3:
		 case 5:
		 case 20:
		 case 18:
			 if(currentCircle.equals(getCircles(field)[0])){
				 
				 turnToCircle(getCircles(field)[1]);
				 leg_Functionality.moveTo_blockingCall(0.4f, 0f, 0f);
				 while(head_Functionality.getCirclePosition(0, getCircles(field)[1]).get(0)>1f){
					 leg_Functionality.moveTo(0.15f, 0f, 0f); 
					 turnToCircle(getCircles(field)[1]);
				 }
				 while(!lookForBlackCircle()){
				 leg_Functionality.moveTo(0f, 0f, 1.2f);
				 }
				 goInFrontOfBlackCircle();
				 
			 }
			 else if(currentCircle.equals(getCircles(field)[1])){
				 
				 turnToCircle(getCircles(field)[0]);
				 leg_Functionality.moveTo_blockingCall(0.4f, 0f, 0f);
				 while( head_Functionality.getCirclePosition(0, getCircles(field)[0]).get(0)>1f){
					 leg_Functionality.moveTo(0.15f, 0f, 0f); 
					 turnToCircle(getCircles(field)[0]);
				 }
				 while(!lookForBlackCircle()){
				 leg_Functionality.moveTo(0f, 0f, -1.2f);
				 }
				 goInFrontOfBlackCircle();
				 
			 }
			 else{
				 System.out.println("Path does not exist!");
			 }
			 break;
		 // outer corner fields
		 case 0:
		 case 2:
		 case 23:
		 case 21:
			 if(currentCircle.equals(getCircles(field)[0])){
				 
				 turnToCircle(getCircles(field)[1]);
				 leg_Functionality.moveTo_blockingCall(0.4f, 0f, 0f);
				 while(head_Functionality.getCirclePosition(0, getCircles(field)[1]).get(0)>0.85f){
					 leg_Functionality.moveTo(0.15f, 0f, 0f); 
					 turnToCircle(getCircles(field)[1]);
				 }
				 leg_Functionality.moveTo_blockingCall(0f, 0f, -1f);
				 while(!lookForBlackCircle()){
					 angles = alM.getAngles("Head", false);
					 angles.set(1, 0f);
					 alM.angleInterpolationWithSpeed("Head", angles, 0.2f);
					 if(!lookForBlackCircle())
					 leg_Functionality.moveTo(0f, 0f, -1f);
				 }
				 goInFrontOfBlackCircle();
				 
			 }
			 else if(currentCircle.equals(getCircles(field)[1])){
				 
				 turnToCircle(getCircles(field)[0]);
				 leg_Functionality.moveTo_blockingCall(0.4f, 0f, 0f);
				 while( head_Functionality.getCirclePosition(0, getCircles(field)[0]).get(0)>0.85f){
					 leg_Functionality.moveTo(0.15f, 0f, 0f); 
					 turnToCircle(getCircles(field)[0]);
				 }
				 leg_Functionality.moveTo_blockingCall(0f, 0f, 1f);
				 while(!lookForBlackCircle()){
					 angles = alM.getAngles("Head", false);
					 angles.set(1, 0f);
					 alM.angleInterpolationWithSpeed("Head", angles, 0.2f);
					 if(!lookForBlackCircle())
					 leg_Functionality.moveTo(0f, 0f, 1f);
				 }
				 goInFrontOfBlackCircle();
				 
			 }
			 else{
				 System.out.println("Path does not exist!");
			 }
			 break;
		 }
		 
		 break;
		 
		//pathType 2 (gazing the playboard) 
	 case "pink and orange":
	 case "blue and purple":
	 case "red and khaki":
	 case "grey and green":
		
		 // inner middle fields
		 switch(field){
		 case 4:
		 case 13:
		 case 19:
		 case 10:
			 if(currentCircle.equals(getCircles(field)[0])){
				 
				 turnToCircle(getCircles(field)[1]);
				 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
				 turnToCircle(getCircles(field)[1]);
				 while(head_Functionality.getCirclePosition(0, getCircles(field)[1]).get(0)>1.1f){
					 leg_Functionality.moveTo(0.1f, 0f, 0f);
					 turnToCircle(getCircles(field)[1]);
				 }
				 turnToCircle("beige");
				 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
				 turnToCircle("beige");
				 while(head_Functionality.getCirclePosition(0, "beige").get(0)>1.22f){
					 leg_Functionality.moveTo(0.1f, 0f, 0f);
					 turnToCircle("beige");
				 }
				 leg_Functionality.moveTo_blockingCall(0f, 0f, -0.5f);
				 while(!lookForBlackCircle()){
				 leg_Functionality.moveTo(0f, 0f, -0.5f);
				 }
				 goInFrontOfBlackCircle();
			 }
			 else if(currentCircle.equals(getCircles(field)[1])){
				 
				 
				 turnToCircle(getCircles(field)[0]);
				 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
				 turnToCircle(getCircles(field)[0]);
				 while(head_Functionality.getCirclePosition(0, getCircles(field)[0]).get(0)>1.1f){
					 leg_Functionality.moveTo(0.1f, 0f, 0f);
					 turnToCircle(getCircles(field)[0]);
				 }
				 turnToCircle("beige");
				 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
				 turnToCircle("beige");
				 while(head_Functionality.getCirclePosition(0, "beige").get(0)>1.22f){
					 leg_Functionality.moveTo(0.1f, 0f, 0f);
					 turnToCircle("beige");
				 }
				 leg_Functionality.moveTo_blockingCall(0f, 0f, 0.5f);
				 while(!lookForBlackCircle()){
				 leg_Functionality.moveTo(0f, 0f, 0.5f);
				 }
				 goInFrontOfBlackCircle();
				 
			 }
			 else{
				 System.out.println("Path does not exist!");
			 }
			 break;
		 // outer middle fields
		 case 1:
		 case 14:
		 case 22:
		 case 9:
			 if(currentCircle.equals(getCircles(field)[0])){
				 
				 turnToCircle(getCircles(field)[1]);
				 leg_Functionality.moveTo_blockingCall(0.35f, 0f, 0f);
				 turnToCircle(getCircles(field)[1]);
				 while(head_Functionality.getCirclePosition(0, getCircles(field)[1]).get(0)>1f){
					 leg_Functionality.moveTo(0.1f, 0f, 0f);
					 turnToCircle(getCircles(field)[1]);
				 }
				 while(!lookForBlackCircle()){
				 leg_Functionality.moveTo(0f, 0f, 0.7f);
				 }
				 goInFrontOfBlackCircle();
				 
			 }
			 else if(currentCircle.equals(getCircles(field)[1])){
				 
				 turnToCircle(getCircles(field)[0]);
				 leg_Functionality.moveTo_blockingCall(0.35f, 0f, 0f);
				 turnToCircle(getCircles(field)[0]);
				 while(head_Functionality.getCirclePosition(0, getCircles(field)[0]).get(0)>1f){
					 leg_Functionality.moveTo(0.1f, 0f, 0f);
					 turnToCircle(getCircles(field)[0]);
				 }
				 while(!lookForBlackCircle()){
				 leg_Functionality.moveTo(0f, 0f, -0.7f);
				 }
				 goInFrontOfBlackCircle();
			 }
			 else{
				 System.out.println("Path does not exist!");
			 }
			 break;
		 }
		 break;
	
		 //pathType center
	 case "beige and beige":
		 
		 switch(field){
		 
		 case 6: //corner field
			 turnToCircle("pink");
			 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "pink").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("pink");
			 }		 
			 turnToCircle("green");
			 leg_Functionality.moveTo_blockingCall(0.2f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "green").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("green");
			 }	
			 goInFrontOfBlackCircle(); 	
		 break;
		 case 7://middle field
			 turnToCircle("pink");
			 while(head_Functionality.getCirclePosition(0, "pink").get(0)>1.6f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("pink");
			 }
			 turnToCircle("orange");
			 while(head_Functionality.getCirclePosition(0, "orange").get(0)>2.1f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("orange");
			 }
			 goInFrontOfBlackCircle(); 	
		 break;
		 case 8://corner field
			 turnToCircle("orange");
			 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "orange").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("orange");
			 }		 
			 turnToCircle("blue");
			 leg_Functionality.moveTo_blockingCall(0.2f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "blue").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("blue");
			 }	
			 goInFrontOfBlackCircle(); 	
		 break;
		 case 12://middle field
			 turnToCircle("blue");
			 while(head_Functionality.getCirclePosition(0, "blue").get(0)>1.6f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("blue");
			 }
			 turnToCircle("purple");
			 while(head_Functionality.getCirclePosition(0, "purple").get(0)>2.1f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("purple");
			 }
			 goInFrontOfBlackCircle(); 	
		 break;
		 case 17://corner field
			 turnToCircle("red");
			 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "red").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("red");
			 }		 
			 turnToCircle("purple");
			 leg_Functionality.moveTo_blockingCall(0.2f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "purple").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("purple");
			 }	
			 goInFrontOfBlackCircle(); 	
		 break;
		 case 16://middle field
			 turnToCircle("red");
			 while(head_Functionality.getCirclePosition(0, "red").get(0)>1.6f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("red");
			 }
			 turnToCircle("khaki");
			 while(head_Functionality.getCirclePosition(0, "khaki").get(0)>2.1f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("khaki");
			 }
			 goInFrontOfBlackCircle(); 
		 break;
		 case 15://corner field
			 turnToCircle("khaki");
			 leg_Functionality.moveTo_blockingCall(0.3f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "khaki").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("khaki");
			 }		 
			 turnToCircle("grey");
			 leg_Functionality.moveTo_blockingCall(0.2f, 0f, 0f);
			 while(head_Functionality.getCirclePosition(0, "grey").get(0)>1.5f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f);
				 turnToCircle("grey");
			 }	
			 goInFrontOfBlackCircle(); 	
		 break;
		 case 11://middle field
			 turnToCircle("grey");
			 while(head_Functionality.getCirclePosition(0, "grey").get(0)>1.6f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("grey");
			 }
			 turnToCircle("green");
			 while(head_Functionality.getCirclePosition(0, "green").get(0)>2.1f){
				 leg_Functionality.moveTo(0.1f, 0f, 0f); 
				 turnToCircle("green");
			 }
			 goInFrontOfBlackCircle(); 
		 break;
			 
		 }
		 break;
		 default: System.out.println("Error!!!");
	 }
	 }
	 catch(Exception e){
		 
	 }
 }
 

 /**
  * Turning Nao's body to the direction of a circle further away.
  * @param color of the circle as a String (pink, orange, blue, purple, red, khaki, grey, green, beige)
  */
	void turnToCircle(String color) {
	 
		
		pause = nao_handler.getPause();	
		
		while(pause){
			try{
			Thread.sleep(1000); pause = nao_handler.getPause();
			}
			catch(Exception e){
				
			}
		}
		
		try{
	if(lookForCircle(color)){
		ArrayList<Float> position = head_Functionality.getCirclePosition(0, color);
	 if((position.get(2)>0.05)||(position.get(2)<-0.05)){
		 
		 leg_Functionality.moveTo(0f, 0f, head_Functionality.getCirclePosition(0, color).get(2));
		 turnToCircle(color);
	 }
	}
		}
		catch(Exception e){
			
		}
 }
	

	/**	
	 * 
	 *  Posture which signalizes that Nao is waiting for his turn.
 */
	void thinkingPosture(){
		
		
		pause = nao_handler.getPause();	
		
		while(pause){
			try{
			Thread.sleep(1000); pause = nao_handler.getPause();
			}
			catch(Exception e){
				
			}
		}
		
		try{
		ALMotion alM = new ALMotion(nao_handler.getSession());
		List<Float> angles = alM.getAngles("Body", false);
		
		//head
		angles.set(0, 0.5f);
		angles.set(1, 0.5f);
		
		
		//Larm
		angles.set(2, 1.8f);
		angles.set(3, -0.3f);
		angles.set(4, -2f);
		angles.set(5, -0.5f);
		
		//Rarm
		angles.set(20, 1.5f);
		angles.set(21, -0.5f);
		angles.set(22, 0f);
		angles.set(23, 1.5f);
		angles.set(25, 1f);
		// leg
		angles.set(8, -0.2f);
		angles.set(9, 0.2f);
		angles.set(14, -0.2f);
		angles.set(10, 0f);
		angles.set(16, 0.2f);
		angles.set(11, 0.2f);
		angles.set(17, 0f);
		angles.set(12, 0f);
		angles.set(18, 0f);
		alM.angleInterpolationWithSpeed("Body", angles, 0.2f);
		
		}
		catch(Exception e){
			
		}
	}
		
	String getCurrentCircle(){
	 
		return currentCircle;
	}	
	
	/**	
	 * The method gets all placed men of the inner row of the play board. The analysis start in the row in front of the blue and purple circle with the middle field and then go from the right to the left and turns 90 degree afterwards and so on.
	 * @return ArrayList<Boolean> gets a list of 8 fields
	 */
	
	ArrayList<Boolean> getPlacedInnerGamingPieces() throws Exception{
		ALMotion alm=new ALMotion(nao_handler.getSession());
		ALTracker alt=new ALTracker(nao_handler.getSession());
		ALColorBlobDetection blob=new ALColorBlobDetection(nao_handler.getSession());
		ArrayList<Boolean> placedPieces=new ArrayList<Boolean>();
		List<Float> position = new ArrayList<Float>();
		position = alm.getAngles("Head", false);
		for(int i=0;i<3;i++){
			head_Functionality.pause();
			if(i==0){
				for(int j=0;j<2;j++){
						if(j==0){
							while(!lookForCircle("blue",false,true)){
								head_Functionality.pause();
							}
							position.set(0, 0.05f);
							position.set(1, 0f);	
							alm.angleInterpolationWithSpeed("Head", position, 0.1f);
							while(head_Functionality.getCirclePosition(0, "orange", false).size()!=0){
								leg_Functionality.moveTo(0f, 0f, 0.5f);
							}
							if(head_Functionality.getCirclePosition(0, "purple",false).size()!=0 && head_Functionality.getCirclePosition(0, "blue",false).size()!=0){
								position.set(0, 0f); //middle
								position.set(1, 0.15f);		
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
							}
							else{
								position.set(0, 0.5f); //right									
								position.set(1, 0f);
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								while(head_Functionality.getCirclePosition(0, "red", false).size()!=0){
									leg_Functionality.moveTo(0f, 0f, -0.4f);
								}
								if(head_Functionality.getCirclePosition(0, "blue",false).size()!=0 && head_Functionality.getCirclePosition(0, "purple",false).size()!=0){
									leg_Functionality.moveTo(0f, 0f, 0.5f);
									position.set(0, 0f); //middle
									position.set(1, 0.15f);									
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								}
								else{
									position.set(0, -0.07f); //right									
									position.set(1, 0f);	
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
									if(head_Functionality.getCirclePosition(0, "blue",false).size()!=0 && head_Functionality.getCirclePosition(0, "purple",false).size()!=0){
										leg_Functionality.moveTo(0f, 0f, -0.2f);
										position.set(0, 0f); //middle
										position.set(1, 0.15f);									
										alm.angleInterpolationWithSpeed("Head", position, 0.06f);
									}
									position.set(0, 0.2f); //right									
									position.set(1, 0f);	
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
									if(head_Functionality.getCirclePosition(0, "blue",false).size()!=0 && head_Functionality.getCirclePosition(0, "purple",false).size()!=0){
										leg_Functionality.moveTo(0f, 0f, 0.2f);
										position.set(0, 0f); //middle
										position.set(1, 0.15f);									
										alm.angleInterpolationWithSpeed("Head", position, 0.06f);
									}
								}
							}
							blob.setActiveCamera(1);
							blob.setColor(254, 77, 77, 8);
							alt.trackEvent("ALTracker/ColorBlobDetected");
							blob.setObjectProperties(8, 0.01f,"Unknown");
							if(alt.getTargetPosition().size()!=0){
								placedPieces.add(false);
								alt.stopTracker();	
								break;
							}	
						}
						else{
							blob.setActiveCamera(1);
							blob.setColor(57, 86, 170, 8);//blue nr.6 khaki
							alt.trackEvent("ALTracker/ColorBlobDetected");
							blob.setObjectProperties(8, 0.01f,"Unknown");
							if(alt.getTargetPosition().size()!=0){
								placedPieces.add(true);
							}
							else
								placedPieces.add(null);
						}
						alt.stopTracker();
					}
				}
			else if(i==1){
				for(int j=0;j<2;j++){
						if(j==0){
							position.set(0, 0f);
							position.set(1, 0f);	
							alm.angleInterpolationWithSpeed("Head", position, 0.06f);
							position.set(0, -0.73f); //right									
							position.set(1, -0.07f);
							alm.angleInterpolationWithSpeed("Head", position, 0.06f);
							blob.setActiveCamera(1);
							blob.setColor(254, 77, 77, 8);
							alt.trackEvent("ALTracker/ColorBlobDetected");
							blob.setObjectProperties(8, 0.01f,"Unknown");
							if(alt.getTargetPosition().size()!=0){
								placedPieces.add(false);
								alt.stopTracker();	
								break;
							}	
						}
						else{
							blob.setActiveCamera(1);
							blob.setColor(57, 86, 170, 8);//blue nr.8 beige
							alt.trackEvent("ALTracker/ColorBlobDetected");
							blob.setObjectProperties(8, 0.01f,"Unknown");
							if(alt.getTargetPosition().size()!=0){
								placedPieces.add(true);
							}
							else
								placedPieces.add(null);
						}
						alt.stopTracker();

					}
				}
			else if(i==2){
					for(int j=0;j<2;j++){
							if(j==0){
								position.set(0, 0f);
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								position.set(0, 0.77f); //right									
								position.set(1, -0.07f);
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								blob.setActiveCamera(1);
								blob.setColor(254, 77, 77, 8);
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(false);
									alt.stopTracker();	
									break;
								}	
							}
							else{
								blob.setActiveCamera(1);
								blob.setColor(57, 86, 170, 8);//blue nr.8 beige
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(true);
								}
								else
									placedPieces.add(null);
							}
							alt.stopTracker();
						}
					}
				}
				alt.stopTracker();
				leg_Functionality.moveTo(0f, 0f, 1.74f);
				for(int j=0;j<2;j++){
					if(j==0){
						while(!lookForCircle("red",false,true)){
							head_Functionality.pause();
						}
						position.set(0, 0.05f);
						position.set(1, 0f);	
						alm.angleInterpolationWithSpeed("Head", position, 0.06f);
						while(head_Functionality.getCirclePosition(0, "purple", false).size()!=0){
							leg_Functionality.moveTo(0f, 0f, 0.5f);
						}
						if(head_Functionality.getCirclePosition(0, "khaki",false).size()!=0 && head_Functionality.getCirclePosition(0, "red",false).size()!=0){
							position.set(0, 0.f); //right									
							position.set(1, 0.15f);
							alm.angleInterpolationWithSpeed("Head", position, 0.06f);
						}
						else{
							position.set(0, 0.5f); //right									
							position.set(1, 0f);	
							alm.angleInterpolationWithSpeed("Head", position, 0.06f);
							if(head_Functionality.getCirclePosition(0, "red",false).size()!=0 && head_Functionality.getCirclePosition(0, "khaki",false).size()!=0){
								leg_Functionality.moveTo(0f, 0f, 0.6f);
								position.set(0, 0f); //middle
								position.set(1, 0.15f);									
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
							}
							else{
								position.set(0, 0.05f); //right									
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								position.set(0, 0.2f); //right									
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								if(head_Functionality.getCirclePosition(0, "red",false).size()!=0 && head_Functionality.getCirclePosition(0, "khaki",false).size()!=0){
									leg_Functionality.moveTo(0f, 0f, 0.3f);
									position.set(0, 0f); //middle
									position.set(1, 0.15f);									
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								}
							}
						}
						blob.setActiveCamera(1);							blob.setColor(254, 77, 77, 8);
						alt.trackEvent("ALTracker/ColorBlobDetected");
						blob.setObjectProperties(8, 0.01f,"Unknown");
						if(alt.getTargetPosition().size()!=0){
						placedPieces.add(false);
						alt.stopTracker();	
						break;
						}	
					}
					else{
						blob.setActiveCamera(1);
						blob.setColor(57, 86, 170, 8);//blue nr.8 beige
						alt.trackEvent("ALTracker/ColorBlobDetected");
						blob.setObjectProperties(8, 0.01f,"Unknown");
						if(alt.getTargetPosition().size()!=0){
							placedPieces.add(true);
						}
						else
							placedPieces.add(null);
					}
					alt.stopTracker();
				}
				alt.stopTracker();
				leg_Functionality.moveTo(0f, 0f, 0.9f); 
				leg_Functionality.moveTo(0f, 0f, 0.92f);
				for(int i=0;i<3;i++){
					head_Functionality.pause();
					if(i==0){
						for(int j=0;j<2;j++){
							if(j==0){
								while(!lookForCircle("grey",false,true)){
									head_Functionality.pause();
								}
								position.set(0, 0.05f);
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								blob.setActiveCamera(0);
								while(head_Functionality.getCirclePosition(0, "khaki", false).size()!=0){
									leg_Functionality.moveTo(0f, 0f, 0.4f);
								}
								while(head_Functionality.getCirclePosition(0, "pink", false).size()!=0){
									leg_Functionality.moveTo(0f, 0f, -0.4f);
								}
								if(head_Functionality.getCirclePosition(0, "green",false).size()!=0 && head_Functionality.getCirclePosition(0, "grey",false).size()!=0){
									position.set(0, 0.05f); //middle
									position.set(1, 0.15f);									
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								}
								else{
									leg_Functionality.moveTo(0f, 0f, 0.18f);
									position.set(0, 0.5f); //right									
									position.set(1, 0f);	
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
									while(head_Functionality.getCirclePosition(0, "pink", false).size()!=0){
										leg_Functionality.moveTo(0f, 0f, -0.4f);
									}	
									if(head_Functionality.getCirclePosition(0, "green",false).size()!=0 && head_Functionality.getCirclePosition(0, "grey",false).size()!=0){
										leg_Functionality.moveTo(0f, 0f, 0.45f);
										position.set(0, 0f); //middle
										position.set(1, 0.15f);	
									}
									else{
										position.set(0, -0.07f); //middle
										position.set(1, 0f);	
										alm.angleInterpolationWithSpeed("Head", position, 0.06f);
										if(head_Functionality.getCirclePosition(0, "green",false).size()!=0 && head_Functionality.getCirclePosition(0, "grey",false).size()!=0){
											leg_Functionality.moveTo(0f, 0f, -0.2f);
											position.set(0, 0f); //middle
											position.set(1, 0.15f);									
											alm.angleInterpolationWithSpeed("Head", position, 0.06f);
										}
										position.set(0, 0.2f); //right									
										position.set(1, 0f);	
										alm.angleInterpolationWithSpeed("Head", position, 0.06f);
										if(head_Functionality.getCirclePosition(0, "green",false).size()!=0 && head_Functionality.getCirclePosition(0, "grey",false).size()!=0){
											leg_Functionality.moveTo(0f, 0f, 0.3f);
											position.set(0, 0.05f); //middle
											position.set(1, 0.15f);									
											alm.angleInterpolationWithSpeed("Head", position, 0.06f);
										}
									}
								}
								blob.setActiveCamera(1);
								blob.setColor(254, 77, 77, 8);
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(false);
									alt.stopTracker();	
									break;
								}	
							}
							else{
								blob.setActiveCamera(1);
								blob.setColor(57, 86, 170, 25);//blue nr.6 khaki
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(true);
								}
								else
									placedPieces.add(null);
							}
							alt.stopTracker();
						}
					}
				else if(i==1){
						for(int j=0;j<2;j++){
							if(j==0){
								position.set(0, 0.05f);
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								position.set(0, -0.78f); //right									
								position.set(1, -0.05f);
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								blob.setActiveCamera(1);
								blob.setColor(254, 77, 77, 8);
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(false);
									alt.stopTracker();	
									break;
								}	
							}
							else{
								blob.setActiveCamera(1);
								blob.setColor(57, 86, 170, 8);//blue nr.8 beige
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(true);
								}
								else
									placedPieces.add(null);
							}
							alt.stopTracker();
						}
					}
					else if(i==2){
						for(int j=0;j<2;j++){
							if(j==0){
								position.set(0, 0f);
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								position.set(0, 0.78f); //right									
								position.set(1, -0.05f);
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								blob.setActiveCamera(1);
								blob.setColor(254, 77, 77, 8);
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(false);
									alt.stopTracker();	
									break;
								}	
							}
							else{
								blob.setActiveCamera(1);
								blob.setColor(57, 86, 170, 8);//blue nr.8 beige
								alt.trackEvent("ALTracker/ColorBlobDetected");
								blob.setObjectProperties(8, 0.01f,"Unknown");
								if(alt.getTargetPosition().size()!=0){
									placedPieces.add(true);
								}
								else
									placedPieces.add(null);
							}
							alt.stopTracker();
						}
					}
				}
				alt.stopTracker();
				leg_Functionality.moveTo(0f, 0f, 1.82f); 
				for(int j=0;j<2;j++){
					if(j==0){
						while(!lookForCircle("pink",false,true)){
							head_Functionality.pause();
						}
						position.set(0,	0.05f);
						position.set(1, -0.05f);	
						alm.angleInterpolationWithSpeed("Head", position, 0.06f);
						while(head_Functionality.getCirclePosition(0, "green", false).size()!=0){
							leg_Functionality.moveTo(0f, 0f, 0.5f);
						}
						if(head_Functionality.getCirclePosition(0, "orange",false).size()!=0 && head_Functionality.getCirclePosition(0, "pink",false).size()!=0){
							position.set(0, 0f); //right									
							position.set(1, 0.15f);
							alm.angleInterpolationWithSpeed("Head", position, 0.06f);
						}
						else{
							position.set(0, 0.5f); //right									
							position.set(1, 0f);	
							alm.angleInterpolationWithSpeed("Head", position, 0.06f);
							if(head_Functionality.getCirclePosition(0, "pink",false).size()!=0 && head_Functionality.getCirclePosition(0, "orange",false).size()!=0){
								leg_Functionality.moveTo(0f, 0f, 0.6f);
								position.set(0, 0.05f); //middle
								position.set(1, 0.15f);									
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
							}
							else{
								position.set(0, 0f); //right									
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								position.set(0, 0.2f); //right									
								position.set(1, 0f);	
								alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								if(head_Functionality.getCirclePosition(0, "pink",false).size()!=0 && head_Functionality.getCirclePosition(0, "orange",false).size()!=0){
									leg_Functionality.moveTo(0f, 0f, 0.3f);
									position.set(0, 0.05f); //middle
									position.set(1, 0.15f);									
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								}
							}
						}
						blob.setActiveCamera(1);
						blob.setColor(254, 77, 77, 15);
						alt.trackEvent("ALTracker/ColorBlobDetected");
						blob.setObjectProperties(8, 0.01f,"Unknown");
						if(alt.getTargetPosition().size()!=0){
							placedPieces.add(false);
							alt.stopTracker();	
							break;
						}	
					}
					else{
						blob.setActiveCamera(1);
						blob.setColor(57, 86, 170, 15);//blue nr.8 beige
						alt.trackEvent("ALTracker/ColorBlobDetected");
						blob.setObjectProperties(8, 0.01f,"Unknown");
						if(alt.getTargetPosition().size()!=0){
							placedPieces.add(true);
						}
						else
							placedPieces.add(null);
					}
					alt.stopTracker();
				}
		alt.stopTracker();
		return placedPieces;
	}
	
	/**	
	 * The method gets all placed men of the outer two rows of the play board on the orange, purple, khaki and green circle. The analysis start in the second row with the left field, then the right one, then the corner point(vertex) and at last one the middle field of the first row
	 * @param color the current circle
	 * @return ArrayList<Boolean> gets a list of 4 fields from the current circle
	 */
	
	ArrayList<Boolean> getPlacedOuterGamingPieces(String color) throws Exception{
		ALMotion alm=new ALMotion(nao_handler.getSession());
		ALTracker alt=new ALTracker(nao_handler.getSession());
		ALColorBlobDetection blob=new ALColorBlobDetection(nao_handler.getSession());
		ArrayList<Boolean> placedPieces=new ArrayList<Boolean>();
		List<Float> position = new ArrayList<Float>();
		position = alm.getAngles("Head", false);
		for(int i=1;i<=4;i++){
			head_Functionality.pause();
				if(i==1){
					for(int j=0;j<2;j++){
						if(j==0){
								position.set(0, 0.5f);  //nr. 2 bottom cam 
								position.set(1, -0.5f);
								alm.angleInterpolationWithSpeed("Head", position, 0.045f);
							}
							blob.setActiveCamera(1);
							if(color.equals("orange")){
								if(j==0){
									blob.setColor(211, 69, 69, 15); //red nr.1,2 orange
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(false);
										alt.stopTracker();
										break;
									}	
								}
								else{
									blob.setColor(49, 73, 145, 15);//blue nr.1,2 orange
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(true);	
									}
									else
										placedPieces.add(null);
								}
							}
							else if(color.equals("purple")){
								if(j==0){
									blob.setColor(174, 57, 57, 15);//red nr.1,2 purple
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(false);
										alt.stopTracker();
										break;
	
									}	
								}
								else{
									blob.setColor(40, 59, 118, 15);//blue nr.1,2 purple
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(true);
									}
									else
										placedPieces.add(null);
								}
							}
							else if(color.equals("khaki")){
								position.set(0, 0.465f);  //nr. 2 bottom cam 
								position.set(1, -0.05f);
								alm.angleInterpolationWithSpeed("Head", position, 0.045f);
								if(j==0){
									blob.setColor(102, 32, 32, 15);//red nr.1,2 khaki
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(false);
										alt.stopTracker();
										break;
	
									}	
								}
								else{
									blob.setColor(39, 59, 117, 8);//blue nr.2 khaki
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(true);
									}
									else
										placedPieces.add(null);
								}
							}
							else if(color.equals("green")){
								position.set(0, 0.47f);  //nr. 2 bottom cam 
								position.set(1, -0.05f);
								alm.angleInterpolationWithSpeed("Head", position, 0.045f);
								if(j==0){
									blob.setColor(102, 32, 32, 3);//red nr.1,2,5 green
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(false);
										alt.stopTracker();
										break;
	
									}	
								}
								else{
									blob.setColor(25, 38, 74, 15);//blue nr.1,2,5 green
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(true);
									}
									else
										placedPieces.add(null);
								}
							}
							alt.stopTracker();
						}
						alt.stopTracker();
					}
				else if(i==2){
						for(int j=0;j<2;j++){
								if(j==0){
									position.set(0, -0.6f); // nr.5 top cam
									position.set(1, 0.4f);
									alm.angleInterpolationWithSpeed("Head", position, 0.06f);
								}
								blob.setActiveCamera(0);
								if(color.equals("orange")){
									if(j==0){
										blob.setColor(102, 32, 32, 15);//red nr.5 orange
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
										}	
									}
									else{
										blob.setColor(25, 38, 74, 15);//blue nr.5 orange
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
										}
										else
											placedPieces.add(null);
									}
								}
								else if(color.equals("purple")){
									if(j==0){
										blob.setColor(211, 69, 69, 15); //red nr.5 purple
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
	
											
										}	
									}
									else{
										blob.setColor(49, 73, 145, 15);//blue nr.5 purple
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
										}
										else
											placedPieces.add(null);
									}
								}
								else if(color.equals("khaki")){
									if(j==0){
										blob.setColor(174, 57, 57, 15);//red nr.5 khaki
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
	
										}	
									}
									else{
										blob.setColor(39, 59, 117, 15);//blue nr.5 khaki
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
										}
										else
											placedPieces.add(null);
									}
								}
								else if(color.equals("green")){
									if(j==0){
										blob.setColor(102, 32, 32, 3);//red nr.1,2,5 green
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
	
										}	
									}
									else{
										blob.setColor(25, 38, 74, 15);//blue nr.1,2,5 green
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
										}
										else
											placedPieces.add(null);
									}
								}
								alt.stopTracker();
							}
							alt.stopTracker();
						}
					else if(i==3){
						for(int j=0;j<2;j++){
								if(j==0){
									position.set(0, -1.2f); //nr.6 top cam
									position.set(1, 0.35f);
									alm.angleInterpolationWithSpeed("Head", position, 0.045f);
								}
								blob.setActiveCamera(0);
								if(color.equals("orange")){
									if(j==0){
										blob.setColor(255, 80, 80, 20); //red nr.6 orange
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
	
										}	
									}
									else{
										blob.setColor(57, 86, 170, 15);//blue nr.6 orange
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
	
										}
										else{
											placedPieces.add(null);
										}
									}
								}
								else if(color.equals("purple")){
									if(j==0){
										blob.setColor(255, 80, 80, 20); //red nr.6 purple
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
	
										}	
									}
									else{
										blob.setColor(57, 86, 170, 15);//blue nr.6 purple
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
										}
										else{
											placedPieces.add(null);
										}
									}
								}
								else if(color.equals("khaki")){
									if(j==0){
										blob.setColor(255, 80, 80, 20); //red nr.6 khaki
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
	
										}	
									}
									else{
										blob.setColor(57, 86, 170, 15);//blue nr.6 khaki
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
										}
										else{
											placedPieces.add(null);
										}
									}
								}
								else if(color.equals("green")){
									if(j==0){
										blob.setColor(255, 80, 80, 15); //red nr.6 green
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(false);
											alt.stopTracker();
											break;
	
										}	
									}
									else{
										blob.setColor(57, 86, 170, 15);//blue nr.6 green
										alt.trackEvent("ALTracker/ColorBlobDetected");
										blob.setObjectProperties(8, 0.01f,"Unknown");
										if(alt.getTargetPosition().size()!=0){
											placedPieces.add(true);
										}
										else{
											placedPieces.add(null);
										}
									}
								}
								alt.stopTracker();
							}
							alt.stopTracker();
						}
				else if(i==4){
					for(int j=0;j<2;j++){
							if(color.equals("green")){
								while(!lookForCircle("grey",false,false));
								blob.setActiveCamera(0);
								if(j==0){
									blob.setColor(102, 32, 32, 3);//red nr.1,2,5 green
									blob.setObjectProperties(8, 0.01f,"Unknown");
									alt.trackEvent("ALTracker/ColorBlobDetected");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,false);
										alt.stopTracker();
										break;
									}	
								}
								else if(j==1){
									blob.setColor(25, 38, 74, 15);//blue nr.1,2,5 green
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,true);
									}
									else
										placedPieces.add(0,null);
								}
							}
							else if(color.equals("orange")){
								while(!lookForCircle("pink",false,false));
								blob.setActiveCamera(0);
								if(j==0){
									blob.setColor(211, 69, 69, 8); //red nr.1,2 orange
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,false);
										alt.stopTracker();
										break;
	
									}	
								}
								else{
									blob.setColor(49, 73, 145, 15);//blue nr.1,2 orange
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,true);
									}
									else
										placedPieces.add(0,null);
								}
							}
							else if(color.equals("purple")){
								while(!lookForCircle("blue",false,false));
								blob.setActiveCamera(0);
								if(j==0){
									blob.setColor(174, 57, 57, 15);//red nr.1,2 purple
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,false);
										alt.stopTracker();
										break;
	
									}	
								}
								else{
									blob.setColor(40, 59, 118, 15);//blue nr.1,2 purple
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,true);
									}
									else
										placedPieces.add(0,null);
								}
							}
							else if(color.equals("khaki")){
								while(!lookForCircle("red",false,false));
								blob.setActiveCamera(0);
								if(j==0){
									blob.setColor(102, 32, 32, 15);//red nr.1,2 khaki
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,false);
										alt.stopTracker();
										break;
	
									}	
								}
								else{
									blob.setColor(25, 38, 74, 15);//blue nr.1 khaki
									alt.trackEvent("ALTracker/ColorBlobDetected");
									blob.setObjectProperties(8, 0.01f,"Unknown");
									if(alt.getTargetPosition().size()!=0){
										placedPieces.add(0,true);
									}
									else
										placedPieces.add(0,null);
								}
							}
							alt.stopTracker();
						}
						
					alt.stopTracker();
					}
			}
		return placedPieces;
	}
	
	/**	
	 * The method let the robot turn, so that the robot look towards the field side to analyze in a right way
	 */
	
	void turn() throws Exception{
		try{
		ArrayList<Float> root=new ArrayList<Float>();		
		root.add(1.6231234f);
		root.add(0.36551136f);
		root.add(0.28726241f);
		ArrayList<Float> look=new ArrayList<Float>();
		for(int i=0;i<3;i++){
			lookForCircle("beige",true,true);
			look=head_Functionality.getCirclePosition(0, "beige", true);
			if(look.get(2)>root.get(2) && look.get(2)>0){
				float k=(look.get(2)-look.get(2));
				leg_Functionality.moveTo(0f,0f,k);
			}
			else if(look.get(2)>root.get(2) && look.get(2)<0){
				leg_Functionality.moveTo(0f,0f,-(root.get(2)-look.get(2)));	
			}
			else
				leg_Functionality.moveTo(0f,0f,-(root.get(2)-look.get(2)));
		}
		} catch(IndexOutOfBoundsException e){
			e.getMessage();
			turn();
		}	
	}
}	
		
			

		
			
