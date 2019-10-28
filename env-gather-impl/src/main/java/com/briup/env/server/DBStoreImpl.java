package com.briup.env.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import com.briup.env.Configuration;
import com.briup.env.bean.Environment;
import com.briup.env.support.ConfigurationAware;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.Log;

public class DBStoreImpl implements DBStore,PropertiesAware,ConfigurationAware {
	
	static String driver;
	static String url;
	static String user;
	static String password;

	private Connection conn;
	private Log logger;

	@Override
	public void init(Properties properties) throws Exception {
		driver = properties.getProperty("driver");
		url = properties.getProperty("url");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
	}
	
	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		logger = configuration.getLogger();
		System.out.println("--------"+logger);
	}	

	@Override
	public void saveDB(Collection<Environment> c) {

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
		// 获取数据连接
		System.out.println("--------"+logger);
		logger.info("连接数据库......");
		try {
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// PrepareStatement对象预存储SQL同构语句
		String sql = null;
		PreparedStatement prepareStatement = null;
		String sql2 = null;

		// 记录数据条数
		int count = 0;
		// 遍历数据集合中Environment对象
		for (Environment en : c) {
			// 具体哪一天对应的哪一张表
			@SuppressWarnings("deprecation")
			int date = en.getGather_date().getDate();
			logger.info("一条数据插入到>>>e_detail_" + date + "表中");
			sql = "insert into e_detail_" + date + " values(?,?,?,?,?,?,?,?,?,?)";
			
			//判断是否是一样的sql同构的语句
			if (prepareStatement == null) {
				try {
					prepareStatement = conn.prepareStatement(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				if (!sql.equals(sql2)) {
					if (count % 1000 == 0) {
						try {
							prepareStatement.executeBatch();
						} catch (SQLException e) {
							try {
								conn.rollback();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							e.printStackTrace();
						}
					}
					try {
						prepareStatement.executeBatch();
					} catch (SQLException e) {
						try {
							conn.rollback();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					} finally {
						if (prepareStatement!=null) {
							try {
								prepareStatement.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}													
						}
					}
					try {
						prepareStatement = conn.prepareStatement(sql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			sql2 = sql;

			count++;
			try {
				prepareStatement.setString(1, en.getName());
				prepareStatement.setString(2, en.getSrcId());
				prepareStatement.setString(3, en.getDesId());
				prepareStatement.setString(4, en.getDevId());
				prepareStatement.setString(5, en.getSersorAddress());
				prepareStatement.setInt(6, en.getCount());
				prepareStatement.setString(7, en.getCmd());
				prepareStatement.setInt(8, en.getStatus());
				prepareStatement.setFloat(9, en.getData());
				prepareStatement.setDate(10, new java.sql.Date(en.getGather_date().getTime()));
				
				prepareStatement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// 批处理：每2000执行SQL
			if (count % 1000 == 0) {
				try {
					prepareStatement.executeBatch();
				} catch (SQLException e) {
					try {
						conn.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
		logger.info(count + "条数据入库成功!");
		//最后执行批处理
		try {
			prepareStatement.executeBatch();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			//提交事务
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (prepareStatement!=null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
