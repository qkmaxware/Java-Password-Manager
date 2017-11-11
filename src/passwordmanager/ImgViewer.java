/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Colin Halseth
 */
public class ImgViewer extends JPanel {

        public BufferedImage image;

        public ImgViewer() {
            super();
            this.setBackground(new Color(255,255,255,0));
            this.setOpaque(false);
        }
        
        public ImgViewer(BufferedImage img){
            this();
            this.image = img;
        }

        @Override
        public void paintComponent(Graphics g) {
            if (image == null) {
                return;
            }
            
            int cx = this.getWidth() / 2 ;
            int cy = this.getHeight() / 2;
            
            //Scale image to fill view size
            float scaleFactor = Math.min(this.getWidth(), this.getHeight()) / (float)Math.max(image.getWidth(), image.getHeight());
            
            int nw = (int)(image.getWidth() * scaleFactor);
            int ny = (int)(image.getHeight() * scaleFactor);
            
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(image, cx - nw/2, cy - ny/2, nw, ny, null);
        }

    }