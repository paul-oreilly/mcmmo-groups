package com.oreilly.mmogroup.interaction.players;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;

import com.oreilly.mmogroup.api.PlayerAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;


public class ChooseSpeciality extends InteractionPage {
	
	public ChooseSpeciality() {
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
		PlayerHelper helper = new PlayerHelper( interaction );
		for ( String name : helper.getEligableSpecialityOptions() )
			choices.addInternalChoice( WordUtils.capitalize( name ), name );
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
				PlayerAPI.setSpecialisation( helper.playerRecord, choice );
			} catch ( PluginNotEnabled e ) {
				e.printStackTrace();
			}
			return null; //TODO: Later, return variable in a context that includes choice..
		}
	}
	
}
