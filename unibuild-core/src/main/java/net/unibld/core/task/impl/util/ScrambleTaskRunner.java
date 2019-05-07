package net.unibld.core.task.impl.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.Scrambler;

@Component
public class ScrambleTaskRunner extends BaseTaskRunner<ScrambleTask> {
	private static final Logger LOG=LoggerFactory.getLogger(ScrambleTaskRunner.class);

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "scramble";
	}

	

	@Override
	protected ExecutionResult execute(ScrambleTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (StringUtils.isEmpty(task.getInput())) {
			throw new BuildException("Input file not specified");
		}
		
		File input=new File(task.getInput());
		if (!input.exists()) {
			throw new BuildException("Input file does not exist: "+task.getInput());
		}
		if (StringUtils.isEmpty(task.getOutput())) {
			throw new BuildException("Output file not specified");
		}
		
		byte[] scr=null;
		if (StringUtils.isEmpty(task.getScrambler())) {
			logTask("Scrambling file: {0}-{1}...", task.getInput(),task.getOutput());
		} else {
			logTask("Scrambling file: {0}-{1} (using {2})...", task.getInput(),task.getOutput(),task.getScrambler());
			File scrFile=new File(task.getScrambler());
			if (!scrFile.exists()) {
				throw new BuildException("Scrambler file does not exist: "+task.getScrambler());
			}
			try {
				scr=FileUtils.readFileToByteArray(scrFile);
			} catch (IOException e) {
				LOG.error("Failed to read scramble file: "+task.getScrambler(),e);
				throw new BuildException("Failed to read scramble file: "+task.getScrambler(),e);
			}
		}
		
		byte[] in=null;
		try {
			in=FileUtils.readFileToByteArray(input);
		} catch (IOException e) {
			LOG.error("Failed to read input file: "+task.getInput(),e);
			throw new BuildException("Failed to read input file: "+task.getInput(),e);
		}
		byte[] out = Scrambler.scramble64(in, scr);
		File output=new File(task.getOutput());
		try {
			FileUtils.writeByteArrayToFile(output,out);
		} catch (IOException e) {
			LOG.error("Failed to write output file: "+task.getOutput(),e);
			throw new BuildException("Failed to write output file: "+task.getOutput(),e);
		}
		return ExecutionResult.buildSuccess();
	}

	
	
}
