package com.oreilly.mmogroup.interaction;

import java.util.HashMap;

import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
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
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addPageChoice( variable.define( "toggleAutoTeleport" ), new ToggleAutomaticTeleporting() );
		choices.addPageChoice( variable.define( "changeSpecialBonus" ), new ChangeDefaultSpecialisationBonus() );
		// TODO: Add other config options
		return choices;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		HashMap< String, Object > variables = new HashMap< String, Object >();
		variables.put( "autoTeleportStatus", ( MMOGroup.instance.config.teleportOnJoin ) ? "enabled" : "disabled" );
		variables.put( "specialBonus", Numbers.doubleAsPercentage( MMOGroup.instance.config.specialisationBonus, 1 ) );
		return variables;
	}
}
