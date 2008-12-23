package com.jakeapp.jake.fss;

import com.jakeapp.jake.fss.exceptions.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link IFSService}
 * @author johannes
 * @see IFSService
 */
public class FSService implements IFSService, IModificationListener {
	
	private String rootPath = null;
	
	private FileHashCalculator hasher = null;
	
	private FolderWatcher fw = null;

	private FileLauncher launcher = null;
	
	List<IProjectModificationListener> modificationListener;
	
	public FSService() throws NoSuchAlgorithmException{
		hasher = new FileHashCalculator();
		launcher = new FileLauncher();
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	public void unsetRootPath(){
		if(fw != null) {
			fw.cancel();
			fw.removeListener(this);
		}
		modificationListener = null;
	}
	public void setRootPath(String path) throws FileNotFoundException, NotADirectoryException {
		unsetRootPath();
		File f = new File(path);
		if(!f.exists()) 
			throw new FileNotFoundException();
		if(!f.isDirectory())
			throw new NotADirectoryException();
		rootPath = path;
		
		try {
			fw = new FolderWatcher(new File(this.rootPath), 700);
		} catch (NoSuchAlgorithmException e) {
			/* won't happen as we use the same algorithm here and it loaded. */
		}
		fw.initialRun();
		fw.addListener(this);
		fw.run();
	}
	
	public void addModificationListener(IProjectModificationListener l) {
		if(modificationListener == null){
			modificationListener = new ArrayList<IProjectModificationListener>();
		}
		modificationListener.add(l);
	}
	
	public void removeModificationListener(IProjectModificationListener l) {
		if(modificationListener != null)
			modificationListener.remove(l);
	}
	
	public Boolean fileExists(String relpath) throws InvalidFilenameException {
		File f = new File(getFullpath(relpath));
		return f.exists() && f.isFile();
	}

	public List<String> listFolder(String relpath) throws InvalidFilenameException {
		while(relpath.startsWith("/")){
			relpath = relpath.substring(1);
		}
		File f = new File(getFullpath("/" + relpath));
		String[] flist = f.list();
		List<String> list = new ArrayList<String>();
		for(String file : flist){
			if(relpath != "")
				file = relpath + '/' + file;
			
			if(isValidRelpath(file))
				list.add(file);
		}
		return list;
	}

	public List<String> recursiveListFiles() throws InvalidFilenameException, IOException {
		List<String> list = new ArrayList<String>();
		List<String> dirlist = listFolder("");
		for(int i = 0;i<dirlist.size(); i++){
			String f = dirlist.get(i);
			if(folderExists(f)){
				dirlist.addAll(listFolder(f));
			}else if(fileExists(f)){
				list.add(f);
			}
		}
		return list;
	}

	public byte[] readFile(String relpath) throws InvalidFilenameException, 
			NotAFileException, 
			FileNotFoundException, NotAReadableFileException{
		
		String filename = getFullpath(relpath);
		File f = new File(filename);
		if(!f.exists())
			throw new FileNotFoundException();
		if(!f.isFile())
			throw new NotAFileException();
		if(f.length() > Integer.MAX_VALUE)
			throw new FileTooLargeException();

		FileInputStream fr = null;
		try{
			fr = new FileInputStream(filename);
		}catch(FileNotFoundException e){ 
			/* This is thrown if permissions wrong. we already know the file 
			 * exists. */
			throw new NotAReadableFileException();
		}
		
		int len = (int)f.length();
		byte[] buf = new byte[len];
		int n;
		try{
			n = fr.read(buf, 0, len);
		}catch (IOException e) {
			throw new NotAReadableFileException();
		}
		if(len > n) 
			throw new NotAReadableFileException();
		return buf;
	}

	public String getFullpath(String relpath) throws InvalidFilenameException {
		if(getRootPath()==null)
			return null;
		if(!isValidRelpath(relpath))
			throw new InvalidFilenameException("File "+relpath + " is not a valid filename!");
		File f = new File(joinPath(getRootPath(), relpath));
		return f.getAbsolutePath();
	}

	public String joinPath(String rootPath, String relpath) {
		if('/'!=File.separatorChar)
			relpath = relpath.replace('/', File.separatorChar);
		String p = rootPath + File.separator + relpath;
		if(File.separatorChar == '\\'){
			p = p.replaceAll("\\\\\\\\", "\\\\");
		}else{
			p = p.replaceAll(File.separator + File.separator, File.separator);
		}
		return p;
	}

	public void writeFile(String relpath, byte[] content)
		throws InvalidFilenameException, IOException, FileTooLargeException,
			NotAFileException, CreatingSubDirectoriesFailedException
	{
		String filename = getFullpath(relpath);
		File f = new File(filename);
		
		if(f.exists() && !f.isFile())
			throw new NotAFileException();
		if(content.length > Integer.MAX_VALUE)
			throw new FileTooLargeException();
		if(f.getParentFile().exists()){
			if(!f.getParentFile().isDirectory())
				throw new CreatingSubDirectoriesFailedException();
		}else{
			if(!f.getParentFile().mkdirs())
				throw new CreatingSubDirectoriesFailedException();
		}
		
		FileOutputStream fr = null;
		fr = new FileOutputStream(filename);
		fr.write(content);
		fr.close();
	}

	public Boolean folderExists(String relpath)
			throws InvalidFilenameException, IOException {
		File f = new File(getFullpath(relpath));
		return f.exists() && f.isDirectory();
	}

	public String getTempDir() throws IOException {
		String tempdir;
		File f = File.createTempFile("jakefss", "testfile");
		tempdir = f.getParentFile().getAbsolutePath();
		f.delete();
		return tempdir;
	}

	public String getTempFile() throws IOException {
		File f = File.createTempFile("jake", "");
		return f.getAbsolutePath();
	}

	public Boolean isValidRelpath(String relpath) {
		String regex = "[A-Z a-z0-9\\-+_./\\(\\)]+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(relpath);
		
		if(!(m.find() && m.start() == 0 && m.end() == relpath.length())){
			return false;
		}
		if (relpath.contains("/../") || 
			relpath.startsWith("../") || 
			relpath.endsWith("/..") ||
			relpath.equals("..")
		){
			return false;
		}
		return true;
	}
	public boolean deleteFile(String relpath) 
		throws InvalidFilenameException, FileNotFoundException, NotAFileException
	{
		File f = new File(getFullpath(relpath));
		if(!f.exists())
			throw new FileNotFoundException();
		if(!f.isFile())
			throw new NotAFileException();
		if(!f.delete())
			return false;
		
		/* TODO: Check if this is a infinite loop on a empty drive on windows*/
		do{
			f = f.getParentFile();
		}while(f.isDirectory() && f.getAbsolutePath().startsWith(getRootPath()) 
			&& f.list().length>0 && f.delete());
		
		return true;
	}

	public void launchFile(String relpath) 
		throws InvalidFilenameException, LaunchException 
	{
		launcher.launchFile(new File(getFullpath(relpath)));
	}

	public long getFileSize(String relpath)
		throws InvalidFilenameException, FileNotFoundException, NotAFileException 
	{
		String filename = getFullpath(relpath);
		File f = new File(filename);
		if(!f.exists())
			throw new FileNotFoundException();
		if(!f.isFile())
			throw new NotAFileException();
		return f.length();
	}

	public String calculateHash(byte[] bytes) {
		return hasher.calculateHash(bytes);
	}

	public String calculateHashOverFile(String relpath) throws InvalidFilenameException, NotAReadableFileException, FileNotFoundException {
		return hasher.calculateHash(readFile(relpath));
	}

	public int getHashLength() {
		return hasher.getHashLength();
	}

	public long getLastModified(String relpath) throws InvalidFilenameException, NotAFileException {
		if(!fileExists(relpath))
			throw new NotAFileException();
		File f = new File(getFullpath(relpath));
		return f.lastModified();
	}

	public void fileModified(File f, ModifyActions action) {
		if(rootPath == null)
			return;
		
		String relpath = f.getAbsolutePath().replace(rootPath + File.separator, "")
			.replace(File.separatorChar, '/');
		if(modificationListener!=null) {
			for(IProjectModificationListener l : modificationListener){
				l.fileModified(relpath, action);
			}
		}
	}


}