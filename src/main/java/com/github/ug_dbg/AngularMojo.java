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
 * Goal which executes Angular ng command.
 */
@Mojo(name = "exec-ng", threadSafe = true, defaultPhase = LifecyclePhase.COMPILE)
public class AngularMojo extends AbstractMojo {

	/**
	 * The ng command to execute, such as 'build', 'serve', etc. Required.
	 */
	@Parameter(property = "ng.command")
	private String command;

	/**
	 * The arguments to pass to ng command. Optional.
	 */
	@Parameter(property = "ng.args")
	private String[] args;

	/**
	 * The working directory. Optional. If not specified, basedir will be used.
	 */
	@Parameter(property = "ng.workingDir", defaultValue = "${basedir}")
	private File workingDir;

	/**
	 * The directory that contains ng executable. Optional. If not specified,
	 * we will assume that ng is in the system path.
	 */
	@Parameter(property = "ng.home")
	private File ngHome;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		for (String arg : this.args) {
			CommandLine cmd = this.addCommand(this.getNGCommand());
			for (String parsedArgument : parseArgument(arg)) {
				cmd = cmd.addArgument(parsedArgument);
			}
			
			this.getLog().info("Executing [" + cmd.toString() + "] in [" + this.workingDir.toString() + "]");
			this.execute(cmd);
		}
	}
	
	private void execute(CommandLine cmdLine) throws MojoExecutionException, MojoFailureException {
		try {
			DefaultExecutor executor = new DefaultExecutor();
			executor.setWorkingDirectory(this.workingDir);
			executor.setStreamHandler(new PumpStreamHandler(
				new LogHandler.StdOut(this.getLog(), this.logLevel(), "ERROR", "WARNING", "DEBUG"), 
				new LogHandler.StdErr(this.getLog(), this.logLevel(), "ERROR", "WARNING", "DEBUG"),  
				System.in
			));

			executor.execute(cmdLine);
		} catch (ExecuteException e) {
			throw new MojoFailureException("Angular ng failure", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Error executing Angular ng", e);
		}
	}
	
	private CommandLine addCommand(CommandLine cmdLine) {
		return cmdLine.addArgument(this.command);
	}

	private CommandLine getNGCommand() {
		return getCommand("ng", this.ngHome);
	}
}
