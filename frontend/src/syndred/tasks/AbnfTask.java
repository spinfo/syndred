package syndred.tasks;

public class AbnfTask extends Task {

	public AbnfTask(String gramma) {
		super(gramma);
	}

	@Override
	public void run() {
		System.out.println("abnf");
	}

}
