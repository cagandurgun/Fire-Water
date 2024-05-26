import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Oyun için klavye arayüzü sağlar.
 */
public class Klavye implements KeyListener {
    private boolean bir_tuşu;
    private boolean iki_tuşu;
    private boolean üç_tuşu;
    private boolean boşluk_tuşu;
    private boolean sol_tuşu;
    private boolean sağ_tuşu;
    private boolean yukarı_tuşu;
    private boolean p_tuşu;
    private boolean k_tuşu;
    private boolean c_tuşu;
    private boolean l_tuşu;

    @Override
    public void keyTyped(KeyEvent e) {
        char typedChar = e.getKeyChar();

        if (typedChar == KeyEvent.VK_1) {
            bir_tuşu = true;
        }
        if (typedChar == KeyEvent.VK_2) {
            iki_tuşu = true;
        }
        if (typedChar == KeyEvent.VK_3) {
            üç_tuşu = true;
        }
        if (typedChar == KeyEvent.VK_SPACE) {
            boşluk_tuşu = true;
        }
        if (typedChar == KeyEvent.VK_UP) {
            yukarı_tuşu = true;
        }
        if (typedChar == KeyEvent.VK_P) {
            p_tuşu = true;
        }
        if (typedChar == KeyEvent.VK_K) {
            k_tuşu = true;
        }

        if (typedChar == KeyEvent.VK_C) {
            c_tuşu = true;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_1) {
            bir_tuşu = true;
        }
        if (code == KeyEvent.VK_2) {
            iki_tuşu = true;
        }
        if (code == KeyEvent.VK_3) {
            üç_tuşu = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            boşluk_tuşu = true;
        }
        if (code == KeyEvent.VK_LEFT) {
            sol_tuşu = true;
        }
        if (code == KeyEvent.VK_RIGHT) {
            sağ_tuşu = true;
        }
        if (code == KeyEvent.VK_UP) {
            yukarı_tuşu = true;
        }
        if (code == KeyEvent.VK_P) {
            p_tuşu = true;
        }
        if (code == KeyEvent.VK_K) {
            k_tuşu = true;
        }
        if (code == KeyEvent.VK_C) {
            c_tuşu = true;
        }
        if (code == KeyEvent.VK_L) {
            l_tuşu = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_LEFT) {
            sol_tuşu = false;
        }
        if (code == KeyEvent.VK_RIGHT) {
            sağ_tuşu = false;
        }
        if (code == KeyEvent.VK_L) {
            l_tuşu = false;
        }

    }

    /**
     * Getter & Setter'lar
     */
    public boolean isBir_tuşu() {
        return bir_tuşu;
    }

    public void setBir_tuşu(boolean bir_tuşu) {
        this.bir_tuşu = bir_tuşu;
    }

    public boolean isIki_tuşu() {
        return iki_tuşu;
    }

    public void setIki_tuşu(boolean iki_tuşu) {
        this.iki_tuşu = iki_tuşu;
    }

    public boolean isÜç_tuşu() {
        return üç_tuşu;
    }

    public void setÜç_tuşu(boolean üç_tuşu) {
        this.üç_tuşu = üç_tuşu;
    }

    public boolean isBoşluk_tuşu() {
        return boşluk_tuşu;
    }

    public void setBoşluk_tuşu(boolean boşluk_tuşu) {
        this.boşluk_tuşu = boşluk_tuşu;
    }

    public boolean isSol_tuşu() {
        return sol_tuşu;
    }

    public void setSol_tuşu(boolean sol_tuşu) {
        this.sol_tuşu = sol_tuşu;
    }

    public boolean isSağ_tuşu() {
        return sağ_tuşu;
    }

    public void setSağ_tuşu(boolean sağ_tuşu) {
        this.sağ_tuşu = sağ_tuşu;
    }

    public boolean isYukarı_tuşu() {
        return yukarı_tuşu;
    }

    public void setYukarı_tuşu(boolean yukarı_tuşu) {
        this.yukarı_tuşu = yukarı_tuşu;
    }

    public boolean isP_tuşu() {
        return p_tuşu;
    }

    public void setP_tuşu(boolean p_tuşu) {
        this.p_tuşu = p_tuşu;
    }

    public boolean isK_tuşu() {
        return k_tuşu;
    }

    public void setK_tuşu(boolean k_tuşu) {
        this.k_tuşu = k_tuşu;
    }

    public boolean isC_tuşu() {
        return c_tuşu;
    }

    public void setC_tuşu(boolean c_tuşu) {
        this.c_tuşu = c_tuşu;
    }

    public boolean isL_tuşu() {
        return l_tuşu;
    }

    public void setL_tuşu(boolean l_tuşu) {
        this.l_tuşu = l_tuşu;
    }
}

