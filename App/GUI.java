package App;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

/**
 *  @author Victor Chen
 */
public class GUI {

    public static void main(String...args) {
        new GUI();
    }

    GUI() {
        JFrame _frame = new JFrame();
        _frame.setSize(1200, 800);
        _frame.setLocation(150, 100);
        JPanel _container = new JPanel();
        _container.setLayout(new BoxLayout(_container, BoxLayout.X_AXIS));

        _container.add(createLeftPanel());
        _container.add(createRightPanel());

        _frame.add(_container, BorderLayout.CENTER);
        _statusbar = new JLabel(" ", JLabel.RIGHT);
        _frame.add(_statusbar, BorderLayout.SOUTH);
        _menubar = new Menubar(this);
        _frame.setJMenuBar(_menubar.createMenuBar());

        _frame.setVisible(true);
        _frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (getMenubar().getManager().closeAll() == 1){
                    _frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                } else {
                    _frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    /** Return the TextArea in GUI. */
    JTabbedPane getTextArea() {
        return _textarea;
    }

    /** Return the Menubar in GUI. */
    Menubar getMenubar() {
        return _menubar;
    }

    /** Return the Statusbar in GUI. */
    JLabel getStatusbar() {
        return _statusbar;
    }

    /** Return Right panel. */
    JPanel getRightPanel() {
        return _right;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 800));
        String curpath = System.getProperty("user.dir");
        File file = new File(curpath);
        DefaultMutableTreeNode cur = new DefaultMutableTreeNode(file.getName());
        createDicTree(file, cur);
        JTree dic = new JTree(cur);
        dic.getSelectionModel().addTreeSelectionListener(e -> {
            TreePath path = e.getNewLeadSelectionPath();
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) path.getLastPathComponent();
            /* NEED to be fixed. */
        });
        dic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selRow = dic.getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    TreePath selpath = dic.getPathForLocation(e.getX(), e.getY());
                    if (e.getClickCount() == 2) {
                        String abpath = curpath;
                        Object[] path = Arrays.copyOfRange(selpath.getPath(), 1, selpath.getPath().length);
                        for (Object o : path) {
                            abpath += "/" + o;
                        }
                        File selected = new File(abpath);
                        _menubar.getManager().open(selected);                   /* NEED to be fixed. */
                    }
                }
            }
        });
        panel.add(BorderLayout.CENTER, new JScrollPane(dic));
        return panel;
    }

    private JPanel createRightPanel() {
        _right = new JPanel();
        _right.setPreferredSize(new Dimension(900, 800));
        _right.setLayout(new BoxLayout(_right, BoxLayout.Y_AXIS));
        _textarea = new JTabbedPane();
        _right.add(_textarea);
        return _right;
    }

    /** Tree structure of files in current directory. Sort files by name in
     * ascending order, put directories at front. */
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

    /** Right Panel. */
    private JPanel _right;

    /** Status bar. */
    private JLabel _statusbar;

    /** Menu bar. */
    private Menubar _menubar;

    /** Typing area. */
    private JTabbedPane _textarea;
}
