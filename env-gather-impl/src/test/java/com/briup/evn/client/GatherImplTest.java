package com.briup.evn.client;


import org.junit.Test;

import com.briup.env.client.GatherImpl;

public class GatherImplTest {

	@Test
	public void gatherTest() throws Exception {
		
		GatherImpl gatherImpl = new GatherImpl();
		gatherImpl.gather();
	}
}
