package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.Constants;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupRemoveBonus extends InteractionPage {
	
	public GroupRemoveBonus() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError, PageFailure {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addInternalChoice( variable.define( "yes" ), "yes" );
		choices.addCancel( variable.define( "cancel" ) );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String choice ) throws GeneralInteractionError,
			ContextDataRequired {
		if ( choice.contentEquals( "yes" ) ) {
			String bonusName = interaction.getContextData( String.class, interaction, Constants.SELECTED_GROUP_SKILL );
			SkillType bonusSkill = SkillType.valueOf( bonusName.toUpperCase() );
			if ( bonusSkill != null ) {
				GroupHelper helper = new GroupHelper( interaction );
				try {
					GroupAPI.removeBonus( helper.record, bonusSkill );
					return "Bonus for " + bonusName.toLowerCase() + " has been removed";
				} catch ( PluginNotEnabled error ) {
					throw new GeneralInteractionError( "Plugin not enabled" );
				}
			} else
				throw new GeneralInteractionError( "Unable to determine bonus based on selection " + bonusName );
		} else
			return "Cancelled. Bonuses are unchanged.";
	}
	
}
