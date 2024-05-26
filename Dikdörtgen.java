import java.awt.*;

/**
 * Çizim elemanı olarak kullanılmaktadır.
 */
public class Dikdörtgen {
    private String name;
    private Color color;
    private int x;
    private int y;
    private int coin;
    private int width;
    private int height;

    public Dikdörtgen(int coin, String name, Color color, int x, int y, int width, int height) {
        this.coin = coin;
        this.name = name;
        this.color = color;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getCoin() {
        return coin;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

