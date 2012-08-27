package com.oreilly.mmogroup.interaction.players;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.api.PlayerAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;


// TODO:
public class JoinGroup extends InteractionPage {
	
	public JoinGroup() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		PlayerHelper helper = new PlayerHelper( interaction );
		return helper.getVariables();
	}
	
	
	// TODO: Later, allow permissions to be set, so players may be limited in what they 
	// groups they can join.
	
	@Override
	public Choices generateChoices( Interaction interaction ) {
		Choices choices = new Choices( this, interaction );
		try {
			for ( String groupName : GroupAPI.getAllGroupNames() )
				choices.addInternalChoice( WordUtils.capitalize( groupName ), groupName );
		} catch ( PluginNotEnabled e ) {
			e.printStackTrace();
		}
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String choice ) {
		if ( choice.contentEquals( "cancel" ) )
			return null;
		else {
			PlayerHelper helper = new PlayerHelper( interaction );
			try {
				PlayerAPI.joinGroup( helper.playerRecord, choice );
				return "You are now a part of " + choice;
			} catch ( PluginNotEnabled e ) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
