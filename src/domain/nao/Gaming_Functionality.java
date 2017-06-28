package domain.nao;



import java.util.ArrayList;

import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import foundation.data.Move;

/**
 * Class providing the functionality to play Nine Men's Morris
 * @author WeiHao, Aljoscha
 *
 */
public class Gaming_Functionality {
	
	Body_Functionality body;
	NAO_Handler nao_Handler;
	private boolean start=false;
	
	Gaming_Functionality(NAO_Handler naoHandler) {
		
		this.nao_Handler = naoHandler;
		body = new Body_Functionality(nao_Handler);
	
	}
	/**
	 * The method analyze the whole play board if on the fields are men or not. Can start from any circles and ends always at the orange circle
	 * @return Boolean[] Array of the play board read in with the size 24
	 *
	 */
	
	Boolean[] analyzePlayboard() throws Exception{
		ALRobotPosture alp=new ALRobotPosture(nao_Handler.getSession());
		alp.goToPosture("Stand", 0.5f);
		ArrayList<Boolean> partOfBoard=new ArrayList<Boolean>();
		ArrayList<Boolean> board=new ArrayList<Boolean>();
		for(int i=0;i<24;i++){
			board.add(null);
		}
		int counter=0;
		while(counter<5){
			if(counter==4){
				body.goToCircle("beige",true);
			}
			if(Body_Functionality.currentCircle.equals("orange") || Body_Functionality.currentCircle.equals("green") || Body_Functionality.currentCircle.equals("purple") ||  Body_Functionality.currentCircle.equals("khaki")){
					if(start){
						body.goToCircle(Body_Functionality.currentCircle,true);
						body.turn();
					}
					partOfBoard=body.getPlacedOuterGamingPieces(Body_Functionality.currentCircle);
					counter++;
					if(Body_Functionality.currentCircle.equals("orange")){
						board.set(1,partOfBoard.get(0));
						board.set(2,partOfBoard.get(3));
						board.set(4,partOfBoard.get(1));
						board.set(5,partOfBoard.get(2));
						start=true;
						if(counter<4)
							body.goToCircle("pink",true);
					}
					else if(Body_Functionality.currentCircle.equals("green")){
						board.set(0,partOfBoard.get(3));
						board.set(3,partOfBoard.get(2));
						board.set(9,partOfBoard.get(0));
						board.set(10,partOfBoard.get(1));
						if(counter<4)
							body.goToCircle("grey",true);
					}
					else if(Body_Functionality.currentCircle.equals("khaki")){
						board.set(18,partOfBoard.get(2));
						board.set(19,partOfBoard.get(1));
						board.set(21,partOfBoard.get(3));
						board.set(22,partOfBoard.get(0));
						if(counter<4)
							body.goToCircle("red",true);
					}
					else if(Body_Functionality.currentCircle.equals("purple")){
						board.set(13,partOfBoard.get(1));
						board.set(14,partOfBoard.get(0));
						board.set(20,partOfBoard.get(2));
						board.set(23,partOfBoard.get(3));
						if(counter<4)
							body.goToCircle("blue",true);
					}
			}
			else if(Body_Functionality.currentCircle.equals("beige")){
				body.goToCircle("beige",true);
				partOfBoard=body.getPlacedInnerGamingPieces();
				board.set(12,partOfBoard.get(0));
				board.set(8,partOfBoard.get(1));
				board.set(17,partOfBoard.get(2));
				board.set(16,partOfBoard.get(3));
				board.set(11,partOfBoard.get(4));
				board.set(15,partOfBoard.get(5));
				board.set(6,partOfBoard.get(6));
				board.set(7,partOfBoard.get(7));
				counter++;
				body.goToCircle("orange",true);
			}
			else{
				if(Body_Functionality.currentCircle.equals("pink")){
					body.goToCircle("green",true);
				}
				else if(Body_Functionality.currentCircle.equals("grey")){
					body.goToCircle("khaki",true);
				}
				else if(Body_Functionality.currentCircle.equals("red")){
					body.goToCircle("purple",true);
				}
				else if(Body_Functionality.currentCircle.equals("blue")){
					body.goToCircle("orange",true);
				}
			}
		}
		Boolean[] boardArray = new Boolean[24];
		for (int i = 0; i < boardArray.length; i++) {
			boardArray[i] = board.get(i);
		}
		return boardArray;
	}
	
	/**
	 * This method will execute a given move physically.
	 * @param move
	 */
	void executeMove(Move move){
		
		int moveType = move.length();
		String color;
		if(move.getColor(0))
			color = "blue";
		else
			color = "red";
		
		switch(moveType){
		
		case 2:
			
			body.leg_Functionality.wakeUpPosition();
			
			if(move.getField(0).getIndex()<=23){
				
			System.out.println("Taking " + color +" gaming piece from field " + move.getField(0).getIndex());
			
			body.goToField(move.getField(0).getIndex());
			body.leg_Functionality.torsoDown();
			body.arm_Functionality.grab(move.getColor(0));
			body.leg_Functionality.torsoUp();
			body.goToCircle(body.getCurrentCircle());
			
			}
			
			System.out.println("Placing " + color +" gaming piece onto field " + move.getField(1).getIndex());
			
			body.goToField(move.getField(1).getIndex());
			body.leg_Functionality.torsoDown();
			body.arm_Functionality.letGo();
			body.leg_Functionality.torsoUp();
			body.goToCircle(body.getCurrentCircle());
			if(body.getCurrentCircle().equals("beige"))
				body.goToCircle("orange");
			body.turnToCircle("beige");
			body.thinkingPosture();
			
			break;
			
		case 4:
			
			body.leg_Functionality.wakeUpPosition();
			
			if(move.getField(0).getIndex()<=23){
				
				System.out.println("Taking " + color +" gaming piece from field " + move.getField(0).getIndex());
				
				body.goToField(move.getField(0).getIndex());
				body.leg_Functionality.torsoDown();
				body.arm_Functionality.grab(move.getColor(0));
				body.leg_Functionality.torsoUp();
				body.goToCircle(body.getCurrentCircle());
				
				}

				System.out.println("Placing " + color +" gaming piece onto field " + move.getField(1).getIndex());
			
				body.goToField(move.getField(1).getIndex());
				body.leg_Functionality.torsoDown();
				body.arm_Functionality.letGo();
				body.leg_Functionality.torsoUp();
				body.goToCircle(body.getCurrentCircle());
				
				System.out.println("Removing " + color +" gaming piece to field " + move.getField(1).getIndex());
				
				body.goToField(move.getField(2).getIndex());
				body.leg_Functionality.torsoDown();
				body.leg_Functionality.torsoUp();
				body.goToCircle(body.getCurrentCircle());
				if(body.getCurrentCircle().equals("beige"))
					body.goToCircle("orange");
				body.turnToCircle("beige");
				body.thinkingPosture();
				
			break;
		}
	}

}
