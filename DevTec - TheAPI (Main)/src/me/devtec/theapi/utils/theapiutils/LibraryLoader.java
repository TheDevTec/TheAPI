package me.devtec.theapi.utils.theapiutils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import me.devtec.theapi.utils.reflections.Ref;

public class LibraryLoader {
	
	static ClassLoader classes;

	public void load(ClassLoader classLoader) {
		classes=classLoader;
	}
	static List<String> loaded = new ArrayList<>();
	
	public static void load(String path) {
		if(loaded.contains(path))return;
		loaded.add(path);
		try {
			File f = new File("plugins/TheAPI/libraries/"+path+".jar");
			if(!f.exists()) {
				f.getParentFile().mkdirs();
				//DOWNLOAD REQUIRED LIBRARY
			    f.createNewFile();
			    URL url = new URL("https://github.com/TheDevTec/TheAPI/blob/master/"+path+".jar?raw=true");
			    OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			    InputStream in = url.openConnection().getInputStream();
			    byte[] buf = new byte[1024];
			    int r;
			    while ((r = in.read(buf)) != -1)
			        out.write(buf, 0, r);
			    if (in != null)
			        in.close();
			    if (out != null)
			        out.close();
			}
			SuperJar jar = new SuperJar((File)Ref.get(classes, "file"));
			jar.file.add(new JarFile(f));
			Ref.set(classes, "manifest", jar);
			Ref.set(classes, "jar", jar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class SuperJar extends JarFile {
		public List<JarFile> file = new ArrayList<>();
		public SuperJar(File file) throws IOException {
			super(file);
		}
		
		public JarEntry getJarEntry(String name) {
			JarEntry find = super.getJarEntry(name);
			if(find==null) {
				for(JarFile search : file) {
					find=search.getJarEntry(name);
					if(find!=null)return find;
				}
			}
			return null;
		}
		
		public InputStream getInputStream(ZipEntry name) throws IOException {
			InputStream find = super.getInputStream(name);
			if(find==null) {
				for(JarFile search : file) {
					find=search.getInputStream(name);
					if(find!=null)return find;
				}
			}
			return null;
		}
		
		public void close() throws IOException {
			super.close();
			for(JarFile f : file)f.close();
			file.clear();
		}
		
	}

}
