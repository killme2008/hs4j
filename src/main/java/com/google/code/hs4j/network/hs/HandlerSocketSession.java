package com.google.code.hs4j.network.hs;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.network.core.Session;

/**
 * Represent a connection to handlersocket
 * 
 * @author dennis
 * @date 2010-11-29
 */
public interface HandlerSocketSession extends Session {

	public abstract void destroy();

	/**
	 * is allow auto recconect if closed?
	 * 
	 * @return
	 */
	public abstract boolean isAllowReconnect();

	public abstract void setAllowReconnect(boolean reconnected);

	public abstract void addCommand(Command command);

	public abstract void setCurrentCommand(Command cmd);

	public abstract Command getCurrentCommand();

	public abstract void takeCurrentCommand();

}