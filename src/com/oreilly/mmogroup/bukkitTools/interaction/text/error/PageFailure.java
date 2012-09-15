package com.oreilly.mmogroup.bukkitTools.interaction.text.error;

public class PageFailure extends Exception {
	
	private static final long serialVersionUID = -1144299562324757153L;
	public String reason = null;
	
	
	public PageFailure( String reason ) {
		this.reason = reason;
	}
	
}
