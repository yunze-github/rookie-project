package com.briup.env.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.briup.env.Configuration;
import com.briup.env.support.ConfigurationAware;

public class BackupImpl implements Backup,ConfigurationAware {
	private Log logger;
	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
	}

	@Override
	public Object load(String fileName, boolean del) throws Exception {
		BackupImpl.class.getClassLoader();
		String resource = ClassLoader.getSystemResource(fileName).getPath();
		File file = new File(resource.substring(resource.indexOf("/") + 1));
		if (!file.exists()) {
			file.createNewFile();
			//logger.info("备份文件不存在！");
			return null;
		}
		if (file.length()==0) {
			logger.info("备份数据不存在！");
			return null;
		}
		//logger.info("读取备份数据......");
		@SuppressWarnings("resource")
		ObjectInputStream ois = 
		new ObjectInputStream(new FileInputStream(file));
		Object readObject = ois.readObject();
		//logger.info("读取备份数据完成！");
		
		//删除文件
		if (del) {
			//logger.info("删除备份数据！");
			file.delete();
		}			
		return readObject;						
		
	}

	@Override
	public void store(String fileName, Object obj, boolean append) throws Exception {
		BackupImpl.class.getClassLoader();
		String resource = ClassLoader.getSystemResource(fileName).getPath();
		File file = new File(resource.substring(resource.indexOf("/") + 1));
		if (file!=null) {
			file.createNewFile();
		}
		// 收集数据异常，进行数据备份
		//System.out.println(logger);
		//logger.info("开始数据备份......");
		
		@SuppressWarnings("resource")
		ObjectOutputStream oos = 
		new ObjectOutputStream(new FileOutputStream(file, append));
		
		oos.writeObject(obj);
		oos.flush();
		//logger.info("数据备份完成！");
	}
}
