package de.tuberlin.sese.swtpp.gameserver.model.deathstacks;

import java.util.LinkedList;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;


public class DeathStacksGame extends Game {
	

	/** 
	 * 
	 */
	private static final long serialVersionUID = -3053592017994489843L;
	/************************
	 * member
	 ***********************/
	
	// just for better comprehensibility of the code: assign blue and red player
	private Player bluePlayer;
	private Player redPlayer;

	// internal representation of the game state 
	StacksBoard gameBoard;
	Rules gameRules;
	public LinkedList<Move> history;
	
	/************************
	 * constructors
	 ***********************/
	
	public DeathStacksGame() throws Exception{
		super();
		this.gameBoard = new StacksBoard();
		this.setBoard("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb");
		this.setNextPlayer(redPlayer);
		this.gameRules = new Rules(this.gameBoard);
		this.history = new LinkedList<Move>();
	}
	
	public String getType() {
		return "deathstacks";
	}
	
	/*******************************************
	 * Game class functions already implemented
	 ******************************************/
	
	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);
			
			if (players.size() == 2) {
				started = true;
				this.redPlayer = players.get(0);
				this.bluePlayer = players.get(1);
				nextPlayer = this.redPlayer;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public String getStatus() {
		if (error) return "Error";
		if (!started) return "Wait";
		if (!finished) return "Started";
		if (surrendered) return "Surrendered";
		if (draw) return "Draw";
		
		return "Finished";
	}
	
	@Override
	public String gameInfo() {
		String gameInfo = "";
		
		if(started) {
			if(blueGaveUp()) gameInfo = "blue gave up";
			else if(redGaveUp()) gameInfo = "red gave up";
			else if(didRedDraw() && !didBlueDraw()) gameInfo = "red called draw";
			else if(!didRedDraw() && didBlueDraw()) gameInfo = "blue called draw";
			else if(draw) gameInfo = "draw game";
			else if(finished)  gameInfo = bluePlayer.isWinner()? "blue won" : "red won";
		}
			
		return gameInfo;
	}	
	
	@Override
	public String nextPlayerString() {
		return isRedNext()? "r" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}
	
	@Override
	public boolean callDraw(Player player) {
		
		// save to status: player wants to call draw 
		if (this.started && ! this.finished) {
			player.requestDraw();
		} else {
			return false; 
		}
	
		// if both agreed on draw:
		// game is over
		if(players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			redPlayer.finishGame();
			bluePlayer.finishGame();
		}	
		return true;
	}
	
	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.redPlayer == player) { 
				redPlayer.surrender();
				bluePlayer.setWinner();
			}
			if (this.bluePlayer == player) {
				bluePlayer.surrender();
				redPlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			redPlayer.finishGame();
			bluePlayer.finishGame();
			
			return true;
		}
		
		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/
	
	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isRedNext() {
		return nextPlayer == redPlayer;
	}
	
	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// public for tests
		if (started && !finished) {
			player.setWinner();
			finished = true;
			redPlayer.finishGame();
			bluePlayer.finishGame();
			
			return true;
		}
		return false;
	}

	public boolean didRedDraw() {
		return redPlayer.requestedDraw();
	}

	public boolean didBlueDraw() {
		return bluePlayer.requestedDraw();
	}

	public boolean redGaveUp() {
		return redPlayer.surrendered();
	}

	public boolean blueGaveUp() {
		return bluePlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/
	
	@Override
	public void setBoard(String state) {
		this.gameBoard.loadFEN(state);
	}
	
	@Override
	public String getBoard() {
		return this.gameBoard.toFENFromMatrix();
	}
	/**	
	 * -> This method checks if the supplied move is possible
	 * to perform in the current game state/status:
	 * --> The move is done by the correct player (It is his/her turn)
	 * --> The format of supplied move-String is correct
	 * --> There is no other move that the Player is forced to perform
	 * e.g.: Too-Tall-Rule (see the rules)
	 * --> It is a valid move (and it is not out-of-border, like a8)
	 * 
	 * -> If all the conditions are satisfied, this method also performs the supplied move:
	 * --> The board state will be updated
	 * --> The board status has to be set (check if game is finished: e.g. Repeating-State-Rule
	 * or win of the player) => it should be represented in attributes of Game Class (???)
	 * --> The next player has to be set (move is over => it is next players turn)
	 * --> History should be updated (???) (see superclass Game)
	 * 
	 * @param move String representation of move
	 * @param Player who tries the move
	 * @return true if move was performed
	 * TODO: McCabe Metrics 25 rows pro function
	 */
	@Override
	public boolean tryMove(String moveString, Player player) {
		// Set rules:
		this.gameRules.playerColour = this.isRedNext() ? 'r' : 'b';
		// game is not finished and the move is done by the correct player and this under game rules:
		if (this.isFinished() || this.getNextPlayer() != player || this.gameRules.rules_preconditionsSatisfied(moveString) == false) {
			return false;
		}
		// perform the supplied move on board and write new state to inner-history of board :
		this.history = this.gameBoard.updateHistory(this.history, moveString, player);
		this.gameBoard.makeMove(moveString);
		// Update history (see Game, see Move):
		this.setHistory(this.history);
		// End of move (check whether game is over):
		if (this.gameRules.gameOver()) {
			this.finish(player); 
		}
		if (this.gameRules.stateRepeatedTo3Time()) {
			this.draw = true;
			this.finished = true;
		}
		// At the end another player must be next:
		if (this.isRedNext()) {	
			this.setNextPlayer(bluePlayer);
		}
		else {
			this.setNextPlayer(redPlayer);
		}
		this.gameRules.updatePlayerColor();
		return true;
	}
		
}
