package com.client;

import java.util.Scanner;

public class MainClass {
	public static void main(String arg[]) {
		 new Linker(65534,65535,"���ү"){
			@Override
			protected void linkSuccessed() {
				MsgSender msgSender = new MsgSender(this, 60000){
					@Override
					protected void sendSuccessed() {
						System.out.println("���ͳɹ�");
					}
					@Override
					protected void serverLost() {
						System.out.println("����������");
					}
				};
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				System.out.println("��ʼ���룺");
				while(scanner.hasNextLine())
				{
					System.out.println("���ڷ���");
					msgSender.sendMSG(scanner.nextLine());
				}
			}
			@Override
			protected void linkTimeOut() {
				System.out.println("���ӳ�ʱ");
			}
		};
	}
}
