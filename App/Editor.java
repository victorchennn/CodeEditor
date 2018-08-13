package App;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.*;

/**
 *  @author Victor Chen
 */
class Editor extends JTextArea {
    Editor(File file, boolean newfile, GUI gui) {
        _file = file;
        _newfile = newfile;
        writeFile(file);
        _inittext = getText();
        _initrows = getLineCount();
        _label = createIndexBar();
        _gui = gui;
        _gui.getStatusbar().setText(file.getName() + "   0:0   ");
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateIndexBar();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateIndexBar();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        addCaretListener(e -> {
            int pos = getCaretPosition();
            int row = 0, col = 0;
            try {
                row = getLineOfOffset(pos) + 1;
                col = pos - getLineStartOffset(row - 1) + 1;
            } catch (BadLocationException err) {
                /* Ignore BadLocationException */
            }
            _gui.getStatusbar().setText(file.getName() + "   " + row + ":" + col + "   ");
        });
    }

    JPanel getLabel() {
        return _label;
    }

    File getFile() {
        return _file;
    }

    boolean ifChanged() {
        return _inittext!= null && !_inittext.equals(getText());
    }

    boolean isNewFile() {
        return _newfile;
    }

    private void writeFile(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                this.append(line + "\n");
            }
            fr.close();
            br.close();
        } catch (IOException e) {
            /* Ignore IOException. */
        }
        this.setCaretPosition(0);
    }

    private JPanel createIndexBar() {
        JPanel label = new JPanel();
        label.setLayout(new BoxLayout(label, BoxLayout.Y_AXIS));
        label.setBackground(Color.WHITE);
        label.setPreferredSize(new Dimension(30, 10000));
        label.setMaximumSize(new Dimension(30, 10000));
        for (int i = 1; i <= getLineCount(); i++) {
            JLabel index = new JLabel(Integer.toString(i));
            label.add(index);
        }
        return label;
    }

    private void updateIndexBar() {
        int curlinecount = getLineCount();
        if (_initrows != curlinecount) {
            _initrows = curlinecount;
            _label.removeAll();
            _label.repaint();
            for (int i = 1; i <= _initrows; i++) {
                JLabel index = new JLabel(Integer.toString(i));
                _label.add(index);
            }
        }
    }

    private GUI _gui;

    private JPanel _label;

    private int _initrows;

    private String _inittext;

    private File _file;

    private boolean _newfile;
}
