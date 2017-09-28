package gov.lanl.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import com.sun.jna.Native;

import gov.lanl.opencvDLL.V3_3_x86.CLibrary;

public class Generate {

	public CLibrary cl;
	
	int h1;
	int w1;
	byte[] buf1;
	
	public Generate(){		
		
		Native.setProtected(true);
		//remember you added the external folder location in Build Path -> Library -> Add External Folder
		cl = (CLibrary) Native.loadLibrary("OpenCV3_3DLL", CLibrary.class);

		cl.setDetector(3);
		cl.setMatcher(1);			
	}
	
	public void generateDescriptorsTextFile() {
		
		  try {
	        	
				//cl.getSubjectKeypoints();
				//cl.printSubjectDescriptors();
				String s = cl.getSubjectDescriptors();
				Files.write(Paths.get("C:\\output\\subjectDescriptors.txt"), s.getBytes(), StandardOpenOption.CREATE);
	        } catch(Exception e) {
	        	
	        }		
	}
	
	public void generateKeypointsTextFile() {
		
		cl.getSubjectKeypoints();
	}
	
	private boolean setBuffersAndDimensions(String imagePath) throws IOException {
    	/* Load object we are searching for */
		BufferedImage img = ImageIO.read(new File(imagePath));
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
			return false;
		}
		
		return true;
	}
	
	
	public static void main(String[] args){
		Generate gt = new Generate();
		try {
			
			gt.setBuffersAndDimensions("C:\\Users\\299490\\Desktop\\Alexie\\Subject.jpg");
			gt.generateDescriptorsTextFile();
			gt.generateKeypointsTextFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
