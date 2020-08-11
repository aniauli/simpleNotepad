import javafx.util.Pair;

import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Notatnik extends JFrame{
    private JLabel statusLabel = new JLabel(Calendar.getInstance().getTime().toString());
    private String fileName;
    private Clipboard clipboard = getToolkit().getSystemClipboard();
    JTabbedPane tabbedPane = new JTabbedPane();
    private ArrayList<Pair <Integer, JTextArea>> textAreas = new ArrayList<>();
    private ArrayList<Pair <Integer, String>> fileTexts = new ArrayList<>();
    private JScrollPane scrollPane;
    private JTextArea actualTextArea = new JTextArea();

    public void closeTab(){
        if(tabbedPane.getTabCount() > 1)
            tabbedPane.remove(tabbedPane.getSelectedIndex());
        else
            System.exit(0);
    }

    public void addNewTab(String tabName) {
        JTextArea tmpTextArea = new JTextArea();
        tmpTextArea.setText("");
        scrollPane = new JScrollPane(tmpTextArea);

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tabbedPane.addTab(tabName, scrollPane);

        fileTexts.add(new Pair<>(tabbedPane.getTabCount() - 1, ""));
        textAreas.add(new Pair<>(tabbedPane.getTabCount() - 1, tmpTextArea));
        actualTextArea = tmpTextArea;

        super.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    public Notatnik() {
        super("Notatnik");

        addNewTab("Nowy");

        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                actualTextArea = textAreas.get(index).getValue();
            }
        };

        tabbedPane.addChangeListener(changeListener);
        setJMenuBar(setNotatnikMenu());
    }

    public JMenuBar setNotatnikMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu plik = new JMenu("Plik");
        JMenuItem nowy = new JMenuItem("Nowy");
        JMenuItem wczytaj = new JMenuItem("Wczytaj");
        JMenuItem zapisz = new JMenuItem("Zapisz");
        JMenuItem zamknij = new JMenuItem("Zamknij");
        JMenuItem zakoncz= new JMenuItem("Zakoncz");

        JMenu edycja = new JMenu("Edycja");
        JMenuItem kopiuj = new JMenuItem("Kopiuj");
        JMenuItem wklej = new JMenuItem("Wklej");
        JMenuItem wytnij = new JMenuItem("Wytnij");

        JMenu pomoc = new JMenu("Pomoc");
        JMenuItem info = new JMenuItem("Informacje o autorze");

        nowy.addActionListener(actionEvent -> {addNewTab("Nowy");
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);});

        wczytaj.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showDialog(null, "Wczytaj plik");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = chooser.getSelectedFile().getAbsolutePath();
                addNewTab(chooser.getSelectedFile().getName());
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                try {
                    BufferedReader plikDoOdczytu = new BufferedReader(new FileReader(fileName));
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    fileTexts.set(tabbedPane.getSelectedIndex(), new Pair<Integer, String>(tabbedPane.getSelectedIndex(), ""));
                    while ((line = plikDoOdczytu.readLine()) != null) {
                        stringBuilder.append(line + System.lineSeparator());
                        actualTextArea.setText(stringBuilder.toString());
                    }
                    fileTexts.set(tabbedPane.getSelectedIndex(), new Pair<Integer, String>(tabbedPane.getSelectedIndex(),
                            actualTextArea.getText()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        zapisz.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showDialog(null, "Zapisz plik");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = chooser.getSelectedFile().getAbsolutePath();
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), chooser.getSelectedFile().getName());
                try {
                    BufferedWriter plikDoZapisu = new BufferedWriter(new FileWriter(fileName));
                        plikDoZapisu.write(actualTextArea.getText());
                        plikDoZapisu.close();

                    } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        zamknij.addActionListener(actionEvent -> {
            actualTextArea = textAreas.get(tabbedPane.getSelectedIndex()).getValue();
            if (!(fileTexts.get(tabbedPane.getSelectedIndex()).getValue().equals(actualTextArea.getText()))) {
                    int answer;
                answer = JOptionPane.showConfirmDialog(null, "Czy chcesz zapisac zmiany?");
                if (answer == 0) {
                    JFileChooser chooser = new JFileChooser();
                    int returnVal = chooser.showDialog(null, "Zapisz plik");
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        fileName = chooser.getSelectedFile().getAbsolutePath();
                        try {
                            BufferedWriter plikDoZapisu = new BufferedWriter(new FileWriter(fileName));
                            plikDoZapisu.write(actualTextArea.getText());
                            plikDoZapisu.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    closeTab();
                }
                else if (answer == 1) {
                    closeTab();
                }
            }
            else
                closeTab();
        });

        zakoncz.addActionListener(actionEvent -> {
            while(tabbedPane.getTabCount() > 0) {
                actualTextArea = textAreas.get(tabbedPane.getSelectedIndex()).getValue();
                if (!fileTexts.get(tabbedPane.getSelectedIndex()).getValue().equals(actualTextArea.getText())) {
                    int answer;
                    answer = JOptionPane.showConfirmDialog(null, "Czy chcesz zapisac zmiany w pliku " +
                            tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + "?");
                    if (answer == 0) {
                        JFileChooser chooser = new JFileChooser();
                        int returnVal = chooser.showDialog(null, "Zapisz plik");
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            fileName = chooser.getSelectedFile().getAbsolutePath();
                            try {
                                BufferedWriter plikDoZapisu = new BufferedWriter(new FileWriter(fileName));
                                plikDoZapisu.write(actualTextArea.getText());
                                plikDoZapisu.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        closeTab();
                    } else if (answer == 1) {
                        closeTab();
                    }
                }
                else
                    closeTab();
            }
        });

        kopiuj.addActionListener(actionEvent -> {
            String copyString = actualTextArea.getSelectedText();
            StringSelection copySelection = new StringSelection(copyString);
            clipboard.setContents(copySelection, copySelection);
        });

        wklej.addActionListener(actionEvent -> {
            try {
                Transferable pasteSelection = clipboard.getContents(tabbedPane);
                String pasteString = (String) pasteSelection.getTransferData(DataFlavor.stringFlavor);
                actualTextArea.replaceRange(pasteString, actualTextArea.getSelectionStart(), actualTextArea.getSelectionEnd());
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        });

        wytnij.addActionListener(actionEvent -> {
            String cutString = actualTextArea.getSelectedText();
            StringSelection cutSelecttion = new StringSelection(cutString);
            clipboard.setContents(cutSelecttion, cutSelecttion);
            actualTextArea.replaceRange("", actualTextArea.getSelectionStart(), actualTextArea.getSelectionEnd());
        });

        info.addActionListener(actionEvent -> JOptionPane.showMessageDialog(null, "Autor: Anna Bienkowska, " +
                "program: Notatnik, na podstawie " + "opracowania do wykladu dr Zyglarskiego."));

        plik.add(nowy);
        plik.add(wczytaj);
        plik.add(zapisz);
        plik.add(zamknij);
        plik.addSeparator();
        plik.add(zakoncz);

        edycja.add(kopiuj);
        edycja.add(wklej);
        edycja.add(wytnij);

        pomoc.add(info);

        menuBar.add(plik);
        menuBar.add(edycja);
        menuBar.add(pomoc);

        return menuBar;
    }
}
