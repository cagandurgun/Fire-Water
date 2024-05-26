import javax.swing.*;
import java.awt.*;

/**
 * Bu abstract sınıf, oyundaki canlı ({@link Karakter}, {@link Canavar},
 * {@link Mermi}) sınıflarının atasıdır. Bu sınıf sayesinde, projedeki kod tekrarı,
 * ortak özellikleri kullanmak suretiyle
 * azaltılmıştır. <p>
 * Bu sınıftaki önemli metodlar:
 * <ul>
 *     <li><strong>{@link Canlı#sağaGit(Harita, Canlı)}</strong></li>
 *     <li><strong>{@link Canlı#solaGit(Harita, Canlı)}</strong></li>
 *     <li><strong>{@link Canlı#yerÇekimi(Harita, Canlı)}</strong></li>
 *     <li><strong>{@link Canlı#canlıDuruyorMu()} </strong></li>
 *     <li><strong>{@link Canlı#canlıYerdeMi()}</strong></li>
 *
 * </ul>
 * olarak listelenebilir.
 */
public abstract class Canlı extends JPanel {
    /**
     * Canlının önceki hareket yönünün belirlenmesinde rol
     * oynayan değişkendir. {@link Canavar#hareketEt(Harita, Canlı)}
     * metodunda kullanılır.
     * <ul>
     *     <li><strong>true</strong> ise <strong>sol</strong>.</li>
     *     <li><strong>false</strong> ise <strong>sağ</strong>.</li>
     * </ul>
     * yönü ifade etmektedir.
     */
    private boolean öncekiHaraketYönü;
    private int yerÇekimi = 10;
    private Color renk;
    private int boyut;
    private int hız;
    private int can;
    private int x;
    private int y;
    private int öncekiY;
    private int öncekiX;

    /**
     * Bu Constructor {@link Mermi} sınıfına özeldir.
     */
    public Canlı(Color renk, int x, int y) {
        this.renk = renk;
        this.x = x;
        this.y = y;
    }

    /**
     * Bu Constructor {@link Canavar}lar ve
     * {@link Karakter} için ortak olarak kullanılır.
     */
    public Canlı(int x, int y, int öncekiY) {
        this.x = x;
        this.y = y;
        this.öncekiY = öncekiY;
        this.boyut = 48;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this instanceof Karakter || this instanceof Mermi) {
            g.setColor(new Color(0, 0, 0, 0));
        } else {
            g.setColor(this.renk);
        }
        g.fillRect(this.x, this.y, this.boyut, this.boyut);
    }


    /**
     * Canlılar için ortak kullanılan bu metod,
     * {@link Canlı#paintComponent(Graphics)} metoduna
     * erişmek için kullanılmaktadır.
     */
    public void çiz(Graphics g, Klavye klavye) {
        paintComponent(g);
    }

    /**
     * Canlıların yer çekimine tabi olmasından sorumludur.
     */
    public void yerÇekimi(Harita harita, Canlı canlı) {
        if (canlı instanceof Karakter) {
            yerÇekimi = 6;

            Dikdörtgen yeniDikdörtgen = new Dikdörtgen(0,"",new Color(0,0,0),0,816,0,0);
            int mesafe = yeniDikdörtgen.getY() - (canlı.getY() + canlı.getBoyut());

            for (Dikdörtgen dikdortgen : harita.dikdortgenler) {
                // bu satırda canlının, yatay düzlemde, bir duvara denk geldiği garanti edilir.

                if ((!dikdortgen.getName().equals("coin")) && (!dikdortgen.getName().equals("bitiş"))) {
                    if ((dikdortgen.getX()<=(canlı.getX()+canlı.getBoyut()))&&((dikdortgen.getX()+dikdortgen.getWidth())>= canlı.getX())) {
                        // dikdörtgen altta mı

                        if ((canlı.getY()+canlı.getBoyut())< dikdortgen.getY()) {

                            int mesafeKontrol = dikdortgen.getY() - (canlı.getY() + canlı.getBoyut());
                            if (mesafeKontrol<mesafe) {
                                mesafe = mesafeKontrol;
                                yeniDikdörtgen = dikdortgen;
                            }
                        }
                    }
                }
            }

            if (mesafe>0) {
                canlı.setÖncekiY(canlı.getY());
                if (mesafe<yerÇekimi) {
                    canlı.setY(canlı.getY() + mesafe - 1);
                } else {
                    canlı.setY(canlı.getY() + yerÇekimi);
                }
            }
            yerÇekimi = 10;
        } else if (canlı instanceof Canavar) {
            // solundaki duvarı bul // aynı hizada
            Dikdörtgen yeniDikdörtgen = new Dikdörtgen(0,"",new Color(0,0,0),0,816,0,0);
            int mesafe = yeniDikdörtgen.getY() - (canlı.getY() + canlı.getBoyut());

            for (Dikdörtgen dikdortgen : harita.dikdortgenler) {
                // bu satırda canlının, yatay düzlemde, bir duvara denk geldiği garanti edilir.

                if ((!dikdortgen.getName().equals("coin")) && (!dikdortgen.getName().equals("bitiş"))) {
                    if ((dikdortgen.getX()<=(canlı.getX()+canlı.getBoyut()))&&((dikdortgen.getX()+dikdortgen.getWidth())>= canlı.getX())) {
                        // dikdörtgen altta mı

                        if ((canlı.getY()+canlı.getBoyut())< dikdortgen.getY()) {

                            int mesafeKontrol = dikdortgen.getY() - (canlı.getY() + canlı.getBoyut());
                            if (mesafeKontrol<mesafe) {
                                mesafe = mesafeKontrol;
                                yeniDikdörtgen = dikdortgen;
                            }
                        }
                    }
                }
            }

            if (mesafe>0) {
                canlı.setÖncekiY(canlı.getY());
                if (mesafe<yerÇekimi+1) {
                    canlı.setY(canlı.getY() + mesafe - 1);
                } else {
                    canlı.setY(canlı.getY() + yerÇekimi);
                }
            }
        }
    }

    /**
     * Bir canlının sola gitmesini sağlar.
     */
    public void solaGit(Harita harita, Canlı canlı) {
        setÖncekiHaraketYönü(true);
        // solundaki duvarı bul // aynı hizada
        Dikdörtgen yeniDikdörtgen = new Dikdörtgen(0,"",new Color(0,0,0),0,0,0,0);
        int mesafe = canlı.getX() - (yeniDikdörtgen.getX()+yeniDikdörtgen.getWidth());

        for (Dikdörtgen dikdortgen : harita.dikdortgenler) {
            // bu satırda canlının, yatay düzlemde, bir duvara denk geldiği garanti edilir.

            if ((!dikdortgen.getName().equals("coin")) && (!dikdortgen.getName().equals("bitiş"))) {
                if ((dikdortgen.getY()<(canlı.getY()+canlı.getBoyut()))&&(canlı.getY()<(dikdortgen.getY()+dikdortgen.getHeight()))) {
                    // dikdörtgen solda mı

                    if ((dikdortgen.getX() + dikdortgen.getWidth()) < canlı.getX()) {

                        int mesafeKontrol = canlı.getX() - (dikdortgen.getX()+dikdortgen.getWidth());
                        if (mesafeKontrol<mesafe) {
                            mesafe = mesafeKontrol;
                            yeniDikdörtgen = dikdortgen;
                        }
                    }
                }
            }
        }
        if (mesafe>0) {
            setÖncekiX(getX());

            if (mesafe<canlı.getHız()+1) {
                canlı.setX(canlı.getX() - mesafe + 1);
            } else {
                canlı.setX(canlı.getX() - canlı.getHız());
            }
        }
    }

    /**
     * Bir canlının sağa gitmesini sağlar.
     */
    public void sağaGit(Harita harita, Canlı canlı) {
        setÖncekiHaraketYönü(false);
        // solundaki duvarı bul // aynı hizada
        Dikdörtgen yeniDikdörtgen = new Dikdörtgen(0,"",new Color(0,0,0),1536,0,0,0);
        int mesafe = yeniDikdörtgen.getX() - (canlı.getX() + canlı.getBoyut());

        for (Dikdörtgen dikdortgen : harita.dikdortgenler) {
            // bu satırda canlının, yatay düzlemde, bir duvara denk geldiği garanti edilir.
            if ((!dikdortgen.getName().equals("coin")) && (!dikdortgen.getName().equals("bitiş"))) {
                if ((dikdortgen.getY()<(canlı.getY()+canlı.getBoyut()))&&(canlı.getY()<(dikdortgen.getY()+dikdortgen.getHeight()))) {

                    if (dikdortgen.getX() > (canlı.getX() + canlı.getBoyut())) {

                        int mesafeKontrol = dikdortgen.getX() - (canlı.getX() + canlı.getBoyut());
                        if (mesafeKontrol<mesafe) {
                            mesafe = mesafeKontrol;
                            yeniDikdörtgen = dikdortgen;
                        }
                    }
                }
            }
        }
        if (mesafe>0) {
            setÖncekiX(getX());
            if (mesafe<canlı.getHız()+1) {
                canlı.setX(canlı.getX() + mesafe - 1);
            } else {
                canlı.setX(canlı.getX() + canlı.getHız());
            }
        }

    }

    /**
     * Canlıların havada olup olmadığını kontrol eden önemli
     * bir metodtur. Karakterin zıplaması için kullanılan
     * {@link Karakter#zıpla(Karakter, Harita)} metodunda ve
     * canavarların hareket etmesini sağlayan
     * {@link OyunEkranı#canavarKonumu()} metodunda kullanılmaktadır.
     */
    public boolean canlıYerdeMi() {
        return öncekiY == y;
    }

    /**
     * Bir canlının yatayda durup durmadığını belirleyen önemli bir metodtur.
     * {@link Canavar#hareketEt(Harita, Canlı)} metodunda ve
     * {@link  OyunEkranı#mermiler()} metodunda kullanılır.
     */
    public boolean canlıDuruyorMu() {
        return getÖncekiX() == getX();
    }

    /**
     * Getter's & Setter's
     */
    public Color getRenk() {
        return renk;
    }

    public void setRenk(Color renk) {
        this.renk = renk;
    }

    public int getBoyut() {
        return boyut;
    }

    public void setBoyut(int boyut) {
        this.boyut = boyut;
    }

    public int getHız() {
        return hız;
    }

    public void setHız(int hız) {
        this.hız = hız;
    }

    public int getCan() {
        return can;
    }

    public void setCan(int can) {
        this.can = can;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getÖncekiY() {
        return öncekiY;
    }

    public void setÖncekiY(int öncekiY) {
        this.öncekiY = öncekiY;
    }

    public int getÖncekiX() {
        return öncekiX;
    }

    public void setÖncekiX(int öncekiX) {
        this.öncekiX = öncekiX;
    }

    public boolean isÖncekiHaraketYönü() {
        return öncekiHaraketYönü;
    }

    public void setÖncekiHaraketYönü(boolean öncekiHaraketYönü) {
        this.öncekiHaraketYönü = öncekiHaraketYönü;
    }
}

