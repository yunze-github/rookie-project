package com.briup.env.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HelloTest {
	Map<String, Object> map = new HashMap<String, Object>();
	Properties p = new Properties();
	@SuppressWarnings("unchecked")
	private void show() {
		SAXReader reader = new SAXReader();
		try {
			Document document = 
					reader.read(getClass().getClassLoader().getResourceAsStream("conf.xml"));
			Element rootElement = document.getRootElement();
			List<Element> elements = rootElement.elements();
			for (Element element : elements) {
				String name = element.getName();
				Object newInstance = Class.forName(element.attributeValue("class")).newInstance();
			map.put(name, newInstance);
			System.out.println(name);
			System.out.println(newInstance);
			
			List<Element> elementss = element.elements();
				for (Element element2 : elementss) {
					String name2 = element2.getName();
					String text = element2.getText();
					p.setProperty(name2, text);
					System.out.println(name2);
					System.out.println(text);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	@SuppressWarnings("unused")
	private void initOtherModel() {
		for (Object obj : map.values()) {
			String property = p.getProperty("dataBackup");
			System.err.println(property);
		}
	}
	public static void main(String[] args) {
		
		HelloTest helloTest = new HelloTest();
		helloTest.show();
		helloTest.initOtherModel();
		
	}
}
