package me.DevTec.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;

import me.DevTec.Other.Decompression;
 
public class IFile {
    private File f;
    private FileWriter w;
    private StringBuffer sc;
    private boolean c;
    private final boolean onlyReading;
    public IFile(InputStream f) {
        sc=Decompression.getText(f);
        onlyReading=true;
    }
    public IFile(File f) {
    	onlyReading=false;
        this.f=f;
        c=true;
        StringBuffer buffer = new StringBuffer();
        try {
        Scanner sc = new Scanner(f);
        while (sc.hasNextLine())
            buffer.append(sc.nextLine()+System.lineSeparator());
        sc.close();
        } catch (Exception e) {
        }
        sc=buffer;
    }
    
    public boolean onlyReading() {
    	return onlyReading;
    }
    
    public void setContents(StringBuffer neww) {
    	sc=neww;
    }
    
    public StringBuffer getContents() {
    	return sc;
    }
    
    public File getFile() {
        return f;
    }
    
    public FileWriter getWriter() {
        if(c)open();
        return w;
    }
    
    public void close() {
        c=true;
        try {
        w.close();
        }catch(Exception e) {
        }
    }
    
    public void save() {
    	try {
			getWriter().append(sc);
			getWriter().flush();
		} catch (Exception e) {
		}
    	close();
    }
    
    public void open() {
        c=false;
        f.getParentFile().mkdir();
        if(!f.exists())
        try {
           f.createNewFile();
        }catch (Exception e) {}
        try {
            w=new FileWriter(f);
        }catch(Exception e) {
        }
    }
 
    public String getName() {
        return f.getName();
    }
}