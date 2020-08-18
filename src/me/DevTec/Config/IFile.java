package me.DevTec.Config;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
 
public class IFile {
    private File f;
    private FileWriter w;
    private String[] scf;
    private boolean c;
    
    public IFile(File f) {
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
        setContents(buffer);
    }
    
    public void setContents(StringBuffer neww) {
    	scf=neww.toString().split(System.lineSeparator());
    }
    
    public String[] getContents() {
    	return scf;
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
            for(String sc:scf)
			getWriter().append(sc+System.lineSeparator());
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