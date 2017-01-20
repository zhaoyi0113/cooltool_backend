package com.cooltoo.go2nurse.chart.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;

public class ImageUtil {

	public static ImageUtil newInstance() {
		return new ImageUtil();
	}

	public final BufferedImage sharperImage(BufferedImage originalPic){
		int imageWidth = originalPic.getWidth();
		int imageHeight = originalPic.getHeight();

		BufferedImage newPic = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_3BYTE_BGR);
		float[] data = { -1.0f, -1.0f, -1.0f, -1.0f, 10.0f, -1.0f, -1.0f, -1.0f, -1.0f };

		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		co.filter(originalPic, newPic);
		return newPic;
	}

	public final boolean saveImage(BufferedImage originalPic, File dest, int dpi) {
		boolean success = false;
		ImageOutputStream ios = null;
		ImageWriter imageWriter = null;
		try {
			// Image writer
			imageWriter = ImageIO.getImageWritersBySuffix("jpg").next();
			ios = ImageIO.createImageOutputStream(dest);
			imageWriter.setOutput(ios);

			// Compression
			JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
			jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(0.7f);

			// Metadata (dpi)
			IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(originalPic), jpegParams);
			IIOMetadataNode tree = (IIOMetadataNode)data.getAsTree("javax_imageio_jpeg_image_1.0");
			IIOMetadataNode jfif = (IIOMetadataNode)tree.getElementsByTagName("app0JFIF").item(0);
			jfif.setAttribute("Xdensity", Integer.toString(dpi));
			jfif.setAttribute("Ydensity", Integer.toString(dpi));
			jfif.setAttribute("resUnits", "1"); // density is dots per inch
			data.mergeTree("javax_imageio_jpeg_image_1.0",tree);

			// Write
			imageWriter.write(null, new IIOImage(originalPic, null, data), jpegParams);
			imageWriter.dispose();
			success = true;
		}
		catch (Exception e){ e.printStackTrace(); }
		finally {
			//clean up
			if (null!=ios) { try { ios.close(); } catch (Exception ex) {} }
			if (null!=imageWriter) { try { imageWriter.dispose(); } catch (Exception ex) {} }
		}
		return success;
	}

}
