package com.briup.env.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.env.Configuration;
import com.briup.env.bean.Environment;
import com.briup.env.support.ConfigurationAware;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.BackupImpl;
import com.briup.env.util.Log;

public class ServerImpl implements Server,PropertiesAware,ConfigurationAware {
	private ServerSocket server;
	
	private int serverPort;
	private String dataBackup;
	// 循环标识
	private static volatile boolean flag = true;
	private Log logger;
	
	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
	}
	
	@Override
	public void init(Properties properties) throws Exception {
		dataBackup = properties.getProperty("dataBackup");
		serverPort = Integer.parseInt(properties.getProperty("serverport"));
	}

	class ClientThread extends Thread {
		Socket socket;
		Collection<Environment> reciverList;

		public ClientThread() {
		}

		public ClientThread(Socket socket) {
			this.socket = socket;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {

			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				reciverList = (Collection<Environment>) ois.readObject();
				logger.debug("服务器模块--服务器接收到" + reciverList.size() + "条数据！");

			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	@Override
	public void reciver() throws Exception {
		
		new Thread() {
			public void run() {
				ServerSocket shutDownSocket;
				try {
					shutDownSocket = new ServerSocket(8088);
					shutDownSocket.accept();
					ServerImpl.this.shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		logger.info("服务器模块--<服务器建立>");
		logger.info("服务器模块--监听-"+serverPort+"-端口");
		server = new ServerSocket(serverPort);

		while (flag) {
			Socket socket = server.accept();
			logger.info("服务器模块--客户端成功连接服务器！");

			ClientThread clientThread = new ClientThread(socket);
			// 线程启动
			logger.info("服务器模块--线程启动......");
			clientThread.start();
			Collection<Environment> reciverList = clientThread.reciverList;
			if (reciverList != null&& reciverList.size()!=0) {
				//服务器数据备份
				logger.info("服务器模块--服务器进行数据备份......");
				new BackupImpl().store(dataBackup, clientThread.reciverList, false);
				// 接收数据并入库
				logger.info("服务器模块--数据入库......");
				new DBStoreImpl().saveDB(clientThread.reciverList);
				logger.info("服务器模块--数据入库完成！");
			}
		}
	}

	@Override
	public void shutdown() throws Exception {
		ServerImpl.flag = false;
	}

}