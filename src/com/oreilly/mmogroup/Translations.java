package com.oreilly.mmogroup;

import java.util.HashMap;
import java.util.logging.Level;

import com.oreilly.mmogroup.bukkitTools.text.Translater;


public class Translations {
	
	public MMOGroup plugin;
	public Translater translater = null;
	
	
	public Translations( MMOGroup plugin ) {
		this.plugin = plugin;
		translater = new Translater( plugin.config.translationDirectory, plugin.log );
		// DEBUG:
		HashMap< String, String > defaultTranslations = getTranslations( "default" );
		if ( defaultTranslations == null )
			MMOGroup.instance.log.log( Level.SEVERE, "No default translation found" );
		else {
			System.out.println( "** DEFAULT TRANSLATION DATA **" );
			for ( String key : defaultTranslations.keySet() )
				System.out.println( key + ": " + defaultTranslations.get( key ) );
		}
	}
	
	
	public HashMap< String, String > getTranslations() {
		return getTranslations( plugin.config.defaultTranslation );
	}
	
	
	public HashMap< String, String > getTranslations( String name ) {
		return translater.getTranslation( name );
	}
	
}
