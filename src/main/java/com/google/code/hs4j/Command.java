package com.google.code.hs4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.network.hs.HandlerSocketSession;

public interface Command {
	public void setExceptionMessage(String t);

	public String getExceptionMessage();

	public Object getResult();

	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException;

	public void countDown();

	public IoBuffer getIoBuffer();

	public boolean decode(HandlerSocketSession session, IoBuffer buffer);

	public void encode();

	public Future<Boolean> getWriteFuture();

	public void setWriteFuture(Future<Boolean> future);

	public int getResponseStatus();

	public int getNumColumns();
}
