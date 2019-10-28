package com.briup.env.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.env.Configuration;
import com.briup.env.bean.Environment;
import com.briup.env.support.ConfigurationAware;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.Log;

public class ClientImpl extends Thread implements Client,PropertiesAware,ConfigurationAware {
	
	private String host;
	private int port;
	private Log logger;
	
	@Override
	public void send(Collection<Environment> c) throws Exception {

		Socket client = null;
		ObjectOutputStream oos = null;
		try {
			//没有数据可进行读取
			if (c.size()!=0) {
				logger.debug("客户端连接服务器......");
				client = new Socket(host, port);
				
				
				logger.debug("传输数据......");
				oos = new ObjectOutputStream(client.getOutputStream());
				
				logger.debug("客户端接收数据="+c.size()+"条");
				oos.writeObject(c);
				oos.flush();
				logger.debug("传输数据完成!");
				
			}else {
				logger.debug("全部数据已经读取过！");
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
	}

	@Override
	public void init(Properties properties) throws Exception {
		port = Integer.parseInt(properties.getProperty("port"));
		host = properties.getProperty("host");
	}
}
