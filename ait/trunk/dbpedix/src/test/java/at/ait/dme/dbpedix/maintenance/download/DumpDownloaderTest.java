package at.ait.dme.dbpedix.maintenance.download;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import at.ait.dme.dbpedix.config.Config;

public class DumpDownloaderTest {
	
	@Test
	public void testDownload() throws IOException {
		// Download
		DumpDownloader downloader = new DumpDownloader();
		downloader.downloadFile(DumpDownloader.getGeoCoordinatesURL("en"));
		File archive = new File(Config.getInstance().getDumpDirectory(), "geo_coordinates_en.nt.bz2");
		Assert.assertTrue(archive.exists());
		Assert.assertTrue(archive.isFile());
		Assert.assertTrue(archive.length() > 0);
		
		// Uncompress
		downloader.uncompressFile(archive);
		File uncompressed = new File(Config.getInstance().getDumpDirectory(), "geo_coordinates_en.nt");
		Assert.assertTrue(uncompressed.exists());
		Assert.assertTrue(uncompressed.isFile());
		Assert.assertTrue(uncompressed.length() > 0);
	}
	
}
