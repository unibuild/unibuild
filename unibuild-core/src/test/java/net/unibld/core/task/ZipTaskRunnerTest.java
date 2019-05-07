package net.unibld.core.task;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.config.TaskContext;
import net.unibld.core.task.impl.util.ZipTask;
import net.unibld.core.task.impl.util.ZipTaskRunner;
import net.unibld.core.test.UnitTestContext;
import net.unibld.core.util.Zip;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class ZipTaskRunnerTest {
	@Autowired
	private ZipTaskRunner zipTaskRunner;
	
	@BeforeClass
	public static void cleanup() {
		File dir = UnitTestContext.getFolder("existing","cp");
		File dir2 = UnitTestContext.getFolder("existing.copy","cp");
		if (dir.exists()) {
	 		try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (dir2.exists()) {
	 		try {
				FileUtils.deleteDirectory(dir2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testZipExistingFiles() throws IOException {
		File dir = UnitTestContext.getFolder("existing","zip/src");
		dir.mkdirs();
		
		File out = UnitTestContext.getFolder("existing","zip/out");
		out.mkdirs();
		
		File unzipped = UnitTestContext.getFolder("existing","zip/unzip");
		unzipped.mkdirs();
		
		
		
		File orig = new File(dir.getAbsolutePath()+"/1.txt");
		FileUtils.writeStringToFile(orig, "test", "UTF-8");
		
		File orig2 = new File(dir.getAbsolutePath()+"/2.txt");
		FileUtils.writeStringToFile(orig2, "test2", "UTF-8");
		
		String outpath = FilenameUtils.concat(out.getAbsolutePath(),"files.zip");
		
		
		executeFiles(new File[]{orig,orig2},outpath,true);
		
		File zipped = new File(outpath);
		
		Assert.assertTrue(zipped.exists()&&zipped.isFile());
		
		Zip.unzip(outpath, unzipped.getAbsolutePath());
		Assert.assertEquals(unzipped.listFiles().length, 2);
	}
	@Test
	public void testZipExistingFolder() throws IOException {
		File dir = UnitTestContext.getFolder("existing","zip/src");
		dir.mkdirs();
		
		File out = UnitTestContext.getFolder("existing","zip/out");
		out.mkdirs();
		
		File unzipped = UnitTestContext.getFolder("existing","zip/unzip");
		unzipped.mkdirs();
		
		
		File orig = new File(dir.getAbsolutePath()+"/1.txt");
		FileUtils.writeStringToFile(orig, "test", "UTF-8");
		
		File orig2 = new File(dir.getAbsolutePath()+"/2.txt");
		FileUtils.writeStringToFile(orig2, "test2", "UTF-8");
		
		String outpath = FilenameUtils.concat(out.getAbsolutePath(),"folder.zip");
		executeDir(dir,outpath,true);
		
		File zipped = new File(outpath);
		
		Assert.assertTrue(zipped.exists()&&zipped.isFile());
		
		Zip.unzip(outpath, unzipped.getAbsolutePath());
		
		Assert.assertEquals(unzipped.listFiles().length, 2);
	}
	
	private void executeDir(File dir,String target,boolean logging) {
		ZipTask t=new ZipTask();
		t.setContext(new TaskContext());
		t.setDir(dir.getAbsolutePath());
		t.setOutput(target);
		
		zipTaskRunner.run(t);
	}
	
	
	private void executeFiles(File[] files,String target,boolean logging) {
		ZipTask t=new ZipTask();
		t.setContext(new TaskContext());
		StringBuilder b=new StringBuilder();
		int i=0;
		for (File f:files) {
			if (i>0) {
				b.append(',');
			}
			b.append(f.getAbsolutePath());
			i++;
		}
		t.setFiles(b.toString());
		t.setOutput(target);
		zipTaskRunner.run(t);
	}
	
}
