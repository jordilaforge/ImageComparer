package main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;


public class CompareScreenshot {
	//private static final Logger LOGGER = Logger.getLogger(CompareScreenshot.class);
	 /**
     *threshold tunes how strict the comparison is
     *0 = only exactly same color of pixel
     *more than 0 = kind of the same color
     */
	final int threshold = 0;

	/**
	 * Method to calculate similarity of two Files(JPG/PNG) returns percentage of similarity
	 * 0% = Images totally different
	 * 100% = Images are the same
	 * @param file1 		image1 for comparison 
	 * @param file2			image2 for comparison
	 * @return				percentage of similarity between image1 and image2
	 */
	public int compare(String file1, String file2) {
		long startTime = System.currentTimeMillis();
		checkPreconditions(file1, file2);
		Image image1 = loadImage(file1);
		Image image2 = loadImage(file2);
		int similarity =(int) Math.floor(100*(compareImages(image1, image2)));
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		//System.out.println("Comparing screenshots similarity: "+similarity+"% time: "+ totalTime+"ms");
		return similarity;
	}


	/**
	 *Method to calculate similarity of two Images returns percentage of similarity
	 *0.00 = Images totally different
	 *100.00 = Images are the same
	 * @param imageA		image1 for comparison
	 * @param imageB		image2 for comparison 
	 * @return				percentage of similarity between image1 and image2
	 */
	private double compareImages(Image imageA, Image imageB) {
		BufferedImage img1 = imageToBufferedImage(imageA);
		BufferedImage img2 = imageToBufferedImage(imageB);
		if(img1.getHeight()>10000|img1.getWidth()>10000|img2.getHeight()>10000|img2.getWidth()>10000){
			throw new IllegalArgumentException("Images are to Big");
		}
		if (img1.getHeight() != img2.getHeight() | img1.getWidth() != img2.getWidth()) {
			return compareBufferedImagesNotSameScale(img1, img2);
		}
		return compareBufferedImages(img1, img2);
	}

	/**
	 * Method for Images with different scale Factors
	 * @param img1			image1 for comparison
	 * @param img2			image2 for comparison
	 * @return				percentage of similarity between image1 and image2
	 */
	private double compareBufferedImagesNotSameScale(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() == img2.getWidth() & img1.getHeight() == img2.getHeight()) {
			throw new IllegalArgumentException("Images have same Resolution!");
		}
		int area_img1 = img1.getHeight() * img1.getWidth();
		int area_img2 = img2.getHeight() * img2.getWidth();
		double area_diff = Math.min(img1.getHeight(), img2.getHeight()) * Math.min(img1.getWidth(), img2.getWidth());
		double percentageAreaDiff = (area_diff / Math.max(area_img1, area_img2));
		BufferedImage subimage1;
		BufferedImage subimage2;
		double similarity;
		int cutting_width=Math.min(img1.getWidth(), img2.getWidth());
		int cutting_height=Math.min(img1.getHeight(), img2.getHeight());
		subimage1 = img1.getSubimage(0, 0, cutting_width, cutting_height);
		subimage2 = img2.getSubimage(0, 0, cutting_width, cutting_height);	
		similarity = compareBufferedImages(subimage1, subimage2);
		return (percentageAreaDiff*similarity);
	}
	

	/**
	 * Method to calculate similarity of two BufferedImages returns percentage of similarity
	 * 0.00 = Images totally different
	 * 100.00 = Images are the same
	 * @param img1			image1 for comparison
	 * @param img2			image2 for comparison
	 * @return				percentage of similarity between image1 and image2
	 */
	private double compareBufferedImages(BufferedImage img1, BufferedImage img2) {
		if ((img1.getHeight() == img2.getHeight()) && (img1.getWidth() == img2.getWidth())) {
			int pixelsimilarity = 0;
			int totalPixel = img1.getHeight() * img1.getWidth();
			for (int x = img1.getMinX(); x < img1.getWidth(); ++x) {
				for (int y = img1.getMinY(); y < img1.getHeight(); ++y) {
					int diffPixel = Math.abs(img1.getRGB(x, y) - img2.getRGB(x, y));
					if (diffPixel <= threshold) {
						++pixelsimilarity;
					}
				}
			}
			return ((double)pixelsimilarity/totalPixel);
		} else {
			throw new IllegalArgumentException("Images not the Same Size");
		}

	}


	/**
	 * Method to transfer Image to BufferedImage
	 * @param img		image to convert to BufferedImage
	 * @return			BufferedImage of image
	 */
	protected static BufferedImage imageToBufferedImage(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, null, null);
		return bi;
	}

	/**
	 * Method to ReadFile (JPG/PNG tested)
	 * @param filename		filepath of file
	 * @return				image
	 */
	protected static Image loadImage(String filename) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(filename);
		} catch (java.io.FileNotFoundException io) {
			io.printStackTrace();
		}
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(in);
			in.close();
		} catch (java.io.IOException io) {
			io.printStackTrace();
		}
		return bi;
	}
	
	/**
	 * Method to Check if one of the Files is not null
	 * @param fileA			image1 for comparison
	 * @param fileB			image2 for comparison
	 */
	private void checkPreconditions(String fileA, String fileB) {
		if (fileA == null) {
			throw new IllegalArgumentException("First file argument must not be null.");
		}
		if (fileB == null) {
			throw new IllegalArgumentException("Second file argument must not be null.");
		}
	}

}