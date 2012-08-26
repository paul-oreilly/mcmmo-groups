package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralInteractionError;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.api.PlayerAPI;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;


public class ChangePlayersGroup extends InteractionPage {
	
	public ChangePlayersGroup() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public String getCustomTranslationKey( Interaction interaction ) {
		PlayerHelper helper;
		try {
			helper = PlayerHelper.fromSelectedPlayer( interaction );
			if ( helper.playersGroup == null )
				return getTranslationKey() + "_nogroup";
		} catch ( ContextDataRequired e ) {
			e.printStackTrace();
		}
		return getTranslationKey();
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		PlayerHelper helper;
		try {
			helper = PlayerHelper.fromSelectedPlayer( interaction );
			return helper.getVariables();
		} catch ( ContextDataRequired e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		try {
			for ( String groupName : GroupAPI.getAllGroupNames() )
				choices.addInternalChoice( groupName, groupName );
		} catch ( PluginNotEnabled e ) {
			e.printStackTrace();
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String groupName ) throws GeneralInteractionError,
			ContextDataRequired {
		try {
			PlayerHelper helper = PlayerHelper.fromSelectedPlayer( interaction );
			if ( helper.playersGroup == null )
				PlayerAPI.joinGroup( helper.playerRecord, groupName );
			else
				PlayerAPI.changeGroup( helper.playerRecord, groupName );
			return helper.playerRecord.getName() + " is now assigned to " + groupName;
		} catch ( PluginNotEnabled error ) {
			error.printStackTrace();
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
	
}
