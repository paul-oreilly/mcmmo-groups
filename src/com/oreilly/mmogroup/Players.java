package com.oreilly.mmogroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


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
	
	
	public void save( List< PlayerRecord > records ) {
		plugin.io.savePlayerRecords( records );
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
	void _internal_PlayerRecordGroupUpdate( PlayerRecord record, String oldGroup ) {
		save( record );
	}
	
	
	void _internal_PlayerRecordSpecialisationUpdate( PlayerRecord record, String oldSpecialisation ) {
		save( record );
	}
	
	
	void specialityNameChange( GroupRecord groupRecord, String oldName, String newName ) {
		String groupName = groupRecord.getName();
		LinkedList< PlayerRecord > changedRecords = new LinkedList< PlayerRecord >();
		for ( PlayerRecord playerRecord : players.values() )
			if ( playerRecord.groupName.contentEquals( groupName ) )
				if ( playerRecord.specialisation.contentEquals( oldName ) ) {
					playerRecord.specialisation = newName;
					changedRecords.add( playerRecord );
				}
		save( changedRecords );
	}
	
}
