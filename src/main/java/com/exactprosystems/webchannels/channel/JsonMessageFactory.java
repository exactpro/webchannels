/*
 * *****************************************************************************
 *  Copyright 2009-2018 Exactpro (Exactpro Systems Limited)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package com.exactprosystems.webchannels.channel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.exactprosystems.webchannels.exceptions.DecodingException;
import com.exactprosystems.webchannels.exceptions.EncodingException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonMessageFactory extends AbstractMessageFactory {
	
	private static final TypeReference<List<WithSeqnumWrapper>> type = new TypeReference<List<WithSeqnumWrapper>>() {};
	
	private final JsonFactory factory;

	private final ObjectMapper mapper;
	
	public JsonMessageFactory() {
		this.factory = new JsonFactory();
		this.mapper = new ObjectMapper(factory);
		this.mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
		this.mapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
		//this.mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "messageType");
		this.mapper.findAndRegisterModules();
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
			mapper.writerWithType(type).writeValue(stream, messages);
		} catch (Exception e) {
			throw new EncodingException("Cannot encode json message", e);
		}
		
		return stream;
		
	}

	@Override
	public Writer encodeMessage(List<WithSeqnumWrapper> messages, Writer writer) throws EncodingException {
		
		try { 
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
