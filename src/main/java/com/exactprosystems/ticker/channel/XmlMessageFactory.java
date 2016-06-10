package com.exactprosystems.ticker.channel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.exactprosystems.ticker.exceptions.DecodingException;
import com.exactprosystems.ticker.exceptions.EncodingException;

public class XmlMessageFactory extends AbstactMessageFactory {

	@Override
	public List<WithSeqnumWrapper> decodeMessage(InputStream stream) throws DecodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream encodeMessage(List<WithSeqnumWrapper> message, OutputStream stream) throws EncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public Writer encodeMessage(List<WithSeqnumWrapper> message, Writer writer) throws EncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WithSeqnumWrapper> decodeMessage(Reader reader) throws DecodingException {
		// TODO Auto-generated method stub
		return null;
	}

}
