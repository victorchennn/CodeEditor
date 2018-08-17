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

/** All things to do with file commands.
 *  @author Victor Chen
 */
class FileManager {

    FileManager(GUI gui) {
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
        JTabbedPane textarea = _gui.getTextArea();
        String entertitle = "<html>Enter the name for the new file.<br><br></html>";
        String title = JOptionPane.showInputDialog(null, entertitle, "Untitled");
        if (title != null) {
            Editor newone = new Editor(new File(title), true, _gui);
            JPanel editorArea = createEditorArea(newone);
            textarea.addTab(title, new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
            _history.add(newone);
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
        _gui.getTextArea().addTab(title, new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        updateHistory(editor);
        _gui.getMenubar().createHistory();
    }

    /** Save the file currently working on. Directly modify the file if
     * originally has one, or create a new file. */
    void saveFile() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor != null && currentEditor.ifChanged()) {
            if (currentEditor.isNewFile()) {
                saveAsFile();
            } else {
                save(currentEditor);
            }
        }
    }

    /** Create a new file and save it to the current directory. Show alert
     * when there is a file with same path already exists. */
    void saveAsFile() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor != null) {
            String title = currentEditor.getFile().getName();
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
                        save(currentEditor);
                    }
                } else {
                    save(currentEditor);
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
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor!= null) {
            if (currentEditor.ifChanged()) {
                String alert = "<html><b>'" + currentEditor.getFile().getName() +
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
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor != null) {
            switch (c) {
                case 1: currentEditor.cut(); break;
                case 2: currentEditor.copy(); break;
                case 3: currentEditor.paste(); break;
                case 4: currentEditor.replaceSelection(""); break;
                case 5: currentEditor.selectAll(); break;
                case 6: currentEditor.insert(new Date().toString(),
                        currentEditor.getSelectionStart()); break;
            }
        }
    }

    /** Copy file information to system clipboard, either file path
     * or cursor position (row, col). */
    void copyInfo(boolean ifpath) {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor != null) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection ss;
            if (ifpath) {
                String path = currentEditor.getFile().getAbsolutePath();
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
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor != null) {
            UndoManager undoman = currentEditor.getUndo();
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
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor != null) {
            _search = new Search(currentEditor);
            JPanel right = _gui.getRightPanel();
            right.add(_search.getSearch());
            right.revalidate();
            right.repaint();
        }
    }

    /** Go to a row or position. */
    void goTo() {
        JScrollPane pane = (JScrollPane)  _gui.getTextArea().getSelectedComponent();
        Editor currentEditor = getSelectedEditor(pane);
        if (currentEditor != null) {
            try {
                int initcol = currentEditor.getLineOfOffset
                        (currentEditor.getCaretPosition()) + 1;
                String hint = "Enter a <row> or <row>:<column> to go there. \n" +
                        "Examples: '3' for row 3 or '2:7' for row 2 and column 7. \n ";
                String value = JOptionPane.showInputDialog(null, hint, initcol);
                if (value != null) {
                    String pos = value.trim().replaceAll(" ", "");
                    if (pos.matches("\\d+")) {
                        int row = Integer.parseInt(pos) - 1;
                        currentEditor.setCaretPosition(currentEditor.getLineStartOffset(row));
                    } else if (pos.matches("\\d+:\\d+")){
                        String row = pos.split(":")[0];
                        String col = pos.split(":")[1];
                        int cod = currentEditor.getLineStartOffset(
                                Integer.parseInt(row) - 1) - 1 + Integer.parseInt(col);
                        currentEditor.setCaretPosition(cod);
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

    /** Get the history records. */
    Deque<Editor> getHistory() {
        return _history;
    }

    /** Clear the history queue. */
    void clearHistory() {
        _history.clear();
        _gui.getMenubar().createHistory();
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

    private Search _search;

    /** FileChooser for fileManager. */
    private JFileChooser _filechooser;

    /** Restore all the files have been loaded chronologically. */
    private Deque<Editor> _history;

    /** GUI in display. */
    private GUI _gui;
}
