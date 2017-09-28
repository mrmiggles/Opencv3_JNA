package gov.lanl.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import com.sun.jna.Native;

import gov.lanl.opencvDLL.V3_3_x86.CLibrary;

public class StringToDLLMat {
	
	public CLibrary cl;
	
	public StringToDLLMat(){		
		
		Native.setProtected(true);
		//remember you added the external folder location in Build Path -> Library -> Add External Folder
		
		cl = (CLibrary) Native.loadLibrary("OpenCV3_3DLL", CLibrary.class);
		cl.setDetector(3);
		cl.setMatcher(1);
					
	}
	
	public void run() {
		String csvString = "";
		try {
			readFileAndAddToSubjectDescriptorVector("C:\\output\\subjectDescriptors.txt", csvString);
			cl.setSubjectDescriptorsAfterVectorFill(1118, 64, 5);
			//cl.printSubjectDescriptors();
			
			readFileAndAddToSubjectKeypoints("C:\\output\\subjectKeypoints.txt", csvString);
			//cl.getSubjectKeypoints();
			
			
			/* Set up image 2 for comparison */
			 String searchScene = "C:\\Users\\299490\\Desktop\\Alexie\\Scene.jpg";
			int h2;
			int w2;
        	byte[] buf2;
            System.out.println(searchScene);
			BufferedImage img1 =ImageIO.read(new File(searchScene));
			
			int size = (img1.getHeight() * img1.getWidth())/1024;


			buf2 = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
			h2 = img1.getHeight();
			w2  = img1.getWidth();	

			
			//long startTime = System.currentTimeMillis();  
			cl.compareToSubject(buf2, h2, w2);			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void readFileAndAddToSubjectDescriptorVector(String filePath, String store) throws IOException {
		
	    String line;
	    
        File fl = new File(filePath);
        FileReader frd = new FileReader(fl);
        BufferedReader brd = new BufferedReader(frd);

        while ((line=brd.readLine())!=null)
            parseLine(line);
        brd.close();
        frd.close();
    
	}
	
	
	private void readFileAndAddToSubjectKeypoints(String filePath, String store) throws IOException {
		
	    String line;
	    
        File fl = new File(filePath);
        FileReader frd = new FileReader(fl);
        BufferedReader brd = new BufferedReader(frd);

        while ((line=brd.readLine())!=null)
        	addLineToKeypoint(line);
        brd.close();
        frd.close();
    
	}	
	
	private void addLineToKeypoint(String line) {
		String[] vals = line.split(",");
		for(String val : vals) {
			//System.out.println(val);
			String[] xy = val.split(" ");
			if(xy.length < 2) return;
			//System.out.println("x: " + xy[0] + " y: " + xy[1]);
			cl.addToSubjectKeyPoints(Float.parseFloat(xy[0]), Float.parseFloat(xy[1]));
		}
	}	
	
	private void parseLine(String line) {
		String[] vals = line.split(",");
		for(String val : vals) {
			//System.out.println(val);
			cl.addToSubjectDescriptorVector(val);
		}
	}
	
	public static void main(String[] args){
		StringToDLLMat std = new StringToDLLMat();
		std.run();
	}

}
