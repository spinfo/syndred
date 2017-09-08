package de.uk.spinfo.syndred;

import CP.Ebnf.Ebnf;
import texts.Shared;

public class Parser {

	private int counter;

	private ThreadWord thread;

	public Shared shared;

	public boolean stop = false;

	public boolean parsed = true;

	public Parser() {
		counter = 0;
		shared = new Shared();
		thread = new ThreadWord(shared);
	}

	public void startThread() {
		thread.start();
	}

	class ThreadWord extends Thread {

		private Shared shared;

		public ThreadWord(Shared shared) {
			this.shared = shared;
		}

		private void word() {
			StringBuffer word = new StringBuffer();

			while (true) {
				try {
					char ch = shared.getSym();
					if ((ch != ' ') && (ch != '\r') && (ch != '\n') && (ch != '$'))
						word.append(ch);
					else {
						stop = ch == '$';
						break;
					}
				} catch (Exception e) {
				}
			}
		}

		private void editSyntaxDriven() {
			while (true) {
				counter++;

				if (counter > 100) {
					stop = true;
					break;
				}

				try {
					if (parsed = Ebnf.parse(Ebnf.startsymbol)) {
						System.out.println("parse ok");
						break;
					}
				} catch (Exception e) {
				}

				stop = true;
			}
		}

		public void run() {
			Ebnf.init(shared);
			while (!stop) {
				if (counter > 1000)
					stop = true;
				try {
					editSyntaxDriven();
					sleep(10);
				} catch (Exception e) {
				}

				counter++;
			}
		}
	}
}
