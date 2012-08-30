package com.oreilly.mmogroup.api;

import org.bukkit.entity.Player;

import com.oreilly.mmogroup.GroupRecord;
import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.PlayerRecord;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.events.PlayerJoinGroupEvent;


public class PlayerAPI {
	
	public static boolean DEBUG = true;
	
	
	static public void joinGroup( Player player, String groupName ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		PlayerRecord record = MMOGroup.instance.players.getPlayer( player.getName() );
		joinGroup( record, groupName );
	}
	
	
	static public void joinGroup( PlayerRecord player, GroupRecord group ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		PlayerRecord record = MMOGroup.instance.players.getPlayer( player.getName() );
		joinGroup( record, group.getName() );
	}
	
	
	static public void joinGroup( PlayerRecord player, String groupName ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: joinGroup for " + player.getName() + " to " + groupName );
		PlayerJoinGroupEvent event = new PlayerJoinGroupEvent( player.getName(), groupName );
		MMOGroup.instance.getServer().getPluginManager().callEvent( event );
		if ( event.allow ) {
			player.setGroup( groupName );
		}
	}
	
	
	static public void leaveGroup( Player player ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: leaveGroup for " + player.getName() );
		PlayerRecord record = MMOGroup.instance.players.getPlayer( player.getName() );
		record.setGroup( null );
	}
	
	
	static public void leaveGroup( PlayerRecord record ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: leaveGroup for " + record.getName() );
		record.setGroup( null );
	}
	
	
	static public void changeGroup( Player player, String groupName ) throws PluginNotEnabled {
		leaveGroup( player );
		joinGroup( player, groupName );
	}
	
	
	static public void changeGroup( PlayerRecord playerRecord, GroupRecord groupRecord ) throws PluginNotEnabled {
		leaveGroup( playerRecord );
		joinGroup( playerRecord, groupRecord );
	}
	
	
	static public void changeGroup( PlayerRecord playerRecord, String groupName ) throws PluginNotEnabled {
		leaveGroup( playerRecord );
		joinGroup( playerRecord, groupName );
	}
	
	
	static public String getPlayersGroup( Player player ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getPlayersGroup for " + player.getName() );
		PlayerRecord record = MMOGroup.instance.players.getPlayer( player.getName() );
		return record.getGroupName();
	}
	
	
	static public void setSpecialisation( Player player, String speciality ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		PlayerRecord record = MMOGroup.instance.players.getPlayer( player.getName() );
		setSpecialisation( record, speciality );
	}
	
	
	static public void setSpecialisation( PlayerRecord player, String speciality ) throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: setSpecialisation for " + player.getName() + " to " +
					speciality );
		player.setSpecialisation( speciality );
	}
}
