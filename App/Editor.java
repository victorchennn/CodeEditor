package App;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;

class Editor extends JTextArea {
    Editor(File file, boolean newfile) {
        _file = file;
        _newfile = newfile;
        writeFile(file);
        _inittext = getText();
        _initrows = getLineCount();
        _label = createIndexBar();
        addIndextoBar(_initrows);
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (_initrows != getLineCount()) {
                    addIndextoBar(getLineCount());
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (_initrows != getLineCount()) {
                    addIndextoBar(getLineCount());
                }
            }
            @Override
            public void changedUpdate(DocumentEvent e) {

            }
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
        label.setPreferredSize(new Dimension(20, 800));
        label.setMaximumSize(new Dimension(20, 800));
        return label;
    }

    private void addIndextoBar(int x) {
        _label.removeAll();
        for (int i = 0; i <= x; i++) {
            JLabel index = new JLabel(Integer.toString(i));
            index.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
            _label.add(index);
        }
    }

    private JPanel _label;

    private int _initrows;

    private String _inittext;

    private File _file;

    private boolean _newfile;
}
