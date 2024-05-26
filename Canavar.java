import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Bu sınıf, oyundaki düşman canlıları, ve özelliklerini
 * içermektedir. Canavar hareketleri bu sınıf içerisindeki
 * {@link Canlı#isÖncekiHaraketYönü()} metodu ve
 * {@link Canavar#hareketEt(Harita, Canlı)} metodu sayesinde
 * gerçekleşir. Oyunda üç çeşit canavar bulunmaktadır. Bunlar:
 * <ul>
 *     <li><strong>{@link TuruncuCanavar}</strong></li>
 *     <li><strong>{@link KırmızıCanavar}</strong></li>
 *     <li><strong>{@link MaviCanavar}</strong></li>
 *     <li><strong>{@link YeşilCanavar}</strong></li>
 * </ul>
 * şeklinde listelenir.
 */
public class Canavar extends Canlı {

    private boolean koşuSeç;

    private BufferedImage sağ1,sağ2,sol1,sol2,solDüş,sağDüş;

    /**
     * Canavar için Constructor.
     */
    public Canavar(int x, int y, int öncekiX) {
        super(x, y, öncekiX);
        super.setHız(3);
    }

    public void görüntüleriAl(String canavar) {
        try {
            sağ1 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Canavar/"+canavar+"/Sağ/Koşuyor/1.png"));
            sağ2 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Canavar/"+canavar+"/Sağ/Koşuyor/2.png"));
            sol1 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Canavar/"+canavar+"/Sol/Koşuyor/1.png"));
            sol2 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Canavar/"+canavar+"/Sol/Koşuyor/2.png"));
            solDüş = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Canavar/"+canavar+"/Sol/Düşüyor.png"));
            sağDüş = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Canavar/"+canavar+"/Sağ/Düşüyor.png"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //@Override
    public void çiz(Graphics g, Klavye klavye) {
        super.çiz(g, klavye);
        Graphics2D graphics2D = (Graphics2D)g;
        BufferedImage bufferedImage = null;
        switch (yönTayini(klavye)) {
            case "sola_düş":
                bufferedImage = getSolDüş();
                break;
            case "sağa_düş":
                bufferedImage = getSağDüş();
                break;
            case "sola_koş":
                if (koşuSeç) {
                    bufferedImage = getSol1();
                } else bufferedImage = getSol2();
                break;
            case "sağa_koş":
                if (koşuSeç) {
                    bufferedImage = getSağ2();
                } else bufferedImage = getSağ1();
                break;
            default:
                break;
        }
        graphics2D.drawImage(bufferedImage, super.getX(), super.getY(), super.getBoyut(), super.getBoyut(), null);
    }

    /**
     * Bu metod oldukça önemldir. Canavarın yön tayinini yapar. {@link Karakter#çiz(Graphics, Klavye)} metodu ile ortak çalışır.
     */
    private String yönTayini(Klavye klavye) {
        String Yön = "";
        if (!canlıYerdeMi()) {
            if ((getY()-getÖncekiY())>0) {
                //düşüyor
                if (isÖncekiHaraketYönü()) {
                    Yön = "sola_düş";
                } else {
                    Yön = "sağa_düş";
                }

            }

        } else {
            if (isÖncekiHaraketYönü()) {
                Yön = "sola_koş";
            } else {
                Yön = "sağa_koş";
            }

        }
        return Yön;
    }


    /**
     * Oyundaki canavarların hareket edebilmesi için
     * basit bir kontrol yapısıdır.
     * <P>{@link Canavar#canlıDuruyorMu()} metodu
     * ile birlikte kullanılır
     */
    public void hareketEt(Harita harita, Canlı canlı) {
        if (canlıDuruyorMu()) {
            setÖncekiHaraketYönü(!isÖncekiHaraketYönü());
        }
        if (isÖncekiHaraketYönü()) {
            solaGit(harita, canlı);
        } else {
            sağaGit(harita, canlı);
        }
    }

    public BufferedImage getSağ1() {
        return sağ1;
    }

    public void setSağ1(BufferedImage sağ1) {
        this.sağ1 = sağ1;
    }

    public BufferedImage getSağ2() {
        return sağ2;
    }

    public void setSağ2(BufferedImage sağ2) {
        this.sağ2 = sağ2;
    }

    public BufferedImage getSol1() {
        return sol1;
    }

    public void setSol1(BufferedImage sol1) {
        this.sol1 = sol1;
    }

    public BufferedImage getSol2() {
        return sol2;
    }

    public void setSol2(BufferedImage sol2) {
        this.sol2 = sol2;
    }

    public BufferedImage getSolDüş() {
        return solDüş;
    }

    public void setSolDüş(BufferedImage solDüş) {
        this.solDüş = solDüş;
    }

    public BufferedImage getSağDüş() {
        return sağDüş;
    }

    public void setSağDüş(BufferedImage sağDüş) {
        this.sağDüş = sağDüş;
    }

    public boolean isKoşuSeç() {
        return koşuSeç;
    }

    public void KoşuSeçToggle() {
        koşuSeç = !koşuSeç;
    }
}

/**
 * Turuncu tip canavarlar ölümsüzdür. Ölümsüzlüğünü, görece
 * sonsuz canından alır.
 */
class TuruncuCanavar extends Canavar {

    /**
     * Canavar için Constructor.
     *
     * @param x
     * @param y
     * @param öncekiX
     */
    public TuruncuCanavar(int x, int y, int öncekiX) {
        super(x, y, öncekiX);
        super.setCan(999990000);
        super.setRenk(new Color(0, 0, 0, 0));
        görüntüleriAl("Turuncu");
        repaint();
    }
}

class KırmızıCanavar extends Canavar {

    /**
     * Canavar için Constructor.
     *
     * @param x
     * @param y
     * @param öncekiX
     */
    public KırmızıCanavar(int x, int y, int öncekiX) {
        super(x, y, öncekiX);
        super.setCan(3);
        super.setRenk(new Color(0, 0, 0, 0));
        görüntüleriAl("Kırmızı");
        repaint();
    }
}

class MaviCanavar extends Canavar {

    /**
     * Canavar için Constructor.
     *
     * @param x
     * @param y
     * @param öncekiX
     */
    public MaviCanavar(int x, int y, int öncekiX) {
        super(x, y, öncekiX);
        super.setCan(3);
        super.setRenk(new Color(0, 0, 0, 0));// değiştirince rastgele canavar üretden de deistir ve diğerini de
        görüntüleriAl("Mavi");
        repaint();
    }
}

/**
 * WildCard özelliği bu canavarda görülmektedir.
 * <p> Yeşil canavar, karaktere değdiğinde oyunu
 * baştan başlatmak gerekmez. Ancak bu canavar,
 * kullanıcının r çap yakınında, t süre boyunca
 * kalırsa patlama yapar (<strong>Minecraft Creeper</strong>). Eğer
 * kullanıcı bu patlamanın çapından etkilenirse oyun
 * baştan başlar.
 * <p>Bu özelliklerine karşın canları</p>
 * <p>Patlama öncesinde ve sırasında da görsel olarak değişir ve uyarır.
 * <p>Canı 50 dir. Ve bu karakter öldürüldüğünde bir ulti hakkı doğar.
 * <p>bu ulti duvarları kırmak için kullanılabilir.
 */
class YeşilCanavar extends Canavar {
    private boolean tetik;
    private boolean yakındaMı;
    private final int Sabit = 50;

    /**
     * Canavar için Constructor.
     *
     * @param x
     * @param y
     * @param öncekiX
     */
    public YeşilCanavar(int x, int y, int öncekiX) {
        super(x, y, öncekiX);
        super.setCan(Sabit);
        super.setRenk(new Color(0, 0, 0, 0));
        super.setHız(1);
        yakındaMı = false;
        tetik = false;
        görüntüleriAl("Yeşil");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(getRenk());
        g.fillRoundRect(getX(), getY() - (Sabit - getCan()),getBoyut()+ (Sabit - getCan()),getBoyut() + (Sabit - getCan()) ,(Sabit - getCan()),(Sabit - getCan()));
        float kalınlık = 3.0f;
        g2d.setStroke(new BasicStroke(kalınlık));
        g.setColor(Color.RED);
        g.drawRoundRect(getX()- (Sabit - getCan()), getY() - (Sabit - getCan()),getBoyut()+ ((Sabit - getCan())*2),getBoyut() + ((Sabit - getCan())*2) ,(Sabit - getCan()),(Sabit - getCan()));
    }

    /**
     * WildCard'ın modüler kullanımı için bir metod.
     */
    public void çalıştır(Karakter karakter, OyunEkranı oyunEkranı) {
        karakterYakındaMı(karakter);
        if (yakındaMı) {
            oyunEkranı.setPatlamaGerçekleştiMi(true);
        }
    }

    /**
     * Yeşil canavar için karakterin yakında olup olmadığını belirleyen metod.
     */
    private void karakterYakındaMı(Karakter karakter) {
        int creeperX = this.getX()- ((Sabit - this.getCan())/2);
        int creeperY = this.getY() - (int) Math.round(((Sabit - this.getCan()) / 0.6875));
        int creeperBoyut = this.getBoyut()+ ((Sabit - this.getCan())*2);
        boolean bool1 = ((creeperY<=(karakter.getY()+karakter.getBoyut()))&&(karakter.getY()<=(creeperY+creeperBoyut)));
        boolean bool2 = ((creeperX<(karakter.getX()+karakter.getBoyut()))&&((creeperX+creeperBoyut)> karakter.getX()));
        yakındaMı = bool1 && bool2;
        repaint();
    }
}

