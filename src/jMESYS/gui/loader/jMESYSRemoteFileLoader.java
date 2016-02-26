package jMESYS.gui.loader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import jMESYS.core.cpu.CPU;
import jMESYS.drivers.Sinclair.Spectrum.sites.WOSsite;
import jMESYS.files.FileFormat;
import jMESYS.files.RemoteFile;
import jMESYS.gui.jMESYSDisplay;

public class jMESYSRemoteFileLoader extends JDialog implements TreeSelectionListener, ActionListener {
	
	private JTree tree;
	private JTextField input;
    //private String addrSiteWOS="http://localhost:8080/WOSserver/pub/sinclair/games/";
	private String addrSiteWOS="http://www.worldofspectrum.org/pub/sinclair/games/";
	//private String addrSearchWOS="http://www.worldofspectrum.org/infoseek.cgi?what=1&yrorder=1&year=0&type=0&players=0&turns=0&memory=0&language=0&country=0&licence=0&feature=0&publi=0&release=0&format=0&scheme=0&scorder=1&score=0&have=1&also=1&sort=1&display=1&loadpics=2&regexp=";
	private String addrSearchWOS="http://www.worldofspectrum.org/infoseek.cgi?regexp=";
	//private String addrSearchWOS="http://www.worldofspectrum.org/api/infoseek_search_json.cgi?title=";
    private JPanel panScreen;
    
    private JDialog frame;
    
    private jMESYSDisplay display;
    //private CPU computer;
    private FileFormat[] ff;
	
	//public abstract String getURLroot();
	private String FiletoLoad = null;
	private int option = 0;
	
	private WOSsite site;

	public jMESYSRemoteFileLoader(Frame owner, FileFormat[] filef, jMESYSDisplay disp) {
		super(owner);
		
		display = disp;
		ff = filef;
	}
	
	public int showOpenDialog() {
		
        JPanel panel = new JPanel();
        JPanel panTotal = new JPanel();
        panTotal.setLayout(new BorderLayout());

        panel.setLayout(new GridLayout(1,2));
        panel.setOpaque(true);
      
        initTree("/", addrSiteWOS, "<li><a href=\"", "\">");
		
        //JScrollPane scroller = new JScrollPane(textArea);
        JScrollPane scroller = new JScrollPane(tree);
        scroller.setSize(350, 250);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        input = new JTextField(20);
        JButton buttonSearch = new JButton("Search");
        JButton buttonOK = new JButton("OK");
        JButton buttonCancel = new JButton("Cancel");
        /*DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);*/
        panel.add(scroller);
        
        inputpanel.add(input);
        inputpanel.add(buttonSearch);
        inputpanel.add(buttonOK);
        inputpanel.add(buttonCancel);
        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);
        buttonSearch.addActionListener(this);
        
        panScreen = new JPanel();
        panScreen.setPreferredSize( new Dimension(display.getWidth(), display.getHeight()) );
        Image imgScreen = panScreen.createImage(display.getWidth(), display.getHeight());
        panel.add(panScreen);
        
        panTotal.add(panel, BorderLayout.CENTER);
        panTotal.add(inputpanel, BorderLayout.SOUTH);
        /*frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);*/
        input.requestFocus();
        
        frame = new JDialog(this, "Remote Site", true);
        //frame.setSize(787, 362);
        frame.getContentPane().add(panTotal);
		frame.pack();
		frame.setSize(650, 268);
		frame.setVisible(true);
		//frame.setSize(787, 362);
		System.out.println("WIDTH: "+frame.getWidth());
		System.out.println("HEIGHT: "+frame.getHeight());
		
		return option;
	}
	
	private void initTree(String strRoot, String urlSite, String iniTag, String endTag) {
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(strRoot);
        
		if (tree == null){
        	tree = new JTree(root);
        	tree.addTreeSelectionListener(this);
        } 
        
        tree.removeAll();

        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.setRoot(root);
        //tree.addTreeSelectionListener(this);
        
		//getBranch("http://localhost:8080/WOSserver/pub/sinclair/games/", root);
        getBranch(urlSite, root, iniTag, endTag);
        
        model.reload(root);
		
		tree.expandPath(new TreePath(root.getPath()));
		tree.addTreeSelectionListener(this);
		
	}

	private void getBranch(String addrWOS, DefaultMutableTreeNode parentNode, String iniTag, String endTag) {
		System.out.println("Ruta: "+addrWOS);
		
		site = new WOSsite();
		site.setIniTag(iniTag);
		site.setEndTag(endTag);
		site.setRemoteDownloadAddress(addrSiteWOS);
		
        site.setRemoteAddress( addrWOS );
		
        Vector v = site.readRemotePage();
		
		int numNodes = v.size();
		for (int i=0 ; i<numNodes ; i++) {
			RemoteFile rf = (RemoteFile) v.elementAt(i);
			DefaultMutableTreeNode nodAct = new DefaultMutableTreeNode(rf.getName());
			parentNode.add(nodAct);
		}
		
		tree.expandPath(new TreePath(parentNode.getPath()));
	}
	
	public void valueChanged(TreeSelectionEvent event) {
		System.out.println("Click tree");
		tree.expandPath(event.getPath());
        //input.setText(event.getPath().toString());
        
        String completePath = addrSiteWOS+event.getPath().getLastPathComponent().toString();
        String partialPath = addrSiteWOS;
        TreePath tp = event.getPath();
        String strFile = addrSiteWOS;
        
        int numPath = tp.getPathCount();
        for (int i=0 ; i<numPath ; i++){
        	strFile += (tp.getPath()[i]).toString();
        }
        
        
        System.out.println("CompletePath: "+completePath);
        System.out.println("PartialPath: "+partialPath);
        System.out.println("eventPath: "+tp.toString());
        System.out.println("strFile: "+strFile);
        
        if (event.getPath().getLastPathComponent().toString().endsWith("/")){
        	System.out.println("Directorio "+addrSiteWOS+event.getPath().getLastPathComponent().toString());
        	getBranch(completePath, 
        			(DefaultMutableTreeNode)((JTree)event.getSource()).getLastSelectedPathComponent(), site.getIniTag(), site.getEndTag());
        	
        } else {
        	System.out.println("Fichero");
        	
        	try {
				// load and unzipped remote file
				//FileFormat[] ff = getComputer().getSupportedFileFormats();
				WOSsite wos = new WOSsite();
				
				FileFormat xZ80=null;
				
				
				//getComputer().reset();
				RemoteFile rf = new RemoteFile();
				rf.setName( strFile );
				
				rf.setPath("");
				InputStream fis = wos.getZIPcontents(rf);
				//System.out.println("Formato buscado: "+rf.getExtension());
				
				int countFormats = ff.length;
				
				for (int i=0 ; i<countFormats ; i++){
					//System.out.println("Formato actual "+ff[i].getExtension().toUpperCase());
					if (("."+rf.getExtension().toUpperCase()).equals(ff[i].getExtension().toUpperCase())){
						//System.out.println("Formato Encontrado!");
						xZ80 = ff[i];
					}
				}
				
				//xZ80.loadFormat("/a.z80", wos.getZIPcontents(rf), getComputer());
				xZ80.getScreen("/a."+rf.getExtension(), fis, display, panScreen.getGraphics());
				FiletoLoad = strFile;
				//xZ80.loadFormat("/a.z80", wos.getZIPcontents(rf), getComputer());
			} catch (Exception e) {
				e.printStackTrace(System.out);
				//if (!painted){
		        	System.out.println("NO PNTADO FICHERO");
		        	panScreen.getGraphics().setColor(Color.RED);
		        	panScreen.getGraphics().fillRect(0, 0, display.getWidth(), display.getHeight());
		        //}
			}
        }
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("OK")){
			//FiletoLoad = "/a.z80";
			option = 1;
			frame.dispose();
		} else if (ev.getActionCommand().equals("Cancel")){
			FiletoLoad = null;
			option = 0;
			frame.dispose();
		} else if (ev.getActionCommand().equals("Search")) {
			System.out.println("SEARCH "+input.getText());
			
			if (input.getText().length() > 0){
				initTree("/", addrSearchWOS+input.getText(), "<A HREF=\"/pub/sinclair/games/", "\" TITLE=\"Download to play off-line in an emulator\"");
			}
			/*try {
				sendRequest(addrSearchWOS+input.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}
	
	public String getFiletoLoad(){
		return FiletoLoad;
	}
	
	public void sendRequest(String request) throws Exception
	{
	    // i.e.: request = "http://example.com/index.php?param1=a&param2=b&param3=c";
	    URL url = new URL(request); 
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
	    connection.setDoOutput(true); 
	    connection.setInstanceFollowRedirects(false); 
	    connection.setRequestMethod("POST"); 
	    connection.setRequestProperty("Content-Type", "text/plain"); 
	    connection.setRequestProperty("charset", "utf-8");
	    connection.connect();
	    
	    
	    
	    //BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = "";
        while (line != null)
        {
            line = br.readLine();
            //contents.append(line);
            System.out.println(line);
        }
	}
}
