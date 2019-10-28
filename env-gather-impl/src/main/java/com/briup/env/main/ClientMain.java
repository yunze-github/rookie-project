package com.briup.env.main;

import java.util.Collection;

import com.briup.env.Configuration;
import com.briup.env.ConfigurationImpl;
import com.briup.env.bean.Environment;
import com.briup.env.client.Client;
import com.briup.env.client.Gather;

public class ClientMain {
	public static void main(String[] args) {

		Configuration configuration = new ConfigurationImpl();
		Collection<Environment> coll = null;
		try {
			Gather gather = configuration.getGather();
			Client client = configuration.getClient();
			// 获取数据集合
			coll = gather.gather();
			// 发送数据
			client.send(coll);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
