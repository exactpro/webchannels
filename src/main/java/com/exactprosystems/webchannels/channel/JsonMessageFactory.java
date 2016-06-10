package com.exactprosystems.webchannels.channel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.exactprosystems.webchannels.exceptions.DecodingException;
import com.exactprosystems.webchannels.exceptions.EncodingException;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

public class JsonMessageFactory extends AbstactMessageFactory {
	
	private static final TypeReference<List<WithSeqnumWrapper>> type = new TypeReference<List<WithSeqnumWrapper>>() {};
	
	private final List<Class<?>> classes;
	
	private final JsonFactory factory;

	private final ObjectMapper mapper;
	
	public JsonMessageFactory(MessageFactoryConfigurator configurator) {
		this.classes = new ArrayList<Class<?>>(configurator.getClasses());
		this.factory = new JsonFactory();
		this.factory.disable(Feature.AUTO_CLOSE_TARGET); 
		this.mapper = new ObjectMapper(factory);
		for (Class<?> clazz : classes) {
			JsonTypeName customName = clazz.getAnnotation(JsonTypeName.class);
			String className = clazz.getSimpleName();
			if (customName != null) {
				className = customName.value();
			}
			mapper.registerSubtypes(new NamedType(clazz, className));
		}
	}
	
	@Override
	public List<WithSeqnumWrapper> decodeMessage(InputStream stream) throws DecodingException {
		
		try {
			return mapper.readValue(stream, type);
		} catch (Exception e) {
			throw new DecodingException("Cannot decode json message", e);
		}
		
	}

	@Override
	public OutputStream encodeMessage(List<WithSeqnumWrapper> messages, OutputStream stream) throws EncodingException {
		
		try {
			for (WithSeqnumWrapper wrapper : messages) {
				if (classes.contains(wrapper.getMessage().getClass()) == false) {
					throw new RuntimeException(wrapper.getClass() + " not registered");
				}
			}
			mapper.writerWithType(type).writeValue(stream, messages);
		} catch (Exception e) {
			throw new EncodingException("Cannot encode json message", e);
		}
		
		return stream;
		
	}

	@Override
	public Writer encodeMessage(List<WithSeqnumWrapper> messages, Writer writer) throws EncodingException {
		
		try { 
			for (WithSeqnumWrapper wrapper : messages) {
				if (classes.contains(wrapper.getMessage().getClass()) == false) {
					throw new RuntimeException(wrapper.getClass() + " not registered");
				}
			}
			mapper.writerWithType(type).writeValue(writer, messages);
		} catch (Exception e) {
			throw new EncodingException("Cannot encode json message", e);
		}
		
		return writer;
	}

	@Override
	public List<WithSeqnumWrapper> decodeMessage(Reader reader) throws DecodingException {
		try {			
			return mapper.readValue(reader, type);
		} catch (Exception e) {
			throw new DecodingException("Cannot decode json message", e);
		}
	}

	@Override
	public String getContentType() {
		return "application/json";
	}
	
}
