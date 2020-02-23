package de.tuberlin.sese.swtpp.gameserver.model.deathstacks;

import java.util.LinkedList;

import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
	-> Checker for the supplied move's possibility
 	* 	to perform in the current game state/status:
 	* --> The format of supplied move-String is correct (and it is not out-of-border, like a8)
 	* --> There is no other move that the Player is forced to perform
 	* e.g.: Too-Tall-Rule (see the rules)
 	* --> It is a valid move
 	* ---> Player tries to access his own stack
 	* ---> The move can be performed from this field with given moves to another field
**/
public class Rules implements java.io.Serializable {

	StacksBoard testBoard;
	public char playerColour;
	
	/*
	 * We use Rules to check current state properly:
	 */
	public Rules(StacksBoard board) {
		this.testBoard = board;
	}
	
	
	/**
	 * Checks if the format of supplied move-String is correct
	 * @param move in string representation, which user wants to perform
	 * @return True if format is correct
	 */
	public boolean moveStringFormatIsCorrect(String moveString) {
		if (moveString.equals("")) {
			return false;
		}
		String[] parts = moveString.split("-");
		// contains 3 parts: start, moves, end
		if (parts.length != 3) {
			return false;
		}
		// none of parts is empty
		for (String part: parts) {
			if (part.equals("")) {
				return false;
			}
		}
		// start and end fields exist on board:
		boolean startExists = ( parts[0].length() == 2 && testBoard.getStart(moveString) != null );
		boolean endExists = ( parts[2].length() == 2 && testBoard.getEnd(moveString) != null );
		
		if (startExists == false || endExists == false) {
			return false;
		}
		// count of moves must be greater than zero:
		if (Integer.parseInt(parts[1]) <= 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether there no other move exists, which the player is forced to perform
	 * @param move in string representation, which user wants to perform
	 * @return TRUE if too tall exists and user did not want to perform it OR too tall exists
	 * and user is up to perform move from there, but with not enough pieces (e.g.: there is a stack with 7 pieces,
	 * but user only wants to move 2 from there -> 5 remain => not allowed)
	 */
	public boolean forcedMoveExists(String moveString) {
		// IF ANY BOARDSQUARE HAS more than 4 chars in its linked list
		// and top char is equal to this.playerColour
		// player is forced to move from there
		for (int i = 1; i<=6; i++) {
			for (int j = 1; j<=6; j++) {
				LinkedList<Character> piecesOnPos = this.testBoard.gameMatrix[i][j].pieces;
				if ((piecesOnPos.size()>4) == false || piecesOnPos.get(piecesOnPos.size()-1) != this.playerColour) {
					continue;
				}
				// if player tries to go from another field than he should:
				if (this.testBoard.getStart(moveString) != this.testBoard.gameMatrix[i][j]) {
					return true;
				}
				// if player moves from forced position, but with no enough pieces:
				if (piecesOnPos.size() - this.testBoard.getMovesCount(moveString) > 4) {
					return true;	
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks whether pieces player tries to move belong to him (if the piece above is of his color)
	 * @param move in string representation, which user wants to perform
	 * @return
	 */
	public boolean piecesBelongToPlayer(String moveString) {
		if (this.testBoard.getStart(moveString).pieces.getLast() == this.playerColour) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/** 
	 * Checks whether the distance in this move is possible and can be done by given pieces
	 * @param move in string representation, which user wants to perform
	 * @return
	 */
	public boolean moveIsValid(String moveString) {
		// move-string has following format: x*-n-y*
		BoardSquare start = this.testBoard.getStart(moveString);
		BoardSquare end = this.testBoard.getEnd(moveString);
		// cannot go to the same position:
		if (start.Equals(end)) {
			return false;
		}
 		int movesCount = this.testBoard.getMovesCount(moveString);
		// n cannot be bigger than count of figures i have on square:
		if (start.pieces.size() < movesCount) {
			return false;
		}
		// where can it get? 
		// forward, backwards, left, right, diagonal
		if (this.distanceIsValid(start, end, movesCount) == false) { 
			return false;
		}
		return true;
	}
	// TODO: get int from char properly:
	public boolean distanceIsValid(BoardSquare start, BoardSquare end, int movesCount) {
		// forwards/backwards
		int xs = 0;
		int xz = 0;
		// SIGN MAP to INT: TODO
		for (int i = 1; i<=6; i++) {
			if (this.testBoard.signs[i-1] == start.x) {
				xs = i;
			}
			if (this.testBoard.signs[i-1] == end.x) {
				xz = i;
			}
		}
		
		int ys = start.y;
		int yz = end.y;
		// Can i get x_target/y_target pair by combinations of x and y with movesCount:
		boolean xz_isOk = this.goLeftDown(xs, movesCount) == xz || this.goRightUp(xs, movesCount) == xz || xz == xs;
		boolean yz_isOk = this.goLeftDown(ys, movesCount) == yz || this.goRightUp(ys, movesCount) == yz || yz == ys;
		
		if (xz_isOk && yz_isOk) {
			return true;
		}
		
		return false;
	}

	public int goLeftDown(int value, int movesCount) {
		int mc = movesCount%10;
		if (value > mc) {
			// do not mirror
			return value-mc;
		}
		else {
			return this.goRightUp(1, mc-(value-1));
		}
	}
	
	public int goRightUp(int value, int movesCount) {
		int mc = movesCount%10;
		if (6-value > mc) {
			// do not mirror
			return value+mc;
		}
		else {
			return this.goLeftDown(6, mc-(6-value));
		}
	}
	
	/**
	 * For metrics: put on pre-conditions into one function:
	 */
	public boolean rules_preconditionsSatisfied(String moveString) {
		// Format of supplied move-String is correct:
		if (this.moveStringFormatIsCorrect(moveString) == false) {
			return false;
		}
		// Is player not forced to perform any other move:
		if (this.forcedMoveExists(moveString)) {
			return false;
		}
		// It is a valid move:
		if (this.piecesBelongToPlayer(moveString) == false || this.moveIsValid(moveString) == false) {
			return false;
		}
		return true;
	}
	
	public void updatePlayerColor() {
		this.playerColour = this.playerColour == 'r' ? 'b' : 'r';
	}
	
	
	/**
	 * Checks whether the game is over:
	 * Win of one of players
	 * @param args
	 */
	public boolean gameOver() {
		// playerColour has done the move -> if all the towers belong to him, he won:
		for (int i = 1; i<7; i++) {
			for (int j = 1; j<7; j++) {
				if (this.testBoard.gameMatrix[i][j].pieces.size() > 0 && this.testBoard.gameMatrix[i][j].pieces.getLast() != this.playerColour) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks whether draw or not (Repeating-state rule)
	 * @param args
	 */
	public boolean stateRepeatedTo3Time() {
		// iterate through game history -> if it contains current FEN-State three times, game is over:
		int counter = 0;
		for (int i = 0; i<this.testBoard.stateHistory.size(); i++) {
			if (this.testBoard.stateHistory.get(i).equals(this.testBoard.toFENFromMatrix())) {
				counter++;
			}
			if (counter == 3) {
				return true;
			}
		}
		return false;
	}
	
}
