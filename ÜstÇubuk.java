import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Score değişkeninin tutulup sergilendiği sınıftır. oyunu kapatmak için bir tuş bulundurur.
 */
public class ÜstÇubuk extends JPanel {
    private int puan;

    public ÜstÇubuk() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Puanı sol üst köşeye yaz
        JLabel puanLabel = new JLabel("Puan: " + puan);
        puanLabel.setForeground(Color.WHITE);
        add(puanLabel, BorderLayout.WEST);

        // Kapatma düğmesini sağ üst köşeye ekle
        JButton kapatButton = new JButton("Kapat");
        kapatButton.setForeground(Color.RED);
        kapatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(kapatButton, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Çubuğun boyut ve rengini belirle
        int width = 1536;
        int height = 48;

        // Çubuğu çiz
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, width, height);

        // Puan etiketini güncelle
        ((JLabel) getComponent(0)).setText("Puan: " + puan);
    }

    public int getPuan() {
        return puan;
    }

    public void setPuan(int puan) {
        this.puan = puan;
        repaint();
    }
}

