package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupModifySpecialSkill extends InteractionPage {
	
	public GroupModifySpecialSkill() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		HashMap< String, Object > variables = helper.getVariables();
		if ( helper.selectedSkill != null ) {
			double currentBonusFactor = helper.record.getSkillBonus( helper.selectedSkill );
			variables.put( "currentBonusFactor", Numbers.doubleAsPercentage( currentBonusFactor, 2 ) );
		}
		return variables;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError, PageFailure {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		for ( SkillType skill : SkillType.values() ) {
			if ( skill == SkillType.ALL )
				continue;
			choices.addInternalChoice( WordUtils.capitalizeFully( skill.toString() ), skill.toString() );
		}
		// add a 'cancel' choice as well
		choices.addCancel( variable.define( "cancel" ) );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String selectedSkillName ) throws GeneralInteractionError {
		SkillType selected = SkillType.valueOf( selectedSkillName );
		if ( selected == null )
			throw new GeneralInteractionError( "Unable to determine a skill type based on \"" + selectedSkillName +
					"\"" );
		GroupHelper helper = new GroupHelper( interaction );
		try {
			GroupAPI.changeSpecialitySkill( helper.record, helper.selectedSpecialityName, selected );
			return helper.selectedSpecialityName + " now provides a bonus to " + selectedSkillName;
		} catch ( PluginNotEnabled error ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
	
}
