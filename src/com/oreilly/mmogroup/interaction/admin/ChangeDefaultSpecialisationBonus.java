package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.validator.DoubleValidator;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;


public class ChangeDefaultSpecialisationBonus extends InteractionPage {
	
	public ChangeDefaultSpecialisationBonus() {
		autoPage();
		withValidator( new DoubleValidator() );
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		HashMap< String, Object > variables = new HashMap< String, Object >();
		variables.put( "currentSpecialBonus",
				Numbers.doubleAsPercentage( MMOGroup.instance.config.specialisationBonus ) );
		return variables;
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralInteractionError {
		if ( Double.class.isAssignableFrom( data.getClass() ) ) {
			MMOGroup.instance.config.specialisationBonus = (Double)data;
			return "The new bonus is " + Numbers.doubleAsPercentage( (Double)data, 2 );
		} else {
			interaction.pageWaitingForInput = true;
			throw new GeneralInteractionError( "Unable to get a number from " + data );
		}
	}
	
}
