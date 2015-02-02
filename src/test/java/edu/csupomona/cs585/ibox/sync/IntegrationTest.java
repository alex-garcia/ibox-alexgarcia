package edu.csupomona.cs585.ibox.sync;
import static org.junit.Assert.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class IntegrationTest {

	Drive service;
	GoogleDriveFileSyncManager googleDriveFSM;


	@Before
	public void setUp() throws Exception 
	{
		initialGoogleDriveServices();
		googleDriveFSM = new GoogleDriveFileSyncManager(service);
	}

	@After
	public void tearDown() throws Exception 
	{
		googleDriveFSM = null;
	}


	public void initialGoogleDriveServices() throws IOException 
	{
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		try
		{
			GoogleCredential credential = new  GoogleCredential.Builder()
			.setTransport(httpTransport)
			.setJsonFactory(jsonFactory)
			.setServiceAccountId("115578451106-bac25ddb068jcoa119lnurbvcjrlbgs8.apps.googleusercontent.com")
			.setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
			.setServiceAccountPrivateKeyFromP12File(new java.io.File("CS 585-96645b15f435.p12"))
			.build();

			service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("ibox").build();

		}
		catch(GeneralSecurityException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testIntegration() throws IOException 
	{
		java.io.File testFile = new java.io.File("testfile.txt");
		testFile.createNewFile();
		googleDriveFSM.addFile(testFile);
		assertNotNull(googleDriveFSM.getFileId(testFile.getName()));
		googleDriveFSM.updateFile(testFile);
		assertNotNull(googleDriveFSM.getFileId(testFile.getName()));
		googleDriveFSM.deleteFile(testFile);
		assertNull(googleDriveFSM.getFileId(testFile.getName()));
	}
}