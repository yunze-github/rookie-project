package com.briup.env;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.env.client.Client;
import com.briup.env.client.Gather;
import com.briup.env.server.DBStore;
import com.briup.env.server.Server;
import com.briup.env.support.ConfigurationAware;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.Backup;
import com.briup.env.util.Log;

public class ConfigurationImpl implements Configuration {

	private Map<String, Object> map = new HashMap<String, Object>();
	private Properties p = new Properties();
	
	public ConfigurationImpl() {
		parse("conf.xml");
		initOtherModel();
	}
	
	private void initOtherModel() {
		for (Object obj : map.values()) {
			if (obj instanceof PropertiesAware) {
				try {
					((PropertiesAware) obj).init(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (obj instanceof ConfigurationAware) {
				try {
					((ConfigurationAware) obj).setConfiguration(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parse(String configFilePath) {
		SAXReader reader = new SAXReader();
		try {
			Document document = 
					reader.read(getClass().getClassLoader().getResourceAsStream(configFilePath));
			Element rootElement = document.getRootElement();
			List<Element> elements = rootElement.elements();
			for (Element element : elements) {
			map.put(element.getName(), Class.forName(element.attributeValue("class")).newInstance());
			
			List<Element> elementss = element.elements();
				for (Element element2 : elementss) {
					p.setProperty(element2.getName(), element2.getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Log getLogger() throws Exception {
		return (Log) map.get(ModelName.LOGGER);
	}

	@Override
	public Server getServer() throws Exception {
		return (Server) map.get(ModelName.SERVER);
	}

	@Override
	public Client getClient() throws Exception {
		return (Client) map.get(ModelName.CLIENT);
	}

	@Override
	public DBStore getDbStore() throws Exception {
		return (DBStore) map.get(ModelName.DBSTORE);
	}

	@Override
	public Gather getGather() throws Exception {
		return (Gather) map.get(ModelName.GATHER);
	}

	@Override
	public Backup getBackup() throws Exception {
		return (Backup) map.get(ModelName.BACKUP);
	}
	
	private interface ModelName {
		String GATHER = "gather";
		String CLIENT = "client";
		String SERVER = "server";
		String DBSTORE = "dbStore";
		String BACKUP = "backup";
		String LOGGER = "logger";
	}

}
