package com.oreilly.mmogroup.interaction.players;

import java.util.HashMap;

import com.oreilly.mmogroup.api.PlayerAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;


// confirmation dialog
public class LeaveGroup extends InteractionPage {
	
	public LeaveGroup() {
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
		choices.addInternalChoice( variable.define( "confirm" ), "yes" );
		choices.addCancel( variable.define( "cancel" ) );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String choice ) {
		if ( choice.contentEquals( "yes" ) ) {
			PlayerHelper helper = new PlayerHelper( interaction );
			try {
				PlayerAPI.leaveGroup( helper.playerRecord );
				return "You are no longer a part of any group";
			} catch ( PluginNotEnabled e ) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
