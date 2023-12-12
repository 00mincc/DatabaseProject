package DB_vSub;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
 

public class Login extends JFrame{

	JTextField tfid = null;
	JTextField tfname = null;
	JTextField tfdep = null;
	JTextField tfadd = null;

	DefaultTableModel model = null;

	JTable table = null;

	Connection conn=null;
	Statement stmt = null;
	
	static final String driver = "oracle.jdbc.driver.OracleDriver";
    static final String url = "jdbc:oracle:thin:@localhost:1521:xe";
    static final String id = "c##project";
    static final String pw = "1234";
    
    private JTextField usernameField;
    private JPasswordField passwordField;

	public Login() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
                                               "c##project", "1234");
		}catch (Exception e) {
			e.printStackTrace();
		}


		this.setTitle("로그인");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					if (conn!=null) {
						conn.close();	
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				} 			
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});

		
		Container c = this.getContentPane();
		c.setLayout(new FlowLayout());

		
		c.add(new JLabel("  아이디  "));
		usernameField = new JTextField(20);
		c.add(usernameField);
		c.add(new JLabel("비밀번호"));
		passwordField = new JPasswordField(20);
		c.add(passwordField);
		JButton btnSearch = new JButton("로그인");
		
		usernameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("아이디")) {
                    usernameField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("  아이디");
                }
            }
        });

        // 비밀번호
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (passwordField.getText().equals("       ")) {
                   passwordField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getText().isEmpty()) {
                   passwordField.setText("       ");
                } 
            }
        });
		
		btnSearch.addActionListener(new ActionListener() {
			@Override
			 public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                try {
                    if (checkCredentials(username, password)) {
                        JOptionPane.showMessageDialog(null, username + "님 로그인 성공!");
                        new Home();
                        setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "로그인 실패. 이름 또는 비밀번호를 확인하세요.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

		});
		c.add(btnSearch);

		this.setSize(320,130);

		this.setVisible(true);
		
	
	}
	
	public static boolean checkCredentials(String usernameToFind, String passwordToFind) throws Exception {
        Class.forName(driver);
        Connection db = DriverManager.getConnection(url, id, pw);

        String sql = "SELECT * FROM LOGIN WHERE LOGIN_ID = ? AND LOGIN_PASSWORD = ?";
        PreparedStatement pstmt = db.prepareStatement(sql);
        pstmt.setString(1, usernameToFind);
        pstmt.setString(2, passwordToFind);

        ResultSet rs = pstmt.executeQuery();
        
        boolean credentialsMatch = rs.next();
        
        db.close();

        return credentialsMatch;
    }
}
