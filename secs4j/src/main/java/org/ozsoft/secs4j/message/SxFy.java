package org.ozsoft.secs4j.message;

import org.ozsoft.secs4j.SecsMessage;
import org.ozsoft.secs4j.SecsParseException;
import org.ozsoft.secs4j.format.Data;

/**
 * 
 * @author nguyenchitam
 *
 */
public class SxFy extends SecsMessage {

	private int stream;
	private int function;
	private boolean replyBit;
	private Data<?> body;

	public SxFy(int stream, int function) {
		this.stream = stream;
		this.function = function;
	}

	@Override
	public int getStream() {
		return stream;
	}

	@Override
	public int getFunction() {
		return function;
	}

	public void setReplyBit(boolean replyBit) {
		this.replyBit = replyBit;
	}

	@Override
	public boolean withReply() {
		return replyBit;
	}

	@Override
	public String getDescripton() {
		return "SxFy";
	}

	@Override
	public void parseData(Data<?> data) throws SecsParseException {
		body = data;
	}

	@Override
	public Data<?> getData() throws SecsParseException {
		return body;
	}

	@Override
	public String toString() {
		return String.format("S%dF%d%s\n%s.", stream, function, replyBit ? " W" : "", (body != null ? body.toSml() : ""));
	}
}
