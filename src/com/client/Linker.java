package com.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class Linker extends Thread{
	private InetAddress serverAdd;
	private InetAddress group;
	private String broadCastAdd;
	private String username;
	private int dstport;

	private int srcport;
	private int linktime = 0;
	public Linker(int srcport, int dstport, String username){
		try {
			this.dstport = dstport;
			this.srcport = srcport;
			this.username = username;
			broadCastAdd = this.getLocalBroadCastAdd();
			if(broadCastAdd != null){
				group = InetAddress.getByName(broadCastAdd);
				this.start();	
			}else {
				NetInterfaceNotFound();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		while (true) {
			/*
			 * ���������
			 */
			try {
				DatagramSocket socketSender = new DatagramSocket(dstport);
				byte[] dataRequest = new String("001"+username).getBytes();
				DatagramPacket request = new DatagramPacket(dataRequest, dataRequest.length, group, dstport);
				socketSender.send(request);
				socketSender.close();
				System.out.println("���ͳɹ�:"+new String(dataRequest));
			} catch (IOException e) {
			}
			/*
			 * ���շ�����IP����ʱ���·�������.
			 */
			DatagramSocket socketReciever = null ;
			try {
				socketReciever = new DatagramSocket(srcport); 
				byte[] dataResponce = new byte[1024];
				DatagramPacket reply = new DatagramPacket(dataResponce, dataResponce.length);
				socketReciever.setSoTimeout(100);
				socketReciever.receive(reply);
				socketReciever.close();
				String massage = byteToString(reply.getData());
				if(!isReply(massage.substring(0, 3))){
					serverAdd = reply.getAddress();
					linkSuccessed();
					System.out.println("�ҵ���������IP��"+serverAdd.getHostAddress());
					Thread.sleep(3600000);
				}
				else{
					Thread.sleep(100);
				}
				

			} catch (IOException | InterruptedException e) {
				System.out.println(e.toString()+"���������ҳ�ʱ��������...");
				linktime++;
				if(linktime == 100){
					socketReciever.close();
					linkTimeOut();
					break;
				}
				socketReciever.close();
			}

		}
	}
	
	public static String byteToString(byte[] bs)
	{
		String result = new String(bs);
		return result.trim();
	}
	
	public boolean isReply(String s){
		return s.equals("001");
		
	}

	public int getDstport() {
		return dstport;
	}

	public void setDstport(int dstport) {
		this.dstport = dstport;
	}

	public int getSrcport() {
		return srcport;
	}

	public void setSrcport(int srcport) {
		this.srcport = srcport;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	protected void linkSuccessed() {
		
	}
	
	protected void linkTimeOut() {
		
	}
	
	protected void NetInterfaceNotFound() {
		
	}

	public InetAddress getServerAdd() {
		return serverAdd;
	}
	
	public String getLocalBroadCastAdd(){
		try {
			InetAddress address = InetAddress.getLocalHost();
			NetworkInterface netInterface;
			netInterface = NetworkInterface.getByInetAddress(address);
			if (!netInterface.isLoopback() && netInterface.isUp()) {
				List<InterfaceAddress> interfaceAddresses = netInterface.getInterfaceAddresses();
				for (InterfaceAddress interfaceAddress : interfaceAddresses) {
					if (interfaceAddress.getBroadcast() != null) {
						System.out.println(interfaceAddress.getBroadcast().getHostAddress());// ����㲥��ַ
						return interfaceAddress.getBroadcast().getHostAddress();
					}
				}
			}
		} catch (SocketException | UnknownHostException e) {
			return null;
		}
		return null;
	}
	

}
