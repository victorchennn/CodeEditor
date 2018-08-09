package App;

import javax.swing.*;
import java.io.*;

class Editor extends JTextArea {
    Editor(File file, boolean newfile) {
        _file = file;
        _newfile = newfile;
        writeFile(file);
        _inittext = getText();
    }

    File getFile() {
        return _file;
    }

    boolean ifChanged() {
        return !_inittext.equals(getText());
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

    private String _inittext;

    private File _file;

    private boolean _newfile;
}
