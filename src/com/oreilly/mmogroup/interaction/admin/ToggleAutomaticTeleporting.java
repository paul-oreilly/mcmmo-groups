package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralInteractionError;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.MMOGroup;


public class ToggleAutomaticTeleporting extends InteractionPage {
	
	public ToggleAutomaticTeleporting() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		HashMap< String, Object > variables = new HashMap< String, Object >();
		variables.put( "currentState", MMOGroup.instance.config.teleportOnJoin ? "enabled" : "disabled" );
		return variables;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		if ( MMOGroup.instance.config.teleportOnJoin )
			choices.addInternalChoice( "disable", "disable" );
		else
			choices.addInternalChoice( "enable", "endable" );
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String decision ) {
		if ( decision.contentEquals( "enable" ) ) {
			MMOGroup.instance.config.teleportOnJoin = true;
			return "Automatic teleport on joining is now enabled";
		}
		if ( decision.contains( "disable" ) ) {
			MMOGroup.instance.config.teleportOnJoin = false;
			return "Automatic teleport on joinging is now disabled";
		}
		return null;
		
	}
	
}
