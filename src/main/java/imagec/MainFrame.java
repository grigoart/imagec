/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagec;

import imagec.ImageConverter.ConvertingMode;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Expert72
 */
public class MainFrame {
    
    private static JFrame mainFrame = null;
    private static JPanel mainPanel = null;
    private static JTextField textField = null;
    private static JTextField cssField = null;
    private static MCanvasImg imageCanvas = null;
    private static JRadioButton asBlocksRadio;
    private static JRadioButton asCharRadio;
    private static JButton btnConvert;
    private static JButton btnBrowse;
    private static ImageConverter converter;
    
    private static String lastOpenedFilePath = "/";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //<editor-fold defaultstate="collapsed" desc="Setting up testing GUI">
        //create main form and main panel
        mainFrame = new JFrame();
        mainFrame.setSize(new Dimension(600, 400));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel(new GridLayout());
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(8, 1));
        mainPanel.add(controlPanel);
        mainFrame.add(mainPanel);

        //add css TextArea
        cssField = new JTextField("b {font-size: 1px; line-height: 1px;}");
        controlPanel.add(cssField);

        //add Open image button
        JButton btnOpen = new JButton("Open");
        btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonOpenClick();
            }
        });
        controlPanel.add(btnOpen);

        //add Convert image button
        btnConvert = new JButton("Convert and save");
        btnConvert.setEnabled(false);
        btnConvert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonConvertClick();
            }
        });
        controlPanel.add(btnConvert);

        //add Open converted image button
        btnBrowse = new JButton("Browse");
        btnBrowse.setEnabled(false);
        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonBrowseClick();
            }
        });
        controlPanel.add(btnBrowse);

//        //radiobuttons option
//        ButtonGroup bg = new ButtonGroup();
//        JRadioButton rbtnCSS = new JRadioButton("Extra CSS");
//        extraCSS = rbtnCSS;
//        JRadioButton rbtnHTML = new JRadioButton("One HTML");
//        rbtnHTML.setSelected(true);
//        bg.add(rbtnCSS);
//        bg.add(rbtnHTML);
//        controlPanel.add(rbtnCSS);
//        controlPanel.add(rbtnHTML);

        //add TextArea
        textField = new JTextField();
        controlPanel.add(textField);

        //radiobuttons option
        ButtonGroup bg2 = new ButtonGroup();
        JRadioButton rbtnBlocks = new JRadioButton("As Blocks");
        asBlocksRadio = rbtnBlocks;
        JRadioButton rbtnText = new JRadioButton("As Text");
        JRadioButton rbtnChar = new JRadioButton("As TextBlock");
        asBlocksRadio = rbtnBlocks;
        asCharRadio = rbtnChar;
        rbtnBlocks.setSelected(true);
        bg2.add(rbtnBlocks);
        bg2.add(rbtnText);
        bg2.add(rbtnChar);
        controlPanel.add(rbtnChar);
        controlPanel.add(rbtnBlocks);
        controlPanel.add(rbtnText);

        //add canvas
        imageCanvas = new MCanvasImg();
        imageCanvas.setBounds(50, 50, imageCanvas.getWidth(), imageCanvas.getHeight());
        mainPanel.add(imageCanvas);

        //make main from visible
        mainFrame.setVisible(true);
        //</editor-fold>
        converter = new ImageConverter(imageCanvas.getImg());
    }
    
    //open image file dialog
    private static void buttonOpenClick() {
        JFileChooser fc = new JFileChooser(lastOpenedFilePath);
        fc.setBackground(Color.red);
        if (fc.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            btnConvert.setEnabled(false);
        }
        else {
            btnConvert.setEnabled(true);
            btnBrowse.setEnabled(false);
        }
        lastOpenedFilePath = fc.getSelectedFile().getPath();
        imageCanvas.loadImage(fc.getSelectedFile());
        mainFrame.repaint();
    }
    
    //open converted html file
    private static void buttonBrowseClick() {
        try {
            Desktop.getDesktop().open(new File(lastOpenedFilePath+".html"));
        } catch (Exception e) {
        //==========DEBUG==========
            e.printStackTrace();
        //=========================
        }
    }
    
    //start converting file in new thread
    private static void buttonConvertClick() {
        btnConvert.setEnabled(false);
        btnBrowse.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConvertingMode cm = (asBlocksRadio.isSelected())?ImageConverter.ConvertingMode.asBlocks:(asCharRadio.isSelected())?ImageConverter.ConvertingMode.asChar:ImageConverter.ConvertingMode.asText;
                converter.setConvertingMode(cm);
                converter.setCss(cssField.getText());
                converter.setText(textField.getText());
                //==========DEBUG==========
                    long time;
                    time = System.currentTimeMillis();
                //=========================
                converter.convertAndSaveToFile(lastOpenedFilePath);
                //==========DEBUG==========
                    System.out.println("converter.saveFile(\"" + lastOpenedFilePath + "\") took [ms]: " + (System.currentTimeMillis()-time));
                //=========================
                btnBrowse.setEnabled(true);
                btnConvert.setEnabled(true);
            }
        }).start();
        
    }
    
}
