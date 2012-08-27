package com.oreilly.mmogroup.bukkitTools.interaction.text.formatter;

import org.apache.commons.lang.StringUtils;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;


public class ClearChat extends Formatter {
	
	public int blankLines = 20;
	
	
	public ClearChat() {
		this( 20 );
	}
	
	
	public ClearChat( int blankLines ) {
		this.blankLines = blankLines;
	}
	
	
	@Override
	protected String format( String s, InteractionPage page, Interaction interaction ) {
		return StringUtils.repeat( "\n", blankLines ) + s;
	}
	
}
