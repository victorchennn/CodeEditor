package App;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

/** All things to do with file commands.
 *  @author Victor Chen
 */
class FileManagement {

    FileManagement(GUI gui) {
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
        Editor newone = new Editor(new File(title), true);
        JPanel editorArea = createEditorArea(newone);
        textarea.addTab(title, new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        _history.add(newone);
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
        Editor editor = new Editor(file, false);
        JPanel editorArea = createEditorArea(editor);
        _gui.getTextArea().addTab(title, new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        updateHistory(editor);
        _gui.getMenubar().createHistory();
    }

    /** Save the file currently working on. Directly modify the file if
     * originally has one, or create a new file. */
    void saveFile() {
        Editor currentEditor = (Editor) _gui.getTextArea().getSelectedComponent();
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
        Editor currentEditor = (Editor) _gui.getTextArea().getSelectedComponent();
        if (currentEditor != null) {
            _filechooser.setSelectedFile(new File("Untitled"));
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
            System.out.println(editor.getText());
            fileWriter.write(editor.getText());
            fileWriter.close();
        } catch (IOException e) {
            /* Ignore IOException. */
        }
    }

    /** Close the currently working on file, choose to save it or not. */
    void closeFile() {
        Editor currentEditor = (Editor) _gui.getTextArea().getSelectedComponent();
        if (currentEditor.ifChanged()) {
            String alert = "<html><b>'" + currentEditor.getFile().getName() +
                    "' has changes, do you want to save them?</b><br><br>" +
                    "Your changes will be lost if you close this item without saving.<html>";
            int value = JOptionPane.showConfirmDialog(null, alert);
            if (value == JOptionPane.YES_OPTION) {
                saveFile();
                _gui.getTextArea().remove(currentEditor);
            } else if (value == JOptionPane.NO_OPTION) {
                _gui.getTextArea().remove(currentEditor);
            }
        } else {
            _gui.getTextArea().remove(currentEditor);
        }
    }

    /** Close all the files in history. */
    void closeAll() {
        for (Editor e : _history) {
            closeFile();
        }
    }

    /** Get the history records. */
    Deque<Editor> getHistory() {
        return _history;
    }

    /** Clear the history queue. */
    void clearHistory() {
        _history.clear();
    }


    /** Update the history array, put newest file at last position. */
    private void updateHistory(Editor editor) {
        for (Editor e : _history) {
            if (e.getFile().getAbsolutePath().equals(editor.getFile().getAbsolutePath())) {
                _history.remove(e);
                break;
            }
        }
        _history.add(editor);
    }

    private JPanel createEditorArea(Editor editor) {
        JPanel editorArea = new JPanel();
        editorArea.add(editor.getLabel());
        editorArea.add(editor);
        editorArea.setPreferredSize(new Dimension(900, 800));
        editorArea.setLayout(new BoxLayout(editorArea, BoxLayout.X_AXIS));
        return editorArea;
    }

    /** FileChooser for fileManager. */
    private JFileChooser _filechooser;

    /** Restore all the files have been loaded chronologically. */
    private Deque<Editor> _history;

    /** GUI on display. */
    private GUI _gui;
}
