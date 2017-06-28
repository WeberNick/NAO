package domain.represent;

import static org.junit.Assert.*;
import foundation.data.*;
import foundation.exception.GameLostException;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Mike Siefert, Jonas Thietke
 *
 */
public class PlayboardTest {
Playboard board;
ArrayList<Move> moves;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		board = new Playboard();
		moves = new PlayboardDemo().createMoves();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testUpdateByAI(){

		board.testSetter("empty");
		board.updateByAI(moves.get(0), false);
		assertEquals(board.getField(24).getManCount(),8);
		
		for (int i=0;i<24;i++){
			if (i==1){
				assertEquals(board.getField(1).getMan(),true);
			}
			else{
				assertEquals(board.getField(i).isEmpty(),true);
			}
		}
		board.updateByAI(moves.get(0), true);
		for (int i=0;i<28;i++){
			if(i==24|i==26){
				assertEquals(board.getField(i).getManCount(),9);
				continue;
			}
			assertEquals(board.getField(i).isEmpty(),true);
			
		}
		for (int i=0;i<moves.size();i++){
			board.updateByAI(moves.get(i), false);
		}
		System.out.println(board);
		int[] noone = {0,1,4,5,6,7,9,11,17,19,21,22};
		int[] white ={2,12,13,14,23};
		int[] black = {3,10,18};
		assertEquals(board.getField(24).getManCount(),3);
		assertEquals(board.getField(25).getManCount(),2);
		assertEquals(board.getField(26).getManCount(),4);
		assertEquals(board.getField(27).getManCount(),1);
		for (int i=0;i<noone.length;i++){
			assertTrue(board.getField(noone[i]).isEmpty());
		}
		for (int i=0;i<white.length;i++){
			assertTrue(board.getField(white[i]).getMan().booleanValue());
		}
		for (int i=0;i<black.length;i++){
			assertFalse(board.getField(black[i]).getMan().booleanValue());
		}
		
	}
	@Test	
	public void testUpdateGUI(){
		board.testSetter("empty");
		board.updateByAI(moves.get(0), false);
		Boolean[] gui = board.updateGUI();
		for (int i=0;i<gui.length;i++){
			if(i==1){
				assertTrue(gui[i].booleanValue());
				continue;
			}
			assertTrue(gui[i]==null);
			
		}
	}
	@Test
	public void testAllMenOnBoard(){
		board.testSetter("empty");
		for(int i=0;i<moves.size();i++){
			board.updateByAI(moves.get(i), false);
		}
		assertEquals(board.allMenOnBoard(true, false),5);
		assertEquals(board.allMenOnBoard(false, false),3);
	}
	@Test
	public void testNumberOfMills(){
		board=new Playboard();
		for(int i=0;i<moves.size();i++){
			board.updateByAI(moves.get(i), false);
		}
		assertEquals(board.numberOfMills(true),1);
		assertEquals(board.numberOfMills(false),1);
	}
	
	@Test
	public void testPossibleMoves(){
		board.testSetter("empty");
		ArrayList<Move> muchstuff = new ArrayList<Move>();
		try{
			muchstuff = board.possibleMoves(true);
		}
		catch (GameLostException e){
			e.printStackTrace();
		}
		Move m1 = new Move(false,true,board.getField(24),true,true,board.getField(0));
		Move m2 = new Move(false,true,board.getField(24),true,true,board.getField(10));
		Move m3 = new Move(false,true,board.getField(24),true,true,board.getField(20));
		assertTrue(muchstuff.get(0).equals(m1));
		assertTrue(muchstuff.get(10).equals(m2));
		assertTrue(muchstuff.get(20).equals(m3));
	}
}
