package net.unibld.core.util;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.*;


/**
 * Utility class that creates an MD5 hash for directories based on their content
 * @author andor
 *
 */
public class DirectoryHash {
	
	/**
	 * Calculates an MD5 hash of the specified directory
	 * @param dirToHash Directory to hash
	 * @param exclusions File name exclusion list
	 * @return Calculated MD5 hash of the directory
	 */
	public static String calcMD5HashForDir(File dirToHash, List<String> exclusions) {

	    assert (dirToHash.isDirectory());
	    Vector<FileInputStream> fileStreams = new Vector<FileInputStream>();

	    //System.out.println("Found files for hashing:");
	    collectInputStreams(dirToHash, fileStreams, exclusions);

	    SequenceInputStream seqStream = 
	            new SequenceInputStream(fileStreams.elements());

	    try {
	        String md5Hash = DigestUtils.md5Hex(seqStream);
	        seqStream.close();
	        return md5Hash;
	    }
	    catch (IOException e) {
	        throw new RuntimeException("Error reading files to hash in "
	                                   + dirToHash.getAbsolutePath(), e);
	    }

	}

	private static void collectInputStreams(File dir,
	                                 List<FileInputStream> foundStreams,
	                                 List<String> exclusions) {

	    File[] fileList = dir.listFiles();        
	    Arrays.sort(fileList,               // Need in reproducible order
	                new Comparator<File>() {
	                    public int compare(File f1, File f2) {                       
	                        return f1.getName().compareTo(f2.getName());
	                    }
	                });

	    for (File f : fileList) {
	        if (containsExclusions(f, exclusions)) {
	            // Skip it
	        }
	        else if (f.isDirectory() && !containsExclusions(f, exclusions)) {
	            collectInputStreams(f, foundStreams, exclusions);
	        }
	        else {
	            try {
	                //System.out.println("\t" + f.getAbsolutePath());
	                foundStreams.add(new FileInputStream(f));
	            }
	            catch (FileNotFoundException e) {
	                throw new AssertionError(e.getMessage()
	                            + ": file should never not be found!");
	            }
	        }
	    }

	}

	private static boolean containsExclusions(File f, List<String> exclusions) {
		return exclusions!=null && exclusions.contains(f.getName());
	}
}
