package jMESYS.gui.filters;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public abstract class jMESYSImageFilter {
	
	public abstract Image getFilteredScreenImage(Image imgin, int width, int height);
	
	public static BufferedImage convertToBufferedImage(Image image) {
	    BufferedImage newImage = new BufferedImage(
	        image.getWidth(null), image.getHeight(null),
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
	
	public Image applyFilter(Image imgIn, int width, int height) throws Exception {
		Image imgOut = imgIn;
		
		//Image image = ImageIO.read( imgOut );
		BufferedImage bufferedImage = convertToBufferedImage(imgOut);
		imgOut = getFilteredScreenImage(bufferedImage, width, height);
		
		return imgOut;
	}
	
}
