package domain.represent;
import java.util.ArrayList;
import foundation.data.*;
import foundation.exception.*;
/**
* This class contains a virtual representation of the board as well as all the calculations.
* In this class, the colours remain black and white instead of red and blue.
* black==false==red, white==true==blue;
* @author Jonas
 */
public class Playboard {
	/**
	 * this array contains all the fields and is the heart of the Playboard
	 */
	private Field[] fields = new Field[28];
	/**
	 * all men are tracked. the current position of all coloured man is saved here
	 */
	private Field[] blackfields;
	/**
	 * all men are tracked. the current position of all white man is saved here
	 */
	private Field[] whitefields;
	/**
	 * this boolean tells, if men are to be set or to be moved. gamestate[0] ist for white, [1] for black
	 * and true means all men have been set.
	 */
	private boolean[] gamestate={false,false};
	/**
	 * This method creates a new playboard with all men on their initial piles.
	 * all connections to neighbours are set manually.
	 * The fields are counted from left to right and bottom to top. More can be found in the user guide.
	 * the piles are: white initial (unset white men), white burnt (containing all lost black men), 
	 * black initial (unset black men), black burnt (lost white men)
	 * gamestate contains, if white has set all men (gamestate[0]==true, else false) as well as
	 * if black has set all men (gamestate[1]==true, else false)
	 */

	public Playboard(){
		blackfields = new Field[9];
		whitefields = new Field[9];
				
		//create corners
		fields[0] = new Corner(0);
		fields[2] = new Corner(2);
		fields[3] = new Corner(3);
		fields[5] = new Corner(5);
		fields[6] = new Corner(6);
		fields[8] = new Corner(8);
		fields[15] = new Corner(15);
		fields[17] = new Corner(17);
		fields[18] = new Corner(18);
		fields[20] = new Corner(20);
		fields[21] = new Corner(21);
		fields[23] = new Corner(23);
		//create crosses
		fields[4] = new Cross(4);
		fields[10] = new Cross(10);
		fields[13] = new Cross(13);
		fields[19] = new Cross(19);
		
		//create TBottoms
		fields[1] = new TJunctionBottom(1);
		fields[9] = new TJunctionBottom(9);
		fields[14] = new TJunctionBottom(14);
		fields[22] = new TJunctionBottom(22);
		
		//create TTops
		fields[7] = new TJunctionTop(7);
		fields[11] = new TJunctionTop(11);
		fields[12] = new TJunctionTop(12);
		fields[16] = new TJunctionTop(16);
		
		//create Piles
		for (int i = 24;i<28;i++){
			fields[i]=new Pile(i);
		}
				
		//now the code is some kind of ugly, but it seemed the handiest way...
		fields[0].setNeighbours(fields[9],fields[1]);
		fields[1].setNeighbours(fields[0], fields[2], fields[4]);
		fields[2].setNeighbours(fields[1], fields[14]);
		
		fields[3].setNeighbours(fields[10], fields[4]);
		fields[4].setNeighbours(fields[3], fields[5], fields[7], fields[1]);
		fields[5].setNeighbours(fields[4], fields[13]);
		
		fields[6].setNeighbours(fields[11], fields[7]);
		fields[7].setNeighbours(fields[6], fields[8], fields[4]);
		fields[8].setNeighbours(fields[7], fields[12]);
		
		fields[9].setNeighbours(fields[21], fields[0], fields[10]);
		fields[10].setNeighbours(fields[18], fields[3], fields[11], fields[9]);
		fields[11].setNeighbours(fields[15], fields[6], fields[10]);
		
		fields[12].setNeighbours(fields[8], fields[17], fields[13]);
		fields[13].setNeighbours(fields[5], fields[20], fields[12], fields[14]);
		fields[14].setNeighbours(fields[2], fields[23], fields[13]);
		
		fields[15].setNeighbours(fields[16], fields[11]);
		fields[16].setNeighbours(fields[17], fields[15], fields[19]);
		fields[17].setNeighbours(fields[12], fields[16]);
		
		fields[18].setNeighbours(fields[19], fields[10]);
		fields[19].setNeighbours(fields[20], fields[18], fields[16], fields[22]);
		fields[20].setNeighbours(fields[13], fields[19]);
		
		fields[21].setNeighbours(fields[22], fields[9]);
		fields[22].setNeighbours(fields[23], fields[21], fields[19]);
		fields[23].setNeighbours(fields[14], fields[22]);
		
		//set men on the board
		for (int i=0; i<9;i++){
			((Pile) fields[24]).setMan(true, i);
			((Pile) fields[26]).setMan(false, i);
			whitefields[i]=fields[24];
			blackfields[i]=fields[26];
		}
	}
	
	public Field getField(int index){
		return fields[index];
	}
	
	/**
	 * to be invoked by the GUI
	 * @return an array of 4 integers containing the number of men on the pile.
	 */
	public int[] getPile(){
		int [] rtn = new int[4];
		for(int i=0;i<4;i++){
			rtn[i]=fields[i+24].getManCount();
		}
		return rtn;
	}
	
	/**
	 * This methods is invoked after the robot has analyzed the board.
	 * it either calculates a move out of the changes and checks if it is was possible for the opponent
	 * or just accepts the state the nao read.
	 * @param nao the result of the analyzePlayboard method
	 * @param colour the colour of the opponent
	 * @param strict if true, it is checked if the move was allowed, false for the other case.
	 * @throws InvalidMoveException if no correct move has been made
	 */
	public void updateByNAO(Boolean[] nao, boolean colour, boolean strict) throws InvalidMoveException{
		if(strict)
			updateByNAO(nao,colour);
		else
			updateByNAO(nao);
	}
	/**
	 * this is the relaxed version which just accepts what the nao read without questioning it.
	 * Or, a better way to say it: If mistakes occures, it tries to create a board which is suitable for a new move.
	 * @param nao the result of the analyzePlayboard method
	 */
	private void updateByNAO(Boolean [] nao){
		boolean trouble = false;
		Playboard oldone = deepCopy();
		for (int i=0;i<24;i++){
			if (fields[i].getMan()!=nao[i]){
				fields[i].setMan(nao[i]);
			}
		}
		int black=allMenOnBoard(false, true)-oldone.allMenOnBoard(false, true);
		int white = allMenOnBoard(true, true)-oldone.allMenOnBoard(true,true);
		switch(black){
		case 1:{
			if(white==0){//black set
				if (fields[26].isEmpty()){
					trouble=true;
				}
				else
					fields[26].setMan(null);
			}
			else if(white==-1){//black set, white removed
				fields[26].setMan(null);
				fields[27].setMan(true);
			}
			else
				trouble=true;
			break;
		}
		case 0:{
			if(white==1){//white set
				fields[24].setMan(null);
			}
			else if(white==-1){//white removed
				fields[27].setMan(true);
			}
			else if(white==0){//black or white moved
				
			}
			else
				trouble=true;
			break;
		}
		case -1:{
			if(white==1){//white set, black removed
				fields[24].setMan(null);
				fields[25].setMan(false);
			}
			else if(white==0){//black removed
				fields[25].setMan(false);
			}
			else
				trouble=true;
			break;
		}
		default: trouble=true;
		}
		if(trouble)
			System.err.println("The change of the board was no correct move. The algorithm assumes the position of all existing men.");
		//as the trouble case also works if nothing is wrong, trouble has to set
		//but i keep this boolean because it is interesting if wrong moves are done or not.
		//read all men on the board
		int whitepointer=0;
		int blackpointer=0;
		int memory=0;
		for(int i=0;i<24;i++){
			if(fields[i].manEqualsColour(false)){
				if(blackpointer==9){
					continue;
				}
				blackfields[blackpointer]=fields[i];
				blackpointer++;
			}
			else if(fields[i].manEqualsColour(true)){
				if(whitepointer==9){
					continue;
				}
				whitefields[whitepointer]=fields[i];
				whitepointer++;
			}
			
		}
		//fill with burnt men
		memory=whitepointer;
		while(whitepointer<memory+fields[27].getManCount()){
			if(whitepointer==9){
				break;
			}
			whitefields[whitepointer]=fields[27];
			whitepointer++;
		}
		memory=whitepointer;
		//fill with unset men
		while(whitepointer<memory+fields[24].getManCount()){
			if(whitepointer==9){
				break;
			}
			whitefields[whitepointer]=fields[24];
			whitepointer++;
		}
		//assume the rest to be burnt
		while(whitepointer<9){
			whitefields[whitepointer]=fields[27];
			whitepointer++;
		}
		//change to black
		//fill with burnt men
		memory=blackpointer;
		while(blackpointer<blackpointer+fields[25].getManCount()){
			if(blackpointer==9){
				break;
			}
			blackfields[blackpointer]=fields[25];
			blackpointer++;
		}
		//fill with unset men
		memory=blackpointer;
		while(blackpointer<memory+fields[26].getManCount()){
			if(blackpointer==9){
				break;
			}
			blackfields[blackpointer]=fields[26];
			blackpointer++;
		}
		//assume the rest to be burnt
		while(blackpointer<9){
			blackfields[blackpointer]=fields[25];
			blackpointer++;
		}
		
		//write playboard like the XFields.
		//clear fields
		int i=0;
		
		while(i<24){
			fields[i].setMan(null);
			i++;
		}
		while(i<28){
			((Pile)fields[i]).clear();
			i++;
		}
		for(int a=0;a<9;a++){
			blackfields[a].setMan(false);
			whitefields[a].setMan(true);
		}
	}
	
	/**
	 * This is the strict method, it is deprecated as it is not used by now.
	 * The development team decided to not let NAO check the board a second time but instead 
	 * let the user check it, as this might last only a few seconds instead of 10 minutes. 
	 * As this decision was made before the method was finished, it is partly untested. 
	 * @param nao the result of the analyzePlayboard method
	 * @param colour the oppontent's colour
	 * @throws InvalidMoveException if no correct move has been made
	 * @deprecated
	 */
	private void updateByNAO(Boolean[] nao, boolean colour) throws InvalidMoveException{
		if (nao.length!=24){
			System.err.println("wrong number of fields given");
			return;
		}
		//look for changes
		//if impossible, throw exception
		//else, do it
		Move executed; 
		Playboard oldone = this.deepCopy();
		ArrayList<Integer> manSetFields = new ArrayList<Integer>();
		ArrayList<Boolean> manSetColour = new ArrayList<Boolean>();
		ArrayList<Integer> manRemovedFields = new ArrayList<Integer>();
		ArrayList<Boolean> manRemovedColour = new ArrayList<Boolean>();
		//check for changes an log them
		for (int i =0;i<24;i++){
			if (fields[i].getMan()!=nao[i]){
				if (fields[i].getMan()==null){
					if (nao[i].booleanValue()){
						manSetFields.add(i);
						manSetColour.add(true);
					}
					else{
						manSetFields.add(i);
						manSetColour.add(false);
					}
				}
				else{
					manRemovedFields.add(i);
					if(fields[i].getMan().booleanValue()){
						manRemovedColour.add(true);
					}
					else
						manRemovedColour.add(false);
				}
			}
		}
		
		
		//try to interpret changes
		if (manSetFields.size()==1){
			//only one man has been set.
			switch(manRemovedFields.size()){
				case 0:{//if only a man has been set, either whiteInitial or blackInitial lower about 1
					if(manSetColour.get(0).booleanValue()){
						executed = new Move(false,true,fields[24],true,true,fields[manSetFields.get(0)]);
					}
					else{
						executed = new Move(false,false,fields[26],true,false,fields[manSetFields.get(0)]);
						
					}
					break;
				}
				case 1:{//one man has been set, one removed.
					//if both are of the same colour, the opponent just moved one field without completing a mill
					if(manSetColour.get(0)==manRemovedColour.get(0)){
						executed = new Move(false,manSetColour.get(0),fields[manRemovedFields.get(0)],true,manSetColour.get(0),fields[manSetFields.get(0)]);			
					}//the opponent has set a new man build a mill
					else{
						if(manSetColour.get(0)){
							executed = new Move(false,true,fields[24],true,true,fields[manSetFields.get(0)],false,false,fields[manRemovedFields.get(0)],true,false,fields[25]);
						}
						else{
							executed = new Move(false,false,fields[26],true,false,fields[manSetFields.get(0)],false,true,fields[manRemovedFields.get(0)],true,true,fields[27]);
						}
					}
					break;
				}
				case 2:{//during normal game, a mill has been built - hopefully
					if(manRemovedColour.get(0)==manRemovedColour.get(1)){
						throw new InvalidMoveException("2 man of the same colour have been removed");
					}
					else{
						if(manSetColour.get(0)){
							manSetFields.add(25);
						}
						else{
							manSetFields.add(27);
						}
						if(manRemovedColour.get(0)==manSetColour.get(0)){
							executed = new Move(false,manRemovedColour.get(0),fields[manRemovedFields.get(0)],
									true,manSetColour.get(0),fields[manSetFields.get(0)],
									false,manRemovedColour.get(1),fields[manRemovedFields.get(1)],
									true,manRemovedColour.get(1),fields[manSetFields.get(1)]);
							}
						else{
							executed = new Move(false,manRemovedColour.get(1),fields[manRemovedFields.get(1)],
									true,manSetColour.get(0),fields[manSetFields.get(0)],
									false,manRemovedColour.get(0),fields[manRemovedFields.get(0)],
									true,manRemovedColour.get(1),fields[manSetFields.get(1)]);
						}
					}
					
					break;
				}
				default: throw new InvalidMoveException("more than two man have been removed");
			}
		}
		else{
			throw new InvalidMoveException("more or less than one man have been set");
		}
		
		//check if valid.
		ArrayList<Move> weFreeAgain = new ArrayList<Move>();
		try{
			weFreeAgain = oldone.possibleMoves(colour);
		}
		catch(GameLostException g){
			g.printStackTrace();
			throw new InvalidMoveException("Game has already ended. Why do we search for new moves?");
		}
		if(!weFreeAgain.contains(executed)){
			throw new InvalidMoveException("Opponent has executed an incorrect move - or nao has not recognised some men.");
		}
		else
			this.updateByAI(executed, false);
		
	}
	
	/**
	 * this method is invoked by the AI to change something on the board.
	 * On top, it changes the gamestate, if one of the initial piles gets empty.
	 * @param move the move object containing all changes to the current version of the board
	 * @param reverse if the move is to be reversed, reverse shall be true
	 */
	public void updateByAI(Move move, boolean reverse){
		if (reverse)
			move=move.reverse();
		int rememberme=-1;
		for (int i=0;i<move.length();i++){
			
			if (move.getAction(i)){
				if(move.getColor(i)){
					whitefields[rememberme]=fields[move.getField(i).getIndex()];
				}
				else{
					blackfields[rememberme]=fields[move.getField(i).getIndex()];
				}
				fields[move.getField(i).getIndex()].setMan(move.getColor(i));
				
			}
				
			
			else{
				fields[move.getField(i).getIndex()].setMan(null);
				//System.out.println(fields[move.getField(i).getIndex()]);
				if(move.getColor(i)){
					for (int a=0;a<9;a++){
						if(whitefields[a].getIndex()==move.getField(i).getIndex()){
							rememberme=a;
							}
					}
					
				}
				else{
					for (int a=0;a<9;a++){
						if(blackfields[a].getIndex()==move.getField(i).getIndex()){
							rememberme=a;
						}
					}
				}
			}
		}
		if (!(gamestate[0]&gamestate[1])){
			if (fields[24].getManCount()==0){
				gamestate[0]=true;
			}
			if (fields[26].getManCount()==0){
				gamestate[1]=true;
			}
		}
		
	}
	
	/**
	 * the board displayed by the GUI needs to be updated via an Boolean array with the length of 24.

	 * @return an Array containing only the colours of the men and not the Field objects.
	 */
	public Boolean[] updateGUI(){

		Boolean [] rtn = new Boolean[24];
		for(int i=0;i<9;i++){
			try{rtn[whitefields[i].getIndex()]=true;}
			catch(ArrayIndexOutOfBoundsException e){
			//only happens, if a men is placed on one of the piles. this does not matter to the GUI	
			}
			try{rtn[blackfields[i].getIndex()]=false;}
			catch(ArrayIndexOutOfBoundsException e){
			//only happens, if a men is placed on one of the piles. this does not matter to the GUI	
			}
			
		}
		return rtn;
	}
	
	
	/**
	 * if a user is not satisfied by what NAO assumed the board to look like, he can correct it via an interface
	 * if he does so, the state given is correct and is therefore just written.
	 * @param nao containing all fields up to 23
	 * @param piles containing the piles from 24 to 27 with an integer number containing the men to be placed there.
	 */
	public void updateByGUI(Boolean[] nao, int[] piles){
		int z=0;
		while(z<24){
			fields[z].setMan(nao[z]);
			z++;
		}
		while(z<28){
			for(int a=0;a<9;a++){
				((Pile)fields[z]).setMan(null, a);
			}
			z++;
		}
		boolean c =true;
		for(int a=24;a<28;a++){
			for(int b=0;b<piles[a-24];b++){
				((Pile)fields[a]).setMan(c, b);
			}
			if(a==27)
				c=true;
			else
				c=false;
		}
		//detect piles
		int blackpointer=0;
		int whitepointer=0;
		int memory=0;
		//	track black unset
		while(blackpointer< fields[26].getManCount()){
			 blackfields[blackpointer]= fields[26];
			blackpointer++;
		}//track black lost
		memory=blackpointer;
		while(blackpointer<(memory+ fields[25].getManCount())){
			 blackfields[blackpointer]= fields[25];
			blackpointer++;
		}//track white unset
		while(whitepointer< fields[24].getManCount()){
			 whitefields[whitepointer]= fields[24];
			whitepointer++;
		}//track white lost
		memory=whitepointer;
		while (whitepointer<(memory+ fields[27].getManCount())){
			 whitefields[whitepointer]= fields[27];
			whitepointer++;
		}
		//search the rest.
		for(int i=0;i<24;i++){
			if( fields[i].manEqualsColour(false)){
				if(blackpointer==9){
					System.err.println("StackOverflow 595");
				}
				 blackfields[blackpointer]= fields[i];
				blackpointer++;
			}
			else if( fields[i].manEqualsColour(true)){
				if(whitepointer==9){
					System.err.println("StackOverflow 602");
				}
				 whitefields[whitepointer]= fields[i];
				whitepointer++;
			}
			
		}
		//gamestate
		if(fields[24].getManCount()==0)
			gamestate[0]=true;
		else
			gamestate[0]=false;
		if (fields[26].getManCount()==0)
			gamestate[1]=true;
		else
			gamestate[1]=false;
		
	}
	/**
	 * this method is to be invoked by the AI. It calculates all possible moves depending on
	 * the current state of the game.
	 * @param input is a MultiPrioritySet of Move objects to be handed over by the invoking AI,
	 * it contains all moves executed so far. New moves will be added while the method runs
	 * @param output all possible Moves, but maybe an object being part of the input, 
	 * if the move was possible before
	 * @param colour the moves for the player of this colour shall be calculated
	 * @return returns the output set with all moves
	 * @throws GameLostException if the game was lost, an exception is thrown
	 */
	 
	public MultiPrioritySet<Move> possibleMoves(MultiPrioritySet<Move> input,MultiPrioritySet<Move> output, boolean colour) throws GameLostException{
		int[] ownpiles = {24,25};
		int[] opponentpiles = {26,27};
		Field[] ownfields = whitefields;
		Field[] opponentfields = blackfields;
		boolean stateOfGame;
		//init colour
		if (colour){
			if(gamestate[0]){
				stateOfGame=true;
			}
			else
				stateOfGame=false;
			}
		else{
				int[] change = ownpiles;
				ownpiles=opponentpiles;
				opponentpiles=change;
				ownfields=blackfields;
				opponentfields=whitefields;
				if(gamestate[1]){
					stateOfGame=true;
				}
				else
					stateOfGame=false;
				
		}
		//check if game is not lost by now
		if(((Pile)fields[opponentpiles[1]]).getManCount()==6&&((Pile)fields[ownpiles[1]]).getManCount()==6){
			return output;
		}
		
		if(((Pile)fields[opponentpiles[1]]).getManCount()>6)
			throw new GameLostException("less then 3 men", colour);

		//if you have set your men
		if (stateOfGame){
			ArrayList<Field> mills = new ArrayList<Field>(); //contains opponents fields being part of a mill
			for (int i=0;i<9;i++){
				if(ownfields[i].getIndex()<24){
					int indey = ownfields[i].getIndex();
					Field dis = fields[indey];
					//check for your possibilities to move
					for(int a=0;a<dis.neighbours.length;a++){
						if(dis.neighbours[a].isEmpty()){
							//if you would build a mill
							if (dis.neighbours[a].partOfMill(colour,indey)){
								//check the opponent's men
								for (int b=0;b<9;b++){
								//if the man is on board and not detected as part of mill
									if(opponentfields[b].getIndex()<24 && !mills.contains(opponentfields[b])){
										//if it is really not part of a mill
										if((!fields[opponentfields[b].getIndex()].partOfMill())){
											//new move
										Move helo = new Move(false, colour, dis, true, colour, dis.neighbours[a], false,!colour,fields[opponentfields[b].getIndex()],true,!colour,fields[ownpiles[1]]);
										output.add(input.integrate((helo)));
										}
										//if it is part of mill and detected the first time
										else{
											mills.add(opponentfields[b]);
										}
									}
								}
							}
							//if you do not build a mill
							else{
								//simply add this move
								output.add(input.integrate(new Move(false, colour, dis, true, colour, dis.neighbours[a])));
							}
						}
						
					}
				}
			}
		}
		//if not all men have been set
		else{
			//if there is a free field
			for (int i=0;i<24;i++){
				if(fields[i].getMan()==null){
					//if you build a mill
					if(fields[i].partOfMill(colour)){
						//check the opponent but do not save mills, this would last too long
						for (int b=0;b<9;b++){
							if(opponentfields[b].getIndex()<24){
								if((!fields[opponentfields[b].getIndex()].partOfMill())){
							
								Move helo = new Move(false, colour, fields[ownpiles[0]], true, colour, fields[i], false,!colour,fields[opponentfields[b].getIndex()],true,!colour,fields[ownpiles[1]]);
								output.add(input.integrate(helo));
								}
								
							}
						}
					}
					//if you did not create a mill
					else{
						output.add(input.integrate(new Move(false,colour,fields[ownpiles[0]],true,colour,fields[i])));
					}
				}
			}
		}
		return output;
	}
	
	
	/**
	 * to test the logic without depending on the complex structure of the MultiPrioritySet,
	 * this method equals the possibleMoves, but returns those moves in an ArrayList.
	 * on top, it was used by the strict version of upddateByNAO.
	 * @param colour the moves for the player of this colour shall be calculated
	 * @return all possible Moves in a ArrayList
	 * @throws GameLostException if the game was lost, an exception is thrown
	 */
	public ArrayList<Move> possibleMoves(boolean colour) throws GameLostException{
		int[] ownpiles = {24,25};
		int[] opponentpiles = {26,27};
		Field[] ownfields = whitefields;
		Field[] opponentfields = blackfields;
		boolean stateOfGame;
		//init colour
		if (colour){
			if(gamestate[0]){
				stateOfGame=true;
			}
			else
				stateOfGame=false;
			}
		else{
				int[] change = ownpiles;
				ownpiles=opponentpiles;
				opponentpiles=change;
				ownfields=blackfields;
				opponentfields=whitefields;
				if(gamestate[1]){
					stateOfGame=true;
				}
				else
					stateOfGame=false;
				
		}
		//check if game is not lost by now
		if(((Pile)fields[opponentpiles[1]]).getManCount()==6&&((Pile)fields[ownpiles[1]]).getManCount()==6){
			return new ArrayList<Move>();
		}
		
		if(((Pile)fields[opponentpiles[1]]).getManCount()>6)
			throw new GameLostException("less then 3 men", colour);
		
		ArrayList<Move> freeagain = new ArrayList<Move>();

		//if you have set your men
		if (stateOfGame){
			ArrayList<Field> mills = new ArrayList<Field>(); //contains opponents fields being part of a mill
			for (int i=0;i<9;i++){
				if(ownfields[i].getIndex()<24){
					int indey = ownfields[i].getIndex();
					Field dis = fields[indey];
					//check for your possibilities to move
					for(int a=0;a<dis.neighbours.length;a++){
						if(dis.neighbours[a].isEmpty()){
							//if you would build a mill
							if (dis.neighbours[a].partOfMill(colour,indey)){
								//check the opponent's men
								for (int b=0;b<9;b++){
								//if the man is on board and not detected as part of mill
									if(opponentfields[b].getIndex()<24 && !mills.contains(opponentfields[b])){
										//if it is really not part of a mill
										if((!fields[opponentfields[b].getIndex()].partOfMill())){
											//new move
										Move helo = new Move(false, colour, dis, true, colour, dis.neighbours[a], false,!colour,fields[opponentfields[b].getIndex()],true,!colour,fields[ownpiles[1]]);
										freeagain.add(helo);
										}
										//if it is part of mill and detected the first time
										else{
											mills.add(opponentfields[b]);
										}
									}
								}
							}
							//if you donot build a mill
							else{
								//simply add this move
								freeagain.add(new Move(false, colour, dis, true, colour, dis.neighbours[a]));
							}
						}
						
					}
				}
			}
		}
		//if not all men have been set
		else{
			//if there is a free field
			for (int i=0;i<24;i++){
				if(fields[i].getMan()==null){
					//if you build a mill
					if(fields[i].partOfMill(colour)){
						//check the opponent but donot save mills, this would last too long
						for (int b=0;b<9;b++){
							if(opponentfields[b].getIndex()<24){
								if((!fields[opponentfields[b].getIndex()].partOfMill())){
							
								Move helo = new Move(false, colour, fields[ownpiles[0]], true, colour, fields[i], false,!colour,fields[opponentfields[b].getIndex()],true,!colour,fields[ownpiles[1]]);
								freeagain.add(helo);
								}
								
							}
						}
					}
					//if you did not create a mill
					else{
						freeagain.add(new Move(false,colour,fields[ownpiles[0]],true,colour,fields[i]));
					}
				}
			}
		}
		//if no moves are possible, you lost
		if (freeagain.isEmpty())
			throw new GameLostException("Player cannot move anymore",colour);
		
		return freeagain;
	}
	
	/**
	 * method to be invoked by the AI
	 * @param colour the colour of the player whose mills are searched
	 * @return how many mills the player of the given colour has built.
	 */
	public int numberOfMills(boolean colour){
		Field[] used;
		int count=0;
		if(colour)
			used=whitefields;
		else
			used=blackfields;
		
		for (int i=0;i<9;i++){
			if(used[i].partOfMill())
				count++;
		}
		return count/3;
	}
	/**
	 * method to be invoked by the AI
	 * this version calculates via the tracking arrays if count==false and really counts the men if count==true;
	 * the first version is faster and normally correct. The second version is mainly used by update_NAO
	 * @param colour the colour which shall be evaluated
	 * @param count tells whether the method shall guess the number from data (faster), or really count (100% accurate)
	 * @return all men of a given colour on the board
	 */
	public int allMenOnBoard(boolean colour, boolean count){
		/*if (colour)
			return (9-((Pile)fields[24]).getManCount()-((Pile)fields[27]).getManCount());
		else
			return (9-((Pile)fields[26]).getManCount()-((Pile)fields[25]).getManCount());*/
		if(count){
			int counter=0;
			for (int i=0;i<24;i++){
				if (fields[i].manEqualsColour(colour))
					counter++;
			}
			return counter;
		}
		else{
		int counter=0;
		if(colour){
			for(int i=0;i<whitefields.length;i++){
				if(whitefields[i].getIndex()<24)
					counter++;
			}
		}
		else{
			for(int i=0;i<blackfields.length;i++){
				if(blackfields[i].getIndex()<24)
					counter++;
			}
		}
		return counter;
		}
	}
	
	/**
	 * This method returns a new playboard containing fields with all values set like on the current one.
	 * @return a deep copy of the Playboard
	 */
	public Playboard deepCopy(){
		Playboard rtn = new Playboard();
		Boolean[] whatshallbe = this.updateGUI();//position in same Boolean Objects
		int a=0;
		while(a<whatshallbe.length){
			try{
				rtn.fields[a].setMan(new Boolean(whatshallbe[a].booleanValue()));
			}
			catch (NullPointerException e){
				rtn.fields[a].setMan(null);
			}
			a++;
		}
		//refill the piles with new objects
		while(a<28){
			((Pile)rtn.fields[a]).clear();
			for(int b=0;b<fields[a].getManCount();b++){
				((Pile)rtn.fields[a]).setMan(((Pile)fields[a]).getMan(b), b);
			}
			a++;
		}
		//fill the XFields
		int blackpointer=0;
		int whitepointer=0;
		int memory=0;
		//	track black unset
		while(blackpointer<rtn.fields[26].getManCount()){
			rtn.blackfields[blackpointer]=rtn.fields[26];
			blackpointer++;
		}//track black lost
		memory=blackpointer;
		while(blackpointer<(memory+rtn.fields[25].getManCount())){
			rtn.blackfields[blackpointer]=rtn.fields[25];
			blackpointer++;
		}//track white unset
		while(whitepointer<rtn.fields[24].getManCount()){
			rtn.whitefields[whitepointer]=rtn.fields[24];
			whitepointer++;
		}//track white lost
		memory=whitepointer;
		while (whitepointer<(memory+rtn.fields[27].getManCount())){
			rtn.whitefields[whitepointer]=rtn.fields[27];
			whitepointer++;
		}
		//search the rest.
		for(int i=0;i<24;i++){
			if(rtn.fields[i].manEqualsColour(false)){
				if(blackpointer==9){
					System.err.println("StackOverflow 888");
				}
				rtn.blackfields[blackpointer]=rtn.fields[i];
				blackpointer++;
			}
			else if(rtn.fields[i].manEqualsColour(true)){
				if(whitepointer==9){
					System.err.println("StackOverflow 895");
				}
				rtn.whitefields[whitepointer]=rtn.fields[i];
				whitepointer++;
			}
		}
		//gamestate
		rtn.gamestate[0]=gamestate[0];
		rtn.gamestate[1]=gamestate[1];

		return rtn;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer("All current Men and Positions on this board:\n");
		for (int i=0;i<fields.length-4;i++){
			sb.append("Field "+fields[i].getIndex()+" : ");
			if (fields[i].getMan()==null)
				sb.append("no man; ");
			else if(fields[i].getMan())
				sb.append("white man; ");
			else
				sb.append("coloured man; "); //political correctness is important!
			sb.append("\n");
		}
		for (int i=fields.length-4;i<fields.length;i++){
			sb.append(fields[i].toString());
			sb.append("\n");
		}
		return sb.toString();
		
	}
	
	
	/**
	 * this method will set the Playboard in a certain state allowing testing without using mockups.
	 * @param c the defined case. mostly "empty" is used to initialise an empty board.
	 */
	public void testSetter(String c){
		switch (c){
		case "empty":{
			for (int i=0; i<9;i++){
				((Pile) fields[24]).setMan(true, i);
				((Pile) fields[26]).setMan(false, i);
				whitefields[i]=fields[24];
				blackfields[i]=fields[26];
			}
			break;
		}
		//some men placed, but some fields are empty
		case "1":{
			for (int i=0; i<4;i++){
				((Pile) fields[24]).setMan(true, i);
				((Pile) fields[26]).setMan(false, i);
				}
			int[] helo = {1,3,8,10,16};
			menSetter(helo,true);
			int[] hel = {14,15,20,22,23};
			menSetter(hel,false);
			break;
		}
		//all men on board, no mills possible
		case "2":{
			gamestate[0]=true;
			gamestate[1]=true;
			int[] helo = {1,2,3,5,8,9,10,16,21};
			menSetter(helo,true);
			int[] hel = {0,4,11,13,14,15,20,22,23};
			menSetter(hel,false);
			break;
		}
		//all men on board, mills possible for black, existing mill of white
		case "3":{
			gamestate[0]=true;
			gamestate[1]=true;
			int[] helo = {0,1,2,3,5,8,9,10,16,/*21*/};
			menSetter(helo,true);
			int[] hel = {4,11,13,14,15,18,20,22,25};
			menSetter(hel,false);
			setXFields(helo,whitefields,fields[24]);
			setXFields(hel,blackfields,fields[26]);
			break;
		}
		//white cannot move anymore
		case "4":{
			gamestate[0]=true;
			gamestate[1]=true;
			int[] helo = {0,1,2,3,4,5,6,7,8};
			menSetter(helo,true);
			setXFields(helo,whitefields,fields[24]);
			int[] hel = {9,10,11,12,13,14,15};
			menSetter(hel,false);
			setXFields(hel,blackfields,fields[26]);
			break;
		}
		case "5":{
			gamestate[0]=true;
			gamestate[1]=true;
			for (int i=0; i<7;i++){
				((Pile) fields[25]).setMan(true, i);
			}
			int[] helo = {0,1,2,3,4,5,6,7,8};
			menSetter(helo,true);
			int[] hel = {9,10};
			menSetter(hel,false);
			break;
		}
		case"6":{//how the AI likes to set the men
			int [] w ={16,14,12,10,9,6,4,2,0};
			int [] b={8,15,13,11,21,7,5,3,1};
			menSetter(w,true);
			this.menSetter(b, false);
			this.setXFields(w, whitefields, null);
			this.setXFields(b, blackfields, null);
			gamestate[0]=true;
			gamestate[1]=true;
		}
		}
	}
	/**	
	 * to be invoked by testSetter
	 * this methods sets either the whitefields or the blackfields with the following data
	 * @param pos the index of the fields that shall be tracked
	 * @param xfields whitefields or blackfields
	 * @param pile tells where the rest is to be set.
	 */
	private void setXFields(int[]pos, Field[] xfields, Field pile){
		for (int i=0;i<pos.length;i++){
			xfields[i]=fields[pos[i]];
		}
		for (int i=pos.length;i<xfields.length;i++){
			xfields[i]=pile;
			System.out.println("icke wieder");
		}
	}
	/**
	 * to be invoked by testSetter
	 * this methods sets men on the board
	 * @param pos the position where they shall be afterwards
	 * @param colour the colour of the man
	 */
	private void menSetter(int[] pos, boolean colour){
		for (int i=0;i<pos.length;i++){
			fields[pos[i]].setMan(colour);
		}
	}
	/**
	 * test method, prints the tracked fields completely
	 * @param colour the colour you want to see.
	 */
	public void fieldPrinter(boolean colour){
		Field[] x = blackfields;
		if(colour)
			x=whitefields;
		
		for (int c =0;c<9;c++){
			System.out.println(x[c]);
		}
	}
}
