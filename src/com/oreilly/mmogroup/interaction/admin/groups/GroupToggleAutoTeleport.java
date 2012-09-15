package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupToggleAutoTeleport extends InteractionPage {
	
	public GroupToggleAutoTeleport() {
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
			GeneralInteractionError, PageFailure {
		Choices choices = new Choices( this, interaction );
		GroupHelper helper = new GroupHelper( interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		if ( helper.record.getAutoTeleport() ) {
			choices.addInternalChoice( variable.define( "keep_enabled" ), "cancel" );
			choices.addInternalChoice( variable.define( "disable" ), "disable" );
		} else {
			choices.addInternalChoice( variable.define( "enable" ), "enable" );
			choices.addInternalChoice( variable.define( "keep_disabled" ), "cancel" );
		}
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String input ) throws GeneralInteractionError {
		input = input.trim();
		if ( input.equalsIgnoreCase( "cancel" ) )
			return null;
		try {
			GroupHelper helper = new GroupHelper( interaction );
			if ( input.equalsIgnoreCase( "enable" ) ) {
				GroupAPI.setAutoTeleport( helper.record, true );
				return "Auto teleporting for " + helper.record.getName() + " is now enabled";
			}
			if ( input.equalsIgnoreCase( "disable" ) ) {
				GroupAPI.setAutoTeleport( helper.record, false );
				return "Auto teleporting for " + helper.record.getName() + " is now disabled";
			}
			return null;
		} catch ( PluginNotEnabled error ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
	
}
