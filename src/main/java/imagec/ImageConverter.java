/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagec;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 *
 * @author Expert72
 */
public class ImageConverter {
    
    private final int COLOR_DEFECT = 5000;
    
    private BufferedImage imageCanvas;
    private String text = null;
    private String css = null;
    private boolean arrayNeedsUpdate = true;
    private int[][] arrayOfColors;
    private ConvertingMode convertMode = ConvertingMode.asBlocks;

    /**
     * Create instance and set the input image. <br>
     * Use {@link #setImageCanvas(java.awt.Image)} or
     * {@link #setImageCanvas(java.awt.image.BufferedImage)}
     * to set/change input image to be inverted<br>
     * @param imageCanvas input image
     */
    public ImageConverter(BufferedImage imageCanvas) {
        this.imageCanvas = imageCanvas;
    }
    
    /**
     * Create instance and set the input image. <br>
     * Use {@link #setImageCanvas(java.awt.Image)} or
     * {@link #setImageCanvas(java.awt.image.BufferedImage)}
     * to set/change input image to be inverted<br>
     * @param imageCanvas input image
     */
    public ImageConverter(Image imageCanvas) {
        if (imageCanvas instanceof BufferedImage) {
            this.imageCanvas = (BufferedImage)imageCanvas;
            return;
        }
        this.imageCanvas = toBufferedImage(imageCanvas);
    }
    
    //convert image from imageCanvas to HTML.
    //returns output HTML as StringBuilder
    private StringBuilder buildHTML() {
        //rebuild array of colors if needed
        updateImageArray();
        String theWord = (text.isEmpty() || text == null)?"_":text;
        //resulting HTML builder
        StringBuilder HTMLLines = new StringBuilder();
        //set up CSS for output HTML
        if (!css.isEmpty() && css != null) {
            HTMLLines.append("<style>").append(css).append("</style>");
        }
        //counter, that will be used to take chars in theWord one by one.
        //used only when converting mode is ConvertMode.asText
        int counter = 0;
        for (int i=0; i<arrayOfColors[0].length; i++) {
            for (int j=0; j<arrayOfColors.length; j++) {
                //using prevIsSame to merge tags with same colors
                boolean prevIsSame = false;
                if (j>0) prevIsSame = Math.abs(arrayOfColors[j][i]-arrayOfColors[j-1][i])<=COLOR_DEFECT;
                if (!(i==0 && j==0) && !prevIsSame) HTMLLines.append("</b>");
                if (convertMode == ConvertingMode.asBlocks) {
                    if (!prevIsSame) HTMLLines.append("<b style=background-color:");
                }
                else if (!prevIsSame) HTMLLines.append("<b style=color:");
                if (!prevIsSame) HTMLLines.append(String.format("#%06X", (0xFFFFFF & arrayOfColors[j][i])));
                if (!prevIsSame) HTMLLines.append(">");
                if (convertMode == ConvertingMode.asText) {
                    HTMLLines.append(theWord.charAt(counter++%theWord.length()));
                }
                else {
                    HTMLLines.append(theWord);
                }
            }
            HTMLLines.append("<br/>\n");
        }
        return HTMLLines;
    }
    
    /**
     * Converts image to HTML file. <br>
     * Use {@link #setConvertingMode(imagec.ImageConverter.ConvertMode)} to set
     * the converting mode<br>
     * Use {@link #setCss(java.lang.String)} to set custom CSS for output HTML<br>
     * Use {@link #setText(java.lang.String)} to set the cars sequence to be
     * used as pixel in output file<br>
     * Use {@link #setImageCanvas(java.awt.Image)} or
     * {@link #setImageCanvas(java.awt.image.BufferedImage)}
     * to set/change input image to be inverted<br>
     * @param fileName the name for output file. Get *.html on the tail
     */
    public void convertAndSaveToFile(String fileName) {
            StringBuilder builtHTML = buildHTML();
            try {
                Files.write(Paths.get(fileName+".html"), Arrays.asList(builtHTML), Charset.forName("UTF-8"));
            }
            catch (Exception e) {
            //==========DEBUG==========
                e.printStackTrace();
            //=========================
            }
    }
    
    //represent BufferedImage from imageCanvas as array of colors (int) and save to arrayOfColors
    private boolean updateImageArray() {
        if (!arrayNeedsUpdate) {
            return false;
        }
        arrayOfColors = new int[imageCanvas.getWidth()][imageCanvas.getHeight()];
        for (int i=0; i<imageCanvas.getWidth(); i++) {
            for (int j=0; j<imageCanvas.getHeight(); j++) {
                int pixelColor = imageCanvas.getRGB(i, j);
                arrayOfColors[i][j] = pixelColor;
            }
        }
        arrayNeedsUpdate = false;
        return true;
    }

    /**
     * Set image to convert.
     * @param imageCanvas set image to be converted
     */
    public void setImageCanvas(BufferedImage imageCanvas) {
        this.imageCanvas = imageCanvas;
        arrayNeedsUpdate = true;
    }

    /**
     * Set image to convert.
     * @param imageCanvas set image to be converted
     */
    public void setImageCanvas(Image imageCanvas) {
        if (imageCanvas instanceof BufferedImage) {
            this.imageCanvas = (BufferedImage)imageCanvas;
            return;
        }
        this.imageCanvas = toBufferedImage(imageCanvas);
        arrayNeedsUpdate = true;
    }

    /**
     * Get converting mode.
     * @return current converting mode
     */
    public ConvertingMode getConvertMode() {
        return convertMode;
    }

    /**
     * Set the converting mode. 
     * @see ConvertingMode
     * @param convertMode the way image will be converted.
     */
    public void setConvertingMode(ConvertingMode convertMode) {
        this.convertMode = convertMode;
    }

    /**
     * Get current Text.
     * @return current Text
     */
    public String getText() {
        return text;
    }

    /**
     * Set text to be used as pixels.
     * @param text character(s)
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get current CSS.
     * @return current CSS
     */
    public String getCss() {
        return css;
    }

    /**
     * Set custom CSS for output HTML.
     * @param css CSS as String
     */
    public void setCss(String css) {
        this.css = css;
    }

    //cast Image to BufferedImage
    private BufferedImage toBufferedImage(Image imageCanvas) {
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(imageCanvas.getWidth(null), imageCanvas.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(imageCanvas, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
    
    /**
     * Converting mode for <code>{@link ImageConverter}</code>.<br>
     * Use <code>{@link #setConvertingMode(imagec.ImageConverter.ConvertMode)}</code>
     * function to set converting mode.
     */
    public static enum ConvertingMode {
        
        /**
         * Represent image as empty filled blocks.<br>
         * <b>Background:</b> colored<br>
         * <b>Characters:</b> empty<br>
         */
        asBlocks,

        /**
         * Represent image as same chars.<br>
         * <b>Background:</b> empty<br>
         * <b>Characters:</b> colored<br>
         */
        asChar,

        /**
         * Represent image as text (sequence of chars).<br>
         * <b>Background:</b> empty<br>
         * <b>Characters:</b> colored<br>
         */
        asText;
        
    }
    
}
