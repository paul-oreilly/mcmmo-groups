package com.oreilly.mmogroup.interaction;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.admin.CreateGroup;
import com.oreilly.mmogroup.interaction.admin.DeleteGroup;
import com.oreilly.mmogroup.interaction.admin.ModifyGroup;


// TODO:
public class AdminMenu extends InteractionPage {
	
	public AdminMenu() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		interaction.addBookmark( this );
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addPageChoice( variable.define( "create_new_group" ), new CreateGroup(), new ModifyGroup() );
		choices.addPageChoice( variable.define( "modify_group" ), new SelectGroup(), new ModifyGroup() );
		choices.addPageChoice( variable.define( "delete_group" ), new SelectGroup(), new DeleteGroup() );
		choices.addPageChoice( variable.define( "change_settings" ), new SettingsMenu() );
		choices.addPageChoice( variable.define( "modify_player" ), new SelectPlayer(), new AdminPlayerMenu() );
		choices.addAbortChoice( variable.define( "cancel" ) );
		return choices;
	}
}
