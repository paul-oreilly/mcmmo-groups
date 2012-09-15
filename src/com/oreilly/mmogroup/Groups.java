package com.oreilly.mmogroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Groups {
	
	protected MMOGroup plugin = null;
	HashMap< String, GroupRecord > groups = new HashMap< String, GroupRecord >();
	
	
	public Groups( MMOGroup plugin ) {
		this.plugin = plugin;
	}
	
	
	public void onDisable() {
		plugin.io.saveAllGroups();
	}
	
	
	public void save( GroupRecord record ) {
		plugin.io.saveGroupRecord( record );
	}
	
	
	public void addGroup( GroupRecord group ) {
		groups.put( group.name, group );
	}
	
	
	public void removeGroup( GroupRecord group ) {
		groups.remove( group.name );
		plugin.io.saveAllGroups();
	}
	
	
	public GroupRecord getGroup( String groupName ) {
		return groups.get( groupName );
	}
	
	
	public List< GroupRecord > getAllGroups() {
		LinkedList< GroupRecord > result = new LinkedList< GroupRecord >();
		result.addAll( groups.values() );
		return result;
	}
	
	
	public Set< String > getAllGroupNames() {
		HashSet< String > result = new HashSet< String >();
		result.addAll( groups.keySet() );
		return result;
	}
	
	
	// internal functions, linkages with record data
	void _internal_GroupRecordUpdate( GroupRecord record ) {
		save( record );
	}
	
	
	void _internal_GroupRecordSpecialityNameUpdate( GroupRecord record, String oldName, String newName ) {
		save( record );
		plugin.players.specialityNameChange( record, oldName, newName );
	}
	
}
