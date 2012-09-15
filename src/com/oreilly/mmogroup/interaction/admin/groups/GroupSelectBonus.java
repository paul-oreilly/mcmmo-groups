package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.Constants;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupSelectBonus extends InteractionPage {
	
	public GroupSelectBonus() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError, PageFailure {
		Choices choices = new Choices( this, interaction );
		GroupHelper helper = new GroupHelper( interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		// list all the skills for which this group doesn't have an existing bonus
		Set< SkillType > existingBonuses = helper.record.getSkillBonuses();
		for ( SkillType skill : existingBonuses ) {
			if ( skill == SkillType.ALL )
				continue;
			choices.addInternalChoice( WordUtils.capitalizeFully( skill.toString() ), skill.toString() );
		}
		// add a 'cancel' choice as well
		choices.addCancel( variable.define( "cancel" ) );
		return choices;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String bonusName ) throws GeneralInteractionError {
		SkillType selected = SkillType.valueOf( bonusName );
		if ( selected == null )
			throw new GeneralInteractionError( "Unable to determine a skill type based on \"" + bonusName + "\"" );
		interaction.context.put( Constants.SELECTED_GROUP_SKILL, bonusName );
		return "Selected " + bonusName.toLowerCase();
	}
}
