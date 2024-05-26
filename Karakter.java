import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Oyunda kullanılan karakterlerin üst sınıfıdır. Kullanıcının
 * düşmanlara ateş etmesini ({@link Karakter#ateşEt(Karakter)})
 * ve zıplamasını ({@link Karakter#zıpla(Karakter, Harita)})
 * sağlayan metodlar bu sınıf altında tanımlanmıştır.
 */
public class Karakter extends Canlı {
    private boolean koşuSeç;
    private boolean ulti;
    private int ultiHakkı;
    private int duruşSeç;
    private int zıplamaKuvveti;

    private ArrayList<Mermi> mermis = new ArrayList<>();
    private BufferedImage sağDur1,sağDur2,sağDur3,solDur1,solDur2,solDur3,sağ1,sağ2,sol1,sol2,solDüş,sağDüş,solZıpla,sağZıpla;

    public Karakter(int x, int y, int öncekiY, int ultiHakkı) {
        super(x, y, öncekiY);
        super.setHız(5);
        duruşSeç = 1;
        this.ultiHakkı = ultiHakkı;
        ulti = false;
    }

    /**
     * Pixel art görüntüleri al
     */
    public void görüntüleriAl(String Karakter) {
        try {
            sağDur1 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sağ/Duruyor/1.png"));
            sağDur2 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sağ/Duruyor/2.png"));
            sağDur3 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sağ/Duruyor/3.png"));
            solDur1 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sol/Duruyor/1.png"));
            solDur2 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sol/Duruyor/2.png"));
            solDur3 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sol/Duruyor/3.png"));
            sağ1 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sağ/Koşuyor/1.png"));
            sağ2 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sağ/Koşuyor/2.png"));
            sol1 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sol/Koşuyor/1.png"));
            sol2 = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sol/Koşuyor/2.png"));
            solDüş = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sol/Düşüyor.png"));
            sağDüş = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sağ/Düşüyor.png"));
            solZıpla = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sol/Zıplıyor.png"));
            sağZıpla = ImageIO.read(getClass().getResourceAsStream("/Canlılar/Karakter/"+Karakter+"/Sağ/Zıplıyor.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bu metod oldukça önemldir. Karakter davranışına göre pixel image seçer.
     */
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
            case "sağa_zıpla":
                bufferedImage = getSağZıpla();
                break;
            case "sola_zıpla":
                bufferedImage = getSolZıpla();
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
            case "sağ_dur":
                if (duruşSeç == 1) {
                    bufferedImage = getSağDur1();
                } else if (duruşSeç == 2) {
                    bufferedImage = getSağDur2();
                } else if (duruşSeç == 3) {
                    bufferedImage = getSağDur3();
                }
                break;
            case "sol_dur":
                if (duruşSeç == 1) {
                    bufferedImage = getSolDur1();
                } else if (duruşSeç == 2) {
                    bufferedImage = getSolDur2();
                } else if (duruşSeç == 3) {
                    bufferedImage = getSolDur3();
                }
                break;
            default:
                break;
        }
        graphics2D.drawImage(bufferedImage, super.getX(), super.getY(), super.getBoyut(), super.getBoyut(), null);
    }

    /**
     *  WildCard ile kazanılan
     */
    public void ultiAt(Karakter karakter, Harita harita) {
        if (ateşYönü()) {
            mermis.add(new Mermi(karakter.getRenk(), karakter.getX(),karakter.getY() + (karakter.getBoyut()/2) - 5, true, true));
        } else {
            mermis.add(new Mermi(karakter.getRenk(), karakter.getX() + karakter.getBoyut(),karakter.getY() + (karakter.getBoyut()/2) - 5, false,true));
        }
    }

    /**
     * Bu metod oldukça önemldir. Karakterin yön tayinini yapar. {@link Karakter#çiz(Graphics, Klavye)} metodu ile ortak çalışır.
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

            } else {
                //zıplıyor
                if (isÖncekiHaraketYönü()) {
                    Yön = "sola_zıpla";
                } else {
                    Yön = "sağa_zıpla";
                }
            }
        } else {
            if (!klavye.isSağ_tuşu()&&!klavye.isSol_tuşu())  {
                if (isÖncekiHaraketYönü()) {
                    Yön = "sol_dur";
                } else {
                    Yön = "sağ_dur";
                }
            } else {
                if (isÖncekiHaraketYönü()) {
                    Yön = "sola_koş";
                } else {
                    Yön = "sağa_koş";
                }
            }

        }
        return Yön;
    }

    /**
     * Karakterin ateş etmesini sağlar. Yeni {@link Mermi} üretir.
     */
    public void ateşEt(Karakter karakter) {
        if (ateşYönü()) {
            mermis.add(new Mermi(karakter.getRenk(), karakter.getX(),karakter.getY() + (karakter.getBoyut()/2) - 5, true, false));
        } else {
            mermis.add(new Mermi(karakter.getRenk(), karakter.getX() + karakter.getBoyut(),karakter.getY() + (karakter.getBoyut()/2) - 5, false,false));
        }
    }

    /**
     * Ateş eden karakterlerin hangi yöne ateş etmesi gerektiğini
     * belirler. {@link Karakter#ateşEt(Karakter)} metodu ile
     * birlikte çalışmaktadır.
     */
    private boolean ateşYönü() {
        return getÖncekiX() > getX();
    }

    /**
     * Bir karakterin zıplamasını sağlayan metod.
     */
    public void zıpla(Karakter karakter, Harita harita) {
        if(canlıYerdeMi()) {
            // solundaki duvarı bul // aynı hizada
            Dikdörtgen yeniDikdörtgen = new Dikdörtgen(0,"",new Color(0,0,0),0,0,0,0);
            int mesafe = karakter.getY() - (yeniDikdörtgen.getY()+yeniDikdörtgen.getHeight());

            for (Dikdörtgen dikdortgen : harita.dikdortgenler) {
                // bu satırda canlının, yatay düzlemde, bir duvara denk geldiği garanti edilir.
                if ((!dikdortgen.getName().equals("coin")) && (!dikdortgen.getName().equals("bitiş"))) {
                    if ((dikdortgen.getX()<=(karakter.getX()+karakter.getBoyut()))&&((dikdortgen.getX()+dikdortgen.getWidth())>= karakter.getX())) {
                        // dikdörtgen üstte mı

                        if (karakter.getY()> (dikdortgen.getY()+dikdortgen.getHeight())) {

                            int mesafeKontrol = karakter.getY() - (dikdortgen.getY()+dikdortgen.getHeight());
                            if (mesafeKontrol<mesafe) {
                                mesafe = mesafeKontrol;
                                yeniDikdörtgen = dikdortgen;
                            }
                        }
                    }
                }
            }

            if (mesafe>0) {
                if (mesafe<zıplamaKuvveti) {
                    setY(getY() - mesafe + 1);
                } else {
                    for (int i = 0 ; i< zıplamaKuvveti; i++) {
                        setY(getY()-1);
                        repaint();
                    }
                }
            }
        }
    }

    public void setZıplamaKuvveti(int zıplamaKuvveti) {
        this.zıplamaKuvveti = zıplamaKuvveti;
    }

    public ArrayList<Mermi> getMermis() {
        return mermis;
    }

    public void setMermis(ArrayList<Mermi> mermis) {
        this.mermis = mermis;
    }

    public int getZıplamaKuvveti() {
        return zıplamaKuvveti;
    }

    public BufferedImage getSağDur1() {
        return sağDur1;
    }

    public void setSağDur1(BufferedImage sağDur1) {
        this.sağDur1 = sağDur1;
    }

    public BufferedImage getSağDur2() {
        return sağDur2;
    }

    public void setSağDur2(BufferedImage sağDur2) {
        this.sağDur2 = sağDur2;
    }

    public BufferedImage getSağDur3() {
        return sağDur3;
    }

    public void setSağDur3(BufferedImage sağDur3) {
        this.sağDur3 = sağDur3;
    }

    public BufferedImage getSolDur1() {
        return solDur1;
    }

    public void setSolDur1(BufferedImage solDur1) {
        this.solDur1 = solDur1;
    }

    public BufferedImage getSolDur2() {
        return solDur2;
    }

    public void setSolDur2(BufferedImage solDur2) {
        this.solDur2 = solDur2;
    }

    public BufferedImage getSolDur3() {
        return solDur3;
    }

    public void setSolDur3(BufferedImage solDur3) {
        this.solDur3 = solDur3;
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

    public BufferedImage getSolZıpla() {
        return solZıpla;
    }

    public void setSolZıpla(BufferedImage solZıpla) {
        this.solZıpla = solZıpla;
    }

    public BufferedImage getSağZıpla() {
        return sağZıpla;
    }

    public void setSağZıpla(BufferedImage sağZıpla) {
        this.sağZıpla = sağZıpla;
    }

    public void KoşuSeçToggle() {
        koşuSeç = !koşuSeç;
    }

    public void setKoşuSeç(boolean koşuSeç) {
        this.koşuSeç = koşuSeç;
    }

    public int getDuruşSeç() {
        return duruşSeç;
    }

    public void duruşDeğiştir() {
        if(this.duruşSeç == 3) {
            duruşSeç = 0;
        }
        this.duruşSeç++;
    }

    public boolean isUlti() {
        return ulti;
    }

    public void setUlti(boolean ulti) {
        this.ulti = ulti;
    }

    public int getUltiHakkı() {
        return ultiHakkı;
    }

    public void setUltiHakkı(int ultiHakkı) {
        this.ultiHakkı = ultiHakkı;
    }
}

class KırmızıKarakter extends Karakter {
    public KırmızıKarakter(int x, int y, int öncekiY, int ultiHakkı) {
        super(x, y, öncekiY, ultiHakkı);
        super.setRenk(new Color(255, 0, 0));
        this.setZıplamaKuvveti(100);
        super.görüntüleriAl("Kırmızı");
        repaint();
    }
}

class MaviKarakter extends Karakter {
    public MaviKarakter(int x, int y, int öncekiY, int ultiHakkı) {
        super(x, y, öncekiY, ultiHakkı);
        super.setRenk(new Color(0, 0, 255));
        this.setZıplamaKuvveti(100);
        super.görüntüleriAl("Mavi");
        repaint();
    }
}

class MorKarakter extends Karakter {
    public MorKarakter(int x, int y, int öncekiY, int ultiHakkı) {
        super(x, y, öncekiY, ultiHakkı);
        super.setRenk(new Color(255, 0, 255));
        this.setZıplamaKuvveti(200);
        super.görüntüleriAl("Mor");
        repaint();
    }
}

