import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Karakterlerin kullandığı saldırı birimidir.
 */
public class Mermi extends Canlı {
    private boolean yön;
    private boolean ultiMi;
    private BufferedImage mermiSol, mermiSağ;

    public Mermi(Color renk, int x, int y, boolean yön, boolean ultiMi) {
        super(renk, x, y);
        super.setCan(1);
        super.setBoyut(5);
        super.setHız(15);
        this.yön = yön;
        görüntüleriAl(renginİsmiNe(renk), ultiMi);
        this.ultiMi = ultiMi;
    }
    private String renginİsmiNe(Color color) {
        String string;
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        if (red == 255 && green == 0 && blue == 0) {
            string = "Kırmızı";
        } else if (red == 0 && green == 255 && blue == 0) {
            string = "Yeşil";
        } else if (red == 0 && green == 0 && blue == 255) {
            string = "Mavi";
        } else if (red == 255 && green == 0 && blue == 255) {
            string = "Mor";
        } else if (red == 255 && green == 127 && blue == 0) {
            string = "Turuncu";
        } else {
            string = "HATALI_RENK_GİRİŞİ";
        }

        return string;
    }

    @Override
    public void çiz(Graphics g, Klavye klavye) {
        super.çiz(g, klavye);
        Graphics2D graphics2D = (Graphics2D)g;
        BufferedImage bufferedImage = null;
        g.setColor(new Color(0,0,0,0));
        if (this.yön) {
            bufferedImage = mermiSol;
        } else {
            bufferedImage = mermiSağ;
        }
        graphics2D.drawImage(bufferedImage, super.getX(), super.getY(), super.getBoyut(), super.getBoyut(), null);
    }

    /**
     * Mermi ve Ulti için Pixel görüntülerini alır
     */
    public void görüntüleriAl(String Karakter, boolean ultiMi) {
        if (ultiMi) {
            super.setBoyut(24);
            try {
                mermiSol = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Ulti/"+Karakter+"/Sol.png"));
                mermiSağ = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Ulti/"+Karakter+"/Sağ.png"));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                mermiSol = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Mermi/"+Karakter+"/sol.png"));
                mermiSağ = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Mermi/"+Karakter+"/sağ.png"));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isYön() {
        return yön;
    }

    public boolean isUltiMi() {
        return ultiMi;
    }

    public void setUltiMi(boolean ultiMi) {
        this.ultiMi = ultiMi;
    }
}

