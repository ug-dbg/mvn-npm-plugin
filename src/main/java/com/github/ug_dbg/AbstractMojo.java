package com.github.ug_dbg;

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.logging.Level;

/**
 * A mojo with a given log level that can be set using a property parameter.
 */
abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {
	
	/** 
	 * Log level of the Mojo logger. 
	 * Default is INFO. 
	 * @see java.util.logging.Level#parse 
	 */
	@Parameter(property = "log.level", defaultValue = "INFO")
	private String logLevel;
	
	Level logLevel() {
		try {
			return Level.parse(this.logLevel);
		} catch (RuntimeException e) {
			this.getLog().warn("Error reading log level [" + this.logLevel + "]. Using INFO.");
		}
		return Level.INFO;
	}
	
	protected static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	protected static CommandLine getCommand(String name, File home) {
		String cmdValue = home == null ? name : new File(home, name).getAbsolutePath();

		if (isWindows()) {
			cmdValue = cmdValue + ".cmd";
			CommandLine cmd = new CommandLine("cmd");
			cmd.addArgument("/c");
			cmd.addArgument(cmdValue);
			return cmd;
		} else {
			return new CommandLine(cmdValue);
		}
	}
}
