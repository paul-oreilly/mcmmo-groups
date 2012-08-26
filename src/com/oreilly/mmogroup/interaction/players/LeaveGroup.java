package com.oreilly.mmogroup.interaction.players;

import java.util.HashMap;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.api.PlayerAPI;
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
	public Choices generateChoices( Interaction interaction ) {
		Choices choices = new Choices( this, interaction );
		choices.addInternalChoice( "Yes, I'm sure.", "yes" );
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String choice ) {
		if ( choice.contentEquals( "cancel" ) )
			return null;
		if ( choice.contentEquals( "yes" ) ) {
			PlayerHelper helper = new PlayerHelper( interaction );
			try {
				PlayerAPI.leaveGroup( helper.playerRecord );
				return null; //TODO: Later, a message.
			} catch ( PluginNotEnabled e ) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
