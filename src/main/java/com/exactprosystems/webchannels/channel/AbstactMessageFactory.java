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
import com.exactprosystems.webchannels.messages.AbstractMessage;

/**
 * 
 * Abstract class for creating concrete instances of {@link AbstractMessage}
 * 
 * @author dmitry.zavodchikov
 *
 */
public abstract class AbstactMessageFactory {

	/**
	 * Convert binary stream to object message
	 * @param fieldsMap map of fields -> value
	 * @return {@link AbstractMessage} object with suitable fields
	 * @throws EncodingException unknown message exception
	 */
	public abstract List<WithSeqnumWrapper> decodeMessage(InputStream stream) throws DecodingException;

	/**
	 * Convert character stream to object message
	 * @param fieldsMap map of fields -> value
	 * @return {@link AbstractMessage} object with suitable fields
	 * @throws EncodingException unknown message exception
	 */
	public abstract List<WithSeqnumWrapper> decodeMessage(Reader reader) throws DecodingException;
	
	/**
	 * 
	 * @param message
	 * @param stream
	 * @return
	 * @throws EncodingException
	 */
	public abstract OutputStream encodeMessage(List<WithSeqnumWrapper> message, OutputStream stream) throws EncodingException;

	/**
	 * 
	 * @param message
	 * @param writer
	 * @return
	 * @throws EncodingException
	 */
	public abstract Writer encodeMessage(List<WithSeqnumWrapper> message, Writer writer) throws EncodingException;
	
	/**
	 * Specify content type
	 * @return
	 */
	public abstract String getContentType();
	
}
