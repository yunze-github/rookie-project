package com.briup.env.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.briup.env.Configuration;
import com.briup.env.bean.Environment;
import com.briup.env.support.ConfigurationAware;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.Backup;
import com.briup.env.util.BackupImpl;
import com.briup.env.util.Log;

public class GatherImpl implements Gather,PropertiesAware,ConfigurationAware {
	
	private String dataFile;
	private String gatherCount;
	
	private Log logger;
	private Backup backupImpl;

	private Environment en = null;
	private Environment en1 = null;
	private Collection<Environment> coll = new ArrayList<Environment>();

	int tempC, lightC, carbonC;

	@Override
	public Collection<Environment> gather() throws Exception {

		GatherImpl.class.getClassLoader();
		InputStream sra = ClassLoader.getSystemResourceAsStream(dataFile);
		// 动态获取数据文件缓冲字符流
		BufferedReader br = new BufferedReader(new InputStreamReader(sra));

		// 对之前读取数据进行保存，记录
		logger.debug("收集模块--对之前读取数据进行记录");
		int available = sra.available();
		Integer len = (Integer) backupImpl.load(gatherCount, BackupImpl.LOAD_REMOVE);
		logger.debug("收集模块--已经读取过的文件字节："+len);
		if (len != null) {
			// 跳过之前读取数据
			br.skip(len);			
		}
		logger.debug("收集模块--保存读取过文件字节");
		backupImpl.store(gatherCount, available+2, BackupImpl.STORE_OVERRIDE);

		// 数据行标记
		// 读取data文件中每一行的数据，进行分析
		String readLine = null;
		@SuppressWarnings("unused")
		int index = 0;
		while ((readLine = br.readLine()) != null) {
			index++;

			// 分割处理每行数据，保存到Environment对象中
			en = new Environment();
			String[] split = readLine.split("\\|");
			//数据异常判断
			if (split.length!=9) {
				logger.debug("数据异常！");
				continue;
			}else {
				en.setSrcId(split[0]);
				en.setDesId(split[1]);
				en.setDevId(split[2]);
				en.setSersorAddress(split[3]);
				en.setCount(Integer.parseInt(split[4]));
				en.setCmd(split[5]);
				en.setStatus(Integer.parseInt(split[7]));
				en.setGather_date(new Timestamp(Long.parseLong(split[8])));
				
				// 判断环境类型
				if ("16".equals(split[3])) {
					// 保留小数点后4位
					DecimalFormat df = new DecimalFormat(".0000");
					// 温度
					en.setName("温度");
					Integer temp = Integer.parseInt(split[6].substring(0, 4), 16);
					float tempFloat = Float.parseFloat(df.format((temp * 0.00268127) - 46.85));
					en.setData(tempFloat);
					
					// 湿度
					Integer damp = Integer.parseInt(split[6].substring(0, 4), 16);
					float dampFloat = Float.parseFloat(df.format((damp * 0.00190735) - 6));
					en1 = new Environment("湿度", split[0], split[1], split[2], split[3], Integer.parseInt(split[4]),
							split[5], Integer.parseInt(split[7]), dampFloat, new Timestamp(Long.parseLong(split[8])));
					tempC++;
					coll.add(en1);
				}
				//光照
				else if ("256".equals(split[3])) {
					en.setName("光照");
					en.setData((float) Integer.parseInt(split[6].substring(0, 4), 16));
					lightC++;
				}
				//二氧化碳
				else if ("1280".equals(split[3])) {
					en.setName("二氧化碳");
					en.setData((float) Integer.parseInt(split[6].substring(0, 4), 16));
					carbonC++;
				}else {
					logger.debug("未知数据类型："+readLine);
				}
				coll.add(en);
			}

		}
		for (Environment environment : coll) {
			logger.debug("Collection中的数据=" + environment.toString());
		}
		logger.debug("温湿度数据总计：" + tempC*2);
		logger.debug("二氧化碳数据总计：" + carbonC);
		logger.debug("光照数据总计：" + lightC);

		if (br!=null)br.close();
		if (sra!=null)sra.close();
		return coll;
	}

	@Override
	public void init(Properties properties) throws Exception {
		dataFile = properties.getProperty("dataFile");
		gatherCount = properties.getProperty("gatherCount");
	}

	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
		backupImpl = configuration.getBackup();
	}
}
