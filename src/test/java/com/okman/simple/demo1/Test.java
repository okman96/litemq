package com.okman.simple.demo1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;

import com.okman.litemq.Config;
import com.okman.litemq.core.factory.ILitemqFactory;
import com.okman.litemq.core.factory.LitemqFactory;
import com.okman.litemq.util.LitemqHelper;
import com.okman.simple.util.DateUtil;

/**
 * 优先队列-demo
 *
 * @auth waxuan
 * @since 2018年8月23日上午9:59:30
 */
public class Test {

	public static void main(String[] args) throws Exception {
		String path = Test.class.getResource("/").getPath();
		PropertyConfigurator.configure(path +"com/okman/simple/log4j.properties");  
		
		/**
		 * 创建消费者，并开启遍历模式
		 */
		List<String> keys = new ArrayList<String>();
		keys.add("线程-order1");
		keys.add("线程-order2");
		keys.add("线程-order3");
		Config.getInstance().setIsPersistence(true);	//是否开启持久化
		Config.getInstance().setIsPersistenceInitLoad(true);	//启动时是否加载持久化文件
		ILitemqFactory factory = new LitemqFactory("com.okman.simple.demo1.Consumer", keys, true);
		
		/**
		 * 创建生产者，创建产品
		 */
		int taskSize = 1;
		Executor executor1 = Executors.newFixedThreadPool(taskSize);
		executor1.execute(new Runnable() {
			public void run() {
				for (int i=0;i<10;i++) {
					Product product = new Product();
					int randomInt = 1000 + ((int) (new Random().nextFloat() * (100000 - 1000)));
					long index = System.currentTimeMillis() + randomInt;
					String name = "名称-" + i;
					product.setIndex(index);
					product.setName(name);
					product.setPrice(Long.valueOf(i + 20 + ""));
					System.out.println("放入元素:" + name + " ，开始推送的时间为：" + DateUtil.dateToStr(new Date(index)));
					LitemqHelper.loopOffer(factory, product);
				}
				System.out.println("--------------------------------------------");
	        }
		});
	}
	
}
