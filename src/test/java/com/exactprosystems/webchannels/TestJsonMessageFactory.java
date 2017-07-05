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
		
		JsonMessageFactory messageFactory = new JsonMessageFactory();
		StringWriter writer = new StringWriter();
	
		HeartBeat g = new HeartBeat();
		List<WithSeqnumWrapper> list = new ArrayList<>();
		list.add(new WithSeqnumWrapper(1, g));
		messageFactory.encodeMessage(list, writer);
		
		assertEquals("[{\"seqnum\":1,\"message\":{\"messageType\":\"com.exactprosystems.webchannels.messages.HeartBeat\"}}]", writer.toString());
		
	}
	
	@Test
	public void testSingleDecode() throws Exception {
		
		JsonMessageFactory messageFactory = new JsonMessageFactory();
		StringReader reader = new StringReader("[{\"seqnum\":1,\"message\":{\"messageType\":\"com.exactprosystems.webchannels.messages.HeartBeat\"}}]");
	
		List<WithSeqnumWrapper> list = messageFactory.decodeMessage(reader);
		assertEquals(1, list.size());
		
		WithSeqnumWrapper wrapper = list.get(0);
		assertEquals(1, wrapper.getSeqnum());
		
		Object message = wrapper.getMessage();
		assertTrue(message instanceof HeartBeat);
		
	}
	
}
