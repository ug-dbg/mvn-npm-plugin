package com.github.ug_dbg;

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A mojo with a given log level that can be set using a property parameter.
 */
abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {

	/**
	 * If true, try to redirect the NPM output to a log handler so it integrates nicely into maven output.
	 */
	@Parameter(property = "log.handler", defaultValue = "true")
	private boolean useLogHandler;
	
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

	/**
	 * Should we try to redirect the NPM output to a log handler so it integrates nicely into maven output ?
	 * @return {@link #useLogHandler}
	 */
	protected boolean useLogHandler() {
		return this.useLogHandler;
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
	
	/**
	 * Parse an argument as an arguments String, with respect to quoted ("") elements.
	 * @param argument the argument to parse
	 * @return a list of command line arguments
	 */
	protected static List<String> parseArgument(String argument) {
		// Regular expression hint :
		//[^"]  → token starting with something other than "
		//\S*   → followed by zero or more non-space characters
		// OR
		//".+?" → a "-symbol followed by whatever, until another ".
		Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
		Matcher matcher = pattern.matcher(argument);
		List<String> arguments = new ArrayList<String>();
		while (matcher.find()) {
			arguments.add(matcher.group(1));
		}
		return arguments;
	}
}
