package com.github.ug_dbg;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Goal to clean an npm workspace.
 * Delete : 
 * <ul>
 *     <li>dist</li>
 *     <li>node-modules</li>
 *     <li>package-lock</li>
 * </ul>
 */
@Mojo(name = "clean", threadSafe = true, defaultPhase = LifecyclePhase.CLEAN)
public class CleanMojo extends AbstractMojo {
	
	/** The working directory. Optional. If not specified, basedir will be used. */
	@Parameter(property = "npm.workingDir", defaultValue = "${basedir}")
	private File workingDir;
	
	/**  The npm dist directory name. Optional. If not specified, 'dist' will be used. */
	@Parameter(property = "npm.distDir", defaultValue = "dist")
	private String distDir;

	/** The node modules dir. Optional. If not specified, 'node_modules' will be used. */
	@Parameter(property = "npm.nodeModulesDir", defaultValue = "node_modules")
	private String nodeModulesDir;
	
	/** The package-lock. Optional. If not specified, 'package-lock.json' will be used. */
	@Parameter(property = "npm.packageLockFile", defaultValue = "package-lock.json")
	private String packageLockFile;
	
	/** If 'true', delete the node-modules directory. Default is 'false'. */
	@Parameter(property = "npm.deleteNodeModules", defaultValue = "false")
	private boolean deleteNodeModules;

	/** If 'true', delete the package-lock file. Default is 'false'. */
	@Parameter(property = "npm.deletePackageLock", defaultValue = "false")
	private boolean deletePackageLock;
	
	public void execute() {
		this.delete(new File(this.workingDir, this.distDir));
		
		if (this.deleteNodeModules && this.nodeModulesDir != null) {
			this.delete(new File(this.workingDir, this.nodeModulesDir));
		}
		
		if (this.deletePackageLock && this.packageLockFile != null) {
			this.delete(new File(this.workingDir, this.packageLockFile));
		}
	}

	/**
	 * Delete a file and log the deletion status.
	 * Do not throw any exception.
	 * @param file the file to delete.
	 */
	private void delete(File file) {
		if (file == null || ! file.exists()) {
			return;
		}
		try {
			if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
				this.getLog().info("Deleted directory [" + file.toString() + "] : [OK]");
			} else {
				boolean status = file.delete();
				this.getLog().info("Delete [" + file.toString() + "] : [" + (status ? "OK" : "KO")  + "]");
			}
		} catch (IOException e) {
			this.getLog().error("I/O Error deleting [" + file.toString() + "]", e);
		} catch (RuntimeException e) {
			this.getLog().error("Error deleting [" + file.toString() + "]", e);
		}
	}
}
