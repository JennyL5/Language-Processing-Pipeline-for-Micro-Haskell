// File:   MH_Lexer.java

// Java template file for lexer component of Informatics 2A Assignment 1.
// Concerns lexical classes and lexer for the language MH (`Micro-Haskell').


import java.io.* ;

class MH_Lexer extends GenLexer implements LEX_TOKEN_STREAM {

static class VarAcceptor extends Acceptor implements DFA {
	//small (small + large + digit+')
	
	public String lexClass() {
		return "VAR" ;
	} 

	public int numberOfStates() {
		return 3 ;
	} 

	int next (int state, char c) {
	switch (state) {
		case 0: if (CharTypes.isSmall(c)) return 1; else return 2; //small
		case 1: if ((CharTypes.isSmall(c)) || //small +
				(CharTypes.isLarge(c)) || //large +
				(CharTypes.isSmall(c)) || //digit +
				(c == ('\''))) return 1 ; // apostrophe = accepts
			else return 2; // garbage state

		default: return 2; // garbage state
		}
	}

	boolean accepting (int state) {
		return (state == 1) ;
		} // accepting state is 1
	
	int dead () {
		return 2 ; // garbage state
		}
}



static class NumAcceptor extends Acceptor implements DFA {
	//0 + nonZeroDigit digit ∗
	public String lexClass() {
		return "NUM" ;
		} ;
    public int numberOfStates() {
    	return 4 ;
    	} ;

    int next (int state, char c) {
		switch (state) {
		case 0: if (c=='0') return 2; else if (CharTypes.isDigit(c)) return 1; else return 3;
		case 1: if (CharTypes.isDigit(c)) return 1; else return 3;
			default: return 3; // garbage state
			}
    }

    boolean accepting (int state) {
    	return (state == 1 || state == 2) ;
    	} // accepting state is 1 or 2
    int dead () {
    	return 3 ; // garbage state
    	}
}

static class BooleanAcceptor extends Acceptor implements DFA {
	// True + False
	
		public String lexClass() {
			return "BOOLEAN";
		} 
		
		public int numberOfStates() {
			return 9 ; 
		} 

		int next (int state, char c) {
			switch (state) {
				case 0: if (c=='T') return 1; else if (c=='F') return 4; else return 8; //T or F
				case 1: if (c=='r') return 2; else return 8;
				case 2: if (c=='u') return 3; else return 8;
				case 3: if (c=='e') return 7; else return 8;
				case 4: if (c=='a') return 5; else return 8;
				case 5: if (c=='l') return 6; else return 8;
				case 6: if (c=='s') return 3; else return 8;
		
				default: return  dead() ; // garbage state, declared "dead" below
			}
		}
		boolean accepting (int state) {
			return (state == 7);
		} // accepting state is 7
	    int dead () {
	   		return 8 ;// garbage state
	   	}
}



static class SymAcceptor extends Acceptor implements DFA {
    //symbolic symbolic∗ === 
	
	public String lexClass() {
		return "SYM" ;
	} 

	public int numberOfStates() {
		return 3 ;
	} 

	int next (int state, char c) {
		switch (state) {
		case 0: if (CharTypes.isSymbolic(c)) return 1; else return 2; //is symbolic
		case 1: if (CharTypes.isSymbolic(c)) return 1; else return 2; //is symbolic *
		default: return 2; // garbage state
		}
	}

	boolean accepting (int state) {
		return (state == 1);
		} // accepting state is 1
	
	int dead () {
		return 2 ;
		} // garbage state
}

static class WhitespaceAcceptor extends Acceptor implements DFA {
	// whitespace whitespace∗
		public String lexClass() {
			return "" ;
		} 
			
		public int numberOfStates() {
			return 3 ;
		} 

		int next (int state, char c) {
			switch (state) {
			case 0: if (CharTypes.isWhitespace(c)) return 1; else return  2; //is symbolic
			case 1: if (CharTypes.isWhitespace(c)) return 1; else return  2; //is symbolic *
			default: return 2 ; // garbage state, declared "dead" below
			}
		}

		boolean accepting (int state) {
			return (state == 1);
		} // accepting state is 1
			
		int dead () {
			return 2 ;
		}// garbage state
}

static class CommentAcceptor extends Acceptor implements DFA {
    //  - - -∗	(nonSymbolNewline nonNewline∗ + ε) 
	
	public String lexClass() {
		return "" ;
	} 

	public int numberOfStates() {
		return 6 ;
	} 

	int next (int state, char c) {
		switch (state) {
		case 0: if (c=='-') return 1; else return 5; // -
		case 1: if (c=='-') return 2; else return 5; // -
		case 2: if (c=='-') return 2; else return 3; // -*
		case 3: if ((!CharTypes.isSymbolic(c) && !CharTypes.isNewline(c))) // not symbolic and not newline
					return 4 ; 
				else return 5;
		case 4: if ((!CharTypes.isNewline(c))) // not newline or epsilon
					return 4 ; 
				else return 5;
		default: return 5 ; // garbage state
		}
	}

	boolean accepting (int state) {
		return  (state ==2 || state ==3 || state==4);
		} // accepting state is 3
	int dead () {
		return 5 ;
		} // dead state is dead 5
		
}


static class TokAcceptor extends Acceptor implements DFA {

    String tok ;
    int tokLen ;
    TokAcceptor (String tok) {
    	this.tok = tok ; 
    	tokLen = tok.length() ;
    	}
    
    public String lexClass() {
		return tok ;
	} 

	public int numberOfStates() {
		return (tokLen +2) ; // one for accepting state and one for garbage state
	} 


	int next (int state, char c) {
		if ((state < tokLen) && (c==tok.charAt(state))) {
			return (state + 1);
		} else {
			return (tokLen +1);
		} 
	}

	boolean accepting (int state) {
		return (state == tokLen) ;
	} // accepting state

	int dead () {
		return (tokLen+1);
	} // garbage state is one greater than tokLen
	
}

//construct acceptors for the 14 classes in MH_acceptors array
//Integer, bool, if, then, else and for the three special symbols (, ), ;

static DFA WhitespaceAcc = new WhitespaceAcceptor();
static DFA CommentAcc = new CommentAcceptor();
static DFA leftbracketAcc = new TokAcceptor("(");
static DFA rightbracketAcc = new TokAcceptor(")");
static DFA semicolonAcc = new TokAcceptor(";");
static DFA IntegerAcc = new TokAcceptor("Integer");
static DFA boolAcc = new TokAcceptor("Bool");
static DFA ifAcc = new TokAcceptor("if");
static DFA thenAcc = new TokAcceptor("then");
static DFA elseAcc = new TokAcceptor("else");
static DFA VarAcc = new VarAcceptor();
static DFA NumAcc = new NumAcceptor();
static DFA SymAcc = new SymAcceptor();
static DFA BooleanAcc = new BooleanAcceptor();


static DFA[] MH_acceptors = 
		new DFA[] {WhitespaceAcc, CommentAcc, leftbracketAcc, rightbracketAcc, semicolonAcc, IntegerAcc, boolAcc, ifAcc, thenAcc, elseAcc, VarAcc, NumAcc, SymAcc, BooleanAcc} ;

    MH_Lexer (Reader reader) {
	super(reader,MH_acceptors) ;
    }

}

