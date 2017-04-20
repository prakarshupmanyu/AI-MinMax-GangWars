import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class returnObjects{
	public char[][] state;
	public int val, row, column;
	public String move;
	
	public returnObjects(int n) {
		state = new char[n][n];
		val = 0;
		row = 0;
		column = 0;
		move = "";
	}
}

public class homework {
	
	private static int n, depth;
	private static int[][] cellValues;
	private static char[][] boardState;
	private static String algo, myPlay;
	
	private static void readInputFile(String fileName){
		try{
			BufferedReader r = new BufferedReader(new FileReader(fileName));
			n = Integer.parseInt(r.readLine());
			algo = r.readLine();
			myPlay = r.readLine();
			depth = Integer.parseInt(r.readLine());
			cellValues = new int[n][n];
			boardState = new char[n][n];
			for(int i=0;i<n;i++){
				String[] rowValues = r.readLine().split(" ");
				for(int j=0;j<n;j++){
					cellValues[i][j] = Integer.parseInt(rowValues[j]);
				}
			}
			for(int i=0;i<n;i++){
				boardState[i] = r.readLine().toCharArray();
			}
			r.close();
		}catch(IOException e){
			e.printStackTrace();
		}		
	}
	
	private static void printBoard(char[][] boardState){
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(boardState[i][j]);
			}
			System.out.println();
		}
	}
	
	private static char[][] copyBoard(char[][] board){
		char[][] newBoard = new char[n][n];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}
	
	private static boolean isTerminalState(char[][] state){
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(state[i][j] == '.'){
					return false;
				}
			}
		}
		return true;
	}
	
	private static void printInputTaken(){
		System.out.println("N = "+n);
		System.out.println("Algo = "+algo);
		System.out.println("My Play = "+myPlay);
		System.out.println("Depth = "+depth);
		System.out.println("Board Values : ");
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(cellValues[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println("Board State : ");
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(boardState[i][j]);
			}
			System.out.println();
		}
	}
	
	private static void printOutput(returnObjects obj){
		char column = (char) ((char) obj.column + 65);
		int row = obj.row + 1;
		System.out.println("Move : "+column + row + " " + obj.move);
		System.out.println("Val : "+obj.val);
		System.out.println("output Board : ");
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(obj.state[i][j]);
			}
			System.out.println();
		}
	}
	
	private static int evalGameScore(char me, char[][] boardState){
//		System.out.println("Eval score for : "+me);
		int score = 0;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(boardState[i][j] == '.'){
					continue;
				}else if(boardState[i][j] == me){
					score += cellValues[i][j];
				}else{
					score -= cellValues[i][j];
				}
			}
		}
		return score;
	}
	
	private static char getOpponentMove(char me){
		if(me == 'X'){
			return 'O';
		}
		return 'X';
	}
	
	private static returnObjects decideNextMove(){
		if(algo.equals("MINIMAX")){
			return miniMaxDecision();
		}else if(algo.equals("ALPHABETA")){
			return alphaBetaDecision();
		}
		return null;
	}
	
	private static char[][] performStakeAtIJ(char[][] state, char myPlay, int i, int j){
		char[][] nextState = copyBoard(state);
		nextState[i][j] = myPlay;
		return nextState;
	}
	
	private static boolean checkRaidPossibility(char[][] state, char myPlay, int i, int j){
		if(i-1 >= 0 && state[i-1][j] == myPlay){
	        return true;
		}else if(i+1 < n && state[i+1][j] == myPlay){
	        return true;
		}else if(j-1 >= 0 && state[i][j-1] == myPlay){
	        return true;
		}else if(j+1 < n && state[i][j+1] == myPlay){
	        return true;
		}
	    return false;
	}
	
	private static char[][] performRaidAtIJ(char[][] state, char myPlay, int i,	int j) {
		char[][] nextState = copyBoard(state);
		if (checkRaidPossibility(nextState, myPlay, i, j)) {
			nextState[i][j] = myPlay;
			boolean conquer = false;
			if (i - 1 >= 0 && nextState[i - 1][j] != myPlay	&& nextState[i - 1][j] != '.') {
				nextState[i - 1][j] = myPlay;
				conquer = true;
			}
			if (i + 1 < n && nextState[i + 1][j] != myPlay && nextState[i + 1][j] != '.') {
				nextState[i + 1][j] = myPlay;
				conquer = true;
			}
			if (j - 1 >= 0 && nextState[i][j - 1] != myPlay	&& nextState[i][j - 1] != '.') {
				nextState[i][j - 1] = myPlay;
				conquer = true;
			}
			if (j + 1 < n && nextState[i][j + 1] != myPlay && nextState[i][j + 1] != '.') {
				nextState[i][j + 1] = myPlay;
				conquer = true;
			}
			if (conquer) {
				return nextState;
			}
			return null;
		}
		return null;
	}
	
	private static returnObjects alphaBetaDecision(){
		char[] me = myPlay.toCharArray();
		return maxValueAlphaBeta(boardState, me[0], depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	private static returnObjects maxValueAlphaBeta(char[][] state, char myPlay, int curDepth, int alpha, int beta){
		returnObjects returnMaxValue = new returnObjects(n);
//		System.out.println("In Max Value Alpha Beta : ");
//		printBoard(state);
	    if(curDepth == 0 || isTerminalState(state)){
	        returnMaxValue.val = evalGameScore(myPlay, state);
	        returnMaxValue.state = state;
	        return returnMaxValue;
	    }
	    returnMaxValue.val = Integer.MIN_VALUE;
	    for(int i=0;i<n;i++){
	        for(int j=0;j<n;j++){
	            if(state[i][j] == '.'){
	            	char[][] successorState = performStakeAtIJ(state, myPlay, i, j);
//	            	System.out.println("Performed Stake in MAX AB: ");
//	            	printBoard(successorState);
	                returnObjects minReturn = minValueAlphaBeta(successorState, getOpponentMove(myPlay), curDepth - 1, alpha, beta);
//	                printOutput(minReturn);
	                if(minReturn.val > returnMaxValue.val){
	                    returnMaxValue.row = i;
	                    returnMaxValue.column = j;
	                    returnMaxValue.move = "Stake";
	                    returnMaxValue.state = successorState;
	                    returnMaxValue.val = minReturn.val;
	                }
	                if(returnMaxValue.val >= beta){
	                    return returnMaxValue;
	                }
	                alpha = (alpha > returnMaxValue.val) ? alpha : returnMaxValue.val;
	            }
	        }
	    }
	    for(int i=0;i<n;i++){
	        for(int j=0;j<n;j++){
	            if(state[i][j] == '.'){
	                char[][] successorState = performRaidAtIJ(state, myPlay, i, j);
	                if(null != successorState){
//	                	System.out.println("Performed Raid in MAX AB: ");
//	                	printBoard(successorState);
	                    returnObjects minReturn = minValueAlphaBeta(successorState, getOpponentMove(myPlay), curDepth - 1, alpha, beta);
//	                    printOutput(minReturn);
	                    if (minReturn.val > returnMaxValue.val){
	                        returnMaxValue.row = i;
	                        returnMaxValue.column = j;
	                        returnMaxValue.move = "Raid";
	                        returnMaxValue.state = successorState;
	                        returnMaxValue.val = minReturn.val;
	                    }
	                }
	                if(returnMaxValue.val >= beta){
	                    return returnMaxValue;
	                }
	                alpha = (alpha > returnMaxValue.val) ? alpha : returnMaxValue.val;
	            }
	        }
	    }
	    return returnMaxValue;
	}
	
	private static returnObjects minValueAlphaBeta(char[][] state, char myPlay, int curDepth, int alpha, int beta){
//		System.out.println("Inside Min Value alpha beta : ");
//		printBoard(state);
		returnObjects returnMinValue = new returnObjects(n);
	    if(curDepth == 0 || isTerminalState(state)){
	    	returnMinValue.val = evalGameScore(getOpponentMove(myPlay), state);
	    	returnMinValue.state = state;
	        return returnMinValue;
	    }
	    returnMinValue.val = Integer.MAX_VALUE;
	    for(int i=0;i<n;i++){
	        for(int j=0;j<n;j++){
	            if(state[i][j] == '.'){
	            	char[][] successorState = performStakeAtIJ(state, myPlay, i, j);
//	            	System.out.println("Performed Stake in MIN AB : ");
//	            	printBoard(successorState);
	                returnObjects maxReturn = maxValueAlphaBeta(successorState, getOpponentMove(myPlay), curDepth - 1, alpha, beta);
//	                printOutput(maxReturn);
	                if(maxReturn.val < returnMinValue.val){
	                	returnMinValue.row = i;
	                	returnMinValue.column = j;
	                	returnMinValue.move = "Stake";
	                	returnMinValue.state = successorState;
	                	returnMinValue.val = maxReturn.val;
	                }
	                if(returnMinValue.val <= alpha){
	                    return returnMinValue;
	                }
	                beta = (beta < returnMinValue.val) ? beta : returnMinValue.val;
	            }
	        }
	    }
	    for(int i=0;i<n;i++){
	        for(int j=0;j<n;j++){
	            if(state[i][j] == '.'){
	                char[][] successorState = performRaidAtIJ(state, myPlay, i, j);
	                if(null != successorState){
//	                	System.out.println("Performed Raid in MIN AB : ");
//		            	printBoard(successorState);
	                    returnObjects maxReturn = maxValueAlphaBeta(successorState, getOpponentMove(myPlay), curDepth - 1, alpha, beta);
//	                    printOutput(maxReturn);
	                    if (maxReturn.val < returnMinValue.val){
	                    	returnMinValue.row = i;
	                        returnMinValue.column = j;
	                        returnMinValue.move = "Raid";
	                        returnMinValue.state = successorState;
	                        returnMinValue.val = maxReturn.val;
	                    }
	                }
	                if(returnMinValue.val <= alpha){
	                    return returnMinValue;
	                }
	                beta = (beta < returnMinValue.val) ? beta : returnMinValue.val;
	            }
	        }
	    }
	    return returnMinValue;
	}
	
	private static returnObjects miniMaxDecision(){
		char[] me = myPlay.toCharArray();
		return maxValue(boardState, me[0], depth);
	}
	
	private static returnObjects maxValue(char[][] state, char myPlay, int curDepth){
		returnObjects returnMaxValue = new returnObjects(n);
		if(curDepth == 0 || isTerminalState(state)){
			returnMaxValue.val = evalGameScore(myPlay, state);
			returnMaxValue.state = state;
			return returnMaxValue;
		}
		returnMaxValue.val = Integer.MIN_VALUE;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(state[i][j] == '.'){
					char[][] successorState = performStakeAtIJ(state, myPlay, i, j);
					returnObjects minReturn = minValue(successorState, getOpponentMove(myPlay), curDepth - 1);
					if(minReturn.val > returnMaxValue.val){
						returnMaxValue.row = i;
						returnMaxValue.column = j;
						returnMaxValue.move = "Stake";
						returnMaxValue.state = successorState;
						returnMaxValue.val = minReturn.val;
					}
				}
			}
		}
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(state[i][j] == '.'){
					char[][] successorState = performRaidAtIJ(state, myPlay, i, j);
					if(null != successorState){
						returnObjects minReturn = minValue(successorState, getOpponentMove(myPlay), curDepth - 1);
						if(minReturn.val > returnMaxValue.val){
							returnMaxValue.row = i;
							returnMaxValue.column = j;
							returnMaxValue.move = "Raid";
							returnMaxValue.state = successorState;
							returnMaxValue.val = minReturn.val;
						}
					}
				}
			}
		}
		return returnMaxValue;
	}
	
	private static returnObjects minValue(char[][] state, char myPlay, int curDepth){
		returnObjects returnMinValue = new returnObjects(n);
		if(curDepth == 0 || isTerminalState(state)){
			returnMinValue.val = evalGameScore(getOpponentMove(myPlay), state);
			returnMinValue.state = state;
			return returnMinValue;
		}
		returnMinValue.val = Integer.MAX_VALUE;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(state[i][j] == '.'){
					char[][] successorState = performStakeAtIJ(state, myPlay, i, j);
					returnObjects maxReturn = maxValue(successorState, getOpponentMove(myPlay), curDepth - 1);
					if(maxReturn.val < returnMinValue.val){
						returnMinValue.row = i;
						returnMinValue.column = j;
						returnMinValue.move = "Stake";
						returnMinValue.state = successorState;
						returnMinValue.val = maxReturn.val;
					}
				}
			}
		}
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(state[i][j] == '.'){
					char[][] successorState = performRaidAtIJ(state, myPlay, i, j);
					if(null != successorState){
						returnObjects maxReturn = maxValue(successorState, getOpponentMove(myPlay), curDepth - 1);
						if(maxReturn.val < returnMinValue.val){
							returnMinValue.row = i;
							returnMinValue.column = j;
							returnMinValue.move = "Raid";
							returnMinValue.state = successorState;
							returnMinValue.val = maxReturn.val;
						}
					}
				}
			}
		}
		return returnMinValue;
	}
	
	private static void createOutputFile(returnObjects obj){
		StringBuffer s = new StringBuffer();
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));
			int row = obj.row + 1, column = 65 + obj.column;
			char col = (char) column;
			s.append(col);
			s.append(row);
			s.append(" ");
			s.append(obj.move+"\n");
			w.write(s.toString());
			for(int i=0;i<n;i++){
				s = new StringBuffer();
				for(int j=0;j<n;j++){
					s.append(obj.state[i][j]);
				}
				s.append("\n");
				w.write(s.toString());
			}
			w.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		readInputFile("input.txt");
//		printInputTaken();
		returnObjects obj = decideNextMove();
//		printOutput(obj);
		createOutputFile(obj);
	}

}
