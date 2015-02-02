package edu.csupomona.cs585.ibox.sync;
import java.io.IOException;
import java.util.ArrayList;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.Drive.Files.Update;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;




import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;

public class GoogleDriveFileSyncManagerTest 
{
	
	GoogleDriveFileSyncManager mockGoogleDrive;
	Drive mockService;
	File file;
	java.io.File testFile;
	Files currentFiles;
	List currentList;
	FileList fileList;
	java.util.List<File> fileArray;
	Insert mockInsert;
	Update mockUpdate;
	Delete mockDelete;
	
	@Before
	public void setUp() throws Exception 
	{
		MockitoAnnotations.initMocks(this);	
		
		mockService = mock(Drive.class);
		mockGoogleDrive = new GoogleDriveFileSyncManager(mockService);
		
		testFile = mock(java.io.File.class);
		file = new File();
		fileList = new FileList();
		currentFiles = mock(Files.class);
		currentList = mock(List.class);
		fileArray = new ArrayList<File>();
		
		mockInsert = mock(Insert.class);
		mockUpdate = mock(Update.class);
		mockDelete = mock(Delete.class);
	}
	
	@After
	public void tearDown() throws Exception 
	{
		mockService = null;
		mockGoogleDrive = null;
		testFile = null;
		file = null;
		fileList = null;
		currentFiles = null;
		currentList = null;
		fileArray = null;
		mockInsert = null;
		mockUpdate = null;
		mockDelete = null;
	}
	
	public void initalizeFile()
	{
		File testFile = new File();
		testFile.setId("TestFile");
		testFile.setTitle("testfile.txt");
		
		fileArray.add(testFile);
		fileList.setItems(fileArray);
	}
	
	@Test
	public void testAddFile() throws IOException 
	{
		when(mockService.files()).thenReturn(currentFiles);
		when(currentFiles.insert(isA(File.class), isA(AbstractInputStreamContent.class))).thenReturn(mockInsert);
		when(mockInsert.execute()).thenReturn(file);
		
		mockGoogleDrive.addFile(testFile);		
		
		Mockito.verify(mockService, atLeast(0)).files();
	    Mockito.verify(currentFiles, atLeast(0)).insert(isA(File.class));
	    Mockito.verify(mockInsert, atLeast(0)).execute();		
	}

	@Test
	public void testUpdateFile() throws IOException
	{
		initalizeFile();
		when(testFile.getName()).thenReturn("testfile.txt");
		when(mockService.files()).thenReturn(currentFiles);
		when(currentFiles.list()).thenReturn(currentList);
		when(currentList.execute()).thenReturn(fileList);
		
		String fileId = mockGoogleDrive.getFileId("testfile.txt");
		when(currentFiles.update(eq(fileId),isA(File.class), 
				isA(AbstractInputStreamContent.class))).thenReturn(mockUpdate);
		when(mockUpdate.execute()).thenReturn(file);
		
		mockGoogleDrive.updateFile(testFile);
		verify(mockService, atLeast(0)).files();
	    verify(currentFiles, atLeast(0)).update(eq(fileId), Mockito.isA(File.class));
	    verify(mockUpdate, atLeast(0)).execute();
	}
		
	@Test
	public void testDeleteFile() throws IOException 
	{
		initalizeFile();
		when(testFile.getName()).thenReturn("testfile.txt");
		when(mockService.files()).thenReturn(currentFiles);
		when(currentFiles.list()).thenReturn(currentList);
		when(currentList.execute()).thenReturn(fileList);
		
		String fileId = mockGoogleDrive.getFileId("testfile.txt");
		
		when(currentFiles.delete(eq(fileId))).thenReturn(mockDelete);
		when(mockDelete.execute()).thenReturn(null);
		
		mockGoogleDrive.deleteFile(testFile);
		verify(mockService, atLeastOnce()).files();
	    verify(currentFiles, atLeast(0)).delete(eq(fileId));
	    verify(mockDelete, atLeastOnce()).execute();		
	}
}
