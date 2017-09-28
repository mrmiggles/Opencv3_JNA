package gov.lanl.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import com.sun.jna.Native;

import gov.lanl.opencvDLL.V3_3_x86.CLibrary;

public class DLLTest {
	
	public CLibrary cl;
	public DLLTest(){		
		
		Native.setProtected(true);
		//remember you added the external folder location in Build Path -> Library -> Add External Folder
		cl = (CLibrary) Native.loadLibrary("OpenCV3_3DLL", CLibrary.class);

		cl.setDetector(3);
		cl.setMatcher(1);			
	}
	
	public void run(){
       //String searchObject = "C:\\Users\\Miguel\\Desktop\\Alexie\\Subject.jpg";
       //String searchScene = "C:\\Users\\Miguel\\Desktop\\Alexie\\Scene.jpg";
       String searchObject = "C:\\Users\\299490\\Desktop\\Alexie\\Subject.jpg";
       String searchScene = "C:\\Users\\299490\\Desktop\\Alexie\\Scene.jpg";
			
        try {
        	
        	byte[] buf1, buf2;
        	
        	/* Load object we are searching for */
			BufferedImage img = ImageIO.read(new File(searchObject));
			int h1;
			int w1;			
			int iType1 = img.getType();
			
			if((img.getWidth() * img.getHeight())/1024 < 300){
				img = gov.lanl.image.Processing.scaleUp(img);
			}
			
			if(iType1 != 5 && iType1 != 10 && iType1 != 4 && iType1 != 1) {
				BufferedImage iBGR1;
				System.out.println("Converting to BGR");
				iBGR1 = gov.lanl.image.Processing.convertToBGR(img);
				img.flush();
				buf1 = ((DataBufferByte) iBGR1.getRaster().getDataBuffer()).getData();
				h1 = iBGR1.getHeight();
				w1 = iBGR1.getWidth();
			} else {
				buf1 = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
				h1 = img.getHeight();
				w1 = img.getWidth();
			}  
			

			//set the subject
			
			cl.setSubjectImage(buf1, h1, w1);
			
			if(cl.checkSubject()){
				cl.setSubjectKeypoints();
			} else {
				return;
			}
			readFileAndAddToSubjectDescriptorVector("C:\\output\\subjectDescriptors.txt", "ba");
			cl.setSubjectDescriptorsAfterVectorFill(1118, 64, 5);
						
			//readFileAndAddToSubjectKeypoints("C:\\output\\subjectKeypoints.txt", "bah");
			
			int h2;
			int w2;
        	
            System.out.println(searchScene);
			BufferedImage img1 =ImageIO.read(new File(searchScene));
			
			int size = (img1.getHeight() * img1.getWidth())/1024;

			iType1 = img1.getType();
			if(iType1 != 5 && iType1 != 10 && iType1 != 4 && iType1 != 1){
				System.out.println("Converting to BGR");
				BufferedImage iBGR2;
				iBGR2 = gov.lanl.image.Processing.convertToBGR(img1);
				buf2 = ((DataBufferByte) iBGR2.getRaster().getDataBuffer()).getData();
				h2 = iBGR2.getHeight();
				w2  = iBGR2.getWidth();				
			} else {
				buf2 = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
				h2 = img1.getHeight();
				w2  = img1.getWidth();	
			}
			
			//long startTime = System.currentTimeMillis();  
			cl.compareToSubject(buf2, h2, w2);
			//System.out.println("Comparison: " +  (System.currentTimeMillis() - startTime) + "ms");			     				        			
			
			//img1.flush();
			//buf2 = null;			
			 

			
        } catch(Exception e){
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
	
	private void parseLine(String line) {
		String[] vals = line.split(",");
		for(String val : vals) {
			//System.out.println(val);
			cl.addToSubjectDescriptorVector(val);
		}
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
	
	public static void main(String[] args) {
		DLLTest t = new DLLTest();
		t.run();

	}
}
