package com.oreilly.mmogroup.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerJoinGroupEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	public String playerName;
	public String groupName;
	public boolean allow = true;
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	
	public PlayerJoinGroupEvent( String playerName, String groupName ) {
		this.playerName = playerName;
		this.groupName = groupName;
	}
	
}
