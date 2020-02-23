package de.tuberlin.sese.swtpp.gameserver.model.deathstacks;

import java.util.LinkedList;

public class BoardSquare implements java.io.Serializable {
	
	char x; // from a to f
	int y; // from 1 to 6
	
	// Who stays on this field?:
	LinkedList<Character> pieces; 
	
	public BoardSquare(char x, int y) {
		this.x = x;
		this.y = y;
		this.pieces = new LinkedList<Character>();
	}
	
	// Put pieces on top of this BoardSquare (LinkedList) from another BoardSquare:
	public void putOnTopOfThisBS(BoardSquare start, int moves) {
		// Move {moves} pieces from start to this boardsquare (END):
		for (int i = moves; i>0; i--) {
			char piece = start.pieces.get(start.pieces.size()-i);
			this.pieces.add(piece); // put piece on top
			start.pieces.remove(start.pieces.size()-i);
		}
	}
	// Put pieces on top of this BoardSquare (LinkedList) based on FEN-String (to load any state)
	public void putOnTopFromFEN(String s) {
		// for loadFEN e.g. bsp: bbr in b|b|r -> r under bb 
		char[] s_chars = s.toCharArray();
		for (int i = s.length()-1; i >= 0; i--) {
			this.pieces.add(s_chars[i]);
		}
	}
	// Get pieces, contained in this BoardSquare as string to represent it in FEN
	public String getPiecesAsString() {
		String result = "";
		
		for (int i = this.pieces.size()-1; i>=0; i--) {
			char c = this.pieces.get(i);
			result = result + c;
		}
		return result;
	}
	
	// To compare fields:
	public boolean Equals(BoardSquare anotherBoardSquare) {
		if (this.x == anotherBoardSquare.x && this.y == anotherBoardSquare.y) {
			return true;
		}
		return false;
	}
	
}
