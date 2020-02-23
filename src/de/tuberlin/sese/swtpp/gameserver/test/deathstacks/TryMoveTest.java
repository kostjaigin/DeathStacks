package de.tuberlin.sese.swtpp.gameserver.test.deathstacks;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.deathstacks.DeathStacksGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player redPlayer = null;
	Player bluePlayer = null;
	DeathStacksGame game = null;
	GameController controller;
	
	String gameType ="deathstacks";
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", gameType);
		
		game = (DeathStacksGame) controller.getGame(gameID);
		redPlayer = game.getPlayer(user1);

	}
	
	public void startGame(String initialBoard, boolean redNext) {
		controller.joinGame(user2, gameType);		
		bluePlayer = game.getPlayer(user2);
		
		game.setBoard(initialBoard);
		game.setNextPlayer(redNext? redPlayer:bluePlayer);
	}
	
	public void assertMove(String move, boolean red, boolean expectedResult) {
		if (red)
			assertEquals(expectedResult, game.tryMove(move, redPlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, bluePlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean redNext, boolean finished, boolean draw, boolean redWon) {
		String board = game.getBoard();
				
		assertEquals(expectedBoard,board);
		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(redNext, game.isRedNext());
		} else {
			assertEquals(draw, game.isDraw());
			if (!draw) {
				assertEquals(redWon, redPlayer.isWinner());
				assertEquals(!redWon, bluePlayer.isWinner());
			}
		}
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("d6-1-d4",true,false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}

	//TODO: implement test cases of same kind as example here
	@Test
	public void tryMoveWhenNotYourTurn() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("e1-1-d1",false,false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void tryToMoveEnemiesPieces() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a1-1-b1",true,false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void moveFiguresDown() {
		startGame("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",true);
		assertMove("a6-5-a1",true,true);
		assertGameState(",,,,,/,,,,,/,,,,,/,,,,,/,,,,,/rrrrrbbbbb,,,,,",false,true,false,true);
	}
	@Test
	public void tryBlue() {
		startGame("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",false);
		assertMove("a1--a5",false,false);
		assertGameState("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",false, false, false, false);
	}
	@Test
	public void tryToMoveWhenGameIsFinished() {
		startGame("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",true);
		assertMove("a6-5-a1", true, true);
		assertGameState(",,,,,/,,,,,/,,,,,/,,,,,/,,,,,/rrrrrbbbbb,,,,,",false, true, false, true);
		assertMove("a1-1-a2", false, false);
		assertGameState(",,,,,/,,,,,/,,,,,/,,,,,/,,,,,/rrrrrbbbbb,,,,,",false, true, false, true);
	}
	/**
	 -> Incorrect string tests
	 *--> empty string
	 *--> partly empty
	 *--> start and end field existing
	 *--> amount of moves can't be 0
	 */
	@Test
	public void inputEmpty() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	// TODO Finish it!
	public void inputPartlyEmpty () {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a 1-1-a2",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void startFalse1() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("n23-1-a2",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test 
	public void startFalse2() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove(".",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test 
	// TODO Finish it!
	public void startFalse3() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("k8-2-j9",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void endFalse1() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a1-1-g267",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	// TODO Finish it!
	public void endFalse2() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a1-1-g267",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void notZeroMove() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a1-0-a2",true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void notAcceptableFieldForMove() {
		startGame(",rrrrr,r,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a4-1-b3", true, false);
		assertGameState(",rrrrr,r,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	// TODO Finish it!
	public void notAcceptableAmountOfFigures() {
		startGame(",rrrrrrrr,,,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a5-2-a3", true, false);
		assertGameState(",rrrrrrrr,,,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void startEqualsEnd() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6-1-a6", true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void piecesOnStartLessThenMoves() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6-5-a2", true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void countMoves() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6--a2", true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void gameOverCase() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6--a2", true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void BlueTurn() { 
		startGame("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",false);
		assertMove("a1--a5",false,false);
		assertGameState("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",false, false, false, false);
	}
	@Test
	 public void tryToMirrorMove() {
	  startGame(",,,,,/rrrrr,,,,,/,,,,,/,,,,,/,,,,,/bbb,,,,,", true);
	  assertMove("a5-3-d4", true, true);
	  assertGameState(",,,,,/rr,,,,,/,,,rrr,,/,,,,,/,,,,,/bbb,,,,,", false, false, false, false);
	 }	
	@Test
	public void stateRepeatedTo3Time() {
		startGame("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",true);
		// Red diagonal
		assertMove("a6-5-f1", true, true);
		assertGameState(",,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,rrrrr",false, false, false, false);
		// Blue diagonal
		assertMove("a1-5-f6", false, true);
		assertGameState(",,,,,bbbbb/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,rrrrr",true, false, false, false);
		// Red diagonal
		assertMove("f1-5-a6", true, true);
		assertGameState("rrrrr,,,,,bbbbb/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,",false, false, false, false);
		// Blue diagonal
		assertMove("f6-5-a1", false, true);
		assertGameState("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,",true, false, false, false);
		// Red diagonal
		assertMove("a6-5-f1", true, true);
		assertGameState(",,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,rrrrr",false, false, false, false);
		// Blue diagonal
		assertMove("a1-5-f6", false, true);
		assertGameState(",,,,,bbbbb/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,rrrrr",true, false, false, false);
		// Red diagonal
		assertMove("f1-5-a6", true, true);
		assertGameState("rrrrr,,,,,bbbbb/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,",false, false, false, false);
		assertMove("f6-5-a1", false, true);
		assertGameState("rrrrr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,", true, true, true, false);
	}
	@Test
	public void Equals() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6-2-a6", true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	@Test
	public void fourFiguresForMove() {
		startGame("rrrrrrr,rr,rr,r,,/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true);
		assertMove("a6-2-a4", true,false);
		assertGameState("rrrrrrr,rr,rr,r,,/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);	
	}
	@Test
	public void mirrorCase() {
		startGame(",rr,,rrr,,/,,,,,/,,,,,/,,,,,/,,,,rrrrrrr,/bb,bb,bb,bb,bb,bb", true);
		assertMove("e2-4-c6", true, true);
		assertGameState(",rr,rrrr,rrr,,/,,,,,/,,,,,/,,,,,/,,,,rrr,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	@Test
	public void mirrorCase2() {
		startGame("rr,,,,,/,,,,,/,,,,,/,rrrrrrrrrr,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true);
		assertMove("b3-9-a2", true, true);
		assertGameState("rr,,,,,/,,,,,/,,,,,/,r,,,,/rrrrrrrrr,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	@Test
	public void mirrorCase3() {
		startGame("rr,,,,,/,,,,,/,,,,,/,rrrrrrrrrr,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true);
		assertMove("b3-10-a2", true, false);
		assertGameState("rr,,,,,/,,,,,/,,,,,/,rrrrrrrrrr,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true, false, false, false);
	}
	@Test
	public void tryHorizontalMove() {
		startGame(",rr,,,,/,,,,,/,,,,,/,rrr,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true);
		assertMove("b6-1-a6", true, true);
		assertGameState("r,r,,,,/,,,,,/,,,,,/,rrr,,,,/,,,,,/bb,bb,bb,bb,bb,bb", false, false, false, false);
	}
}
