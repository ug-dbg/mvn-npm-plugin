package com.github.ug_dbg;


import com.github.ug_dbg.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Goal to set the package.json (or any json file) "version" attribute.
 * <br>
 * You can set the output indent factor and characters so the induced modifications are as contained as possible.
 */
@Mojo(name = "version", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class VersionMojo extends AbstractMojo {

	/** Indent characters enum : a space or a tab would be considered empty and the @Parameter set to null. */
	protected enum IndentChars {
		SPACE(" "), TAB("\t");

		private final String value;

		IndentChars(String value) {
			this.value = value;
		}
	}

	/** The working directory. Optional. If not specified, basedir will be used. */
	@Parameter(property = "npm.workingDir", defaultValue = "${basedir}")
	private File workingDir;
	
	/** The npm package.json file name. Optional. If not specified, 'package.json' will be used. */
	@Parameter(property = "npm.packageJsonFile", defaultValue = "package.json")
	private String packageJsonFile;

	/** The package.json target version. */
	@Parameter(property = "npm.packageJsonFileVersion")
	private String packageJsonFileVersion;

	/** The npm package.json indent factor. If not specified, '1' will be used. */
	@Parameter(property = "npm.packageJsonFileIndentFactor", defaultValue = "1")
	private Integer packageJsonFileIndentFactor;

	/** The npm package.json indent characters. SPACE or TAB, if not specified, 'SPACE' will be used. */
	@Parameter(property = "npm.packageJsonFileIndentChars", defaultValue = "SPACE")
	private IndentChars packageJsonFileIndentChars;

	public void execute() {
		this.updateVersion(new File(this.workingDir, this.packageJsonFile), this.packageJsonFileVersion);
	}

	/**
	 * Delete a file and log the deletion status.
	 * Do not throw any exception.
	 * @param packageJson the file to delete.
	 * @param packageJsonFileVersion the target version
	 */
	protected void updateVersion(File packageJson, String packageJsonFileVersion) {
		if (packageJson == null || ! packageJson.exists()) {
			return;
		}
		try {
			Charset utf8 = Charset.forName("UTF-8");

			InputStream fileInput = new FileInputStream(packageJson);
			JSONObject content = new JSONObject(IOUtils.toString(fileInput, utf8));
			fileInput.close();

			content.put("version", packageJsonFileVersion);
			FileOutputStream fileOutput = new FileOutputStream(packageJson, false);
			IOUtils.write(
				content.toString(this.packageJsonFileIndentFactor, this.packageJsonFileIndentChars.value),
				fileOutput,
				utf8
			);
			fileOutput.close();
		} catch (IOException e) {
			this.getLog().error("I/O Error updating version of [" + packageJson + "]", e);
		} catch (RuntimeException e) {
			this.getLog().error("Error updating version of [" + packageJson + "]", e);
		}
	}
}
