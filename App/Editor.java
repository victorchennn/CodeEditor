package App;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
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
        _indexbar = createIndexBar();
        _gui = gui;
        _gui.getStatusbar().setText(file.getName() + "   0:0   ");
        getDocument().addUndoableEditListener(_undo);
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

    /** Return undoManager. */
    UndoManager getUndo() {
        return _undo;
    }

    /** Return indexBar. */
    JPanel getIndexBar() {
        return _indexbar;
    }

    /** Return file in display. */
    File getFile() {
        return _file;
    }

    /** Return true iff there are changes in text. */
    boolean ifChanged() {
        return _inittext!= null && !_inittext.equals(getText());
    }

    /** Return file is new or nor. */
    boolean isNewFile() {
        return _newfile;
    }

    /** Write the file content to editor in display. */
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

    /** Create initial indexBar based on initial number of rows. */
    private JPanel createIndexBar() {
        JPanel label = new JPanel();
        label.setLayout(new BoxLayout(label, BoxLayout.Y_AXIS));
        label.setBackground(Color.WHITE);
        label.setPreferredSize(new Dimension(30, 100));
        label.setMaximumSize(new Dimension(30, 10000));
        for (int i = 1; i <= getLineCount(); i++) {
            JLabel index = new JLabel(Integer.toString(i));
            label.add(index);
        }
        label.setBackground(new Color(248, 245, 231));
        return label;
    }

    /** Update indexBar each time if number of rows changes. */
    private void updateIndexBar() {
        int curlinecount = getLineCount();
        if (_initrows != curlinecount) {
            _initrows = curlinecount;
            _indexbar.removeAll();
            _indexbar.revalidate();
            _indexbar.repaint();
            for (int i = 1; i <= _initrows; i++) {
                JLabel index = new JLabel(Integer.toString(i));
                _indexbar.add(index);
            }
        }
    }

    /** Undo manager. */
    private UndoManager _undo = new UndoManager();

    /** GUI in display. */
    private GUI _gui;

    /** Column index. */
    private JPanel _indexbar;

    /** Store initial number of rows. */
    private int _initrows;

    /** Store initial text. */
    private String _inittext;

    /** File in display. */
    private File _file;

    /** File is newly created or not. */
    private boolean _newfile;
}
