/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Elias
 */
public class ImageUtils {
    public static BufferedImage getScaledImage(BufferedImage srcImg, int maxWH){
        int newWidth, newHeight;
        
        if (srcImg.getHeight() >= srcImg.getWidth()) {
            newHeight = maxWH;
            newWidth = (int)(maxWH * (float)((float)srcImg.getWidth() / (float)srcImg.getHeight()));
        }
        else {
            newHeight = (int)(maxWH * (float)((float)srcImg.getHeight() / (float)srcImg.getWidth()));
            newWidth = maxWH;
        }
        
        BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, srcImg.getType());
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, newWidth, newHeight, null);
        g2.dispose();
        return resizedImg;
    }
    
    public static byte[] getScaledImage(byte[] imgData, String imgType, int maxWH) throws IOException{
        return getScaledImage(new ByteArrayInputStream(imgData), imgType, maxWH);
    }
    
    public static byte[] getScaledImage(InputStream imgIS, String imgType, int maxWH) throws IOException{
        BufferedImage bi = ImageIO.read(imgIS);
        bi = ImageUtils.getScaledImage(bi, maxWH);

        byte[] imageInByte;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bi, imgType, baos);
            baos.flush();
            imageInByte = baos.toByteArray();
        }
        return imageInByte;
    }
}
