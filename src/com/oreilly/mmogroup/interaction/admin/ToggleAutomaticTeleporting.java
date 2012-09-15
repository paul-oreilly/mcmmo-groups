package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;


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
			GeneralInteractionError, PageFailure {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		if ( MMOGroup.instance.config.teleportOnJoin )
			choices.addInternalChoice( variable.define( "disable" ), "disable" );
		else
			choices.addInternalChoice( variable.define( "enable" ), "endable" );
		choices.addCancel( variable.define( "cancel" ) );
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
