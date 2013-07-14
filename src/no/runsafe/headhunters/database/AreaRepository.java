package no.runsafe.headhunters.database;

import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.IRow;
import no.runsafe.framework.api.database.ISet;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.internal.database.Set;
import no.runsafe.headhunters.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AreaRepository extends Repository
{

	public AreaRepository(IDatabase database, IOutput console)
	{
		this.database = database;
		this.console = console;
	}

	@Override
	public String getTableName()
	{
		return "headhunters_areas";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		console.fine("AreaRepository - schema updates");
		HashMap<Integer, List<String>> exec = new HashMap<Integer, List<String>>();
		String query = "CREATE TABLE IF NOT EXISTS `headhunters_areas` (\n" +
			"  `ID` int(8) NOT NULL AUTO_INCREMENT,\n" +
			"  `AREANAME` varchar(64) NOT NULL,\n" +
			"  PRIMARY KEY (`ID`),\n" +
			"  UNIQUE KEY `AREANAME` (`AREANAME`)\n" +
			")";

		List<String> queries = new ArrayList<String>();
		queries.add(query);
		exec.put(1, queries);
		return exec;
	}

	public ArrayList<String> getAreas()
	{
		String query = "SELECT * FROM headhunters_areas";
		ISet set = database.Query(query);

		ArrayList<String> areas = new ArrayList<String>();

		if (set != null && set != Set.Empty)
			for (IRow row : set)
				areas.add(row.String("AREANAME"));

		return areas;
	}

	public boolean addArea(String name)
	{
		if (areaExists(name))
			return false;

		String query = String.format("INSERT INTO headhunters_areas (`ID`, `AREANAME`) VALUES (NULL, '%s');", name);
		console.fine(String.format("Adding %s to the areas", name));
		int id = database.Update(query);
		console.fine(String.format("Inserted Id: %d", id));
		return true;
	}

	public boolean delArea(String name)
	{
		if (!areaExists(name))
			return false;

		String query = String.format("DELETE FROM `headhunters_areas` WHERE `AREANAME` = '%s';", name);
		console.fine(String.format("deleting %s from the areas", name));
		int id = database.Update(query);
		console.fine(String.format("Removed Id: %d", id));
		return true;
	}

	public boolean areaExists(String name)
	{
		return Util.arrayListContainsIgnoreCase(getAreas(), name);
	}

	private final IDatabase database;
	private final IOutput console;
}
