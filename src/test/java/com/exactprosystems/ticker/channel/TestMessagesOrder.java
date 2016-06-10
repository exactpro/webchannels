package com.exactprosystems.ticker.channel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.exactprosystems.ticker.channel.AbstactMessageFactory;
import com.exactprosystems.ticker.channel.AbstractChannel;
import com.exactprosystems.ticker.channel.ChannelSettings;
import com.exactprosystems.ticker.channel.HttpChannel;
import com.exactprosystems.ticker.channel.IChannelHandler;
import com.exactprosystems.ticker.channel.JsonMessageFactory;
import com.exactprosystems.ticker.channel.MessageFactoryConfigurator;
import com.exactprosystems.ticker.channel.WebSocketChannel;
import com.exactprosystems.ticker.channel.WithSeqnumWrapper;
import com.exactprosystems.ticker.messages.AbstractMessage;
import com.google.common.util.concurrent.MoreExecutors;

public class TestMessagesOrder {
	
	private ChannelSettings settings;
	
	private ExecutorService executor;
	
	private AbstactMessageFactory messageFactory;
	
	@Before
	public void init() {
		
		executor = MoreExecutors.sameThreadExecutor();
		settings = new ChannelSettings(); 
		
		MessageFactoryConfigurator configurator = new MessageFactoryConfigurator();
		messageFactory = new JsonMessageFactory(configurator);
		
	}
	
	@After
	public void destroy() {
		
		executor.shutdownNow();
		
	}
	
	@Test
	public void testHttpChannel1() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(5, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		HttpChannel channel = new HttpChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		channel.onClose();
		
	}
	
	@Test
	public void testHttpChannel2() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(7, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		HttpChannel channel = new HttpChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		channel.onClose();
		
	}
	
	@Test
	public void testHttpChannel3() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);	
			}
			
			@Override
			public void onClose() {
				assertEquals(8, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		HttpChannel channel = new HttpChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);

		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		channel.onClose();
		
	}
	
	@Test
	public void testHttpChannel4() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(8, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		HttpChannel channel = new HttpChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		channel.onClose();
		
	}
	
	@Test
	public void testHttpChannel5() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(8, expectedSeqnum);
				assertEquals(2, sentMessages.size());
			}
			
		};
		
		HttpChannel channel = new HttpChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		channel.onClose();
		
	}
	
	@Test
	public void testHttpChannel6() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(11, expectedSeqnum);
				assertEquals(2, sentMessages.size());
			}
			
		};
		
		HttpChannel channel = new HttpChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg9 = new WithSeqnumWrapper(9, new TestBusinessMessage());
		channel.processInputMessage(msg9);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		WithSeqnumWrapper msg8 = new WithSeqnumWrapper(8, new TestBusinessMessage());
		channel.processInputMessage(msg8);
		
		WithSeqnumWrapper msg10 = new WithSeqnumWrapper(10, new TestBusinessMessage());
		channel.processInputMessage(msg10);
		
		channel.onClose();
		
	}
	
	@Test
	public void testHttpChannel7() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(5, expectedSeqnum);
				assertEquals(0, sentMessages.size());
			}
			
		};
		
		HttpChannel channel = new HttpChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper r_msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(r_msg2);
		
		WithSeqnumWrapper r_msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(r_msg3);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		channel.onClose();
		
	}
	
	@Test
	public void testWebsocketChannel1() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(5, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		WebSocketChannel channel = new WebSocketChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		channel.onClose();
		
	}
	
	@Test
	public void testWebsocketChannel2() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(7, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		WebSocketChannel channel = new WebSocketChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		channel.onClose();
		
	}
	
	@Test
	public void testWebsocketChannel3() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(8, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		WebSocketChannel channel = new WebSocketChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		channel.onClose();
		
	}
	
	@Test
	public void testWebsocketChannel4() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(8, expectedSeqnum);
				assertEquals(1, sentMessages.size());
			}
			
		};
		
		WebSocketChannel channel = new WebSocketChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		channel.onClose();
		
	}
	
	@Test
	public void testWebsocketChannel5() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(8, expectedSeqnum);
				assertEquals(2, sentMessages.size());
			}
			
		};
		
		WebSocketChannel channel = new WebSocketChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		channel.onClose();
		
	}
	
	@Test
	public void testWebsocketChannel6() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(11, expectedSeqnum);
				assertEquals(2, sentMessages.size());
			}
			
		};
		
		WebSocketChannel channel = new WebSocketChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg5 = new WithSeqnumWrapper(5, new TestBusinessMessage());
		channel.processInputMessage(msg5);
		
		WithSeqnumWrapper msg9 = new WithSeqnumWrapper(9, new TestBusinessMessage());
		channel.processInputMessage(msg9);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		WithSeqnumWrapper msg6 = new WithSeqnumWrapper(6, new TestBusinessMessage());
		channel.processInputMessage(msg6);
		
		WithSeqnumWrapper msg7 = new WithSeqnumWrapper(7, new TestBusinessMessage());
		channel.processInputMessage(msg7);
		
		WithSeqnumWrapper msg8 = new WithSeqnumWrapper(8, new TestBusinessMessage());
		channel.processInputMessage(msg8);
		
		WithSeqnumWrapper msg10 = new WithSeqnumWrapper(10, new TestBusinessMessage());
		channel.processInputMessage(msg10);
		
		channel.onClose();
		
	}
	
	@Test
	public void testWebsocketChannel7() {
		
		IChannelHandler handler = new IChannelHandler() {
			
			private long expectedSeqnum = 1;
			
			private List<AbstractMessage> sentMessages = new ArrayList<AbstractMessage>();
			
			@Override
			public void onSend(AbstractMessage message, long seqnum) {
				sentMessages.add(message);
			}
			
			@Override
			public AbstractMessage onReceive(AbstractMessage message, long seqnum) {
				assertEquals(expectedSeqnum, seqnum);
				expectedSeqnum++;
				return null;
			}
			
			@Override
			public void onIdle() {
				
			}
			
			@Override
			public void onException(Throwable t) {
				fail(t.getMessage());
			}
			
			@Override
			public void onCreate(AbstractChannel channel) {
				assertEquals(1, expectedSeqnum);
			}
			
			@Override
			public void onClose() {
				assertEquals(5, expectedSeqnum);
				assertEquals(0, sentMessages.size());
			}
			
		};
		
		WebSocketChannel channel = new WebSocketChannel(handler, "test1", settings, messageFactory, executor);
		
		channel.onCreate();
		
		WithSeqnumWrapper msg1 = new WithSeqnumWrapper(1, new TestBusinessMessage());
		channel.processInputMessage(msg1);
		
		WithSeqnumWrapper msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(msg2);
		
		WithSeqnumWrapper msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(msg3);
		
		WithSeqnumWrapper r_msg2 = new WithSeqnumWrapper(2, new TestBusinessMessage());
		channel.processInputMessage(r_msg2);
		
		WithSeqnumWrapper r_msg3 = new WithSeqnumWrapper(3, new TestBusinessMessage());
		channel.processInputMessage(r_msg3);
		
		WithSeqnumWrapper msg4 = new WithSeqnumWrapper(4, new TestBusinessMessage());
		channel.processInputMessage(msg4);
		
		channel.onClose();
		
	}
	
}
