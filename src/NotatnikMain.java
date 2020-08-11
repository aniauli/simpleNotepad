import javax.swing.*;

public class NotatnikMain {
    public static void main(String[] args) {
        Notatnik notatnik = new Notatnik();
        notatnik.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        notatnik.setLocationByPlatform(true);
        notatnik.setSize(1000, 500);
        notatnik.setVisible(true);
    }
}
