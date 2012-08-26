package com.oreilly.mmogroup;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;


public class Events implements Listener {
	
	static boolean experienceAddInProgress = false;
	
	public static final boolean DEBUG = true;
	
	public MMOGroup plugin = null;
	
	
	public Events( MMOGroup plugin ) {
		this.plugin = plugin;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onExperienceGain( McMMOPlayerXpGainEvent event ) {
		if ( experienceAddInProgress ) {
			if ( DEBUG )
				plugin.log.finer( "Event: McMMOPlayerXpGainEvent: Skipped, as experience add in progress" );
			return;
		}
		if ( DEBUG )
			plugin.log.finer( "Event: McMMOPlayerXpGainEvent: XP gain is " + event.getXpGained() );
		// get the player record
		PlayerRecord player = plugin.players.getPlayer( event.getPlayer().getName() );
		// if the player doesn't have a group, they have no bonuses at all...
		GroupRecord group = plugin.groups.getGroup( player.groupName );
		if ( group == null )
			return;
		// if the player has a specialisation, that bonus is dominant
		Double bonus = null;
		SkillType eventSkill = event.getSkill();
		String specialisationName = player.getSpecialisation();
		if ( specialisationName != null ) {
			SkillType specialisation = group.getSpecialitySkill( specialisationName );
			if ( specialisation != null )
				if ( specialisation.equals( eventSkill ) )
					bonus = group.getSpecialitySkillFactor( specialisationName );
		}
		// if we havn't gotten a bonus yet, see if the group has one
		if ( bonus == null )
			bonus = group.getSkillBonus( event.getSkill() );
		// still nothing? Then no action to take.
		if ( bonus == null ) {
			plugin.log.finer( "McMMOPlayerXpGainEvent: No bonus to apply" );
			return;
		}
		// if we have a bonus, reward extra XP
		else {
			// account for the experience already added by this event..
			bonus = bonus - 1;
			if ( DEBUG )
				plugin.log.finer( "McMMOPlayerXpGainEvent: Bonus in effect (" + bonus + "), adding " +
						( (int)( bonus * event.getXpGained() ) ) + " extra experience " );
			experienceAddInProgress = true;
			ExperienceAPI.addMultipliedXP( event.getPlayer(), event.getSkill(), (int)( bonus * event.getXpGained() ) );
			experienceAddInProgress = false;
		}
	}
	
}
