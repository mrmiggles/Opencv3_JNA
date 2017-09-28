package gov.lanl.opencvDLL;

import com.sun.jna.Library;


public class V3_3_x86 {

	
	CLibrary cl;

	/**
	 * 
	 * 
	 * setDectector(int type)
	 * 0 - BRISK
	 * 1 - AKAZE -- Note ** (AKAZE descriptors can only be used with KAZE or AKAZE keypoints)
	 * 2 - ORB -- Note ** (ORB descriptors must be converted to CV_32F in order to work with FLANN)
	 * 3 - SURF 
	 * 4 - SIFT //not yet implemented
	 * 5 - FREAK
	 * 
	 * setMatcher(int type)
	 * 0 - BruteForce
	 * 1 - FLANN
	 * 
	 * 
	 * 
	 * Use: 
	 *  - Set your detector
	 *  - Set your matcher
	 *  - Set your subject image (The base image to search for in other images);
	 *  - Detect Keypoints and Descriptors for Subject Image
	 *  
	 *  
	 */
	public interface CLibrary extends Library {
		
		boolean setSubjectImage(byte[] p, int height, int width);
		boolean checkSubject();
		boolean setDetector(int type); //if not called Default is BRISK
		boolean setMatcher(int type); //if not called Default is BRUTE FORCE W/HAMMING
		boolean setSubjectKeypoints();
		void getSubjectKeypoints();
		void addToSubjectKeyPoints(float x, float y);
		boolean compareToSubject(byte[] p, int h1, int w1);
		void printSubjectDescriptors();
		void setSubjectDescriptorsAfterVectorFill(int rows, int cols, int type);
		void addToSubjectDescriptorVector(String s);
		void compareVectorValsToMat();
		String getSubjectDescriptors();
		String getDesc();
		void cleanUp();
	}
	
	
}
