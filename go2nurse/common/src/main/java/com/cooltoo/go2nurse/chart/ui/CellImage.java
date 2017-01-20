package com.cooltoo.go2nurse.chart.ui;

import com.cooltoo.go2nurse.chart.util.DpiUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by zhaolisong on 19/01/2017.
 */
public class CellImage {
    public static final int IMAGE_STREAM = 0;
    public static final int IMAGE_FILE   = 1;
    public static final int IMAGE_WEB    = 2;

    private int type = IMAGE_FILE;
    private Image image;
    private DpiUtil dpiUtil = DpiUtil.newInstance();

    public CellImage(String filePath) throws IOException {
        type = IMAGE_WEB;
        image = ImageIO.read(new URL(filePath));
    }


    /**
     * @param dpi 每英寸点数
     * @param mmBound 有效内容区域 {mmLeft, mmTop, mmWidth, mmHeight}
     * @param g 绘图句柄
     */
    public Graphics save(int dpi, float[] mmBound, Graphics g) {
        if (null!=image) {
            int pixelLeft  = dpiUtil.mmToPixel(dpi, mmBound[0])+1;
            int pixelTop   = dpiUtil.mmToPixel(dpi, mmBound[1])+1;
            int pixelWidth = dpiUtil.mmToPixel(dpi, mmBound[2])-3;
            int pixelHeight= dpiUtil.mmToPixel(dpi, mmBound[3])-3;

            float imgWidth = image.getWidth(null)*1f;
            float imgHeight= image.getHeight(null)*1f;
            float imgScaleOfWH = imgWidth/imgHeight;

            if (pixelWidth / imgScaleOfWH <= pixelHeight) {
                pixelHeight = (int)(pixelWidth / imgScaleOfWH);
            }
            else {
                pixelWidth = (int)(pixelHeight * imgScaleOfWH);
            }

            g.drawImage(image, pixelLeft, pixelTop, pixelWidth, pixelHeight, null);
        }
        return g;
    }

}
