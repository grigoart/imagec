package imagec;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Swing container for image (extends <code>JPanel</code>)
 */
public class MCanvasImg extends JPanel {
           
    private BufferedImage img = null;
    
    /**
     * Extended <code>JPanel</code> and can draw image inside.<br>
     * Use {@link #loadImage(java.io.File)} to load image.
     */
    public MCanvasImg() {
        super();
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            g.drawImage(img, 0, 0, null);
        }            
    }

    /**
     * Returns image.
     * @return buffered image as <code>BufferedImage</code> object.
     */
    public BufferedImage getImg() {
        return img;
    }

    /**
     * Loads image from file.
     * @param file <code>File</code> object to load
     */
    public void loadImage(File file) {
       try {
           img = ImageIO.read(file);
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
    
}