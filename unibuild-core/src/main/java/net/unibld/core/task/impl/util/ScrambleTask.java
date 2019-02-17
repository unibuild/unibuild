package net.unibld.core.task.impl.util;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;

@Task(name="scramble",runnerClass=ScrambleTaskRunner.class)
public class ScrambleTask extends BuildTask {
	private String input;
	private String output;
	private String scrambler;
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getScrambler() {
		return scrambler;
	}
	public void setScrambler(String scrambler) {
		this.scrambler = scrambler;
	}
}
