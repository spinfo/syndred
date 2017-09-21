MODULE TextsCP;


IMPORT RTS,Console,texts;

TYPE Reader* =RECORD eot*:BOOLEAN;filename*:ARRAY 80 OF CHAR; jReader:texts.Texts;END;
	 Writer*= RECORD eot*:BOOLEAN;filename*:ARRAY 80 OF CHAR; END;
	
	 

PROCEDURE  WriteString*(W:Writer; Str:ARRAY OF CHAR ); 

BEGIN
	Console.WriteString(Str);
END WriteString;

PROCEDURE  WriteStringLn*(Str:ARRAY OF CHAR ); 

BEGIN
	Console.WriteString(Str);
	Console.WriteLn();	
END WriteStringLn;

PROCEDURE WriteInt*(W:Writer; pos:INTEGER; len:INTEGER); 
BEGIN
	Console.WriteInt(pos,len);
END WriteInt;

PROCEDURE WriteLn*(W:Writer); 
BEGIN
	Console.WriteLn();
END WriteLn;

PROCEDURE OpenWriter*(W:Writer);
BEGIN
	W.eot:=FALSE;
	Console.WriteString("OpenWriter");Console.WriteLn();
END OpenWriter;


PROCEDURE Pos*(R:Reader):INTEGER;
BEGIN
	RETURN 0;
END Pos;

PROCEDURE OpenReader*(VAR R:Reader);
BEGIN
	Console.WriteString("OpenReader "+R.filename);Console.WriteLn();
	NEW(R.jReader);	
	R.jReader.open(MKSTR(R.filename));R.eot:=FALSE;
END OpenReader;

PROCEDURE Read*(VAR R:Reader;VAR ch:CHAR);
BEGIN	
		(* eof error *)
		IF R.jReader.eot THEN 
			Console.WriteString("Texts.Read EOF error for file: "+R.filename);
			RTS.Throw("Texts.Read EOF error");
		ELSE
			ch:=R.jReader.readCharFromFile();
			Console.Write(ch);
			IF (ch=0DX) OR (ch=0AX) THEN ch:=' ';END;
			IF R.jReader.eot THEN 
				R.eot:=TRUE; 	
				WriteStringLn("Texts.Read eot set true");
			END;
		END;
END Read;


BEGIN (*Auto-generated*)
	
END TextsCP.