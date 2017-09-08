MODULE RegexApi;

IMPORT RegexParser,RegexMatching, Console;

TYPE Regex*=POINTER TO RECORD
		regex*:RegexParser.Regex
	 END;

(*-----------------------------Aux-Method------------------------------*)
PROCEDURE ArrayToPointer(arr:ARRAY OF CHAR; VAR ptr:POINTER TO ARRAY OF CHAR);
VAR i,j:INTEGER;
BEGIN
	i:=0;
	WHILE (arr[i]#0X) DO INC(i) END;
	NEW(ptr,i+1);
	FOR j:=0 TO i DO ptr^[j]:=arr[j] END;
END ArrayToPointer;


(*--------------------------Api-Procedures----------------------------*)
PROCEDURE CreateRegex*(str:ARRAY OF CHAR):Regex;

VAR reg:Regex;regString:POINTER TO ARRAY OF CHAR;
BEGIN
	regString:=NIL;
	NEW(reg);
	NEW(reg.regex);
	ArrayToPointer(str,regString);
	RegexParser.InitCreateRegex(regString,reg.regex);
	RETURN reg
END CreateRegex;

PROCEDURE CreateRegexPtr*(str:POINTER TO ARRAY OF CHAR):Regex;
VAR reg:Regex;
BEGIN
	NEW(reg);
	NEW(reg.regex);
	RegexParser.InitCreateRegex(str,reg.regex);
	RETURN reg
END CreateRegexPtr;

PROCEDURE GetRegex*(sym:POINTER TO ARRAY OF CHAR):Regex;
VAR reg:Regex;
BEGIN
	NEW(reg);
	NEW(reg.regex);
	RegexParser.InitCreateRegex(sym,reg.regex);
	RETURN reg;
END GetRegex;

PROCEDURE MatchRegex*(regex:ARRAY OF CHAR;target:ARRAY OF CHAR):BOOLEAN;
VAR result:BOOLEAN;
	regString,tarString:POINTER TO ARRAY OF CHAR;
	reg:Regex;
	
BEGIN
	regString:=NIL;tarString:=NIL;
	ArrayToPointer(regex,regString);
	ArrayToPointer(target,tarString);
	NEW(reg);
	NEW(reg.regex);
	RegexParser.InitCreateRegex(regString,reg.regex);
	result:=RegexMatching.Match(reg.regex,tarString);
	RETURN result
END MatchRegex;

PROCEDURE (reg:Regex) Match*(str:ARRAY OF CHAR):BOOLEAN,NEW;
VAR result:BOOLEAN;
	tarString:POINTER TO ARRAY OF CHAR;
	
BEGIN	
	Console.WriteLn();
	Console.WriteString ("Match " + str);
	Console.WriteLn();
	tarString:=NIL;
	ArrayToPointer(str,tarString);
	result:=RegexMatching.Match(reg.regex,tarString);
	RETURN result;
END Match;

PROCEDURE (reg:Regex) MatchPtr*(str:POINTER TO ARRAY OF CHAR):BOOLEAN,NEW;
BEGIN
RETURN RegexMatching.Match(reg.regex,str);
END MatchPtr;

END RegexApi.