package net.unibld.server.web.rest.remote;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.unibld.core.util.Zip;
import net.unibld.server.web.rest.remote.extract.IExtractor;
import net.unibld.server.web.rest.remote.extract.UnzipExtractor;
import net.unibld.server.web.rest.remote.model.BuildRequestDto;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


    public class FileHelper
    {
    	public static final boolean  USE_USER_HOME=false;
        public static final String BASE_DIR="c:\\RemoteBuild";
        private static final SimpleDateFormat DF_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
        private static final boolean USE_DATE_IN_TEMP_DIR = false;
        private static final Logger LOG=LoggerFactory.getLogger(FileHelper.class);
        
        
        private static IExtractor createExtractor()
        {
            return new UnzipExtractor();
        }
        public static String copyToTempDir(BuildRequestDto req) throws IOException
        {
            String tmpDir = createTempDir(req, true);
            String zipFile = FilenameUtils.concat(tmpDir, "uploaded.zip");
            FileUtils.writeByteArrayToFile(new File(zipFile), req.getFile().getBytes());

            return zipFile ;
        }
        public static String extractToTempDir(BuildRequestDto req) throws IOException
        {
            String tmpDir = createTempDir(req,true);
            String zipFile = FilenameUtils.concat(tmpDir, "uploaded.zip");
            FileUtils.writeByteArrayToFile(new File(zipFile),req.getFileContent());


            IExtractor extr = createExtractor();
            String buildDir = USE_DATE_IN_TEMP_DIR? tmpDir:createTempDir(req,false);
            extr.extract(zipFile, buildDir);
            
            return buildDir;
        }

        static String createTempDir(BuildRequestDto req,boolean useDateInTempDir) {
        	return createTempDir(req.getProject(), useDateInTempDir);
        }
        static String createTempDir(String project,boolean useDateInTempDir)
        {
            String tmpDir = getTmpDirPath(project, useDateInTempDir);
            File tmpDirFile=new File(tmpDir);
            if (!tmpDirFile.exists()||!tmpDirFile.isDirectory())
            {
                tmpDirFile.mkdirs();
            }
            else
            {
                if (!USE_DATE_IN_TEMP_DIR)
                {
                    try
                    {
                        FileUtils.deleteDirectory(tmpDirFile);
                        tmpDirFile.mkdirs();

                        LOG.info("Cleaned build dir: {}",tmpDir);
                    }
                    catch (Exception ex)
                    {
                    	LOG.error("Failed to clean build dir: "+tmpDir,ex);
                        throw new RemoteBuildException("Failed to clean build dir: " + tmpDir, ex);
                    }
                    

                }
            }
            return tmpDir;
        }
		public static String getTmpDirPath(String project,
				boolean useDateInTempDir) {
			String dir = createTempBaseDir(project);
            String tmpDir = useDateInTempDir ? FilenameUtils.concat(dir, DF_FORMAT.format(new Date())) : FilenameUtils.concat(dir, "build");
			return tmpDir;
		}

        private static String createTempBaseDir(String project)
        {

            String buildsDir = createBuildsDir();

            String dir = FilenameUtils.concat(buildsDir, project.toLowerCase());
            File f=new File(dir);
            if (!f.exists()||!f.isDirectory())
            {
                f.mkdirs();
            }
            return dir;
        }

        private static String createBuildsDir()
        {
        	File bd = getBaseDir();

            String buildsDir = FilenameUtils.concat(bd.getAbsolutePath(), "builds");
            File bd2=new File(buildsDir);
            if (!bd2.exists()||!bd2.isDirectory())
            {
                bd2.mkdirs();
            }

            return buildsDir;
        }

        static String createSemaphoreDirectory(String project)
        {
            String dir = createTempBaseDir(project);
            String sDir = FilenameUtils.concat(dir, "semaphore");
            File f=new File(sDir);
            if (!f.exists()||!f.isDirectory())
            {
                f.mkdirs();
            }
            return sDir;
        }

       
        public static String extractInnoSetup() throws IOException
        {
        	
        	File bd = getBaseDir();

            //ZipUtils.UnzipStream(new MemoryStream(Properties.Resources.InnoSetup5), BASE_DIR);
            String dir = FilenameUtils.concat(bd.getAbsolutePath(), "InnoSetup5");
            
            File dirFile = new File(dir);
            if (!dirFile.exists()) {

	            InputStream is = FileHelper.class.getResourceAsStream("/bin/InnoSetup5.zip");
	            if (is==null) {
	            	throw new IllegalStateException("/bin/InnoSetup5.zip not found in classpath");
	            }
	            

	            
	            String tmp=FilenameUtils.concat(bd.getAbsolutePath(), "tmp");
	            new File(tmp).mkdirs();
	            
	            
	            String zipFile=FilenameUtils.concat(tmp, "InnoSetup5.zip");
	            byte[] bytes = IOUtils.toByteArray(is);
	            FileUtils.writeByteArrayToFile(new File(zipFile), bytes);
	            
            	dirFile.mkdirs();
				String langs = FilenameUtils.concat(dir, "Languages");
				new File(langs).mkdirs();
            	
				Zip.unzip(zipFile, dir);
            }
			return dir;
        }
		protected static File getBaseDir() {
			String home = System.getProperty("user.home");
			
        	File bd=new File(USE_USER_HOME?FilenameUtils.concat(home, "RemoteBuild"):BASE_DIR);

            if (!bd.exists()||!bd.isDirectory())
            {
                bd.mkdirs();
            }
			return bd;
		}

        public static List<String> getProjectNames()
        {
            String buildsDir=createBuildsDir();
            File di = new File(buildsDir);
            String[] files = di.list();
            List<String> ret = new ArrayList<String>();
            if (files != null && files.length > 0)
            {
                for (String f : files)
                {
                    String path=FilenameUtils.concat(di.getAbsolutePath(), f);
                    File dir=new File(path);
                    if (dir.isDirectory()) {
                    	ret.add(f);
                    }
                }
            }
            return ret;
        }
		public static void extractJres() throws IOException {
			File bd = getBaseDir();
			
			String jre=FilenameUtils.concat(bd.getAbsolutePath(), "jre");
			File jreDir=new File(jre);
			if (!jreDir.exists()||!jreDir.isDirectory()) {
				LOG.info("Extracting JRE-s to: "+jreDir.getAbsolutePath());
				jreDir.mkdirs();
				
				
				InputStream is = FileHelper.class.getResourceAsStream("/bin/jres.zip");
	            if (is==null) {
	            	throw new IllegalStateException("/bin/jre.zip not found in classpath");
	            }
	            

	            
	            String tmp=FilenameUtils.concat(bd.getAbsolutePath(), "tmp");
	            new File(tmp).mkdirs();
	            
	            
	            String zipFile=FilenameUtils.concat(tmp, "jres.zip");
	            byte[] bytes = IOUtils.toByteArray(is);
	            FileUtils.writeByteArrayToFile(new File(zipFile), bytes);
	            
				Zip.unzip(zipFile, jre);
				LOG.info("Successfully extracted JRE-s to: "+jreDir.getAbsolutePath());
				
			}
		}
    }

