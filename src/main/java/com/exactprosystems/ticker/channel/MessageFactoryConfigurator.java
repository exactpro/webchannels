package com.exactprosystems.ticker.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.exactprosystems.ticker.messages.AbstractMessage;

public class MessageFactoryConfigurator {

	private final List<Class<?>> classes;
	
	public MessageFactoryConfigurator() {
		this.classes = new ArrayList<Class<?>>();
	}
	
	public void registerMessage(String packageName) {
		
		Reflections reflections = new Reflections(packageName);
		Set<Class<? extends AbstractMessage>> annotated = 
	               reflections.getSubTypesOf(AbstractMessage.class);
		
		for (Class<?> clazz : annotated) {
			classes.add(clazz);
		}
		
	}

	public void registerMessage(Class<? extends AbstractMessage> clazz) {
		classes.add(clazz);
	}

	public List<Class<?>> getClasses() {
		return classes;
	}
	
}
