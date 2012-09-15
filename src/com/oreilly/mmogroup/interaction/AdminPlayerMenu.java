package com.oreilly.mmogroup.interaction;

import java.util.HashMap;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.admin.ChangePlayersGroup;
import com.oreilly.mmogroup.interaction.admin.ChangePlayersSpecialisation;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;


public class AdminPlayerMenu extends InteractionPage {
	
	public AdminPlayerMenu() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		PlayerHelper helper = new PlayerHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addPageChoice( variable.define( "change_player_group" ), new ChangePlayersGroup() );
		choices.addPageChoice( variable.define( "change_player_special" ), new ChangePlayersSpecialisation() );
		choices.addCancel( variable.define( "cancel" ) );
		return choices;
	}
	
}
