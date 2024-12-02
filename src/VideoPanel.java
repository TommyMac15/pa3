import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoPanel extends JPanel {
    private JLabel label;
    private JLabel fpsLabel;
    private int frameInterval;

    public VideoPanel(int frameInterval) {
        this.frameInterval = frameInterval;
        setLayout(new BorderLayout());
        label = new JLabel();
        fpsLabel = new JLabel("FPS: " + (1000 / frameInterval) + " | Interval: " + frameInterval + " ms");
        fpsLabel.setForeground(Color.RED);
        add(label, BorderLayout.CENTER);
        add(fpsLabel, BorderLayout.SOUTH);
    }

    public void displayImage(BufferedImage image) {
        BufferedImage scaledImage = getScaledImage(image, getWidth(), getHeight());
        label.setIcon(new ImageIcon(scaledImage));
        repaint();
    }

    private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h) {
        Image tmp = srcImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public int getFrameInterval() {
        return frameInterval;
    }
}