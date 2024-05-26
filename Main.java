/*
 * TOBB ETU
 * Bilgisayar Mühendisliği
 *
 * BİL 211 Bilgisayar Programlama II
 * Ödev: Ateş ve Su Klonu
 *
 * Çağan Durgun tarafından yazılmıştır
 */

/**
 * Main sınıfı, "Ateş ve Su Klonu" {@link Oyun}unun çalıştırmanızı sağlar.
 * <p>Argümanlı veya argümansız olarak başlatılabilir.
 * <ul>
 *     <li>Argümanlı olarak başlatılacak ise, oyun kayıtlarını
 *     tutan bir dosya olan <strong>save</strong> kullanılmalıdır.
 *     Kullanımı <strong>java Main save</strong> şeklindedir</li>
 *     <li>Argümansız olarak başlatılacaksa normal bir şekilde
 *     başlatılabilir.</li>
 * </ul>
 */
public class Main {

    /**
     * Oyunun main metodu
     */
    public static void main(String[] args) {
        // Oyun nesnesi oluşturulur.
        Oyun oyun = new Oyun();

        if (args.length == 0) {
            // Yeni oyun başlatılır.
            oyun.başlat();
        } else if (args.length == 1) {
            // save dosyasına kaydedilmiş oyun başlatılır.
            oyun.kayıttanBaşlat(args[0]);
        } else {
            // Argüman kullanımı hatalı ise uyarı bilgisi verilir.
            System.out.println("\u001B[31mHatalı argüman Kullanımı!\u001B[0m");
            System.out.print("Doğru kullanım: ");
            System.out.println("\u001B[32mjava Main save\u001B[0m");
        }
    }
}

