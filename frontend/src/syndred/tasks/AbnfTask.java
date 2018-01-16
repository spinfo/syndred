package syndred.tasks;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apg.GeneratorGrammar;
import apg.GeneratorSyntax;
import apg.Statistics;
import apg.Trace;
import apg.Utilities;
import apg.GeneratorSyntax.Alternation;
import apg.GeneratorSyntax.And;
import apg.GeneratorSyntax.BNum;
import apg.GeneratorSyntax.Concatenation;
import apg.GeneratorSyntax.DNum;
import apg.GeneratorSyntax.GenFile;
import apg.GeneratorSyntax.GenRule;
import apg.GeneratorSyntax.IncAlt;
import apg.GeneratorSyntax.NameDef;
import apg.GeneratorSyntax.Not;
import apg.GeneratorSyntax.Option;
import apg.GeneratorSyntax.Predicate;
import apg.GeneratorSyntax.ProsVal;
import apg.GeneratorSyntax.Rep;
import apg.GeneratorSyntax.RepMax;
import apg.GeneratorSyntax.RepMin;
import apg.GeneratorSyntax.RepMinMax;
import apg.GeneratorSyntax.Repetition;
import apg.GeneratorSyntax.Rnm;
import apg.GeneratorSyntax.Tbs;
import apg.GeneratorSyntax.Tcs;
import apg.GeneratorSyntax.Tls;
import apg.GeneratorSyntax.Trg;
import apg.GeneratorSyntax.Udt;
import apg.GeneratorSyntax.XNum;
import apg.Parser.Result;
import apg.Utilities.LineCatalog;
import syndred.entities.Block;
import syndred.entities.InlineStyleRange;
import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public class AbnfTask extends Task {
	
	static apg.Parser 		abnfParser = null;
	static apg.Grammar 		abnfGrammar = null;
	static Trace 			trace = null;
	static boolean 			displayTrace = false;
	static Statistics 		stats = null;
	static Vector<String> 	errors = new Vector<String>();
	static Vector<String> 	warnings = new Vector<String>();

	public AbnfTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) {
		super(input, output, parser);
		
		System.out.println("AbnfTask");
		try{
		// catalog the line numbers for error & information reporting
		String grammarDef = parser.getGramma() + "\n"; 
		LineCatalog catalog = new LineCatalog(grammarDef);
		System.out.println("");
		System.out.println(catalog.toString());
		if(catalog.getWarningCount() > 0){
			System.out.println("catalog warnings:");
			catalog.displayWarnings(System.out);
		}
		if(catalog.getErrorCount() > 0){
			System.out.println("catalog errors:");
			catalog.displayErrors(System.out);
		}

		// set up the parser
		abnfGrammar = GeneratorGrammar.getInstance();
		abnfParser = new apg.Parser(abnfGrammar);
		if(displayTrace){
			trace = abnfParser.enableTrace(true);
			setTraceOptions(trace);
		}
		stats = abnfParser.enableStatistics(true);
		abnfParser.setInputString(grammarDef.toCharArray());
		abnfParser.setStartRule(GeneratorGrammar.RuleNames.FILE.ruleID());
		
		// the callback function for the selected AST nodes
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.FILE.ruleID(), new GenFile(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.RULE.ruleID(), new GenRule(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.NAMEDEF.ruleID(), new NameDef(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.INCALT.ruleID(), new IncAlt(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.PROSVAL.ruleID(), new ProsVal(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.ALTERNATION.ruleID(), new Alternation(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.CONCATENATION.ruleID(), new Concatenation(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.REPETITION.ruleID(), new Repetition(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.REP.ruleID(), new Rep(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.REP_MIN.ruleID(), new RepMin(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.REP_MAX.ruleID(), new RepMax(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.REP_MIN_MAX.ruleID(), new RepMinMax(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.PREDICATE.ruleID(), new Predicate(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.ANDOP.ruleID(), new And(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.NOTOP.ruleID(), new Not(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.OPTION.ruleID(), new Option(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.RNMOP.ruleID(), new Rnm(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.UDTOP.ruleID(), new Udt(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.TLSOP.ruleID(), new Tls(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.TCSOP.ruleID(), new Tcs(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.TRGOP.ruleID(), new Trg(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.TBSOP.ruleID(), new Tbs(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.DNUM.ruleID(), new DNum(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.XNUM.ruleID(), new XNum(abnfParser));
		abnfParser.setRuleCallback(GeneratorGrammar.RuleNames.BNUM.ruleID(), new BNum(abnfParser));
		
		// translate the input SABNF grammar
		GeneratorSyntax.initSyntax(errors, warnings, catalog);
		if(errors.size() > 0){throw new AssertionError("pre-parsing errors detected");}
		Result result = abnfParser.parse();
		result.displayResult(System.out);
		System.out.println(result.toString());
		if(errors.size() > 0){throw new AssertionError("parsing errors detected");}
		if(warnings.size() > 0){
			System.out.println("catalog warnings:");
			for(String error : warnings){System.out.println(error);}
		}
		
		// prune the redundant opcodes (ALT(1), CAT(1), REP(1,1))
		boolean test = GeneratorSyntax.prune();
		if(!test){throw new AssertionError("error pruning intermediate opcodes");}
		
		// success
		} catch(Exception e){
			if(errors.size() > 0){
				System.out.println("*** java.lang.Exception caught - errors ***");
				for(String error : errors){System.out.println(error);}
			}
			if(warnings.size() > 0){
				System.out.println("*** java.lang.Exception caught - warnings ***");
				for(String error : warnings){System.out.println(error);}
			}
			System.out.println(Utilities.displayException(e));
		}	catch(Error e){
			if(errors.size() > 0){
				System.out.println("*** java.lang.Error caught - errors ***");
				for(String error : errors){System.out.println(error);}
			}
			if(warnings.size() > 0){
				System.out.println("*** java.lang.Error caught - warnings ***");
				for(String error : warnings){System.out.println(error);}
			}
			System.out.println(Utilities.displayError(e));
		}

	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		// blocks to String
		// run parser
		// get parse tree return
		// update state
		
		System.out.println("AbnfParse");
		List<Block> blocks = state.getBlocks();
		
	Iterator<Block> iterator = blocks.iterator();
		while(iterator.hasNext()){
			Block b = iterator.next();
			b.setInlineStyleRanges(removeParseRanges(b.getInlineStyleRanges()));
			List<InlineStyleRange> ranges = new ArrayList<InlineStyleRange>();
			try {
				System.out.println("input text: " + b.getText());
				abnfParser.setInputString(b.getText());
				Result result = abnfParser.parse();
				System.out.println("result: " + result.success() + "\n matchedPhraseLength: " + result.getMatchedPhraseLength());
				if(result.success()){
					System.out.println("full parse succes");
					InlineStyleRange range = new InlineStyleRange();
					range.setOffset(0);
					range.setLength(b.getText().length());
					range.setStyle("success");
					ranges.add(range);
				} else if(result.getState() == false){
					System.out.println("full parse error");
					InlineStyleRange range = new InlineStyleRange();
					range.setOffset(0);
					range.setLength(b.getText().length());
					range.setStyle("error");
					ranges.add(range);
				} else {
					System.out.println("parse partial succes");
					InlineStyleRange range = new InlineStyleRange();
					range.setOffset(0);
					range.setLength(result.getMatchedPhraseLength());
					range.setStyle("success");
					ranges.add(range);
					ranges.addAll(invertRanges(ranges, b.getText().length()));
				}
				b.addInlineStyleRanges(ranges);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return state;
	}
	
	private static void setTraceOptions(Trace trace){trace.enableAllNodes(false);}

}
