package edu.ccrm.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Singleton configuration holder for application-wide settings.
 * Uses eager initialization which is thread-safe in Java.
 */
public final class AppConfig {
	private static final AppConfig INSTANCE = new AppConfig();

	private Path dataRootDirectory;

	private AppConfig() {
		// Prevent external instantiation
	}

	public static AppConfig getInstance() {
		return INSTANCE;
	}

	/**
	 * Initialize configuration and ensure required directories exist.
	 */
	public void initialize() {
		String workingDirectory = System.getProperty("user.dir");
		Path projectRoot = Paths.get(workingDirectory);
		Path dataDir = projectRoot.resolve("data");
		this.dataRootDirectory = dataDir;
		try {
			if (Files.notExists(dataDir)) {
				Files.createDirectories(dataDir);
			}
		} catch (Exception exception) {
			throw new RuntimeException("Failed to initialize AppConfig data directory", exception);
		}
	}

	public Path getDataRootDirectory() {
		return dataRootDirectory;
	}

	@Override
	public String toString() {
		return "AppConfig{" +
				"dataRootDirectory=" + dataRootDirectory +
				'}';
	}
}


