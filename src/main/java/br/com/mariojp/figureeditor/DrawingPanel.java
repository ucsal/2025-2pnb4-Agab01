
package br.com.mariojp.figureeditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class DrawingPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_SIZE = 60;
    private final List<Shape> shapes = new ArrayList<>();
    private Point startDrag = null;

    private Color currentColor = new Color(30, 144, 255);
    
    DrawingPanel() {
        
        setBackground(Color.WHITE);
        setOpaque(true);
        setDoubleBuffered(true);

        var mouse = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && startDrag == null) {
                    int size = Math.max(Math.min(DEFAULT_SIZE, DEFAULT_SIZE), 10);
                    Shape s =  new Ellipse2D.Double(e.getPoint().x, e.getPoint().y, size, size);
                    //return new Rectangle2D.Double(e.getPoint().x, e.getPoint().y, Math.max(DEFAULT_SIZE, 10), Math.max(DEFAULT_SIZE, 10));
                    shapes.add(s);
                    repaint();
                }
            }
        };
        addMouseListener(mouse);        
        addMouseMotionListener(mouse);

    }

    void setCurrentColor(Color c) {
    	if (c!= null) {
    		currentColor= c;
    		repaint();
    	}
    }
    
    public Color getCurrentColor() {
    	return currentColor;
    }
    
    
    
    
    void clear() {
        shapes.clear();
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Shape s : shapes) {
            g2.setColor(currentColor);
            g2.fill(s);
            g2.setColor(new Color(0,0,0,70));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(s);
        }

        g2.dispose();
    }

        public void exportAsPNG(String filename) throws Exception {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            paint(g2);  // pinta o painel no buffer de imagem
            g2.dispose();
            ImageIO.write(image, "png", new File(filename));
        }

        // 🔽 MÉTODO PARA EXPORTAR COMO SVG
        public void exportAsSVG(String filename) throws Exception {
            StringBuilder svg = new StringBuilder();
            svg.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" ")
               .append("width=\"").append(getWidth()).append("\" ")
               .append("height=\"").append(getHeight()).append("\">\n");

            String fillColor = String.format("#%02x%02x%02x",
                    currentColor.getRed(),
                    currentColor.getGreen(),
                    currentColor.getBlue()
            );

            for (Shape s : shapes) {
                if (s instanceof Ellipse2D.Double e) {
                    svg.append(String.format(
                            "<circle cx=\"%f\" cy=\"%f\" r=\"%f\" fill=\"%s\" stroke=\"black\" stroke-opacity=\"0.3\" stroke-width=\"1.2\" />\n",
                            e.getCenterX(), e.getCenterY(), e.width / 2, fillColor
                    ));
                } else if (s instanceof Rectangle2D.Double r) {
                    svg.append(String.format(
                            "<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" fill=\"%s\" stroke=\"black\" stroke-opacity=\"0.3\" stroke-width=\"1.2\" />\n",
                            r.x, r.y, r.width, r.height, fillColor
                    ));
                }
            }

            svg.append("</svg>");
            Files.writeString(Path.of(filename), svg.toString());
        }
    }
