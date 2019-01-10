package App;

import com.sun.org.glassfish.gmbal.ParameterNames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  @author Victor Chen
 */
class SignIn {

    /** Initiate the whole Sign-In interface. */
    void init() {
        frame = new JFrame("Victor");
        frame.setSize(WIDTH_FRAME, HEIGHT_FRAME);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int init_x = dim.width / 2 - frame.getSize().width / 2;
        int init_y = dim.height / 2 - frame.getSize().height;
        frame.setLocation(init_x, init_y);

        addLabel();
        addText();
        addButton();

        frame.add(new Decorate());
        frame.setVisible(true);
    }

    /** Add labels, using MouseListener to open the file user saved before. */
    private void addLabel() {
        JLabel l1 = new JLabel("EMAIL");
        l1.setBounds(X, Y_LABEL1, WIDTH_COMP, HEIGHT_LABEL12);
        l1.setFont(LABEL);
        l1.setForeground(GRAY_COLOR);

        JLabel l2 = new JLabel("PASSWORD");
        l2.setFont(LABEL);
        l2.setForeground(GRAY_COLOR);
        l2.setBounds(X, Y_LABEL2, WIDTH_COMP, HEIGHT_LABEL12);

        JLabel l3 = new JLabel("Already have an account?");
        l3.setFont(CHOOSE);
        l3.setForeground(GRAY_COLOR);
        l3.setBounds(X_LABEL3, Y_LABEL34, WIDTH_LABEL3, HEIGHT_LABEL34);

        JLabel l4 = new JLabel("Sign in");
        l4.setFont(CHOOSE);
        l4.setForeground(WHITE_COLOR);
        l4.setBounds(X_LABEL4, Y_LABEL34, WIDTH_LABEL4, HEIGHT_LABEL34);
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
                    Matcher m = p.matcher(file.getName());
                    if (m.find()) {
                        email.setText(m.group());
                    }
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
        email.setBounds(X, Y_TEXT1, WIDTH_COMP, HEIGHT_TEXT);
        email.setBorder(BorderFactory.createEmptyBorder());
        email.setBackground(TRANSPARENT);
        email.setFont(INFO);
        email.setForeground(WHITE_COLOR);
        email.setToolTipText("email...");

        password = new JPasswordField();
        password.setBounds(X, Y_TEXT2, WIDTH_COMP, HEIGHT_TEXT);
        password.setBackground(TRANSPARENT);
        password.setBorder(BorderFactory.createEmptyBorder());
        password.setFont(INFO);
        password.setForeground(WHITE_COLOR);
        password.setToolTipText("password...");

        frame.add(email);
        frame.add(password);
    }

    /** Add GET STARTED button to current frame, using ActionListener to
     * check if email and password can match. */
    private void addButton() {
        JButton getstarted = new JButton(new ImageIcon("button.jpg"));
        getstarted.setBounds(X, Y_BUTTON, WIDTH_COMP, HEIGHT_BUTTON);
        getstarted.setFont(BUTTON);
        getstarted.addActionListener(e -> {
            String s1 = email.getText();
            String s2 = "";
            for (char c : password.getPassword()) {
                s2 += c;
            }
            /*
             * .....
             */
            frame.setVisible(false);
            new GUI();
        });
        frame.add(getstarted);
    }

    public class Decorate extends JComponent{
        @Override
        public void paintComponent(Graphics g) {
            Toolkit t = Toolkit.getDefaultToolkit();
            Image i = t.getImage("background.jpg");
            g.drawImage(i, 0, 0, this);

            g.setColor(GRAY_COLOR);
            g.fillRect(X, Y_DIS, WIDTH_COMP, HEIGHT_REC);
            g.fillRect(X, Y_DIS * 2, WIDTH_COMP, HEIGHT_REC);
        }

    }

    /** Fonts. */
    private static final Font
            LABEL = new Font("Courier", Font.PLAIN, 18),
            CHOOSE = new Font("SansSerif", Font.PLAIN, 14),
            INFO = new Font("SansSerif", Font.PLAIN, 20),
            BUTTON = new Font("SansSerif", Font.PLAIN, 25);

    /** Colors. */
    private static final Color
            GRAY_COLOR = Color.GRAY,
            WHITE_COLOR = Color.WHITE,
            TRANSPARENT = new Color(0,0,0, 0);

    /** Lengths. */
    private static final int
            WIDTH_FRAME = 700, WIDTH_COMP = 310,
            WIDTH_LABEL3 = 180, WIDTH_LABEL4 = 100,

            HEIGHT_FRAME = 450, HEIGHT_DIFF = 10,
            HEIGHT_LABEL34 = 30, HEIGHT_REC = 2,
            HEIGHT_LABEL12 = HEIGHT_LABEL34 + HEIGHT_DIFF,
            HEIGHT_TEXT = HEIGHT_LABEL12 + HEIGHT_DIFF,
            HEIGHT_BUTTON = HEIGHT_TEXT + HEIGHT_DIFF,

            X = 315, X_LABEL3 = 360, X_LABEL4 = 540,

            Y_DIS = 120, Y_LABEL1 = 30, Y_LABEL2 = Y_LABEL1 + Y_DIS,
            Y_LABEL34 = 330, Y_TEXT1 = 70, Y_TEXT2 = Y_TEXT1 + Y_DIS,
            Y_BUTTON = Y_DIS * 2 + Y_LABEL1;

    /** Match username prefix. */
    private Pattern p = Pattern.compile(".*(?=\\.)");

    /** User's email address. */
    private JTextField email;

    /** User's password. */
    private JPasswordField password;

    /** Current Sign-In interface. */
    private JFrame frame;
}