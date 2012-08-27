package com.oreilly.mmogroup.bukkitTools.interaction.text.helpers;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;


public interface ChoiceImplementer {
	
	public String takeAction( Interaction interaction, String key ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError;
}
