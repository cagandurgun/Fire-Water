import java.awt.*;
import java.awt.Color;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Timer;
import java.util.Random;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Oyunun en temel sınıfıdır. Oyun ile ilgili çoğu mantıksal
 * işlem bu sınıf içerisinde gerçekleştirilir.
 * <p>
 * Oyun döngüsünü sağlayan {@link OyunEkranı#run()} metoduna bakın.
 */
public class OyunEkranı extends JPanel implements Runnable {

    /**
     * Ekran ile ilgili değişkenler...
     */
    final int normalKaroBoyutu = 16;
    final int ölçek = 3;
    final int karoBoyutu = normalKaroBoyutu * ölçek;
    final int azamiSütun = 32;
    final int azamiSatır = 17;
    final int ekranGenişliği = karoBoyutu * azamiSütun; // 1536
    final int ekranYüksekliği = karoBoyutu * azamiSatır; // 768

    /**
     * Saniye Başına Kare (SBK)
     */
    final int SBK = 60;

    /**
     * Oyun durumu ile ilgili değişkenler.
     */
    private boolean oyunDurdu = false;
    boolean yanmaKontrolüYapsınMı;
    boolean oyunBittiMi;
    private final Timer timer = new Timer();
    private int görrselİçinSayaç;
    private boolean patlamaGerçekleştiMi;

    List<Canlı> canlılar = new CopyOnWriteArrayList<>();
    ÜstÇubuk üstÇubuk = new ÜstÇubuk();
    Klavye klavye = new Klavye();
    Karakter karakter = null;
    Thread oyunThread;
    Harita harita;
    ArrayList<Dikdörtgen> çıkarılacakCoinler = new ArrayList<>();

    /**
     * Kaydedilmiş oyun için Constructor
     **/
    public OyunEkranı(String dosyaYolu) {
        görrselİçinSayaç = 0;
        oyunBittiMi = false;
        yanmaKontrolüYapsınMı = true;
        patlamaGerçekleştiMi = false; // bunu dosyadan alabiliriz

        this.setPreferredSize(new Dimension(ekranGenişliği, ekranYüksekliği));
        this.setBackground(new Color(100, 100, 100));
        this.setDoubleBuffered(true);
        this.addKeyListener(klavye);
        this.setFocusable(true);

        oyunuKayıttanBaşlat(dosyaYolu);

        canlılar.add(karakter);
        harita = new Harita(çıkarılacakCoinler);

        this.setLayout(new BorderLayout());
        this.add(üstÇubuk, BorderLayout.NORTH);
        rastgeleCanavarÜret();

        // TimerTask kullanarak periyodik olarak canavar üretimini başlat
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int canavarSayisi = 0;

                for (Canlı canlı : canlılar) {
                    if (canlı instanceof Canavar) {
                        canavarSayisi++;
                    }
                }

                if (canavarSayisi < 20) {
                    rastgeleCanavarÜret();
                }
            }
        }, 30000, 50000); // İlk üretimden hemen sonra başla, ardından her 5 saniyede bir üret
    }

    /**
     * Yeni oyun için Constructor
     **/
    public OyunEkranı() {
        görrselİçinSayaç = 0;
        oyunBittiMi = false;
        yanmaKontrolüYapsınMı = true;
        patlamaGerçekleştiMi = false;

        this.setPreferredSize(new Dimension(ekranGenişliği, ekranYüksekliği));
        this.setBackground(new Color(100, 100, 100));
        this.setDoubleBuffered(true);
        this.addKeyListener(klavye);
        this.setFocusable(true);

        karakter = new KırmızıKarakter(50,700,499, 0);
        canlılar.add(karakter);
        harita = new Harita(çıkarılacakCoinler);

        canavarDoldur();

        this.setLayout(new BorderLayout());
        this.add(üstÇubuk, BorderLayout.NORTH);
        rastgeleCanavarÜret();

        // TimerTask kullanarak periyodik olarak canavar üretimini başlat
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int canavarSayisi = 0;

                for (Canlı canlı : canlılar) {
                    if (canlı instanceof Canavar) {
                        canavarSayisi++;
                    }
                }

                if (canavarSayisi < 20) {
                    rastgeleCanavarÜret();
                }
            }
        }, 30000, 30000); // İlk üretimden hemen sonra başla, ardından her 5 saniyede bir üret
    }

    /**
     * Oyun Thread'ini başlatır.
     */
    public void oyunThreadBaşlat() {
        oyunThread = new Thread(this);
        oyunThread.start();
    }

    /**
     * {@link Runnable} sınıfına ait olan bu metod, oyun ile
     * ilgili değişkenlerin düzgün bir şekilde güncellenmesine
     * olanak tanır. Güncelleme işlemleri modülerlik hedefi ile
     * {@link OyunEkranı#güncelle()} metodunda yapılmıştır.
     * */
    @Override
    public void run() {
        double sıklık = (double) 1000000000 / SBK;
        double sonrakiÇizimAnı = System.nanoTime() + sıklık;

        while (oyunThread != null) {
            // görsellerin değişitilmesi
            if (görrselİçinSayaç%15 == 0) {
                karakter.KoşuSeçToggle();
                for (Canlı canlı : canlılar) {
                    if (canlı instanceof Canavar) {
                        ((Canavar) canlı).KoşuSeçToggle();
                    }
                }
            }

            if (görrselİçinSayaç%20 == 0) {
                harita.coinToggle();
            }

            if (görrselİçinSayaç == 30) {
                karakter.duruşDeğiştir();
                görrselİçinSayaç = 0;
            }
            görrselİçinSayaç++;

            // Oyun durdurulduğunda belli tuşların okunabilmesi için gereklidir.
            oyunDışıTuşTakımı();
            // Oyunun durdurulması.
            if(!oyunDurdu) {
                // Oyun durumları (veriler) güncellenir
                güncelle();

                // Çizimler güncellenir.
                repaint();
            }

            try {
                double kalanZaman = sonrakiÇizimAnı - System.nanoTime();
                kalanZaman = kalanZaman / 1000000;

                if (kalanZaman < 0) {
                    kalanZaman = 0;
                }

                Thread.sleep((long) kalanZaman);
                sonrakiÇizimAnı = sonrakiÇizimAnı + sıklık;
            } catch (InterruptedException e) {
                System.err.println("Oyun thread'i kesildi. " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Oyun durdurulduğunda gerekli tuşları okur. {@link Klavye}
     * sınıfını kullanır
     */
    private void oyunDışıTuşTakımı() {
        if (klavye.isP_tuşu()) {
            oyunToggle();
            klavye.setP_tuşu(false);
        }
        if (klavye.isK_tuşu()) {
            if (oyunDurdu) {
                // oyun kayıt işlemleri
                oyunuKaydet();
            }
            klavye.setK_tuşu(false);
        }
    }

    /**
     * {@link JPanel} sınıfından gelir. Çizim işlemleri için kullanılır.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        harita.çiz(g);

        for (Canlı canlı : canlılar) {
            canlı.çiz(g, klavye);
        }

        for (Mermi mermi : karakter.getMermis()) {
            mermi.çiz(g, klavye);
        }
    }

    /**
     * Bu metod, oyun ile ilgili birçok durumun güncellenmesini
     * sağlayan diğer metodları çağırır.
     */
    public void güncelle() {
        wildCard();

        canavarKonumu();

        yanmaKontrolü();

        yerÇekimi();

        tuşTakımı();

        oyunSonu();

        puanlar();

        mermiler();
    }

    /**
     * <strong>WildCard</strong> için kontrol metodu.
     */
    private void wildCard() {
        for (Canlı canavar : canlılar) {
            if (canavar instanceof YeşilCanavar) {
               if(!patlamaGerçekleştiMi) {
                   ((YeşilCanavar) canavar).çalıştır(karakter, this);
               } else {
                   oyunDurumunuSıfırla();
               }
            }
        }
    }

    /**
     * Canavar konumlarının Güncellenmesinden sorumludur.
     */
    private void canavarKonumu() {
        for (Canlı canlı : canlılar) {
            if (canlı instanceof Canavar) {
                if(canlı.canlıYerdeMi()) {
                    Canavar temp = (Canavar) canlı;
                    canlılar.remove(temp);
                    temp.hareketEt(harita, canlı);
                    canlılar.add(temp);
                }
            }
        }
    }

    /**
     * Karakterin yanlış bir yüzeye değip değmediğini denetler. <p> C tuşu ile aç/kapa.
     */
    private void yanmaKontrolü() {
        // Haritada yanlış bir yere değdi mi?
        if (yanmaKontrolüYapsınMı) {
            ArrayList<Dikdörtgen> dikdörtgens = harita.DeğmekteOlduğuDikdörtgenler(karakter);
            for (Dikdörtgen dikdörtgen : dikdörtgens) {
                if (!karakter.getRenk().equals(dikdörtgen.getColor())) {
                    oyunDurumunuSıfırla();
                }
            }

            // Bir canavara değdi mi?
            for (Canlı canlı : canlılar) {
                if (canlı instanceof Canavar) {
                    // karakter canavar deyiiyor mu
                    boolean bool1 = ((canlı.getY()<=(karakter.getY()+karakter.getBoyut()))&&(karakter.getY()<=(canlı.getY()+canlı.getBoyut())));
                    boolean bool2 = ((canlı.getX()<(karakter.getX()+karakter.getBoyut()))&&((canlı.getX()+canlı.getBoyut())> karakter.getX()));
                    if(bool1&&bool2) {
                        oyunDurumunuSıfırla();
                    }
                }
            }
        }
    }

    /**
     * Karakter yanlış bir yüzeye değdi ise oyunu sıfırlar.
     */
    private void oyunDurumunuSıfırla() {
        çıkarılacakCoinler.removeAll(çıkarılacakCoinler);
        patlamaGerçekleştiMi = false;
        canlılar.removeAll(canlılar);
        üstÇubuk.setPuan(0);
        // burada özellikle kullanıcının ulti hakkı iade edilmiştir.
        // bu kullanım istenmiyorsa aşağıdaki yorum satırındaki kod kullanılabilir
        // karakter = new KırmızıKarakter(100,700,499, 0);
        karakter = new KırmızıKarakter(100,700,499, karakter.getUltiHakkı());
        canlılar.add(karakter);
        harita = new Harita(çıkarılacakCoinler);
        canavarDoldur();
        System.out.println("Oyun durumu sıfırlandı.");
    }

    /**
     * Mermi hariç tüm canlılar için yer çekimi uygular .
     */
    private void yerÇekimi() {
        for (Canlı canlı : canlılar) {
            if (!(canlı instanceof Mermi)) {
                canlı.yerÇekimi(harita, canlı);
            }
        }
    }

    /**
     * Gerekli tuşları okur. {@link Klavye} sınıfını kullanır
     */
    private void tuşTakımı() {
        if (klavye.isBir_tuşu()) {
            // kırmızıya geç
            canlılar.remove(karakter);
            karakter = new KırmızıKarakter(karakter.getX(), karakter.getY(), karakter.getÖncekiY(), karakter.getUltiHakkı());
            canlılar.add(karakter);
            klavye.setBir_tuşu(false);
        }
        if (klavye.isIki_tuşu()) {
            // maviye geç
            canlılar.remove(karakter);
            karakter = new MaviKarakter(karakter.getX(), karakter.getY(), karakter.getÖncekiY(), karakter.getUltiHakkı());
            canlılar.add(karakter);
            klavye.setIki_tuşu(false);
        }
        if (klavye.isÜç_tuşu()) {
            // mora geç
            canlılar.remove(karakter);
            karakter = new MorKarakter(karakter.getX(), karakter.getY(), karakter.getÖncekiY(), karakter.getUltiHakkı());
            canlılar.add(karakter);
            klavye.setÜç_tuşu(false);
        }
        if (klavye.isBoşluk_tuşu()) {
            if (karakter.isUlti()) {
                if (!(karakter instanceof MorKarakter)) {
                    karakter.setUlti(false);
                    klavye.setBoşluk_tuşu(false); // Klavye girişini işledikten sonra sıfırla
                    if (karakter.getUltiHakkı() > 0) {
                        karakter.setUltiHakkı(karakter.getUltiHakkı()-1);
                        karakter.ultiAt(karakter, harita);
                    }
                }

            }  else {
                if (!(karakter instanceof MorKarakter)) {
                    // ateş işlemleri
                    karakter.ateşEt(karakter);
                }
            }

            klavye.setBoşluk_tuşu(false);
        }
        if (klavye.isSol_tuşu()) {
            karakter.solaGit(harita, karakter);
        }
        if (klavye.isSağ_tuşu()) {
            karakter.sağaGit(harita, karakter);
        }
        if (klavye.isYukarı_tuşu()) {
            karakter.zıpla(karakter, harita);

            klavye.setYukarı_tuşu(false);
        }
        // yanma kontrolü için tuş eklentisi
        if (klavye.isC_tuşu()) {
            oyunDurdu = true;
            yanmaKontrolüYapsınMı = !yanmaKontrolüYapsınMı;
            yanmaKontrolüPopUp(yanmaKontrolüYapsınMı);
            oyunDurdu = false;
            klavye.setC_tuşu(false);
        }
        if (klavye.isL_tuşu()) {
            if (!(karakter instanceof MorKarakter)) {
                if (karakter.getUltiHakkı() > 0 && !karakter.isUlti()) { // ulti zaten aktif değilse
                    karakter.setUlti(true);
                    klavye.setL_tuşu(false); // Klavye girişini işledikten sonra sıfırla
                }
            }
        }
    }

    /**
     * Yanma Kontrolü için bilgilendirme mesajı
     */
    private void yanmaKontrolüPopUp(Boolean bool) {
        // Oyun bittiğinde bir popup ekranı oluştur
        if (bool) {
            JOptionPane.showMessageDialog(this, "Yanma Kontrolü Devrede", "Yanma", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Yanma Kontrolü Devre Dışı", "Yanma", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Oyunun durdurulması ve tekrar başlatılması için gerekli denetlemeyi sağlar.
     */
    private void oyunToggle() {
        oyunDurdu = !oyunDurdu; // Toggle the game pause state
    }

    /**
     * Oyun sonunu denetler.
     */
    private void oyunSonu() {
        harita.pembeKapı(this, karakter);

        if (oyunBittiMi) {
            oyunBittiPopup();
            System.exit(0);
        }
    }

    /**
     * Oyun sonunda bilgilendirme mesajı verir.
     */
    private void oyunBittiPopup() {
        // Oyun bittiğinde bir popup ekranı oluştur
        JOptionPane.showMessageDialog(this, "Oyun bitti! Puan: " + üstÇubuk.getPuan(), "Oyun Durumu", JOptionPane.INFORMATION_MESSAGE);

        // Oyun ekranını kapat ve uygulamayı sonlandır
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.dispose();
    }

    /**
     * Oyunu kaydeder.
     */
    public void oyunuKaydet() {
        String dosyaYolu = "save";
        try (BufferedWriter yazici = new BufferedWriter(new FileWriter(dosyaYolu))) {
            // Oyun puanı dosyaya yazılır.
            yazici.write("score " + üstÇubuk.getPuan());

            yazici.newLine();

            // Mermi bilgileri dosyaya yazılır.
            for (Mermi mermi : karakter.getMermis()) {
                yazici.write("mermi " + renginİsmiNe(mermi.getRenk()) + " " + mermi.getX() + " " + mermi.getY() + " " + çeviriciDoğruluk(mermi.isYön()) + " " + çeviriciDoğruluk(mermi.isUltiMi()));
                yazici.newLine();
            }

            // Karakter bilgilerini dosyaya yazma
            yazici.write("karakter " + renginİsmiNe(karakter.getRenk()) + " " + karakter.getX() + " " + karakter.getY() + " " + karakter.getÖncekiY() + " " + karakter.getUltiHakkı());
            yazici.newLine();

            // Canavar bilgilerini dosyaya yazma
            for (Canlı canavar : canlılar) {
                if (canavar instanceof Canavar) {
                    yazici.write("canavar " + canavarRenginİsmiNe((Canavar) canavar) + " " + canavar.getX() + " " + canavar.getY() + " " + canavar.getÖncekiY() + " " + çeviriciDoğruluk(((Canavar) canavar).isÖncekiHaraketYönü()) + " " + canavar.getCan());
                    yazici.newLine();
                }
            }

            // Coin bilgilerini dosyaya yazma
            for (Dikdörtgen coin : çıkarılacakCoinler) {
                yazici.write("coin " + coin.getX() + " " + coin.getY());
                yazici.newLine();
            }

            // Yanma kontrolü bilgisini dosyaya yazma
            yazici.write("yanmaKontrolü " + yanmaKontrolüYapsınMı);
            yazici.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        oyunKayıtPopup();
    }

    private String canavarRenginİsmiNe(Canavar canavar) {
        String renk = null;

        if (canavar instanceof MaviCanavar) {
            renk = "MAVİ";
        } else if (canavar instanceof KırmızıCanavar) {
            renk = "KIRMIZI";
        } else if (canavar instanceof YeşilCanavar) {
            renk = "YEŞİL";
        } else if (canavar instanceof TuruncuCanavar) {
            renk = "TURUNCU";
        }

        return renk;
    }

    /**
     * Kayıtlı oyuna başlar.
     */
    public void oyunuKayıttanBaşlat(String dosyaYolu) {
        ArrayList<Mermi> mermiler = new ArrayList<>();
        oyunDurdu = true;
        try {
            // FileReader ve BufferedReader kullanarak dosyayı okuma işlemi
            FileReader dosyaOkuyucu = new FileReader(dosyaYolu);
            BufferedReader okuyucu = new BufferedReader(dosyaOkuyucu);

            // Dosyadan bir sonraki satırı oku
            String satir = okuyucu.readLine();

            while (satir != null) {
                // Satırı boşluğa göre parçala ve sayıları elde et
                String[] argümanlar = satir.split(" ");

                switch (argümanlar[0]) {
                    case "score":
                        this.üstÇubuk.setPuan(Integer.parseInt(argümanlar[1]));
                        break;
                    case "mermi":
                        mermiler.add(new Mermi(
                                rengiNe(argümanlar[1]),
                                Integer.parseInt(argümanlar[2]),
                                Integer.parseInt(argümanlar[3]),
                                doğrulukÇevirici(argümanlar[4]),
                                doğrulukÇevirici(argümanlar[5])
                        ));
                        break;
                    case "karakter":
                        switch (argümanlar[1]) {
                            case "MAVİ":
                                karakter = new MaviKarakter(
                                        Integer.parseInt(argümanlar[2]),
                                        Integer.parseInt(argümanlar[3]),
                                        Integer.parseInt(argümanlar[4]),
                                        Integer.parseInt(argümanlar[5])
                                );
                                break;
                            case "KIRMIZI":
                                karakter = new KırmızıKarakter(
                                        Integer.parseInt(argümanlar[2]),
                                        Integer.parseInt(argümanlar[3]),
                                        Integer.parseInt(argümanlar[4]),
                                        Integer.parseInt(argümanlar[5])
                                );
                                break;
                            case "MOR":
                                karakter = new MorKarakter(
                                        Integer.parseInt(argümanlar[2]),
                                        Integer.parseInt(argümanlar[3]),
                                        Integer.parseInt(argümanlar[4]),
                                        Integer.parseInt(argümanlar[5])
                                );
                                break;
                            default:
                                break;
                        }
                        break;
                    case "canavar":
                        switch (argümanlar[1]) {
                            case "MAVİ":
                                MaviCanavar maviCanavar = new MaviCanavar(
                                        Integer.parseInt(argümanlar[2]),
                                        Integer.parseInt(argümanlar[3]),
                                        Integer.parseInt(argümanlar[4]
                                        ));
                                maviCanavar.setÖncekiHaraketYönü(doğrulukÇevirici(argümanlar[5]));
                                maviCanavar.setCan(Integer.parseInt(argümanlar[6]));
                                canlılar.add(maviCanavar);
                                break;
                            case "KIRMIZI":
                                KırmızıCanavar kırmızıCanavar = new KırmızıCanavar(
                                        Integer.parseInt(argümanlar[2]),
                                        Integer.parseInt(argümanlar[3]),
                                        Integer.parseInt(argümanlar[4]
                                        ));
                                kırmızıCanavar.setÖncekiHaraketYönü(doğrulukÇevirici(argümanlar[5]));
                                kırmızıCanavar.setCan(Integer.parseInt(argümanlar[6]));
                                canlılar.add(kırmızıCanavar);
                                break;
                            case "TURUNCU":
                                TuruncuCanavar turuncuCanavar = new TuruncuCanavar(
                                        Integer.parseInt(argümanlar[2]),
                                        Integer.parseInt(argümanlar[3]),
                                        Integer.parseInt(argümanlar[4]
                                        ));
                                turuncuCanavar.setÖncekiHaraketYönü(doğrulukÇevirici(argümanlar[5]));
                                turuncuCanavar.setCan(Integer.parseInt(argümanlar[6]));
                                canlılar.add(turuncuCanavar);
                                break;
                            case "YEŞİL":
                                YeşilCanavar yeşilCanavar = new YeşilCanavar(
                                        Integer.parseInt(argümanlar[2]),
                                        Integer.parseInt(argümanlar[3]),
                                        Integer.parseInt(argümanlar[4]
                                        ));
                                yeşilCanavar.setÖncekiHaraketYönü(doğrulukÇevirici(argümanlar[5]));
                                yeşilCanavar.setCan(Integer.parseInt(argümanlar[6]));
                                canlılar.add(yeşilCanavar);
                                break;
                            default:
                                break;
                        }
                        break;
                    case "coin":
                        çıkarılacakCoinler.add(new Dikdörtgen(
                                1,
                                "coin",
                                new Color(255, 255, 0, 255),
                                Integer.parseInt(argümanlar[1]),
                                Integer.parseInt(argümanlar[2]),
                                5,
                                5
                        ));
                        break;
                    case "yanmaKontrolü":
                        yanmaKontrolüYapsınMı = doğrulukÇevirici(argümanlar[1]);
                        break;
                    default:
                        break;
                }
                // Bir sonraki satırı oku
                satir = okuyucu.readLine();
            }

            // Dosyayı kapat
            okuyucu.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        karakter.setMermis(mermiler);
    }

    /**
     * Kayıt dosyasından veri anlamlandırmak için.
     */
    private boolean doğrulukÇevirici(String string) {
        return string.equals("true");
    }
    private String çeviriciDoğruluk(boolean bool) {
        return bool ? "true" : "false";
    }

    /**
     * Kayıt dosyasından veri anlamlandırmak için.
     */
    private Color rengiNe(String string) {
        return switch (string) {
            case "KIRMIZI" -> new Color(255, 0, 0);
            case "YEŞİL" -> new Color(0, 255, 0);
            case "MAVİ" -> new Color(0, 0, 255);
            case "MOR" -> new Color(255, 0, 255, 255);
            case "TURUNCU" -> new Color(255, 127, 0);
            default -> {
                yield new Color(0, 0, 0, 0);
            }
        };
    }

    /**
     * Kayıt dosyasından veri anlamlandırmak için.
     */
    private String renginİsmiNe(Color color) {
        String string;
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        if (red == 255 && green == 0 && blue == 0) {
            string = "KIRMIZI";
        } else if (red == 0 && green == 255 && blue == 0) {
            string = "YEŞİL";
        } else if (red == 0 && green == 0 && blue == 255) {
            string = "MAVİ";
        } else if (red == 255 && green == 0 && blue == 255) {
            string = "MOR";
        } else if (red == 255 && green == 127 && blue == 0) {
            string = "TURUNCU";
        } else {
            string = "HATALI_RENK_GİRİŞİ";
        }

        return string;
    }

    /**
     * Oyun kaydedildiğinde bilgilendirme penceresi açar.
     */
    private void oyunKayıtPopup() {
        JOptionPane.showMessageDialog(this, "Kayıt tamam.", "Kayıt", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Rastgele canavar üretir.
     */
    private void rastgeleCanavarÜret() {
        Random random = new Random();
        int x = random.nextInt(100)+24; // Ekran genişliği içinde rastgele x
        int y = 100; // Ekran yüksekliği içinde rastgele y
        int öncekiX = 99;
        int setÖnceki = random.nextInt(2);
        int canavarTipi = random.nextInt(3); // 0, 1 veya 2 değerleri arasında rastgele canavar tipi
        Canavar yeniCanavar;

        switch (canavarTipi) {
            case 0:
                yeniCanavar = new TuruncuCanavar(x, y, öncekiX);
                yeniCanavar.setRenk(new Color(0, 0, 0, 0));
                break;
            case 1:
                yeniCanavar = new KırmızıCanavar(x, y, öncekiX);
                yeniCanavar.setRenk(new Color(0, 0, 0, 0));
                break;
            case 2:
                yeniCanavar = new MaviCanavar(x, y, öncekiX);
                yeniCanavar.setRenk(new Color(0, 0, 0, 0));
                break;
            default:
                yeniCanavar = null;
                break;
        }

        switch (setÖnceki) {
            case 0:
                yeniCanavar.setÖncekiHaraketYönü(false);
                yeniCanavar.setÖncekiX(yeniCanavar.getX() + 10);
                break;
            case 1:
                yeniCanavar.setÖncekiHaraketYönü(true);
                yeniCanavar.setÖncekiX(yeniCanavar.getX() - 10);
                break;
            default:
                break;
        }

        int beklemeSüresi = new Random().nextInt(30) + 40; // 1 ile 10 saniye arasında rastgele bekleme süresi
        canlılar.add(yeniCanavar);
        yeniCanavar = null;
    }

    /**
     * Başlangıç canavarlarını koyar.
     */
    public void canavarDoldur() {
        TuruncuCanavar turuncuCanavar = new TuruncuCanavar(830,630,800);
        turuncuCanavar.setHız(1);
        canlılar.add(turuncuCanavar);
        canlılar.add(new YeşilCanavar(700,50,10));
        canlılar.add(new MaviCanavar(600,700,104));
        canlılar.add(new MaviCanavar(1100,700,104));
        canlılar.add(new KırmızıCanavar(300,300,104));
        canlılar.add(new MaviCanavar(600,300,104));
        canlılar.add(new KırmızıCanavar(1400,700,104));
        canlılar.add(new TuruncuCanavar(1250,700,104));
    }

    /**
     * Coinlerden alınan puanları toplar ve bu sınıf içerisindeki
     * {@link OyunEkranı#üstÇubuk} nesnesinin
     * {@link ÜstÇubuk#setPuan(int)} metoduna atar.
     */
    private void puanlar() {
        Dikdörtgen yeni = harita.coinKontrol(karakter);
        if (yeni != null) {
            harita.dikdortgenler.remove(yeni);
            çıkarılacakCoinler.add(yeni);

            üstÇubuk.setPuan(üstÇubuk.getPuan() + 5);
            repaint();
        }
    }

    /**
     * Mermileri denetler, {@link OyunEkranı#MermilerZedeler(Mermi)} metodu ile ortak olarak çalışır
     */
    private void mermiler() {
        List<Mermi> silinecekMermiler = new ArrayList<>();
        for (Mermi mermi : karakter.getMermis()) {
            Mermi yeni;
            if (mermi.isUltiMi()) {
                if (mermi.isYön()) {
                    mermi.solaGit(harita, mermi);
                    yeni = UltilerZedeler(mermi);
                } else {
                    mermi.sağaGit(harita, mermi);
                    yeni = UltilerZedeler(mermi);
                }
                silinecekMermiler.add(yeni);

                if (mermi.canlıDuruyorMu()) {
                    silinecekMermiler.add(mermi);
                }
            } else {
                if (mermi.isYön()) {
                    mermi.solaGit(harita, mermi);
                    yeni = MermilerZedeler(mermi);
                } else {
                    mermi.sağaGit(harita, mermi);
                    yeni = MermilerZedeler(mermi);
                }
                silinecekMermiler.add(yeni);
                // çarpan mermiyi çıkartmak için
                if (mermi.canlıDuruyorMu()) {
                    silinecekMermiler.add(mermi);
                }
            }
        }
        // çarpan mermiyi çıkartmak için
        karakter.getMermis().removeAll(silinecekMermiler);
        repaint();
    }

    /**
     * Merminin, canavarın canını düşürüp düşürmeyeceğinin kontrolünü yapar.
     * </P> İlgili canavarın canını düşürür.
     */
    private Mermi MermilerZedeler(Mermi mermi) {
        for (Canlı canlı : canlılar) {
            if (canlı instanceof Canavar) {
                Color color = canavarınRengiNe((Canavar) canlı);
                boolean bool1 = ((canlı.getY()<=(mermi.getY()+mermi.getBoyut()))&&(mermi.getY()<=(canlı.getY()+canlı.getBoyut())));
                boolean bool2 = ((canlı.getX()<(mermi.getX()+mermi.getBoyut()))&&((canlı.getX()+canlı.getBoyut())> mermi.getX()));
                if(bool1&&bool2) {
                    if (mermi.getRenk().equals(color)) {
                        canlı.setCan(canlı.getCan() + mermi.getCan());
                    } else {
                        canlı.setCan(canlı.getCan()-mermi.getCan());
                    }

                    if (canlı.getCan() == 0) {
                        canlılar.remove(canlı);
                        if (canlı instanceof YeşilCanavar) {
                            üstÇubuk.setPuan(üstÇubuk.getPuan()+100);
                            karakter.setUltiHakkı(1);
                        }
                        üstÇubuk.setPuan(üstÇubuk.getPuan()+10);
                    }
                    return mermi;
                }
            }
        }
        return null;
    }

    /**
     * Ultinin, duvarı kırıp kırmayacağının kontrolünü yapar.
     * </P> İlgili canavarın canını düşürür.
     */
    private Mermi UltilerZedeler(Mermi mermi) {
        int yakınlık = 2; // Dikdörtgene olan yakınlık kontrolü
        for (Dikdörtgen dikdortgen : harita.dikdortgenler) {
            boolean bool1 = (dikdortgen.getY() < (mermi.getY() + mermi.getBoyut() + yakınlık))
                    && (mermi.getY() < (dikdortgen.getY() + dikdortgen.getHeight() + yakınlık));
            boolean bool2 = (dikdortgen.getX() < (mermi.getX() + mermi.getBoyut() + yakınlık))
                    && ((dikdortgen.getX() + dikdortgen.getWidth() + yakınlık) > mermi.getX());

            if (bool1 && bool2) {
                if(!dikdortgen.getName().equals("kırılamaz")) {
                    harita.dikdortgenler.remove(dikdortgen);
                }
                repaint();
                return mermi;
            }
        }
        return null;
    }

    /**
     * Mermilerin hangi canavara çarptığını anlamak için kullanılır
     */
    private Color canavarınRengiNe(Canavar canavar) {
        if (canavar instanceof KırmızıCanavar) {
            return new Color(255, 0, 0);
        } else if (canavar instanceof MaviCanavar) {
           return new Color(0, 0, 255);
        } else if (canavar instanceof TuruncuCanavar) {
            return new Color(255, 127, 0);
        } else if (canavar instanceof YeşilCanavar) {
            return new Color(0, 255, 0);
        }
        return new Color(0,0,0,0);
    }

    /**
     * WildCard için boos'un ölüp ölmediğini kontrolüne yardımcı olur.
     */
    public void setPatlamaGerçekleştiMi(boolean patlamaGerçekleştiMi) {
        this.patlamaGerçekleştiMi = patlamaGerçekleştiMi;
    }
}

