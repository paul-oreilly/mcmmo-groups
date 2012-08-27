package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupSetTeleportLocation extends InteractionPage {
	
	public GroupSetTeleportLocation() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		choices.addInternalChoice( "Set to my current location", "current" );
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String input ) throws GeneralInteractionError {
		if ( input.equalsIgnoreCase( "cancel" ) )
			return null;
		GroupHelper helper = new GroupHelper( interaction );
		try {
			if ( interaction.user instanceof Player ) {
				Player player = (Player)( interaction.user );
				
				GroupAPI.setTeleportLocation( helper.record, player.getLocation() );
				return "Teleport location set";
			} else
				return "Unable to cast to player, therefore cannot get location";
		} catch ( PluginNotEnabled e ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
}
