package TestG04.chat;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument.HTMLReader.CharacterAction;

public class ChatServer extends JFrame implements ActionListener{
	Button btn_exit;
	TextArea ta;
	Vector vChat_list;
	ServerSocket sck;
	Socket socket_client;
	
	public ChatServer() {
		setTitle("ä�� ����");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		vChat_list=new Vector<>();
		btn_exit=new Button("���� ����");
		btn_exit.addActionListener(this);
		ta=new TextArea();
		add(ta, BorderLayout.CENTER);
		add(btn_exit, BorderLayout.SOUTH);
		setBounds(250, 250, 200, 200);
		setVisible(true);
		
		//ä�� �� �� �ִ� �޼ҵ带 ȣ��
		chatStart();
	}
	
	public void chatStart() {
		//���� ����
		try {
			sck=new ServerSocket(9000);
			while (true) {
				socket_client=sck.accept();
				ta.append(socket_client.getInetAddress().getHostAddress()+"������\n");
				//�������� ip���
				ChatHandle threadChat=new ChatHandle();
				vChat_list.add(threadChat);
				threadChat.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		dispose();
		
	}
	
	public static void main(String[] args) {
		new ChatServer();
	}
	
	class  ChatHandle extends Thread{
		BufferedReader br=null;
		PrintWriter pw=null;
		
		public ChatHandle() {
			try {
				InputStream isc=socket_client.getInputStream();
				br=new BufferedReader(new InputStreamReader(isc));
				
				OutputStream os=socket_client.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void sendAllClient(String msg) {
			int size=vChat_list.size();
			for (int i = 0; i < size; i++) {
				ChatHandle chr=(ChatHandle)vChat_list.elementAt(i);
				chr.pw.println(msg);
				chr.pw.flush();
			}
		}
		
		@Override
		public void run() {
			try {
				String name=br.readLine();
				sendAllClient(name+"�Բ��� ����");
				while (true) {
					//ä�� ���� �ޱ�
					String msg=br.readLine();
					String str=socket_client.getInetAddress().getHostName();
					ta.append(msg+"\n"); // ä�� ������ msg�� �߰�
					if (msg.equals("@@Exit")) {
						break;
					}else {
						sendAllClient(name+" : "+msg);
						//������ ��ο��� �޽��� ����
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				vChat_list.remove(this);
				try {
					br.close();
					pw.close();
					socket_client.close();
				} catch (IOException e) { } // catch
			} // finally
		} // run
	}
}