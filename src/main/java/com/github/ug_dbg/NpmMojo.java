package com.github.ug_dbg;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Goal which executes npm.
 */
@Mojo(name = "exec", threadSafe = true, defaultPhase = LifecyclePhase.COMPILE)
public class NpmMojo extends AbstractMojo {

	/**
	 * The npm command to execute, such as 'install', 'test', etc. Required.
	 */
	@Parameter(property = "npm.command")
	private String command;

	/**
	 * The arguments to pass to npm command. Optional.
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
		CommandLine cmd = this.addArguments(this.addCommand(this.getNpmCommand()));
		this.getLog().info("Executing [" + cmd.toString() + "] in [" + this.workingDir.toString() + "]");
		this.execute(cmd);
	}

	protected void execute(CommandLine cmdLine) throws MojoFailureException, MojoExecutionException {
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

	protected CommandLine addCommand(CommandLine cmdLine) {
		return cmdLine.addArgument(this.command);
	}

	protected CommandLine addArguments(CommandLine cmdLine) {
		return cmdLine.addArguments(this.args);
	}

	protected CommandLine getNpmCommand() {
		return getCommand("npm", this.npmHome);
	}
}