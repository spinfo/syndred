MODULE RegexMatching;

IMPORT RegexParser, texts, Console;

TYPE Regex=POINTER TO EXTENSIBLE RECORD (RegexParser.RegexType) END;

VAR i:INTEGER;
	ch:CHAR;
	(*tarString:POINTER TO ARRAY OF CHAR;*)
	sh:texts.Shared;
		
PROCEDURE WriteEntry(fromProcedure:ARRAY OF CHAR;ch:CHAR;i:INTEGER);


BEGIN
	
	Console.WriteString(fromProcedure +" Entry:  ");		
	IF ch # 0X THEN Console.Write(ch);Console.WriteLn();
	END;
	IF i >= 0 THEN Console.WriteInt(i,2);
	END;
	Console.WriteLn();
END WriteEntry;


PROCEDURE WriteExit(fromProcedure:ARRAY OF CHAR;valResult:INTEGER;ch:CHAR;i:INTEGER);

VAR res:ARRAY 10 OF CHAR; 
BEGIN
	IF valResult=1  THEN res:="TRUE" ELSIF valResult=-1 THEN res:="FALSE" ELSE res:=""; END;
	Console.WriteString(fromProcedure +" Exit: "+res+"  ");		
	IF ch # 0X THEN Console.Write(ch);Console.WriteLn();
	END;
	IF i >= 0 THEN Console.WriteInt(i,2);
	END;
	Console.WriteLn();
END WriteExit;
		
PROCEDURE GetCharAtPos(pos:INTEGER;sh:texts.Shared):CHAR;
VAR ch:CHAR;

BEGIN
	WriteEntry("GetCharAtPos pos",0X,pos);
	
	ch:= sh.getCharAtTextPos(pos);
	IF sh.backTrack THEN RETURN ' ' END;
	(* ch:= sh.getSym();
	IF pos < sh.texts.getTextLen() THEN
			Console.WriteString("GetCharAtPos ch from sh.texts.getCharAtPos: ");
			ch:= sh.texts.getCharAtPos(pos)
	ELSE 
		Console.WriteString("GetCharAtPos ch from sh.getSym: ");
		ch:= sh.getSym();
	END;*)	
	WriteExit("GetCharAtPos ch ",0,ch,-1);	
	RETURN ch;
END GetCharAtPos;

(*-------------------------Matching Procedures---------------------------*)

PROCEDURE MatchNegRange(range:RegexParser.Range; VAR flag:BOOLEAN);
BEGIN
	IF sh.backTrack THEN RETURN END;
	REPEAT
		flag:=((ch<range.min) OR (ch>range.max));
		range:=range.next;
	UNTIL range=NIL;
END MatchNegRange;

PROCEDURE MatchRange(range:RegexParser.Range; VAR flag:BOOLEAN);
BEGIN
	WriteEntry("MatchRange ",ch,-1);
	IF sh.backTrack THEN RETURN END;
	LOOP
		IF range=NIL THEN EXIT END;
		Console.WriteString("MatchRange range.min, range.max: ");
		Console.Write(range.min);Console.Write(' ');Console.Write(range.max);
		Console.WriteLn();
		flag:=((ch>=range.min) & (ch<=range.max));
		IF flag=TRUE THEN EXIT;
		ELSE range:=range.next END;
	END;
	(*jetzt noch schaun, ob irgendwo subRange *)
 	LOOP
		IF range=NIL THEN EXIT END;
		IF range.sub THEN EXIT END;
		range:=range.next
	END;
	IF range#NIL THEN MatchNegRange(range,flag) END;
	WriteExit("MatchRange ch ",0,ch,-1);
END MatchRange;

PROCEDURE MatchRegex(reg:RegexParser.Regex;VAR flag:BOOLEAN);
VAR  branch:RegexParser.Branch;
	 	j:INTEGER;
	 	res:INTEGER;
	
	PROCEDURE MatchBranch(branch:RegexParser.Branch; VAR flag:BOOLEAN);
	VAR piece:RegexParser.Piece;
	res:INTEGER;
	
		PROCEDURE MatchPiece(piece:RegexParser.Piece; VAR flag:BOOLEAN);
		VAR atom,temp_atom:RegexParser.Atom;
				min,max,q,j1:INTEGER;
				temp_flag:BOOLEAN;
				res:INTEGER;
				
			PROCEDURE MatchAtom(atom:RegexParser.Atom; VAR flag:BOOLEAN);
			VAR range:RegexParser.Range;res:INTEGER;
			
				(*PROCEDURE Final():BOOLEAN;
				BEGIN
					IF (piece.suc = NIL) & (atom.range.min=0X) & (atom.range.max=0X) THEN RETURN TRUE
					ELSE RETURN FALSE;
					END;
				END Final;
				*)
				
			BEGIN (* MatchAtom *)
				WriteEntry("MatchAtom ",ch,i);
				IF sh.backTrack THEN RETURN END;
				IF atom.range=NIL THEN  MatchRegex(atom.regex,flag);
				ELSE	(*         *)
					(*IF Final() THEN flag:=TRUE
					ELSE
						*)
						IF ~(ch=0X) THEN 
							(*ch:=tarString[i];*)
							ch:= GetCharAtPos(i,sh); (* sh.getCharAtTextPos(i);	*)		
							IF sh.backTrack THEN RETURN END;			
							INC(i);
							Console.WriteString("MatchAtom getCharAtTPos ch: ");
							Console.Write(ch); 
							Console.WriteLn();
						END;			
						IF atom.range.pos THEN
							MatchRange(atom.range,flag); 
						ELSE MatchNegRange(atom.range,flag);
						END;
					(*END;*)
				END;
				IF flag THEN res:=1 ELSE res:=-1;END;
				
				WriteExit("MatchAtom ch ",res,ch,-1);
			END MatchAtom;
			
		BEGIN (*MatchPiece*) (*hier Matching-Procedures aufrufen piece.MatchProcQuantified(piece,flag)*)
			(*MatchProcOptional (?)*)
			WriteEntry("MatchPiece ",0X,-1);
			IF sh.backTrack THEN RETURN END;
			flag:=FALSE;temp_flag:=FALSE;q:=0;
			CASE piece.id OF 
				1:  Console.WriteString("MatchPiece Case 1");Console.WriteLn();
				
					atom:=piece.atom;   (*Optional*)
					min:=0;
					max:=1;
					MatchAtom(atom,flag);
					IF sh.backTrack THEN RETURN END;
					IF ~flag THEN flag:=TRUE;
						IF atom.regex=NIL THEN DEC(i) END
					END; 
		
		
				|2: Console.WriteString("MatchPiece Case 2");Console.WriteLn();
					atom:=piece.atom;   (*Quantified*)
					min:=piece.min.val;
					max:=piece.max.val;
					Console.WriteString("MatchPiece min");Console.WriteInt(min,2);
					Console.WriteString("MatchPiece max");Console.WriteInt(max,2);
					q:=0;
					j1:=i;
					REPEAT 
						MatchAtom(atom,flag);
						IF sh.backTrack THEN RETURN END;
						IF flag THEN INC(q) END;
					UNTIL (~flag) OR (q=max);
					IF ~flag & (q>=min) THEN flag:=TRUE; 
						IF atom.regex=NIL THEN DEC(i) END 
					END;
		
				|3: Console.WriteString("MatchPiece Case 3");Console.WriteLn();
					atom:=piece.atom; (*Unbounded*) (*max=NIL*)
					temp_atom:=piece.suc.atom;
					min:=piece.min.val;
					REPEAT 
						MatchAtom(atom,flag);
						IF sh.backTrack THEN RETURN END;
						j1:=i;DEC(i); MatchAtom(temp_atom,temp_flag);
						IF temp_atom.regex#NIL THEN i:=j1 END;
						IF flag THEN INC(q) END;
						IF temp_flag THEN DEC(q); flag:=FALSE END;
					UNTIL (~flag);
					IF ~flag & (q>=min) THEN flag:=TRUE; 
						IF atom.regex=NIL THEN DEC(i) END 
					END;
		
		
			END (*end-case*);
			IF sh.backTrack THEN RETURN END;
			IF flag THEN res:=1 ELSE res:=-1;END;
			
			WriteExit("MatchPiece ch ",res,ch,i);
		END MatchPiece; 
		
		PROCEDURE Final():BOOLEAN;
		(* JR to be refined ? *)
		BEGIN
			IF sh.backTrack THEN RETURN FALSE END;
			IF piece.suc=NIL THEN
				IF piece.atom # NIL THEN
					IF piece.atom.range#NIL THEN
						IF (piece.atom.range.min=0X) & (piece.atom.range.max=0X) THEN RETURN TRUE;
						END;
					END;
				END;					
			END;
			RETURN FALSE;
		END Final;
		
	BEGIN (*MatchBranch*)
		WriteEntry("MatchBranch ",0X,-1);
		
		piece:=branch.piece;
		LOOP 
			IF (piece=NIL) OR Final()(*JR*) THEN  EXIT; (*alle Pieces abgearbeitet und ganzen String*)
			END;
			MatchPiece(piece,flag);
			IF sh.backTrack THEN RETURN END;
			IF flag THEN  piece:=piece.suc; 
				
			ELSE EXIT 
			END;
		END;
		IF flag THEN res:=1 ELSE res:=-1;END;
		WriteExit("MatchBranch ch ",res,ch,-1);
	END MatchBranch;

BEGIN (*Match Regex*)
	j:=i;
	WriteEntry("MatchRegex ch  i and j: ",ch,i);
	branch:=reg.branch;
	
	LOOP
		IF (branch=NIL) THEN EXIT END; 
		MatchBranch(branch,flag);
		IF sh.backTrack THEN RETURN END;
		IF flag THEN EXIT;
		ELSE 
			
			(*	Out.String("MatchRegex: Branch False  j= ");
			Out.Int(j,2);Out.String("ch=");Out.Char(ch);Out.Ln();*)
			Console.WriteLn();Console.WriteString("MatchRegex Branch false j:");
			Console.WriteInt(j,2);Console.WriteString(" ch=");
			Console.Write(ch);Console.WriteLn();
			i:=j;
			(*ch:=tarString[i];*)
			ch := GetCharAtPos(i,sh); (*sh.getCharAtTextPos(i);*)
			IF sh.backTrack THEN RETURN END;
			branch:=branch.alt
		END
	END;
	IF flag THEN res:=1 ELSE res:=-1;END;
	WriteExit("MatchRegex ch ",res,ch,i);
END MatchRegex;
				
PROCEDURE Match*(regex:RegexParser.Regex;target:POINTER TO ARRAY OF CHAR):BOOLEAN;
VAR flag:BOOLEAN; branch:RegexParser.Branch;
BEGIN
	(*tarString:=target;*)
	
	flag:=FALSE;
	i:=0;
	(*ch:=tarString[i];*)
	MatchRegex(regex,flag);
	IF sh.backTrack THEN i:=0; RETURN FALSE END;
	IF ch#0X THEN (*ch:=tarString[i]*) ch:=0X; END;
	IF (flag) & (ch#0X) THEN flag:=FALSE END;
	RETURN flag
END Match; 


PROCEDURE EditMatch*(regex:RegexParser.Regex;shared:texts.Shared):BOOLEAN;
VAR flag:BOOLEAN;
BEGIN
	WriteEntry("EditMatch ",0X,-1);
	
	flag:=FALSE;
	sh:=shared;
	(*i:=shared.texts.getTextPos();*)
	Console.WriteString("RegexMatching.EditMatch i: ");
	Console.WriteInt(i,2);
	Console.WriteLn;
	(*ch:='$';*) (* GetCharAtPos(i,sh); shared.getCharAtTextPos(i);*)
	Console.WriteString("RegexMatching.EditMatch ch: ");
	Console.Write(ch);
	Console.WriteLn;
	Console.WriteString("RegexMatching.EditMatch TextLen: ");
	Console.WriteInt(shared.getSharedText().getTextLen(),2);
	Console.WriteLn;
	MatchRegex(regex,flag);
	IF sh.backTrack THEN i:=0; RETURN FALSE END;
	RETURN flag;
END EditMatch;


PROCEDURE GetStartCh*(shared:texts.Shared);
BEGIN
	ch:=shared.getSym();
	WriteEntry("GetStartCh: ",ch,-1);
	i:=0;
END GetStartCh;




END RegexMatching.