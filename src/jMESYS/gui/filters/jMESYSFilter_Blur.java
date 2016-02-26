package jMESYS.gui.filters;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class jMESYSFilter_Blur extends jMESYSImageFilter {

	public Image getFilteredScreenImage(Image imgin, int width, int height) {
		BufferedImage OutImage;
		
		float[] matrix = {
	        0.1f, 0.1f, 0.1f, 
	        0.1f, 0.1f, 0.1f, 
	        0.1f, 0.1f, 0.1f, 
	    };
		
		//System.out.println(imgin.getClass().getName());
		
		BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
		BufferedImage destImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		OutImage = op.filter( (BufferedImage) imgin, destImage);
			
		return OutImage;
	}

}
