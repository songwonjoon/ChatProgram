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
		setTitle("채팅 서버");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		vChat_list=new Vector<>();
		btn_exit=new Button("서버 종료");
		btn_exit.addActionListener(this);
		ta=new TextArea();
		add(ta, BorderLayout.CENTER);
		add(btn_exit, BorderLayout.SOUTH);
		setBounds(250, 250, 200, 200);
		setVisible(true);
		
		//채팅 할 수 있는 메소드를 호출
		chatStart();
	}
	
	public void chatStart() {
		//소켓 생산
		try {
			sck=new ServerSocket(9000);
			while (true) {
				socket_client=sck.accept();
				ta.append(socket_client.getInetAddress().getHostAddress()+"접속함\n");
				//접속자의 ip얻기
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
				sendAllClient(name+"님께서 입장");
				while (true) {
					//채팅 내용 받기
					String msg=br.readLine();
					String str=socket_client.getInetAddress().getHostName();
					ta.append(msg+"\n"); // 채팅 내용을 msg에 추가
					if (msg.equals("@@Exit")) {
						break;
					}else {
						sendAllClient(name+" : "+msg);
						//접속자 모두에게 메시지 전달
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