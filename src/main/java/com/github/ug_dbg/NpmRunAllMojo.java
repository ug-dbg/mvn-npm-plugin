package com.github.ug_dbg;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Goal which executes all arguments as npm commands.
 */
@Mojo(name = "exec-all", threadSafe = true, defaultPhase = LifecyclePhase.COMPILE)
public class NpmRunAllMojo extends AbstractMojo {

	/**
	 * An npm command to execute on each arg, such as 'install', 'test', etc. Optional.
	 */
	@Parameter(property = "npm.command")
	private String command;

	/**
	 * The arguments to pass as npm commands.
	 */
	@Parameter(property = "npm.args")
	private String[] args;

	/**
	 * The working directory. Optional. If not specified, basedir will be used.
	 */
	@Parameter(property = "npm.workingDir", defaultValue = "${basedir}")
	private File workingDir;

	/**
	 * The directory that contains npm executable. Optional. If not specified,
	 * we will assume that npm is in the system path.
	 */
	@Parameter(property = "npm.home")
	private File npmHome;

	public void execute() throws MojoExecutionException, MojoFailureException {
		for (String arg : this.args) {
			CommandLine cmd = this.addCommand(this.getNpmCommand());
			for (String parsedArgument : this.parseArgument(arg)) {
				cmd = cmd.addArgument(parsedArgument);
			}

			this.getLog().info("Executing [" + cmd.toString() + "] in [" + this.workingDir.toString() + "]");
			this.execute(cmd);
		}
	}

	private void execute(CommandLine cmdLine) throws MojoFailureException, MojoExecutionException {
		try {
			DefaultExecutor executor = new DefaultExecutor();
			executor.setWorkingDirectory(this.workingDir);
			executor.setStreamHandler(new PumpStreamHandler(
				new LogHandler.StdOut(this.getLog(), this.logLevel(), "npm ERR", "npm WARN", "npm notice"), 
				new LogHandler.StdErr(this.getLog(), this.logLevel(), "npm ERR", "npm WARN", "npm notice"), 
				System.in
			));

			executor.execute(cmdLine);
		} catch (ExecuteException e) {
			throw new MojoFailureException("npm failure", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Error executing NPM", e);
		}
	}

	private CommandLine addCommand(CommandLine cmdLine) {
		return StringUtils.isBlank(this.command) ? cmdLine : cmdLine.addArgument(this.command);
	}

	private CommandLine getNpmCommand() {
		return getCommand("npm", this.npmHome);
	}

	/**
	 * Parse an argument as an arguments String, with respect to quoted ("") elements.
	 * @param argument the argument to parse
	 * @return a list of command line arguments
	 */
	private List<String> parseArgument(String argument) {
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