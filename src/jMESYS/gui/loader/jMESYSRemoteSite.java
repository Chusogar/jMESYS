package jMESYS.gui.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jMESYS.files.RemoteFile;

public abstract class jMESYSRemoteSite {
	
	public abstract String getRemoteAddress();
	public abstract void setRemoteAddress(String addr);
	public abstract String getIniTag();
	public abstract String getEndTag();
	
	public Vector readRemotePage (String address, String initag, String endtag) throws Exception {
		/*DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document document = docBuilder.parse(new URL(address).openStream());
		NodeList elem = document.getElementsByTagName(tag);
	    int numElem = elem.getLength();
	    
	    for (int i=0 ; i<numElem ; i++){
	    	Element currentElem = (Element) elem.item(i);
	    	System.out.println(currentElem.getNodeValue());
	    }*/
		
		Vector v=new Vector();
        
		//StringBuilder contents = new StringBuilder(2048);
        BufferedReader br = null;

        try
        {
            URL url = new URL(address);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = "";
            while (line != null)
            {
                line = br.readLine();
                //contents.append(line);
                //System.out.println(line);
                
                // buscamos el trozo
                String strIni = "";
                if (line != null) {
	                if (line.indexOf( getIniTag() ) != -1){
	                	line = line.replace(getIniTag(), "");
	                	if (line.indexOf(getEndTag()) != -1){
		                	line = line.substring(0, line.indexOf(getEndTag()));
		                	
		                	if (line.endsWith("/")) {
		                		System.out.println("DIR: "+address+line);
		                		
		                		RemoteFile currentFile = new RemoteFile();
		                		currentFile.setName(line);
		                		currentFile.setPath(address);
		                		currentFile.isDirectory(true);
		                		// To Be Done - lenght
		                		//currentFile.setLength(length);
		                		if (!(address+line).contains("<ul>")) {
		                			v.addElement(currentFile);
		                		}
		                	} else if (
		                				line.contains(".z80")
		                				|line.contains(".tap")
		                				|line.contains(".sna")
		                				//|line.contains(".dsk")
		                				//|line.contains(".mgt")
		                				|line.contains(".tzx") 
		                				){
		                		
		                		System.out.println(address+"/"+line);
		                		
		                		RemoteFile currentFile = new RemoteFile();
		                		currentFile.setName(line);
		                		currentFile.setPath(address);
		                		// To Be Done - lenght
		                		//currentFile.setLength(length);
		                		
		                		v.addElement(currentFile);
		                	}
	                	}
	                }
                }
            }
        }
        finally
        {
            close(br);
        }
        
        //System.out.println(contents.toString());
        
        
        
        return v;
	}
	
	
	public InputStream getZIPcontents (RemoteFile rf) throws Exception {
		
        
		if (!rf.isDirectory()){
			InputStream entrada;
			String tipo;
	        
	        String urlfile = rf.getPath()+"/"+rf.getName();
	        if ( urlfile.startsWith("/")) {
	        	urlfile = urlfile.substring(1);
	        }
	        
	        URL	url = new URL( urlfile );
			URLConnection snap = url.openConnection();
	
			InputStream input = snap.getInputStream();
	        ZipInputStream zis = new ZipInputStream(input);
	        //get the zipped file list entry
	        ZipEntry ze = zis.getNextEntry();
	        System.out.println(ze.getName());
	        	
	        
			tipo = ze.getName().substring(ze.getName().lastIndexOf(".") + 1, ze.getName().length());
	        tipo = tipo.toUpperCase();
	        rf.setExtension(tipo);
	        
	        System.out.println("Type: "+tipo);
	        
	        // puajjjjj
	        FileOutputStream output = null;
	        byte[] buffer = new byte[2048];
	        try
	        {
	            output = new FileOutputStream("D:/a."+tipo);
	            int len = 0;
	            while ((len = zis.read(buffer)) > 0)
	            {
	                output.write(buffer, 0, len);
	            }
	        }
	        finally
	        {
	            // we must always close the output file
	            if(output!=null) output.close();
	        }
	        
	        output=null;
	        
	        entrada = new FileInputStream(new File("/a."+tipo));
	        
	        return entrada;
		}
        
        return null;
	}
	
	private static void close(Reader br)
    {
        try
        {
            if (br != null)
            {
                br.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
	public Vector readRemotePage() {
		try {
			return readRemotePage(getRemoteAddress(), getIniTag(), getEndTag());
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		return null;
	}
	
	public void getRemoteFile (String urlfile) throws Exception {
		// http://www.worldofspectrum.org/pub/sinclair/games/z/Zip-Zap.z80.zip
	}
}
