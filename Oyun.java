import javax.swing.*;

/**
 * Bu sınıf, {@link OyunEkranı} sınıfı ile {@link Main} sınıfı arasındaki köprü görevini görür.
 * <p>{@link Oyun#başlat başlat()} metodu ile yeni bir oyun başlatılırken,
 * {@link Oyun#kayıttanBaşlat(String dosyaYoly) kayıttanBaşlat(String dosyaYolu)} metodu ile, daha önce kaydedilmiş bir oyuna devam edilir.
 */
public class Oyun {

    /**
     * Yeni bir oyun başlatmak için kullanılır.
     */
    public void başlat() {
        // JFrame nesnesi oluşturulur, gerekli ayarlamalar yapılır.
        JFrame anaEkran = new JFrame();
        anaEkran.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        anaEkran.setUndecorated(true);
        anaEkran.setResizable(false);

        // OyunEkranı nesnesi oluşturulur, JFrame'e eklenir.
        // Burada kullanılan Constructor, yeni bir oyun başlatmak
        // için kullanılan, argümansız bir metottur.
        OyunEkranı oyunEkranı = new OyunEkranı();
        anaEkran.add(oyunEkranı);

        // Gerekli ayarlamalar yapılır.
        anaEkran.pack();
        anaEkran.setLocationRelativeTo(null);
        anaEkran.setVisible(true);

        // Oyun thread'i başlatılır.
        oyunEkranı.oyunThreadBaşlat();
    }

    /**
     * Kaydedilmiş bir oyuna devam etmek için kullanılır.
     * */
    public void kayıttanBaşlat(String dosyaYolu) {
        // JFrame nesnesi oluşturulur, gerekli ayarlamalar yapılır.
        JFrame anaEkran = new JFrame();
        anaEkran.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        anaEkran.setUndecorated(true);
        anaEkran.setResizable(false);

        // OyunEkranı nesnesi oluşturulur, JFrame'e eklenir.
        // Burada kullanılan Constructor, eski bir oyuna devam
        // etmek için kullanılan, argümanlı bir metottur.
        OyunEkranı oyunEkranı = new OyunEkranı(dosyaYolu);
        anaEkran.add(oyunEkranı);

        // Gerekli ayarlamalar yapılır.
        anaEkran.pack();
        anaEkran.setLocationRelativeTo(null);
        anaEkran.setVisible(true);

        // Oyun thread'i başlatılır.
        oyunEkranı.oyunThreadBaşlat();
    }
}

