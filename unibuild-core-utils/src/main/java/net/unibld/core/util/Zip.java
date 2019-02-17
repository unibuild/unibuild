package net.unibld.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * A utility class for zipping and unzipping files.
 * 
 * @author andor
 *
 */
public class Zip {
	private static final boolean TRACE = false;
	static final int BUFFER = 2048;

	/**
	 * Zips the specified source folder to the given destination path
	 * 
	 * @param sourceFolder
	 *            Source folder to zip
	 * @param destPath
	 *            Destination path
	 */
	public static void zip(String sourceFolder, String destPath) {
		zip(sourceFolder, destPath, null);
	}

	/**
	 * Zips the specified source folder to the given destination path
	 * 
	 * @param sourceFolder
	 *            Source folder to zip
	 * @param destPath
	 *            Destination path
	 * @param level Compression level (null for the default one)
	 */
	public static void zip(String sourceFolder, String destPath, Integer level) {
		ZipOutputStream out=null;
		try {
			FileOutputStream dest = new FileOutputStream(destPath);
			out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			if (level != null) {
				out.setLevel(level);
			}
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory
			addFilesOfFolder(sourceFolder, "", out, data);
			out.close();
		} catch (Exception e) {
			System.err.println("Failed to create zip: " + destPath);
			e.printStackTrace();

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.err.println("Failed to close zip output stream");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Zips the specified files to the given destination zip file path
	 * @param files Files to zip
	 * @param destPath Destination zip file path
	 * @param level Compression level (null for the default one)
	 */
	public static void zip(File[] files, String destPath, Integer level) {
		ZipOutputStream out = null;
		try {
			FileOutputStream dest = new FileOutputStream(destPath);
			out = new ZipOutputStream(new BufferedOutputStream(dest));
			if (level != null) {
				out.setLevel(level);
			}
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory

			BufferedInputStream origin = null;
			for (File f : files) {
				try {
					FileInputStream fi = new FileInputStream(f);
					origin = new BufferedInputStream(fi, BUFFER);

					ZipEntry entry = new ZipEntry(f.getName());

					if (TRACE) {
						System.out.println("Adding: " + f.getAbsolutePath()
								+ " to " + f.getName());
					}

					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER)) != -1) {
						out.write(data, 0, count);
					}
				} catch (Exception ex) {
					System.err.println("Failed to add file to zip: "
							+ f.getAbsolutePath());
					ex.printStackTrace();

				} finally {
					if (origin != null) {
						origin.close();
					}
				}
			}

		} catch (Exception e) {
			System.err.println("Failed to create zip: " + destPath);
			e.printStackTrace();

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.err.println("Failed to close zip output stream");
					e.printStackTrace();
				}
			}
		}
	}

	private static void addFilesOfFolder(String sourceFolder, String basedir,
			ZipOutputStream out, byte[] data) throws FileNotFoundException,
			IOException {
		BufferedInputStream origin;
		File f = new File(sourceFolder + "/" + basedir);
		String files[] = f.list();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String filepath = sourceFolder + "/" + basedir + files[i];

				File file = new File(filepath);
				if (file.isDirectory()) {
					addFilesOfFolder(sourceFolder, basedir + files[i] + "/",
							out, data);
				} else {

					FileInputStream fi = new FileInputStream(filepath);
					origin = new BufferedInputStream(fi, BUFFER);
					String target = basedir + files[i];
					ZipEntry entry = new ZipEntry(target);

					if (TRACE) {
						System.out.println("Adding: " + filepath + " to "
								+ target);
					}

					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER)) != -1) {
						out.write(data, 0, count);
					}

					origin.close();
				}
			}
		} else {
			System.out.println("File list was null in " + f.getAbsolutePath());
		}
	}

	/**
	 * Unzips the specified zip file to the given destination path
	 * 
	 * @param zipFilePath
	 *            Path of the zip file to unzip
	 * @param destPath
	 *            Destination path to unzip to
	 */
	public static void unzip(String zipFilePath, String destPath) {
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		ZipFile zipfile = null;
		try {

			ZipEntry entry;
			zipfile = new ZipFile(zipFilePath);
			Enumeration<? extends ZipEntry> e = zipfile.entries();
			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();

				if (!entry.isDirectory()) {
					is = new BufferedInputStream(zipfile.getInputStream(entry));
					int count;
					byte data[] = new byte[BUFFER];
					String filename = entry.getName();
					if (filename.contains("/")) {
						String dirPath = destPath + "/"
								+ filename.substring(0, filename.lastIndexOf("/"));
						File dir = new File(dirPath);
						dir.mkdirs();
						if (TRACE) {
							System.out.println("Creating dir: " + dirPath);
						}
					}
	
					if (TRACE) {
						System.out.println("Extracting: " + entry + " to "
								+ destPath);
					}
	
					FileOutputStream fos = new FileOutputStream(destPath + "/"
							+ filename);
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
					is.close();
					dest = null;
					is = null;
				}
			}

		} catch (Exception e) {
			System.err.println("Failed to extract from zip: " + zipFilePath);
			e.printStackTrace();
		} finally {
			if (dest != null) {
				try {
					dest.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Compresses a string using GZIP and the specified charset
	 * @param string String to compress
	 * @param charset Charset
	 * @return Compressed bytes
	 * @throws IOException If an I/O error occurs
	 */
	public static byte[] compress(String string, String charset)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes(charset));
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}
	/**
	 * Decompresses a byte array using GZIP and the specified charset to a string
	 * @param compressed Byte array to decompress
	 * @param charset Charset
	 * @return Decompressed string
	 * @throws IOException If an I/O error occurs
	 */
	public static String decompress(byte[] compressed, String charset)
			throws IOException {
		final int BUFFER_SIZE = 32;
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			string.append(new String(data, 0, bytesRead, charset));
		}
		gis.close();
		is.close();
		return string.toString();
	}
}
