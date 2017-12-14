MODULE Ebnf;




(* code mainly from Niklaus Wirth Grundlagen und Techniken des Compilerbaus, from English
version Compiler Construction, too (s. http://www.ethoberon.ethz.ch/WirthPubl/CBEAll.pdf) and for more implementation
details from Wirth Compilerbau Stuttgart Teubner 1986 (for Modula 2).
Changes by JR;
Parser is rewritten to be completely recursive for to establish unlimited backtracking in parse.
*)


(* Wirth example: ebnf defined in ebnf
syntax        =     {production}.           
production    =     identifier "=" expression "." . 
expression    =     term {"|" term}. 
term          =     factor {factor}.              
factor        =     identifier | string | "(" expression ")"
 | "[" expression "]" | "{" expression "}". 
By application of the given translation rules and subsequent simplification the following parser 
results. It is formulated as an Oberon module: 
*)


 
IMPORT RTS,TextsCP, texts, Console, RegexApi, RegexMatching,RegexParser,CPmain; 

CONST IdLen = 32; 
    ident = 0; literal = 2; lparen = 3; lbrak = 4; lbrace = 5; bar = 6; eql = 7; 
    rparen = 8; rbrak = 9; rbrace = 10; period = 11; other = 12; 
    
TYPE Identifier = ARRAY IdLen OF CHAR; 

     Symbol*=POINTER TO EXTENSIBLE RECORD alt,next:Symbol 
     END;
     
     Terminal=POINTER TO RECORD(Symbol) sym:INTEGER;name:ARRAY IdLen OF CHAR; 
     	reg:RegexApi.Regex;
     END;
     
     (* wrapper for Symbols p, q,r,s which might be used in ebnf as substitute for call by name;
     to be used for reemplimation in java *)
     
     SymbolsWrapper =POINTER TO RECORD p,q,r,s:Symbol;
     END;
     
     Nonterminal = POINTER TO NTSDesc;
	 NTSDesc = RECORD (Symbol) this: Header END;
	 Header = POINTER TO HDesc;
	 HDesc = RECORD sym: Symbol; entry: Symbol; suc:Header; name: ARRAY IdLen OF CHAR END;
	      
VAR list,sentinel,h:Header;
	q,r,s: Symbol;
	startsymbol*:Symbol (* startsymbol for parse if called from editor, is exported to call of parse procedure*);

	ch: CHAR; 	
    sym:      INTEGER;       
    lastpos:  INTEGER;       
    id:       Identifier;       
    R:        TextsCP.Reader;       
    W:        TextsCP.Writer;       
   
    txt:texts.Texts;
    shared:texts.Shared;
    
  
PROCEDURE error(n: INTEGER); 
              VAR pos: INTEGER;       
BEGIN pos := TextsCP.Pos(R); 
	Console.WriteString("error nr: ");Console.WriteInt(n,2);Console.WriteLn;
    IF pos > lastpos+4 THEN  (*avoid spurious error messages*) 
        TextsCP.WriteString(W, "  pos"); TextsCP.WriteInt(W, pos, 6); 
        TextsCP.WriteString(W, "  err"); TextsCP.WriteInt(W, n, 4); lastpos := pos; 
        TextsCP.WriteString(W,"sym"); TextsCP.WriteInt(W, sym, 4); 
        TextsCP.WriteLn(W);   (* TextsCP.Append(Oberon.Log,W.buf)   *) 
    END;
    RTS.Throw(" error");       
END error;     

PROCEDURE GetSym; 
VAR i:INTEGER;       
BEGIN 
	WHILE ~R.eot & (ch <= " ") DO TextsCP.Read(R, ch) END ;   (*skip blanks*) 
    CASE ch OF       
   		"A" .. "Z", "a" .. "z": sym := ident; i := 0; 
			REPEAT id[i] := ch; INC(i); TextsCP.Read(R, ch) 
			UNTIL (CAP(ch) < "A") OR (CAP(ch) > "Z"); 
            id[i]:=0X       
			|22X:  (*quote*) 
				TextsCP.Read(R, ch); sym := literal; i := 0; 
				(* wirth----------------------------------- 
				WHILE (ch # 22X) & (ch > " ") DO 
                    id[i]:= ch;
                    INC(i);
                    TextsCP.Read(R,ch)       
   				END ; 
				IF ch <= " " THEN error(1) END ; 
				------------------------------------------*)
				(* JR, regex *)
				LOOP
					IF ch=22X THEN
						IF i=0 (*empty terminal string*) THEN EXIT
						ELSIF id[i] # '\'  (* quote is NOT escaped *) THEN EXIT
						ELSIF (i >0) & (id[i-1] = '\') (* '\' is escaped by '\', 
						i.e. termination by '"'*)
								THEN EXIT
						END;
					END;
				    id[i]:= ch;
                    INC(i);
                    IF i > IdLen THEN error(1);
                    END;
                    TextsCP.Read(R,ch)       
   				END ; 
				(* Wirth IF ch <= " " THEN error(1) END ;	*)
				id[i] := 0X; TextsCP.Read(R, ch) 
			|  "=" : sym := eql; TextsCP.Read(R, ch) 
			|  "(" : sym := lparen; TextsCP.Read(R, ch) 
			|  ")" : sym := rparen; TextsCP.Read(R, ch) 
			|  "[" : sym := lbrak; TextsCP.Read(R, ch) 
			|  "]" : sym := rbrak; TextsCP.Read(R, ch) 
			| "{" : sym := lbrace; TextsCP.Read(R, ch) 
			|  "}" : sym := rbrace; TextsCP.Read(R, ch) 
			| "|" : sym := bar; TextsCP.Read(R, ch) 
			|  "." : sym := period; TextsCP.Read(R, ch) 
			ELSE sym := other; 
				(* if entered by jr; otherwise eof error*)
				IF R.eot THEN ch:=' ' ELSE TextsCP.Read(R, ch);END; 
       		END       
END GetSym; 

 
PROCEDURE find(str : ARRAY OF CHAR; VAR h:Header);
VAR h1:Header;
BEGIN
	h1:=list;
	sentinel.name:=str$;
	WHILE h1.name#str DO h1:=h1.suc;END;
	IF h1 = sentinel THEN (*insert*)
		NEW(sentinel);
		h1.suc := sentinel;
		h1.entry:=NIL;
	END;	
	h:=h1;
END find;

PROCEDURE link(p,q:Symbol);
VAR t:Symbol;

BEGIN (* insert q in places indicated by linked chain p *)
	WHILE p # NIL DO
		t := p; p:=t.next; t.next:=q;
	END;
END link;
   

PROCEDURE expression(VAR p,q,r,s:Symbol);   
VAR q1, s1:Symbol;

    
    PROCEDURE term(VAR p,q,r,s:Symbol);  
    VAR p1,q1,r1,s1:Symbol;     

       PROCEDURE factor(VAR p,q,r,s:Symbol);    
       VAR a:Symbol;identifiernonterminal:Nonterminal;literalterminal:Terminal; h:Header;
       BEGIN h:=NIL;a:=NIL;identifiernonterminal:=NIL;literalterminal:=NIL;        		
            IF sym = ident (*nonterminal*) THEN
            	NEW(identifiernonterminal);
            	find(id$,h);
            	(* name of nonterminal symbol may be accessed via h.name);*)
            	identifiernonterminal.this:=h;
            	a:=identifiernonterminal;a.alt:=NIL;a.next:=NIL;
            	
            	(*record(T0, id, 1);*)  
            	p:=a;q:=a;r:=a;s:=a;           
            	GetSym 
            ELSIF sym = literal (*terminal*) THEN 
            	NEW(literalterminal);literalterminal.sym:=sym;
            	literalterminal.name:=id$; 
            	literalterminal.reg:=RegexApi.CreateRegex(id$);
            	a:=literalterminal;a.alt:=NIL;a.next:=NIL;
            	(*record(T1, id, 0);*) 
            	
            	p:=a;q:=a;r:=a;s:=a; 
            	GetSym 
            ELSIF sym = lparen THEN 
         		GetSym; 
         		expression(p,q,r,s); 
                IF sym = rparen THEN GetSym ELSE error(2) END 
            ELSIF sym = lbrak THEN 
         		GetSym; expression(p,q,r,s); 
         		
         		NEW(literalterminal);literalterminal.sym:=sym;
            	literalterminal.name:=""; 
            	a:=literalterminal;a.alt:=NIL;a.next:=NIL;
            	q.alt:=a;s.next:=a;q:=a;s:=a;
               	IF sym = rbrak THEN GetSym ELSE error(3) END 
            ELSIF sym = lbrace THEN 
         		GetSym; expression(p,q,r,s); 
         		
         		NEW(literalterminal);literalterminal.sym:=sym;
            	literalterminal.name:=""; 
            	a:=literalterminal;a.alt:=NIL;a.next:=NIL;
         		q.alt:=a;q:=a;r:=a;s:=a;
               	IF sym = rbrace THEN GetSym ELSE error(4) END 
            ELSE    error(5)    
        	END;        	
        END factor;    
 
    
     BEGIN (*term*) 
      	p1:=NIL;q1:=NIL;r1:=NIL;s1:=NIL;     	
     	factor(p,q,r,s);           
        WHILE sym < bar DO 
        	factor(p1,q1,r1,s1);link(r,p1);r:=r1;s:=s1; 
        END;            
       
     END term; 
           
    BEGIN (*expression*)  
    	q1:=NIL;s1:=NIL;  
    	
    	term(p,q,r,s);      
    	WHILE sym = bar DO GetSym; term(q.alt,q1,s.next,s1);q:=q1;s:=s1; 
    	END;    
    	
    END expression;
 

           
PROCEDURE production;       
BEGIN (*sym = ident*) 
	
	find(id$,h);
	GetSym; 
    IF sym = eql THEN GetSym ELSE error(7) END; 
    expression(h.entry,q,r,s); link(r,NIL);   
    IF sym = period THEN 
    	GetSym
    ELSE error(8) 
    END;
     
END production;


      
PROCEDURE syntax;       
BEGIN    	
	TextsCP.WriteStringLn("syntax start");
    WHILE sym = ident DO production END;  
    TextsCP.WriteStringLn("syntax end"); 
END syntax;  

(* checks whether there is a nonterminalwhich does not lead to a terminal*)
PROCEDURE checkSyntax():BOOLEAN;
VAR h:Header;error:BOOLEAN;(*i:INTEGER;*)
BEGIN
	Console.WriteLn();
	h:=list;error:=FALSE;
	WHILE h # sentinel DO	
		IF h.entry=NIL THEN 
			error:=TRUE;
			Console.WriteString("undefined Symbol "+h.name);Console.WriteLn();
		ELSE Console.WriteString("Symbol "+h.name);Console.WriteLn();
			(*i:=0;
			WHILE h.name[i]#0X DO Console.Write(h.name[i]);INC(i);END;Console.WriteLn();
			*)
		END;
		h:=h.suc;
	END (*while*);
	RETURN ~error;
END checkSyntax;
            
PROCEDURE Compile*():BOOLEAN; 
VAR ok:BOOLEAN;
BEGIN (*set R to the beginning of the text to be compiled*) 
	TextsCP.WriteString(W,"Compile Start read Grammar");Console.WriteLn();
	R.filename:= "C://users//rols//lexGrammar.txt";	
	TextsCP.OpenReader(R);
	Console.WriteString("EBNF nach OpenReader");Console.WriteLn();	
	
	ok:=FALSE;
    lastpos := 0; 
    NEW(sentinel);list:=sentinel;h:=list;
    TextsCP.Read(R, ch); 
    GetSym;
    syntax;  
    IF checkSyntax() THEN ok:=TRUE;
    END;   
    (*TextsCP.Append(Oberon.Log,W.buf) *)   
    IF ok THEN
    	TextsCP.WriteString(W,"Compile ok")
    ELSE TextsCP.WriteString(W,"Compile failed");
    END;
    Console.WriteLn(); 
    RETURN ok; 
END Compile;    


PROCEDURE parse*(node:Symbol):BOOLEAN;

VAR resParse:BOOLEAN; pos:INTEGER;nodeName:ARRAY IdLen OF CHAR;

		PROCEDURE match(tNode:Terminal):BOOLEAN;
	
		VAR index:INTEGER;ch:CHAR;testChar:CHAR;resMatch:BOOLEAN;
	
		(*          *)
		BEGIN
			Console.WriteString("parse.match Start pos: ");
			Console.WriteInt(txt.getTextPos(),2);
			Console.WriteString(" "+tNode.name$);
			Console.WriteLn();
			IF shared.backTrack THEN
			
				Console.WriteString(" Match backTrack");
				Console.WriteLn();
				RETURN FALSE;
			END;
			
			index:=0;
						
			resMatch:=RegexMatching.EditMatch(tNode.reg.regex,shared);
			IF resMatch THEN 
				Console.WriteString(" after EditMatch resMatch true");
			ELSE
				Console.WriteString(" after EditMatch resMatch false");
			END;
			
			Console.WriteString(" for "+tNode.name$);
			(*Console.WriteInt(txt.getTextPos(),2);*) 
			Console.WriteLn();
			IF shared.backTrack THEN 
				RETURN FALSE 
			ELSE
				RETURN resMatch;
			END;		
		END match;
	
	

BEGIN (*parse*)
	(* 17-12-12*)
	IF node = NIL THEN 
		Console.WriteString("parse entry node nil");
		Console.WriteLn();
		RETURN TRUE;
	END;
	IF shared.backTrack THEN
		IF node#list.entry THEN RETURN FALSE
		ELSE
			txt.setTextPos(0);
			Console.WriteString("parse after backtrack restart");
			Console.WriteLn();
			shared.backTrack:=FALSE;
		END;
	END;
	IF node IS Terminal THEN
		nodeName:=node(Terminal).name$
	ELSE nodeName:=node(Nonterminal).this.name$;
	END;
	Console.WriteString("parse node: "+nodeName);Console.WriteLn();
	pos:=txt.getTextPos();
	resParse:=FALSE;	
	(* 17-12-12 IF node = NIL THEN RETURN TRUE
	ELS*)IF node IS Terminal THEN
			resParse:=match(node(Terminal));
			IF shared.backTrack THEN RETURN FALSE END;
			Console.WriteString("parse resParse after match Pos: ");
			Console.WriteInt(txt.getTextPos(),2);
			IF resParse THEN Console.WriteString(" TRUE")
			ELSE Console.WriteString(" FALSE");
			END;
			Console.WriteLn();			
		(* depth first recursion for nonterminal *)
	ELSE resParse:=parse(node(Nonterminal).this(*pointer to headerlist*).entry);
	END;
	IF shared.backTrack THEN RETURN FALSE END;
	(* bredth second recursion*)
	IF resParse THEN 
		Console.WriteString ("parse vor bredth second");
		Console.WriteLn();
		IF node.next=NIL THEN
			Console.WriteString ("node.next NIL vor bredth second");
			Console.WriteLn();
		END;
		resParse:=parse(node.next);
		IF shared.backTrack THEN RETURN FALSE
		ELSIF resParse  THEN RETURN TRUE;
		END;
	END;
	IF shared.backTrack THEN RETURN FALSE 
	END;
	(* alternative after fail, reset position in text *)
	txt.setTextPos(pos);
	(* no alt node is fail; if needed for distinction of case of empty node which is matched
		without change of pos*)
	IF node.alt=NIL THEN RETURN FALSE
	ELSIF parse(node.alt) THEN 
		IF shared.backTrack THEN RETURN FALSE ELSE RETURN TRUE
		END;
	ELSE txt.setTextPos(pos);RETURN FALSE;		
	END;
	
END parse;


PROCEDURE init*(sh:texts.Shared):BOOLEAN;

BEGIN
	Console.WriteString("Init entry");Console.WriteLn();	
	IF Compile() THEN 		
		Console.WriteString("nach Compile");Console.WriteLn();			
		startsymbol:=list.entry;
		shared:=sh;
		txt:=shared.getSharedText();(* for getTextPos and setTextPos access*)
		RegexMatching.GetStartCh(sh);
		
		RETURN TRUE;
	ELSE RETURN FALSE;
	END;
END init;

BEGIN (*Auto-generated*)
	(********************************************************************
	shared:=NIL;txt:=NIL;startsymbol:=NIL;
	Console.WriteString("EBNF Start ");Console.WriteLn();
	
		
	IF init(shared) THEN 		
		Console.WriteString("EBNF nach Init");Console.WriteLn();			
		(*  *)
		(*txt:=shared.texts;*)
		
		IF parse(list.entry(* before: list only *)) THEN
			Console.WriteString(" parse ok")
		ELSE Console.WriteString(" parse failed");
		END;
		
	END;
	
	
	Console.WriteString("EBNF End");Console.WriteLn();
	************************************************************************)
END Ebnf.
