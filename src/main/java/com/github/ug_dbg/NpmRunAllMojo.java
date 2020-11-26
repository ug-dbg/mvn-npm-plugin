package com.github.ug_dbg;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

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
			for (String parsedArgument : parseArgument(arg)) {
				cmd = cmd.addArgument(parsedArgument);
			}

			this.getLog().info("Executing [" + cmd.toString() + "] in [" + this.workingDir.toString() + "]");
			this.execute(cmd);
		}
	}

	private void execute(CommandLine cmdLine) throws MojoFailureException, MojoExecutionException {
		NpmMojo.execute(cmdLine, this.workingDir, this.useLogHandler(), this.getLog(), this.logLevel());
	}

	private CommandLine addCommand(CommandLine cmdLine) {
		return StringUtils.isBlank(this.command) ? cmdLine : cmdLine.addArgument(this.command);
	}

	private CommandLine getNpmCommand() {
		return getCommand("npm", this.npmHome);
	}
}