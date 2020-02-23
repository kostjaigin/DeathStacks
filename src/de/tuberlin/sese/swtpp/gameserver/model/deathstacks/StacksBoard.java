package de.tuberlin.sese.swtpp.gameserver.model.deathstacks;

import java.util.HashMap;
import java.util.LinkedList;

import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

public class StacksBoard implements java.io.Serializable {
	
	public final int[] digits = {1, 2, 3, 4, 5, 6};
	public final char[] signs = {'a', 'b', 'c', 'd', 'e', 'f'};
	
	HashMap<Integer, Character> signsMap;
	/**
	 * Matrix of BoardSquares, where each SQUARE contains Stack of pieces (r or b) [see BoardSquare] and
	 * each SQUARE also has it`s x-Position (as char from a to f) and y-Position (as integer from 1 to 6)
	 * This matrix is used for cleaner and easier implementation of moves and game rules.
	 */
	BoardSquare[][] gameMatrix;
	LinkedList<String> stateHistory;
	
	public StacksBoard() {
		this.signsMap = new HashMap<Integer, Character>();
		signsMap.put(1, 'a');
		signsMap.put(2, 'b');
		signsMap.put(3, 'c');
		signsMap.put(4, 'd');
		signsMap.put(5, 'e');
		signsMap.put(6, 'f');
		// Create all the game fields:
		this.gameMatrix = new BoardSquare[7][7]; // 7 because field [0][0] not used
		
		this.stateHistory = new LinkedList<String>();
		
		// Fill it:
		this.initMatrix();
	}
	
	// Board Matrix update with supplied move: e.g. d2-3-e3
	public void makeMove(String moveString) {
		  int count = this.getMovesCount(moveString);
		  
		  BoardSquare start = this.getStart(moveString);
		  
		  BoardSquare end =  this.getEnd(moveString);
		  
		  end.putOnTopOfThisBS(start, count); //mglsweise: end = this.gameMatrix...
		  
		  // add new state to history:
		  this.stateHistory.add(this.toFENFromMatrix());
	}
	
	// update history of the game with this move:
	public LinkedList<Move> updateHistory(LinkedList<Move> prevHistory, String moveString, Player player) {
		LinkedList<Move> result = prevHistory;
		Move doneMove = new Move(moveString, this.toFENFromMatrix(), player);
		result.add(doneMove);
		return result;
	}
	
	// Get start field from supplied moveString
	public BoardSquare getStart(String moveString) {
		String[] parts = moveString.split("-");
		BoardSquare start = this.getSquareInMatrix(parts[0]);
		return start;
	}
	// Get target field from supplied moveString
	public BoardSquare getEnd(String moveString) {
		String[] parts = moveString.split("-");
		BoardSquare end = this.getSquareInMatrix(parts[2]);
		return end;
	}
	// Get count of moves, we are supposed to do in supplied move string
	public int getMovesCount(String moveString) {
		String[] parts = moveString.split("-");
		// TODO: is parts[1] integer? 
		return Integer.parseInt(parts[1]);
	}
	
	// From FEN String
	public void loadFEN(String fen) {
		// Make board empty:
		this.initMatrix();
		// Create partitions of whole FEN (parts between '/'):
		String[] rows = fen.split("/");
		// Go through these rows-partitions and create separated (,) figures on board:
		for (int i = 0; i<6; i++) {
			if (rows[i].equals(",,,,,") == true) { 
				continue;
			}
			String[] piecesOnRow = rows[i].split(",");
			for (int j = 0; j<6; j++) {
				if (j < piecesOnRow.length && piecesOnRow[j].equals("") == false) {
					this.gameMatrix[j+1][(6-i)].putOnTopFromFEN(piecesOnRow[j]);
				}
			}
		}
		
		// add loaded state to history:
		this.stateHistory.add(fen);
	}
	
	// To FEN String
	public String toFENFromMatrix() {
		// Our input matrix is our game matrix 
		String fen = "";
		// Iteration through the rows
		for (int j = 6; j > 0; j-- ) {
		// Iteration through the columns
			for (int i = 1; i < 6; i++) {
			    // Getting the first element and place it to the string with comma
			    fen = fen + gameMatrix[i][j].getPiecesAsString() + ",";
			}
			// position 61 without slash
			if (j!=1){
				fen = fen + gameMatrix[6][j].getPiecesAsString()+ "/";
			}
			else {
				fen = fen + gameMatrix[6][j].getPiecesAsString();
			}
		}
		
		return fen;
	}
	
	/**
	 * Can be used to initialize matrix
	 * Can be used to set board free of pieces
	 */
	public void initMatrix() {
		// Create board fields again:
		for (int i = 1; i<=6; i++) {
			for (int j = 1; j<=6; j++) {
				BoardSquare square = new BoardSquare(signsMap.get(i), j);
				gameMatrix[i][j] = square;
			}
		}
	}
	
	public BoardSquare getSquareInMatrix(String pos) {
		char[] posArray = pos.toCharArray();

		for (int i = 1; i<=6; i++) {
			for (int j = 1; j<=6; j++) {
				if (this.gameMatrix[i][j].x == posArray[0] && 
						this.gameMatrix[i][j].y == Character.getNumericValue(posArray[1])) {
					return this.gameMatrix[i][j];
				}
			}
		}
		return null; 
	}
	
}
