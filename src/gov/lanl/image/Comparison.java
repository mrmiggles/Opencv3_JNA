package gov.lanl.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.jna.Native;

import gov.lanl.opencvDLL.V3_3_x86.CLibrary;

public class Comparison {

	public CLibrary cl;
	
	public Comparison(){		
		
		Native.setProtected(true);
		//remember you added the external folder location in Build Path -> Library -> Add External Folder
		
		cl = (CLibrary) Native.loadLibrary("OpenCV3_3DLL", CLibrary.class);
		cl.setDetector(3);
		cl.setMatcher(1);
					
	}
	
	public void run() {
	       String searchObject = "C:\\Users\\299490\\Desktop\\Alexie\\Subject.jpg";				
	        try {
	        	
	        	byte[] buf1;
	        	
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
				
				
			readFileAndAddToSubjectDescriptorVector("C:\\output\\java_out.txt", "blah");
			cl.setSubjectDescriptorsAfterVectorFill(3114, 32, 0);
			
			cl.compareVectorValsToMat();
			
			
		} catch (IOException e) {
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
	
	public static void main(String[] args){
		Comparison c = new Comparison();
		c.run();
	}
}
