package net.unibld.core.build;


/**
 * An interface to signal the progress of a build process.
 * @author andor
 *
 */
public interface ProgressListener {
	/**
	 * Signals progress of a build process in percentages
	 * @param buildId GUID of the build
	 * @param perc Percentage 0-100
	 * @param taskIdx Index of the task just completed
	 */
	void progress(String buildId,int perc,int taskIdx);
}

