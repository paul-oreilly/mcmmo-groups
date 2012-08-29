package com.oreilly.mmogroup.interaction.helpers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.oreilly.mmogroup.GroupRecord;
import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.PlayerRecord;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.interaction.Constants;


public class PlayerHelper {
	
	static public PlayerHelper fromSelectedPlayer( Interaction interaction ) throws ContextDataRequired {
		String playerName = interaction.getContextData( String.class, interaction, Constants.SELECTED_PLAYER );
		if ( playerName != null )
			return new PlayerHelper( playerName );
		else
			return null;
	}
	
	public PlayerRecord playerRecord = null;
	public GroupRecord playersGroup = null;
	int powerLevel = 0;
	
	
	public PlayerHelper( Interaction interaction ) {
		init( interaction.user.getName() );
	}
	
	
	public PlayerHelper( Player player ) {
		init( player.getName() );
	}
	
	
	public PlayerHelper( PlayerRecord record ) {
		init( record );
	}
	
	
	public PlayerHelper( CommandSender sender ) {
		if ( sender instanceof Player )
			init( ( (Player)sender ).getName() );
	}
	
	
	public PlayerHelper( String playerName ) {
		init( playerName );
	}
	
	
	private void init( String playerName ) {
		init( MMOGroup.instance.players.getPlayer( playerName ) );
	}
	
	
	private void init( PlayerRecord record ) {
		if ( record != null ) {
			playerRecord = record;
			playersGroup = record.getGroup();
		}
		setPowerLevel();
	}
	
	
	private void setPowerLevel() {
		if ( MMOGroup.mcMMO != null ) {
			PlayerProfile profile = MMOGroup.mcMMO.getPlayerProfile( playerRecord.getName() );
			if ( profile != null )
				powerLevel = profile.getPowerLevel();
		}
	}
	
	
	public List< String > getEligableSpecialityOptions() {
		LinkedList< String > result = new LinkedList< String >();
		if ( playersGroup != null ) {
			for ( String specialityName : playersGroup.getSpecialityNames() ) {
				if ( powerLevel >= playersGroup.getSpecialityRequiredPowerLevel( specialityName ) )
					result.add( specialityName );
			}
		}
		return result;
	}
	
	
	public HashMap< String, Object > getVariables() {
		HashMap< String, Object > variables = new HashMap< String, Object >();
		variables.put( "playerName", playerRecord.getName() );
		String specialisation = playerRecord.getSpecialisation();
		variables.put( "playerSpecial", ( specialisation == null ) ?
				"none" : specialisation.toString() );
		variables.put( "playerGroup", ( playersGroup == null ) ?
				"none" : playersGroup.getName() );
		variables.put( "playerPowerLevel", powerLevel );
		return variables;
	}
	
}
