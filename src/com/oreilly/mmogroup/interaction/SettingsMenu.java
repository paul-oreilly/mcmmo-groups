package com.oreilly.mmogroup.interaction;

import java.util.HashMap;

import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.admin.ChangeDefaultSpecialisationBonus;
import com.oreilly.mmogroup.interaction.admin.ToggleAutomaticTeleporting;


public class SettingsMenu extends InteractionPage {
	
	public SettingsMenu() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, PageFailure,
			ContextDataRequired, GeneralInteractionError {
		interaction.addBookmark( this );
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addPageChoice( variable.define( "toggle_auto_teleport" ), new ToggleAutomaticTeleporting() );
		choices.addPageChoice( variable.define( "change_special_bonus" ), new ChangeDefaultSpecialisationBonus() );
		// TODO: Add other config options
		choices.addFail( variable.define( "cancel" ) );
		return choices;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		HashMap< String, Object > variables = new HashMap< String, Object >();
		variables.put( "auto_teleport_status", ( MMOGroup.instance.config.teleportOnJoin ) ? "enabled" : "disabled" );
		variables.put( "special_bonus", Numbers.doubleAsPercentage( MMOGroup.instance.config.specialisationBonus, 1 ) );
		return variables;
	}
}
