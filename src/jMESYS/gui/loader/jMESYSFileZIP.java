package jMESYS.gui.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class jMESYSFileZIP {

	public static String checkZIP (String name) {
		String cad=name;
		
		if (cad.toLowerCase().endsWith(".zip")) {
			System.out.println("Fichero ZIP");
			try {
			// uncompress zip file
			FileInputStream input = new FileInputStream(cad);
			//InputStream input = snap.getInputStream();
	        ZipInputStream zis = new ZipInputStream(input);
	        //get the zipped file list entry
	        ZipEntry ze = zis.getNextEntry();
	        System.out.println(ze.getName());			        	
	        
			String tipo = ze.getName().substring(ze.getName().lastIndexOf(".") + 1, ze.getName().length());
	        tipo = tipo.toLowerCase();
	        
	        System.out.println("Tipo: "+tipo);
	        
	        // puajjjjj
	        FileOutputStream output = null;
	        byte[] buffer = new byte[2048];
	        try
	        {
	        	
	            output = new FileOutputStream("/tmp."+tipo);
	            int len = 0;
	            while ((len = zis.read(buffer)) > 0)
	            {
	                output.write(buffer, 0, len);
	            }
	            cad = "/tmp."+tipo;
	        }
	        finally
	        {
	            // we must always close the output file
	            if(output!=null) output.close();
	        }
	        
	        output=null;
	        
			
			
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		
		return cad;
	}
	
	public static InputStream checkZIP (InputStream input) {
		System.out.println("Fichero ZIP");
		String cad = "";
		InputStream zipInput = null;
		
		try {
		// uncompress zip file
		//FileInputStream input = new FileInputStream(cad);
		//InputStream input = snap.getInputStream();
        ZipInputStream zis = new ZipInputStream(input);
        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();
        System.out.println(ze.getName());			        	
        
		String tipo = ze.getName().substring(ze.getName().lastIndexOf(".") + 1, ze.getName().length());
        tipo = tipo.toLowerCase();
        
        System.out.println("Tipo: "+tipo);
        
        // puajjjjj
        FileOutputStream output = null;
        byte[] buffer = new byte[2048];
        try
        {
        	
            output = new FileOutputStream("/tmp."+tipo);
            int len = 0;
            while ((len = zis.read(buffer)) > 0)
            {
                output.write(buffer, 0, len);
            }
            cad = "/tmp."+tipo;
        }
        finally
        {
            // we must always close the output file
            if(output!=null) output.close();
        }
        
        output=null;
        
		zipInput = new FileInputStream(cad);
		
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		return zipInput;
	}
	
	
	
}
