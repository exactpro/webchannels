package com.exactprosystems.webchannels;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.exactprosystems.webchannels.channel.JsonMessageFactory;
import com.exactprosystems.webchannels.channel.MessageFactoryConfigurator;
import com.exactprosystems.webchannels.channel.WithSeqnumWrapper;
import com.exactprosystems.webchannels.exceptions.DecodingException;
import com.exactprosystems.webchannels.exceptions.EncodingException;
import com.exactprosystems.webchannels.messages.AbstractMessage;
import com.exactprosystems.webchannels.messages.HeartBeat;
import com.exactprosystems.webchannels.messages.TestRequest;

import static org.junit.Assert.*;

public class TestJsonMessageFactory {
	
	@Test
	public void testSingleEncode() throws Exception {
		
		MessageFactoryConfigurator configurator = new MessageFactoryConfigurator();
		configurator.registerMessage(HeartBeat.class);
		configurator.registerMessage(TestRequest.class);
		
		JsonMessageFactory messageFactory = new JsonMessageFactory(configurator);
		StringWriter writer = new StringWriter();
	
		HeartBeat g = new HeartBeat();
		List<WithSeqnumWrapper> list = new ArrayList<>();
		list.add(new WithSeqnumWrapper(1, g));
		messageFactory.encodeMessage(list, writer);
		
		assertEquals("[{\"seqnum\":1,\"message\":{\"messageType\":\"HeartBeat\"}}]", writer.toString());
		
	}
	
	@Test(expected=EncodingException.class)
	public void testSingleEncodeNegative() throws Exception {
		
		MessageFactoryConfigurator configurator = new MessageFactoryConfigurator();
		
		JsonMessageFactory messageFactory = new JsonMessageFactory(configurator);
		StringWriter writer = new StringWriter();
	
		HeartBeat g = new HeartBeat();
		List<WithSeqnumWrapper> list = new ArrayList<>();
		list.add(new WithSeqnumWrapper(1, g));
		messageFactory.encodeMessage(list, writer);
		
	}
	
	@Test
	public void testSingleDecode() throws Exception {
		
		MessageFactoryConfigurator configurator = new MessageFactoryConfigurator();
		configurator.registerMessage(HeartBeat.class);
		configurator.registerMessage(TestRequest.class);
		
		JsonMessageFactory messageFactory = new JsonMessageFactory(configurator);
		StringReader reader = new StringReader("[{\"seqnum\":1,\"message\":{\"messageType\":\"HeartBeat\"}}]");
	
		List<WithSeqnumWrapper> list = messageFactory.decodeMessage(reader);
		assertEquals(1, list.size());
		
		WithSeqnumWrapper wrapper = list.get(0);
		assertEquals(1, wrapper.getSeqnum());
		
		AbstractMessage message = wrapper.getMessage();
		assertTrue(message instanceof HeartBeat);
		
	}
	
	@Test(expected=DecodingException.class)
	public void testSingleDecodeNegative() throws Exception {
		
		MessageFactoryConfigurator configurator = new MessageFactoryConfigurator();
		
		JsonMessageFactory messageFactory = new JsonMessageFactory(configurator);
		StringReader reader = new StringReader("[{\"seqnum\":1,\"message\":{\"messageType\":\"HeartBeat\"}}]");
	
		messageFactory.decodeMessage(reader);
		
	}
	
}
