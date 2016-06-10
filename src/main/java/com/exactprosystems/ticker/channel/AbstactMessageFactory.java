package com.exactprosystems.ticker.channel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.exactprosystems.ticker.exceptions.DecodingException;
import com.exactprosystems.ticker.exceptions.EncodingException;
import com.exactprosystems.ticker.messages.AbstractMessage;

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
