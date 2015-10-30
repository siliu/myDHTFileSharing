package dhtfs.utils;

import java.io.Serializable;


/**
 * @author siliu
 *
 */
 public class Message implements Serializable  {

	private Command cmd;
	private Object content;

	public Message(){
		
	}
	public Message(Command cmd, Object content) {
		super();
		this.cmd = cmd;
		this.content = content;
	}

	public Command getCmd() {
		return cmd;
	}

	public void setCmd(Command cmd) {
		this.cmd = cmd;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}
