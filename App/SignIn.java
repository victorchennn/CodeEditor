package App;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComponent;
import javax.swing.WindowConstants;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

class SignIn {

    /** Initiate the whole Sign-In interface. */
    void init() {
        frame = new JFrame("Victor");
        frame.setSize(700, 450);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int init_x = dim.width / 2 - frame.getSize().width / 2;
        int init_y = dim.height / 2 - frame.getSize().height;
        frame.setLocation(init_x, init_y);

        addLabel();
        addText();
        addButton();

        frame.add(new Paint());
        frame.setVisible(true);
    }

    /** Add labels, using MouseListener to open the file user saved before. */
    private void addLabel() {
        JLabel l1 = new JLabel("EMAIL");
        l1.setBounds(315, 30, 300, 40);
        l1.setFont(new Font("Courier", Font.PLAIN, 18));
        l1.setForeground(Color.GRAY);

        JLabel l2 = new JLabel("PASSWORD");
        l2.setFont(new Font("Courier", Font.PLAIN, 18));
        l2.setForeground(Color.GRAY);
        l2.setBounds(315, 150, 300, 40);

        JLabel l3 = new JLabel("Already have an account?");
        l3.setFont(new Font("Courier", Font.PLAIN, 14));
        l3.setForeground(Color.GRAY);
        l3.setBounds(340, 340, 200, 30);

        JLabel l4 = new JLabel("Sign in");
        l4.setFont(new Font("Courier", Font.PLAIN, 13));
        l4.setForeground(Color.WHITE);
        l4.setBounds(540, 340, 100, 30);
        l4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fc = new JFileChooser();
                String current_path = System.getProperty("user.dir");
                fc.setCurrentDirectory(new File(current_path));
                int value = fc.showOpenDialog(null);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    FileReader fr = null;
                    try {
                        fr = new FileReader(file);
                    } catch (FileNotFoundException exp) {
                        /* ... */
                    }
                    BufferedReader br = new BufferedReader(fr);
                    /*
                     * ....
                     */
                }
            }
        });
        frame.add(l1);
        frame.add(l2);
        frame.add(l3);
        frame.add(l4);
    }

    /** Add textfield EMAIL and passwordfield PASSWORD. */
    private void addText() {
        email = new JTextField();
        email.setBounds(314, 70, 310, 50);
        email.setBorder(BorderFactory.createEmptyBorder());
        email.setBackground(new Color(255,255,255, 0));
        email.setFont(new Font("SansSerif", Font.PLAIN, 20));
        email.setForeground(Color.WHITE);

        password = new JPasswordField();
        password.setBounds(314, 190, 310, 50);
        password.setBackground(new Color(255,255,255, 0));
        password.setBorder(BorderFactory.createEmptyBorder());
        password.setFont(new Font("SansSerif", Font.PLAIN, 20));
        password.setForeground(Color.WHITE);

        frame.add(email);
        frame.add(password);
    }

    /** Add GET STARTED button to current frame, using ActionListener to
     * check if email and password can match. */
    private void addButton() {
        JButton getstarted = new JButton(new ImageIcon("button.jpg"));
        getstarted.setBounds(314, 280, 310, 60);
        getstarted.setFont(new Font("SansSerif", Font.PLAIN, 25));
        getstarted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = email.getText();
                String s2 = "";
                for (char c : password.getPassword()) {
                    s2 += c;
                }
                /*
                 * ....
                 */
            }
        });
        frame.add(getstarted);
    }

    public class Paint extends JComponent{
        @Override
        public void paintComponent(Graphics g) {
            Toolkit t = Toolkit.getDefaultToolkit();
            Image i = t.getImage("background.jpg");
            g.drawImage(i, 0, 0, this);

            g.setColor(Color.GRAY);
            g.fillRect(314, 120, 310, 2);
            g.fillRect(314, 240, 310, 2);
        }

    }

    /** User's email address. */
    private JTextField email;

    /** User's password. */
    private JPasswordField password;

    /** Current Sign-In interface. */
    private JFrame frame;
}