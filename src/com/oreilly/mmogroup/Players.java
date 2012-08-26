package com.oreilly.mmogroup;

import java.util.HashMap;


// TODO: Permission check - if players do not have the "join" permission, auto-remove them from any groups

public class Players {
	
	protected MMOGroup plugin = null;
	HashMap< String, PlayerRecord > players = new HashMap< String, PlayerRecord >();
	
	
	public Players( MMOGroup plugin ) {
		this.plugin = plugin;
	}
	
	
	public void onDisable() {
		plugin.io.saveAllPlayers();
	}
	
	
	public void save( PlayerRecord record ) {
		plugin.io.savePlayerRecord( record );
	}
	
	
	public void addPlayer( PlayerRecord player ) {
		players.put( player.name, player );
	}
	
	
	public PlayerRecord getPlayer( String playerName ) {
		PlayerRecord result = players.get( playerName );
		if ( result == null ) {
			result = new PlayerRecord( plugin, playerName );
			players.put( playerName, result );
		}
		return result;
	}
	
	
	// internal functions, linkages with record data
	public void _internal_PlayerRecordGroupUpdate( PlayerRecord record, String oldGroup ) {
		save( record );
	}
	
	
	public void _internal_PlayerRecordSpecialisationUpdate( PlayerRecord record, String oldSpecialisation ) {
		save( record );
	}
	
}
