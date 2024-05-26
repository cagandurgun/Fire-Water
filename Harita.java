import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Bu sınıfta oyun haritası üretilir, denetlenir. Coin işlemleri
 * yapılır. Sınıfın en önemli metodlarından biri {@link  Harita#haritayiAl(ArrayList)}
 * metodudur.
 */
public class Harita extends JPanel {
    private boolean coinSeç;
    private BufferedImage coin1, coin2;
    ArrayList<Dikdörtgen> dikdortgenler = new ArrayList<>();

    public Harita(ArrayList<Dikdörtgen> çıkarılacakCoinler) {
        coinSeç = true;
        dikdortgenler = haritayiAl(çıkarılacakCoinler);
        coinleriAl();
    }

    private void coinleriAl() {
        try {
            coin1 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Para/1.png"));
            coin2 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Para/2.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void coinToggle() {
        coinSeç = !coinSeç;
    }

    // @Override
    protected void paintComponent(Graphics g, Dikdörtgen dikdortgen) {
        if(dikdortgen.getName().equals("coin")) {
            super.paintComponent(g);

            Graphics2D graphics2D = (Graphics2D)g;

            if (coinSeç) {
                graphics2D.drawImage(coin1, dikdortgen.getX(), dikdortgen.getY(), dikdortgen.getWidth(), dikdortgen.getHeight(), null);
            } else {
                graphics2D.drawImage(coin2, dikdortgen.getX(), dikdortgen.getY(), dikdortgen.getWidth(), dikdortgen.getHeight(), null);
            }
            repaint();
        } else {
            super.paintComponent(g);

            g.setColor(dikdortgen.getColor());
            g.fillRect(dikdortgen.getX(), dikdortgen.getY(), dikdortgen.getWidth(), dikdortgen.getHeight());
            repaint();
        }
    }

    public void çiz(Graphics g) {
        for (Dikdörtgen dikdortgen : dikdortgenler) {
            paintComponent(g, dikdortgen);
        }
    }

    /**
     * Coinlerin Yerleştirilmesinden sorumlu metod
     */
    private void coinleriYerlestir(ArrayList<Dikdörtgen> dikdortgenler) {
        ArrayList<Dikdörtgen> yeniDikdortgenler = new ArrayList<>();
        for (Dikdörtgen dikdortgen : dikdortgenler) {
            if (dikdortgen.getHeight() < dikdortgen.getWidth()) {
                for (int i = 5; i < dikdortgen.getWidth(); i = i + 33) {
                    if (dikdortgen.getCoin() == 1) {
                        Dikdörtgen temp = new Dikdörtgen(1,"coin", new Color(255, 255, 0, 255), dikdortgen.getX() + i, dikdortgen.getY() - 10, 5, 5);
                        yeniDikdortgenler.add(temp);
                    }
                }
            }
        }

        // Add the new elements to the main list
        dikdortgenler.addAll(yeniDikdortgenler);
    }

    /**
     * Oyun Sonu Kontrolü
     */
    public void pembeKapı(OyunEkranı oyunEkranı, Karakter karakter) {
        for (Dikdörtgen dikdortgen : dikdortgenler) {
            if (dikdortgen.getName().equals("bitiş")) {
                boolean bool1 = ((dikdortgen.getY()<=(karakter.getY()+karakter.getBoyut()))&&(karakter.getY()<=(dikdortgen.getY()+dikdortgen.getHeight())));
                boolean bool2 = ((dikdortgen.getX()<(karakter.getX()+karakter.getBoyut()))&&((dikdortgen.getX()+dikdortgen.getWidth())> karakter.getX()));
                if(bool1&&bool2) {
                    oyunEkranı.oyunBittiMi = true;
                }
            }
        }
    }

    /**
     * Bu metod, <strong>harita.txt</strong> dosyasındaki harita verilerinden,
     * oyunun haritasını oluşturur.
     */
    private ArrayList<Dikdörtgen> haritayiAl(ArrayList<Dikdörtgen> çıkarılacakCoinler) {
        ArrayList<Dikdörtgen> dikdortgenler = new ArrayList<>();
        String dosyaAdi = "harita.txt";

        try {
            // FileReader ve BufferedReader kullanarak dosyayı okuma işlemi
            FileReader dosyaOkuyucu = new FileReader(dosyaAdi);
            BufferedReader okuyucu = new BufferedReader(dosyaOkuyucu);

            // Dosyanın ilk satırını atla
            okuyucu.readLine();

            // Dosyadan bir sonraki satırı oku
            String satir = okuyucu.readLine();

            while (satir != null) {
                // Satırı boşluğa göre parçala ve sayıları elde et
                String[] sayilar = satir.split(" ");

                Dikdörtgen dikdortgen = new Dikdörtgen(
                        Integer.parseInt(sayilar[0]),
                        sayilar[1],
                        rengiNe(sayilar[2]),
                        Integer.parseInt(sayilar[3]),
                        Integer.parseInt(sayilar[4]),
                        Integer.parseInt(sayilar[5]),
                        Integer.parseInt(sayilar[6])
                );

                // Elde edilen dikdörtgeni koleksiyona ekle
                dikdortgenler.add(dikdortgen);

                // Bir sonraki satırı oku
                satir = okuyucu.readLine();
            }

            // Dosyayı kapat
            okuyucu.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        coinleriYerlestir(dikdortgenler);

        ArrayList<Dikdörtgen> kaldırılacaklar = new ArrayList<>();
        for (Dikdörtgen dikdörtgen : dikdortgenler) {
            for (Dikdörtgen coin : çıkarılacakCoinler) {
                if (dikdörtgen.getName().equals("coin")) {
                    if (coin.getX() == dikdörtgen.getX() && coin.getY() == dikdörtgen.getY()) {
                        kaldırılacaklar.add(dikdörtgen);
                    }
                }
            }
        }

        dikdortgenler.removeAll(kaldırılacaklar);
        repaint();
        return dikdortgenler;
    }

    /**
     * <strong>harita.txt</strong> dosyasında yazı ile yazılmış rengi {@link Color} objesine dönüştürür.
     */
    private Color rengiNe(String string) {
        Color color;
        switch (string) {
            case "gri":
                color = new Color(28, 28, 28);
                break;
            case "kırmızı":
                color = new Color(255, 0, 0);
                break;
            case "mavi":
                color = new Color(0, 0, 255);
                break;
            case "pembe":
                color = new Color(255, 0, 255, 255);
                break;
            default:
                color = new Color(255, 255, 255);
                break;
        }
        return color;
    }

    /**
     * Coinlerin Puan Kontrolü İçin Kullanılır.
     */
    public Dikdörtgen coinKontrol(Karakter karakter) {
        for (Dikdörtgen dikdortgen : dikdortgenler) {
            if (dikdortgen.getName().equals("coin")) {

                boolean bool1 = ((dikdortgen.getY()<=(karakter.getY()+karakter.getBoyut()))&&(karakter.getY()<=(dikdortgen.getY()+dikdortgen.getHeight())));
                boolean bool2 = ((dikdortgen.getX()<(karakter.getX()+karakter.getBoyut()))&&((dikdortgen.getX()+dikdortgen.getWidth())> karakter.getX()));
                if(bool1&&bool2) {
                    repaint();
                    return dikdortgen;
                }
            }
        }
        return null;
    }

    /**
     * {@link OyunEkranı#yanmaKontrolü()} metodu için kullanılır.
     */
    public ArrayList<Dikdörtgen> DeğmekteOlduğuDikdörtgenler(Karakter karakter) {
        ArrayList<Dikdörtgen> döndür = new ArrayList<>();
        int yakınlık = 2; // Dikdörtgene olan yakınlık kontrolü

        for (Dikdörtgen dikdortgen : dikdortgenler) {
            Color renk = dikdortgen.getColor();
            if (!(
                    renk.equals(new Color(28, 28, 28)) ||
                            renk.equals(new Color(250, 130, 200, 255)) ||
                            renk.equals(new Color(255, 255, 0, 255))
            )) {
                boolean bool1 = (dikdortgen.getY() < (karakter.getY() + karakter.getBoyut() + yakınlık))
                        && (karakter.getY() < (dikdortgen.getY() + dikdortgen.getHeight() + yakınlık));
                boolean bool2 = (dikdortgen.getX() < (karakter.getX() + karakter.getBoyut() + yakınlık))
                        && ((dikdortgen.getX() + dikdortgen.getWidth() + yakınlık) > karakter.getX());

                if (bool1 && bool2) {
                    döndür.add(dikdortgen);
                }
            }
        }

        return döndür;
    }
}

