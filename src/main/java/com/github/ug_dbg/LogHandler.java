package com.github.ug_dbg;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.util.logging.Level;

/**
 * A handler to write what is read from an output stream (e.g. std.out or std.err) into a logger. 
 */
abstract class LogHandler extends LogOutputStream {
	final Log logger;

	private String errPrefix;
	private String warnPrefix;
	private String debugPrefix;
	
	LogHandler(
		Log logger,
		Level logLevel,
		String errPrefix,
		String warnPrefix,
		String debugPrefix) {
		super(logLevel.intValue());
		this.logger = logger;
		this.errPrefix = errPrefix;
		this.warnPrefix = warnPrefix;
		this.debugPrefix = debugPrefix;
	}

	@Override
	protected void processLine(String line, int logLevel) {
		if (this.isDebug(line)) {
			this.logger.debug(line);
		} else if (this.isWarn(line)) {
			this.logger.warn(line);
		} else if (this.isError(line)) {
			this.logger.error(line);
		} else {
			this.logDefault(line);
		}
	}

	/**
	 * Implement to chose the default log level for an input line.
	 * @param line the line to log, that has not been identified as ERROR/WARN/DEBUG.
	 */
	abstract void logDefault(String line);
	
	boolean isError(String line) {
		return StringUtils.startsWithIgnoreCase(line, this.errPrefix);
	}

	boolean isWarn(String line) {
		return StringUtils.startsWithIgnoreCase(StringUtils.trim(line), this.warnPrefix);
	}

	boolean isDebug(String line) {
		return StringUtils.startsWithIgnoreCase(StringUtils.trim(line), this.debugPrefix);
	}

	/**
	 * Implementation that logs default line to 'warn' : that kinda suits the std.err output.
	 */
	static class StdErr extends LogHandler {
		StdErr(
			Log logger,
			Level logLevel,
			String errPrefix,
			String warnPrefix,
			String debugPrefix) {
			super(logger, logLevel, errPrefix, warnPrefix, debugPrefix);
		}
	
		void logDefault(String line) {
			this.logger.warn(line);
		}
	}

	/**
	 * Implementation that logs default line to 'info' : that kinda suits the std.out output.
	 */
	static class StdOut extends LogHandler {
			StdOut(
			Log logger,
			Level logLevel,
			String errPrefix,
			String warnPrefix,
			String debugPrefix) {
			
			super(logger, logLevel, errPrefix, warnPrefix, debugPrefix);
		}
	
		void logDefault(String line) {
			this.logger.info(line);
		}
	}
}
