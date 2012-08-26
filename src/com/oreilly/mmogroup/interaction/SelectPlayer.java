package com.oreilly.mmogroup.interaction;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.helpers.Choice;
import com.oreilly.common.interaction.text.helpers.Choices;


public class SelectPlayer extends InteractionPage {
	
	public SelectPlayer() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction {
		Choices choices = new Choices( this, interaction );
		List< String > shortNames = new LinkedList< String >();
		String playerName;
		// see which players we could provide a short (3 letter) name for..
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			playerName = player.getName();
			String shortName = playerName.toLowerCase().substring( 0, 2 );
			if ( shortNames.contains( shortName ) )
				shortNames.remove( shortName );
			else
				shortNames.add( shortName );
		}
		// add active players to the front of the list..
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			playerName = player.getName();
			Choice choice = choices.addInternalChoice( WordUtils.capitalize( playerName ), playerName )
					.withAlias( WordUtils.capitalize( playerName ) )
					.withAlias( playerName.toLowerCase() );
			String shortName = playerName.toLowerCase().substring( 0, 2 );
			if ( shortNames.contains( shortName ) ) {
				choice.withAlias( shortName ).withAlias( WordUtils.capitalize( shortName ) );
			}
		}
		// add offline players at the end of the list
		for ( OfflinePlayer player : Bukkit.getOfflinePlayers() ) {
			playerName = player.getName();
			choices.addInternalChoice( WordUtils.capitalize( playerName ) + " (offline)", playerName )
					.withAlias( WordUtils.capitalize( playerName ) )
					.withAlias( playerName.toLowerCase() );
		}
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String selectedPlayer ) {
		interaction.context.put( Constants.SELECTED_PLAYER, selectedPlayer );
		return null;
	}
	
}
