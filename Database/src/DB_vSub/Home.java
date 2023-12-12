package DB_vSub;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
 

public class Home extends JFrame{

	JTextField tfid = null;
	JTextField tfname = null;
	JTextField tfdep = null;
	JTextField tfadd = null;

	DefaultTableModel model = null;

	JTable table = null;

	Connection conn=null;
	Statement stmt = null;


	public Home() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", 
                                               "c##project", "1234");
		}catch (Exception e) {
			e.printStackTrace();
		}


		this.setTitle("학사관리 시스템");
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
		 
		tfname = new JTextField(25);
		tfdep = new JTextField(25);
		tfadd = new JTextField(25);
		
		c.add(new JLabel("학번"));
		tfid = new JTextField(19);
		c.add(tfid);

		JButton btnSearch = new JButton("검색");
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stmt = conn.createStatement();					
					ResultSet rs = stmt.executeQuery("select * from INFO where INFO_ID ='"+tfid.getText()+"'");

					model.setNumRows(0);

					while (rs.next()) {
						String[] row = new String[4]; //컬럼 갯수가 4
						row[0] = rs.getString("INFO_ID");
						row[1] = rs.getString("INFO_NAME");
						row[2] = rs.getString("INFO_DEPT");
						row[3] = rs.getString("INFO_ADRESS");
						model.addRow(row);			

					 	tfname.setText(rs.getString("INFO_NAME"));
					 	tfdep.setText(rs.getString("INFO_DEPT"));
						tfadd.setText(rs.getString("INFO_ADRESS"));
					}

					rs.close();

				}catch (Exception e1) {
					 e1.printStackTrace();
				}
			}

		});


		c.add(btnSearch);
		c.add(new JLabel("이름"));
		
		c.add(tfname);
		c.add(new JLabel("학과"));
		
		c.add(tfdep);
		c.add(new JLabel("주소"));
		
		c.add(tfadd);


        //표에 출력할 컬럼명
		String colName[] = {"학번", "이름", "학과", "주소"};

        //표의 데이터
		model = new DefaultTableModel(colName,0);

        //테이블에 모델(데이터) 바인딩
		table = new JTable(model);

	
		//테이블 사이즈
		table.setPreferredScrollableViewportSize(new Dimension(320,270));

		c.add(new JScrollPane(table));

		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				table = (JTable)e.getComponent();
				model = (DefaultTableModel)table.getModel();
				String id = (String)model.getValueAt(table.getSelectedRow(), 0); //id
				String name = (String)model.getValueAt(table.getSelectedRow(), 1); //name
				String dept = (String)model.getValueAt(table.getSelectedRow(), 2); //dept
				String address = (String)model.getValueAt(table.getSelectedRow(), 3); //address

				tfid.setText(id);
				tfname.setText(name);
				tfdep.setText(dept);
				tfadd.setText(address);
			}
		});
		
		JButton btnInsert = new JButton("등록");
		btnInsert.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        if (tfid.getText().equals("") || tfname.getText().equals("") || tfdep.getText().equals("") || tfadd.getText().equals("")) {
		            System.out.println("등록이 정상처리 되지 않았습니다.");
		            JOptionPane.showMessageDialog(null, "항목을 모두 입력해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
		        } else {
		            if (isDuplicateID(tfid.getText())) {
		                JOptionPane.showMessageDialog(null, "이미 존재하는 학번입니다. 다른 학번을 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
		            } else {
		                int option = JOptionPane.showConfirmDialog(null, "입력한 내용이 맞습니까?\n"
		                        + "\n학번 : " + tfid.getText() + "\n이름 : " + tfname.getText()
		                        + "\n학과 : " + tfdep.getText() + "\n주소 : " + tfadd.getText(),
		                        "확인", JOptionPane.YES_NO_OPTION);

		                if (option == JOptionPane.YES_OPTION) {
		                    try {
		                        stmt = conn.createStatement();
		                        stmt.executeUpdate("insert into INFO(INFO_ID,INFO_NAME,INFO_DEPT,INFO_ADRESS) "
		                                + "values('" + tfid.getText() + "','" + tfname.getText() + "','"
		                                + tfdep.getText() + "','" + tfadd.getText() + "')");

		                        model.setNumRows(0);

		                        tfid.setText("");
		                        tfname.setText("");
		                        tfdep.setText("");
		                        tfadd.setText("");

		                        showList();

		                        JOptionPane.showMessageDialog(null, "등록되었습니다");

		                    } catch (Exception e2) {
		                        e2.printStackTrace();
		                    } finally {
		                        // Statement를 닫아주어야 함
		                        try {
		                            if (stmt != null) {
		                                stmt.close();
		                            }
		                        } catch (Exception ex) {
		                            ex.printStackTrace();
		                        }
		                    }
		                }
		            }
		        }
		    }
		});
		c.add(btnInsert);

		

		JButton btnList = new JButton("목록");
		btnList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ResultSet rs = null;
				try {
					stmt = conn.createStatement();
					rs = stmt.executeQuery("select * from INFO");

					//목록 초기화
					model.setNumRows(0);

					while (rs.next()) {
						String[] row = new String[4]; //컬럼 갯수가 4
						row[0] = rs.getString("INFO_ID");
						row[1] = rs.getString("INFO_NAME");
						row[2] = rs.getString("INFO_DEPT");
						row[3] = rs.getString("INFO_ADRESS");
						model.addRow(row);		
					}

					showList();

				} catch (Exception e2) {
					e2.printStackTrace();
				} finally {
					try {
						if (stmt!=null) {
							stmt.close();
						}if (rs!=null) {
							rs.close();
						}
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			}
		});

		c.add(btnList);

		

		JButton btnUpdate = new JButton("수정");
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stmt = conn.createStatement();
					stmt.executeUpdate("update INFO set "
							+ "INFO_NAME='"+tfname.getText()
							+ "', INFO_DEPT='"+tfdep.getText()
							+ "', INFO_ADRESS='"+tfadd.getText()+"' "
							+ "where INFO_ID='"+tfid.getText()+"'");

					JOptionPane.showMessageDialog(null, "수정되었습니다");

					showList();

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		c.add(btnUpdate);

		

		JButton btnDelete = new JButton("삭제");
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null, "삭제하시겠습니까?","Delete",
						JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
					System.out.println("삭제처리...");

					JOptionPane.showMessageDialog(null, "삭제되었습니다.","알림",JOptionPane.INFORMATION_MESSAGE);

				}

				try {
					stmt = conn.createStatement();
					stmt.executeUpdate("delete from INFO where INFO_ID='"+tfid.getText()+"'");

					//목록 초기화
					model.setNumRows(0);

					//text label 초기화
					tfid.setText(""); 
					tfname.setText("");
					tfdep.setText(""); 
					tfadd.setText(""); 

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		c.add(btnDelete);

		this.setSize(350,500);
		this.setVisible(true);

	}

	public void showList(){

	    try{
	         ResultSet rs = stmt.executeQuery("select * from INFO");

	         //JTable 초기화
	         model.setNumRows(0);

	         while(rs.next()){
	              String[] row=new String[4];//컬럼의 갯수가 4
	              row[0]=rs.getString("INFO_ID");
	              row[1]=rs.getString("INFO_NAME");
	              row[2]=rs.getString("INFO_DEPT");
	              row[3]=rs.getString("INFO_ADRESS");
	              model.addRow(row);
	         }

	         rs.close();

	    }catch(Exception e){
	         e.getStackTrace();
	    }
	 }
	
	// 중복 학번 체크 메서드
	private boolean isDuplicateID(String studentID) {
	    try {
	        Statement stmtCheck = conn.createStatement();
	        ResultSet rsCheck = stmtCheck.executeQuery("SELECT COUNT(*) FROM INFO WHERE INFO_ID = '" + studentID + "'");
	        rsCheck.next();
	        int count = rsCheck.getInt(1);

	        rsCheck.close();
	        stmtCheck.close();

	        return count > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return true; // 에러 발생 시 중복으로 처리
	    }
	}
}
