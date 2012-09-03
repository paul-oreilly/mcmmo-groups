package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import com.oreilly.mmogroup.api.PlayerAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;


public class ChangePlayersSpecialisation extends InteractionPage {
	
	public ChangePlayersSpecialisation() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public String getCustomTranslationKey( Interaction interaction ) {
		// TODO: Add option for fail "insufficent power"
		try {
			PlayerHelper helper = PlayerHelper.fromSelectedPlayer( interaction );
			if ( helper.playersGroup == null )
				return this.getClass().getSimpleName() + ".fail.nogroup";
		} catch ( ContextDataRequired error ) {
			// TODO: Later, have an abort even thrown (and support for catching it!)
		}
		return this.getClass().getSimpleName() + ".normal";
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		PlayerHelper helper;
		try {
			helper = PlayerHelper.fromSelectedPlayer( interaction );
			return helper.getVariables();
		} catch ( ContextDataRequired e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		PlayerHelper helper = PlayerHelper.fromSelectedPlayer( interaction );
		for ( String name : helper.getEligableSpecialityOptions() )
			choices.addInternalChoice( name, name )
					.withAlias( name.toLowerCase() );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String specialityName ) throws GeneralInteractionError,
			ContextDataRequired {
		PlayerHelper helper = PlayerHelper.fromSelectedPlayer( interaction );
		try {
			PlayerAPI.setSpecialisation( helper.playerRecord, specialityName );
			return helper.playerRecord.getName() + " now has the speciality " + specialityName;
		} catch ( PluginNotEnabled e ) {
			e.printStackTrace();
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
	
}
