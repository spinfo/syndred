MODULE RegexApiExamples;

IMPORT RegexApi;

PROCEDURE CreateRegex*();
VAR regString,tarString:ARRAY 64 OF CHAR;
		regex:RegexApi.Regex;
		name:RegexApi.Regex;
		result:BOOLEAN;
		
BEGIN
	(*regString:="<\i\c*(\s+\i\c*='[^<]*')*(/>|>)";
	tarString:="<Tag Att1='wert1' Att2='wert2'>"; *)
	(*regString:="[0-9]{3}\-[0-9]{2}\-[0-9]{4}";
	tarString:="123-12-1234";*) 
	(*regString:="[a-z-[b]]*x";
	tarString:="acccax";*)
	(*regString:="Hallo";
	tarString:="Hallouuuh";*)
	(*regString:="ge.*x";
	tarString:="gegangenx";*)
	(*regString:="<\i\c*\s?(/>|>)";
	tarString:="<_Tree>";*)
	(*regString:="[^<]*<";
	tarString:="irgendwelches CharData <";*)
	regString:="<\?xml\s*\?>";
	tarString:="<?xml ?>";

	name:=RegexApi.CreateRegex(regString);
	result:=name.Match(tarString);
	(*HALT(127);*)
END CreateRegex;

PROCEDURE FehlerCheck*();
VAR regString:ARRAY 64 OF CHAR;
regex:RegexApi.Regex;
BEGIN
	regString:="[&#300;]";
	regex:=RegexApi.CreateRegex(regString);
END FehlerCheck;

PROCEDURE MatchRegex*();
VAR  result:BOOLEAN;
BEGIN
(*result:=RegexApi.MatchRegex("(er|sie) ging|wir gingen","wir ging");*)
(*result:=RegexApi.MatchRegex("<\?xml\s+version(\s+)?=1\.0\?>","<?xml version=1.0?>");*)
(*result:=RegexApi.MatchRegex("ge.*x","gegangen"); //Fehler*)
result:=RegexApi.MatchRegex("^a","^a");
HALT(127)
END MatchRegex;

END RegexApiExamples.


