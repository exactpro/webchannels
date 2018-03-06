package com.exactprosystems.webchannels.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.exactprosystems.webchannels.messages.AbstractMessage;

@Deprecated
public class MessageFactoryConfigurator {

	private final List<Class<?>> classes;
	
	public MessageFactoryConfigurator() {
		this.classes = new ArrayList<Class<?>>();
	}

	public void registerMessage(Class<? extends AbstractMessage> clazz) {
		classes.add(clazz);
	}

	public List<Class<?>> getClasses() {
		return classes;
	}
	
}
