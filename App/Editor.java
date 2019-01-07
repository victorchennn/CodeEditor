package App;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

/** Plain text editor.
 *  @author Victor Chen
 */
class Editor extends JTextArea {

    /** Colors of highlighted line and indexBar background. */
    private static final Color
            HIGHLIGHT = new Color(201, 222, 193),
            BAR_BACKGROUND = new Color(248, 245, 231),
            ORIGINAL = new Color(238, 238, 238);

    Editor(File file, boolean newfile, GUI gui) {
        setLineWrap(true);
        _file = file;
        _newfile = newfile;
        writeFile(file);
        _inittext = getText();
        _initrows = getLineCount();
        _highlighter = getHighlighter();
        _gui = gui;
        _gui.getStatusbar().setText(file.getName() + "   0:0   ");
        _indexbar = createIndexBar();
        _indexbar.setVisible(_gui.getMenubar().getIndexBar().isSelected());
        getDocument().addUndoableEditListener(_undo);
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateIndexBar();
                _highlighter.removeAllHighlights();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateIndexBar();
                _highlighter.removeAllHighlights();
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
            _gui.getStatusbar().setText(String.format("%s   %d:%d   ",
                    file.getName(), row, col));
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

    /** Write file content to editor in display. */
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
        _indexbar = new JPanel();
        _indexbar.setLayout(new BoxLayout(_indexbar, BoxLayout.Y_AXIS));
        _indexbar.setBackground(Color.WHITE);
        _indexbar.setPreferredSize(new Dimension(30, 800));
        _indexbar.setMaximumSize(new Dimension(30, 10000));
        createIndex(getLineCount());
        _indexbar.setBackground(BAR_BACKGROUND);
        return _indexbar;
    }

    /** Update indexBar each time if number of rows changes. */
    private void updateIndexBar() {
        int curlinecount = getLineCount();
        if (_initrows != curlinecount) {
            _initrows = curlinecount;
            _indexbar.removeAll();
            _indexbar.revalidate();
            _indexbar.repaint();
            createIndex(_initrows);
        }
    }

    /** Use a label to represent each line in indexBar. When clicking the label,
     * turn the label of that line to green and select all the text at that line. */
    private void createIndex(int linecount) {
        for (int i = 1; i <= linecount; i++) {
            JLabel index = new JLabel(Integer.toString(i));
            index.setMaximumSize(new Dimension(30, 16));
            index.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        int line = index.getLocation().y / index.getSize().height;
                        if (!index.isOpaque()) {
                            index.setOpaque(true);
                            index.setBackground(HIGHLIGHT);
                            setSelectionStart(getLineStartOffset(line));
                            setSelectionEnd(getLineEndOffset(line));
                        } else {
                            index.setOpaque(false);
                            index.setBackground(ORIGINAL);
                            setCaretPosition(getLineStartOffset(line));
                        }
                    } catch (BadLocationException er) {
                        /* Impossible */
                    }
                }
            });
            _indexbar.add(index);
        }
    }

    /** Highlighter of the editor. */
    private Highlighter _highlighter;

    /** Undo manager. */
    private UndoManager _undo = new UndoManager();

    /** GUI in display. */
    private GUI _gui;

    /** Index of row. */
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
