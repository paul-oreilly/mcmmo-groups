package com.oreilly.mmogroup.interaction.players;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;

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
	public Choices generateChoices( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		PlayerHelper helper = new PlayerHelper( interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		for ( String name : helper.getEligableSpecialityOptions() )
			choices.addInternalChoice( WordUtils.capitalizeFully( name ), name );
		choices.addCancel( variable.define( "cancel" ) );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String choice ) {
		PlayerHelper helper = new PlayerHelper( interaction );
		try {
			PlayerAPI.setSpecialisation( helper.playerRecord, choice );
			return "Your speciality is now \"" + choice + "\"";
		} catch ( PluginNotEnabled e ) {
			e.printStackTrace();
		}
		return null; //TODO: Later, return variable in a context that includes choice..
	}
}
