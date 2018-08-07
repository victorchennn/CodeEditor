package App;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

/**
 *  @author Victor Chen
 */
public class GUI {

    public static void main(String...args) {
        new GUI();
    }

    private GUI() {
        JFrame _frame = new JFrame();
        _frame.setSize(1200, 800);
        _frame.setLocation(150, 100);
        JPanel _container = new JPanel();
        _container.setLayout(new BoxLayout(_container, BoxLayout.X_AXIS));

        _container.add(createLeftPanel());
        _container.add(createRightPanel());

        _frame.add(_container);
        Menubar menubar = new Menubar();

        _frame.setJMenuBar(menubar.createMenuBar());
        _frame.setVisible(true);
        _frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }



    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 800));
        File file = new File(System.getProperty("user.dir"));
        DefaultMutableTreeNode cur = new DefaultMutableTreeNode(file.getName());
        createDicTree(file, cur);
        JTree dic = new JTree(cur);
        dic.getSelectionModel().addTreeSelectionListener(e -> {
            TreePath path = e.getNewLeadSelectionPath();
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) path.getLastPathComponent();
            /* ...*/
        });
        panel.add(BorderLayout.CENTER, new JScrollPane(dic));
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(900, 800));
        JTabbedPane tp = new JTabbedPane();

        JTextArea ta = new JTextArea(200,200);
        tp.addTab("A", new JScrollPane(ta));
        JTextArea tb = new JTextArea(200,200);
        tp.addTab("B", new JScrollPane(tb));

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(tp);
        return panel;
    }

    /**
     * Tree structure of files in current directory. Sort files by name in
     * ascending order, put directories at front.
     */
    private void createDicTree(File path, DefaultMutableTreeNode cur) {
        String[] sorted_files = path.list();
        if (sorted_files != null) {
            Arrays.sort(sorted_files, (o1, o2) -> {
                File file1 = new File(path, o1);
                File file2 = new File(path, o2);
                if (file1.isDirectory() && !file2.isDirectory()) {
                    return -1;
                } else if (!file1.isDirectory() && file2.isDirectory()) {
                    return 1;
                } else {
                    return o1.compareTo(o2);
                }
            });
            for(String filename : sorted_files) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(filename);
                cur.add(node);
                File file = new File(path, filename);
                if (file.isDirectory()) {
                    createDicTree(file, node);
                }
            }
        }
    }
}
