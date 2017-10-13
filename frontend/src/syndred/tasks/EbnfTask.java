package syndred.tasks;

import CP.Ebnf.Ebnf;
import texts.Shared;

public class EbnfTask extends Task {

	public Shared shared = new Shared();

	public EbnfTask(String gramma) {
		super(gramma);
	}

	@Override
	public void run() {
		Ebnf.init(shared);
		Ebnf.parse(Ebnf.startsymbol);
	}

}
