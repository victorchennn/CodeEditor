package App;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** All things to do with Editor commands.
 *  @author Victor Chen
 */
class Commands {

    Commands(GUI gui) {
        _gui = gui;
        _history = new ArrayDeque<>();
        _filechooser = new JFileChooser();
        _filechooser.addChoosableFileFilter(new FileNameExtensionFilter
                ("Text Files(*.txt)","txt"));
        _filechooser.addChoosableFileFilter(new FileNameExtensionFilter
                ("Java Source Files(*.java)","java"));
        _filechooser.addChoosableFileFilter(new FileNameExtensionFilter
                ("Python Source Files(*.py)","py"));
        String current_path = System.getProperty("user.dir");
        _filechooser.setCurrentDirectory(new File(current_path));
    }

    /** Create a new empty editor. */
    void newFile() {
        String entertitle = "<html>Enter the name for the new file.<br><br></html>";
        String title = JOptionPane.showInputDialog(null, entertitle, "Untitled");
        if (title != null) {
            Editor editor = new Editor(new File(title), true, _gui);
            JPanel editorArea = createEditorArea(editor);
            JScrollPane newone = new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            _gui.getTextArea().addTab(title, newone);
            _gui.getTextArea().setSelectedComponent(newone);
            _history.add(editor);
            if (_search != null) {
                _gui.getRightPanel().remove(_search.getSearch());
            }
            editor.requestFocus();
        }
    }

    /** Open the user selected file. */
    void openFile() {
        int value = _filechooser.showOpenDialog(null);
        if (value == JFileChooser.APPROVE_OPTION) {
            File file = _filechooser.getSelectedFile();
            open(file);
        }
    }

    /** Open the last recorded file. */
    void openLastFile() {
        open(_history.peekLast().getFile());
    }

    /** Open file FILE, add it to editor. */
    void open(File file) {
        String title = file.getName();
        Editor editor = new Editor(file, false, _gui);
        JPanel editorArea = createEditorArea(editor);
        JScrollPane newone = new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        _gui.getTextArea().addTab(title, newone);
        _gui.getTextArea().setSelectedComponent(newone);
        updateHistory(editor);
        _gui.getMenubar().createHistory();
        if (_search != null) {
            _gui.getRightPanel().remove(_search.getSearch());
        }
        editor.requestFocus();
    }

    /** Save the file currently working on. Directly modify the file if
     * originally has one, or create a new file. */
    void saveFile() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null && curEd.ifChanged()) {
            if (curEd.isNewFile()) {
                saveAsFile();
            } else {
                save(curEd);
            }
        }
    }

    /** Create a new file and save it to the current directory. Show alert
     * when there is a file with same path already exists. */
    void saveAsFile() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null) {
            String title = curEd.getFile().getName();
            _filechooser.setSelectedFile(new File(title));
            int value = _filechooser.showSaveDialog(null);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = _filechooser.getSelectedFile();
                if (file.exists()) {
                    String alert = "<html><b>'" + file.getName() + "' already " +
                            "exists. Do you want to replace it?</b><br><br> A file " +
                            "or folder with the same name already exists in <br> " +
                            "the folder " + file.getParentFile().getName() + ". " +
                            "Replacing it will overwrite its current contents.</html>";
                    if (JOptionPane.showConfirmDialog(null, alert)
                            == JFileChooser.APPROVE_OPTION) {
                        save(curEd);
                    }
                } else {
                    save(curEd);
                }
            }
        }
    }

    /** Save all the files in history. */
    void saveAll() {
        for (Editor e : _history) {
            saveFile();
        }
    }

    /** Write contents in editor EDITOR to file. */
    private void save(Editor editor) {
        try {
            FileWriter fileWriter = new FileWriter(editor.getFile());
            fileWriter.write(editor.getText());
            fileWriter.close();
        } catch (IOException e) {
            /* Ignore IOException. */
        }
    }

    /** Close the currently working on file, choose to save it or not. */
    int closeFile() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd!= null) {
            if (curEd.ifChanged()) {
                String alert = "<html><b>'" + curEd.getFile().getName() +
                        "' has changes, do you want to save them?</b><br><br>" +
                        "Your changes will be lost if you close this item without " +
                        "saving.<html>";
                int value = JOptionPane.showConfirmDialog(null, alert);
                if (value == JOptionPane.YES_OPTION) {
                    saveFile();
                    _gui.getTextArea().remove(pane);
                } else if (value == JOptionPane.NO_OPTION) {
                    _gui.getTextArea().remove(pane);
                } else {
                    return 0;
                }
            } else {
                _gui.getTextArea().remove(pane);
            }
        }
        if (_search != null) {
            _gui.getRightPanel().remove(_search.getSearch());
        }
        return 1;
    }

    /** Close all the files in history. */
    int closeAll() {
        for (Editor e : _history) {
            int i = closeFile();
            if (i == 0) {
                return 0;
            }
        }
        return 1;
    }

    /** Do simple edit commands in current editor, including Cut, Copy, Paste,
     * Delete, SelectAll and insert timestamp. */
    void simpleEditCommand(int c) {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null) {
            switch (c) {
                case 1: curEd.cut(); break;
                case 2: curEd.copy(); break;
                case 3: curEd.paste(); break;
                case 4: curEd.replaceSelection(""); break;
                case 5: curEd.selectAll(); break;
                case 6: curEd.insert(new Date().toString(),
                        curEd.getSelectionStart()); break;
            }
        }
    }

    /** Copy file information to system clipboard, either file path
     * or cursor position (row, col). */
    void copyInfo(boolean ifpath) {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection ss;
            if (ifpath) {
                String path = curEd.getFile().getAbsolutePath();
                ss = new StringSelection(path);
            } else {
                String ref = _gui.getStatusbar().getText();
                ss = new StringSelection(ref);
            }
            cb.setContents(ss, ss);
        }
    }

    /** Undo or Redo the last change in current editor. */
    void undo(boolean undo) {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null) {
            UndoManager undoman = curEd.getUndo();
            if (undo && undoman.canUndo()) {
                undoman.undo();
            } else if (!undo && undoman.canRedo()) {
                undoman.redo();
            }
        }
    }

    /** Evoke the search panel, used for find and replace commands. */
    void search() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null) {
            _search = new Search(curEd);
            JPanel right = _gui.getRightPanel();
            right.add(_search.getSearch());
            right.revalidate();
            right.repaint();
        }
    }

    /** Go to a row or position. */
    void goTo() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null) {
            try {
                int initcol = curEd.getLineOfOffset
                        (curEd.getCaretPosition()) + 1;
                String hint = "Enter a <row> or <row>:<column> to go there. \n" +
                        "Examples: '3' for row 3 or '2:7' for row 2 and column 7. \n ";
                String value = JOptionPane.showInputDialog(null, hint, initcol);
                if (value != null) {
                    String pos = value.trim().replaceAll(" ", "");
                    if (pos.matches("\\d+")) {
                        int row = Integer.parseInt(pos) - 1;
                        curEd.setCaretPosition(curEd.getLineStartOffset(row));
                    } else if (pos.matches("\\d+:\\d+")){
                        String row = pos.split(":")[0];
                        String col = pos.split(":")[1];
                        int cod = curEd.getLineStartOffset(
                                Integer.parseInt(row) - 1) - 1 + Integer.parseInt(col);
                        curEd.setCaretPosition(cod);
                    } else {
                        JOptionPane.showMessageDialog(null, "Unavailable row number.");
                        goTo();
                    }
                }
            } catch (BadLocationException e) {
                JOptionPane.showMessageDialog(null, "Unavailable row number.");
                goTo();
            }
        }
    }

    /** All the selection commands. */
    void select(int commandIndex) {
        JScrollPane pane = (JScrollPane) _gui.getTextArea().getSelectedComponent();
        Editor curEd = getSelectedEditor(pane);
        if (curEd != null) {
            try {
                Pattern p1 = Pattern.compile("\\W");
                Pattern p2 = Pattern.compile("\\S");
                int curPot = curEd.getCaretPosition();
                int curRow = curEd.getLineOfOffset(curPot);
                int ewPot = curPot, bwPot = curPot;
                int nexRowStartPot = curEd.getLineStartOffset(curRow);
                for(; ewPot <= curEd.getDocument().getLength(); ewPot++) {
                    Matcher m = p1.matcher(curEd.getText(ewPot, 1));
                    if (m.find()) {
                        break;
                    }
                }
                for(; bwPot >= 0; bwPot--) {
                    Matcher m = p1.matcher(curEd.getText(bwPot, 1));
                    if (m.find()) {
                        break;
                    }
                }
                for (; nexRowStartPot <= curPot; nexRowStartPot++) {
                    Matcher m = p2.matcher(curEd.getText(nexRowStartPot, 1));
                    if (m.find()) {
                        break;
                    }
                }
                switch (commandIndex) {
                    case 1: curEd.setSelectionStart(0); /* Select to Top. */
                        curEd.setSelectionEnd(curPot);
                        break;
                    case 2: curEd.setSelectionStart(curPot); /* Select to Bottom. */
                        curEd.setSelectionEnd(curEd.getDocument().getLength());
                        break;
                    case 3: curEd.setSelectionStart(curEd.getLineStartOffset(curRow)); /* Select Line. */
                        curEd.setSelectionEnd(curEd.getLineStartOffset(curRow + 1));
                        break;
                    case 4: curEd.setSelectionStart(bwPot + 1); /* Select Word. */
                        curEd.setSelectionEnd(ewPot);
                        break;
                    case 5: curEd.setSelectionStart(bwPot + 1); /* Select BoW. */
                        curEd.setSelectionEnd(curPot);
                        break;
                    case 6: curEd.setSelectionStart(curEd.getLineStartOffset(curRow)); /* Select BoL. */
                        curEd.setSelectionEnd(curEd.getLineStartOffset(curPot));
                        break;
                    case 7: curEd.setSelectionStart(nexRowStartPot); /* Select FCoL. */
                        curEd.setSelectionEnd(curEd.getLineStartOffset(curPot));
                        break;
                    case 8: curEd.setSelectionStart(curPot); /* Select EoW. */
                        curEd.setSelectionEnd(ewPot);
                        break;
                    case 9: curEd.setSelectionStart(curEd.getLineStartOffset(curPot)); /* Select EoL. */
                        curEd.setSelectionEnd(curEd.getLineStartOffset(curRow + 1));
                        break;
                    case 0: int[] r = getBracketsPot(curEd, curPot); /* Select Inside Brackets. */
                        if (r != null) {
                            curEd.setSelectionStart(r[0] + 1);
                            curEd.setSelectionEnd(r[1]);
                        }
                        break;
                }
            } catch (BadLocationException e) {
                /* No such case. */
            }

        }

    }

    /** Get the history records. */
    Deque<Editor> getHistory() {
        return _history;
    }

    /** Clear the history queue. */
    void clearHistory() {
        _history.clear();
        _gui.getMenubar().createHistory();
    }

    /** Search the beginning position and end position of brackets. */
    private int[] getBracketsPot(Editor curEd, int curPot) {
        Pattern p1 = Pattern.compile("[({\\[]");
        Pattern p2 = Pattern.compile("[)}\\]]");
        int ebPot = curPot, bbPot = curPot;
        try {
            int i1 = 0, i2 = 0;
            for(; bbPot >= 0; bbPot--) {
                Matcher m1 = p1.matcher(curEd.getText(bbPot, 1));
                Matcher m2 = p2.matcher(curEd.getText(bbPot, 1));
                if (m2.find()) {
                    i1++;
                }
                if (m1.find()) {
                    i1--;
                    if (i1 < 0) {
                        break;
                    }
                }
                if (bbPot == 0) {
                    return null;
                }
            }
            for(; ebPot <= curEd.getDocument().getLength(); ebPot++) {
                Matcher m1 = p1.matcher(curEd.getText(ebPot, 1));
                Matcher m2 = p2.matcher(curEd.getText(ebPot, 1));
                if (m1.find()) {
                    i2++;
                }
                if (m2.find()) {
                    i2--;
                    if (i2 < 0) {
                        break;
                    }
                }
                if (ebPot == curEd.getDocument().getLength()) {
                    return null;
                }
            }
        } catch (BadLocationException e) {
            /* No such case. */
        }
        return new int[]{bbPot, ebPot};
    }

    /** Get selected editor, return null if does not have. */
    private Editor getSelectedEditor(JScrollPane pane) {
        if (pane == null) {
            return null;
        }
        JPanel selpanel = (JPanel) pane.getViewport().getView();
        return (Editor) selpanel.getComponent(1);
    }

    /** Update the history array, put newest file at last position. */
    private void updateHistory(Editor editor) {
        for (Editor e : _history) {
            if (e.getFile().getAbsolutePath().equals
                    (editor.getFile().getAbsolutePath())) {
                _history.remove(e);
                break;
            }
        }
        _history.add(editor);
    }

    /** Create an editor panel, including a labelBar with column index and a
     * textArea. */
    private JPanel createEditorArea(Editor editor) {
        JPanel editorArea = new JPanel();
        editorArea.add(editor.getIndexBar());
        editorArea.add(editor);
        editorArea.setLayout(new BoxLayout(editorArea, BoxLayout.X_AXIS));
        return editorArea;
    }

    /** All commands in search panel. */
    private Search _search;

    /** FileChooser for fileManager. */
    private JFileChooser _filechooser;

    /** Restore all the files have been loaded chronologically. */
    private Deque<Editor> _history;

    /** GUI in display. */
    private GUI _gui;
}
