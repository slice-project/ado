package org.etri.ado.misc;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.fasterxml.jackson.databind.JsonNode;

import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.geometry_msgs.Vector3;
import ros.tools.MessageUnpacker;
import javax.swing.JSeparator;

public class WaffleTest extends JFrame {
	private static final long serialVersionUID = 213051419017095923L;
	
	private static double Delta = 0.3;
	private static Vector3 LEFT = new Vector3(-Delta, 0, 0);
	private static Vector3 RIGHT = new Vector3(Delta, 0, 0);
	private static Vector3 UP = new Vector3(0, Delta, 0);
	private static Vector3 DOWN = new Vector3(0, -Delta, 0);
	private static Vector3 UPPER_LEFT = new Vector3(-Delta, Delta, 0);
	private static Vector3 UPPER_RIGHT = new Vector3(Delta, Delta, 0);
	private static Vector3 LOWER_LEFT = new Vector3(-Delta, -Delta, 0);
	private static Vector3 LOWER_RIGHT = new Vector3(Delta, -Delta, 0);
	
	private JPanel contentPane;
	private JTextField xPos;
	private JTextField yPos;
	private JTextField ipText;
	private JTextField portText;

	RosBridge bridge = new RosBridge();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WaffleTest frame = new WaffleTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WaffleTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 451, 369);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		contentPane.addKeyListener(new KeyPressedListener());
		
		JLabel lblCurrentLoc = new JLabel("X");
		lblCurrentLoc.setBounds(63, 77, 14, 16);
		contentPane.add(lblCurrentLoc);
		
		xPos = new JTextField();
		xPos.setEditable(false);
		xPos.setBounds(89, 72, 84, 26);
		contentPane.add(xPos);
		xPos.setColumns(10);
		
		JLabel lblY = new JLabel("Y");
		lblY.setBounds(223, 77, 21, 16);
		contentPane.add(lblY);
		
		yPos = new JTextField();
		yPos.setEditable(false);
		yPos.setBounds(253, 72, 93, 26);
		contentPane.add(yPos);
		yPos.setColumns(10);
		
		JButton btnUpperleft = new JButton("UpperLeft");
		btnUpperleft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", UPPER_LEFT);
				System.out.println("Move delat to upper left!");
			}
		});
		btnUpperleft.setBounds(53, 114, 105, 29);
		contentPane.add(btnUpperleft);
		
		JButton btnUpperright = new JButton("UpperRight");
		btnUpperright.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", UPPER_RIGHT);
				System.out.println("Move delat to upper right!");
			}
		});
		btnUpperright.setBounds(260, 114, 117, 29);
		contentPane.add(btnUpperright);
		
		JButton btnLowerleft = new JButton("LowerLeft");
		btnLowerleft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", LOWER_LEFT);
				System.out.println("Move delat to lower left!");
			}
		});
		btnLowerleft.setBounds(53, 224, 117, 29);
		contentPane.add(btnLowerleft);
		
		JButton btnLowerright = new JButton("LowerRight");
		btnLowerright.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", LOWER_RIGHT);
				System.out.println("Move delat to lower right!");
			}
		});
		btnLowerright.setBounds(260, 224, 117, 29);
		contentPane.add(btnLowerright);
		
		JButton btnLeft = new JButton("Left");
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", LEFT);
				System.out.println("Move delat to left!");
			}
		});
		btnLeft.setBounds(119, 171, 61, 29);
		contentPane.add(btnLeft);
		
		JButton btnRight = new JButton("Right");
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", RIGHT);
				System.out.println("Move delat to right!");
			}
		});
		btnRight.setBounds(223, 171, 61, 29);
		contentPane.add(btnRight);
		
		JButton btnUp = new JButton("Up");
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", UP);
				System.out.println("Move delat to up!");
			}
		});
		btnUp.setBounds(170, 136, 61, 29);
		contentPane.add(btnUp);
		
		JButton btnDown = new JButton("Down");
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", DOWN);
				System.out.println("Move delat to down!");
			}
		});
		btnDown.setBounds(170, 201, 61, 29);
		contentPane.add(btnDown);
		
		JLabel lbIP = new JLabel("IP");
		lbIP.setBounds(18, 33, 29, 16);
		contentPane.add(lbIP);
		
		ipText = new JTextField();
		ipText.setBounds(41, 28, 130, 26);
		contentPane.add(ipText);
		ipText.setColumns(10);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(198, 33, 29, 16);
		contentPane.add(lblPort);
		
		portText = new JTextField();
		portText.setBounds(231, 28, 61, 26);
		contentPane.add(portText);
		portText.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println(ipText.getText());
				System.out.println(portText.getText());
				
				StringBuilder builder = new StringBuilder();
				builder.append("ws://");
				builder.append(ipText.getText().trim());
				builder.append(":");
				builder.append(portText.getText());
				
				bridge.connect(builder.toString(), true);
				JOptionPane.showMessageDialog(null, "Connection established!");
				bridge.subscribe(SubscriptionRequestMsg.generate("/waffle/current_loc")
						.setType("geometry_msgs/Vector3")
						.setThrottleRate(1)
						.setQueueLength(1),
					new RosListenDelegate() {
						@Override
						public void receive(JsonNode data, String stringRep) {
							MessageUnpacker<Vector3> unpacker = new MessageUnpacker<Vector3>(Vector3.class);
							Vector3 msg = unpacker.unpackRosMessage(data);
							xPos.setText(Double.toString(msg.x));
							yPos.setText(Double.toString(msg.y));
							System.out.println("position(" + msg.x + ", " + msg.y + ")");							
						}
					}
			);				
				
			}
		});
		btnConnect.setBounds(327, 28, 93, 29);
		contentPane.add(btnConnect);
		
		JButton btnKeyboardControl = new JButton("Keyboard Control");
		btnKeyboardControl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contentPane.requestFocus();
			}
		});
		btnKeyboardControl.setBounds(144, 289, 140, 29);
		contentPane.add(btnKeyboardControl);		
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 268, 439, 12);
		contentPane.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(6, 104, 439, 12);
		contentPane.add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(6, 56, 439, 12);
		contentPane.add(separator_2);
	}
	
	class KeyPressedListener implements KeyListener {

		public KeyPressedListener() {
			
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			
			switch ( keyCode ) {
				case KeyEvent.VK_Q:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", UPPER_LEFT);
					System.out.println("Move delat to upper left!");
					break;
				case KeyEvent.VK_W:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", UP);
					System.out.println("Move delat to up!");
					break;
				case KeyEvent.VK_E:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", UPPER_RIGHT);
					System.out.println("Move delat to upper right!");
					break;
				case KeyEvent.VK_A:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", LEFT);
					System.out.println("Move delat to left!");
					break;
				case KeyEvent.VK_D:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", RIGHT);
					System.out.println("Move delat to right!");
					break;
				case KeyEvent.VK_Z:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", LOWER_LEFT);
					System.out.println("Move delat to lower left!");
					break;
				case KeyEvent.VK_X:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", DOWN);
					System.out.println("Move delat to down!");
					break;
				case KeyEvent.VK_C:
					bridge.publish("/waffle/move_delta",  "geometry_msgs/Vector3", LOWER_RIGHT);
					System.out.println("Move delat to lower right!");
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {			
		}
		
	}
}
