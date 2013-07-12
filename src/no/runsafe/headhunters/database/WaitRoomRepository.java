package no.runsafe.headhunters.database;

import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.IRow;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.internal.database.Set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WaitRoomRepository extends Repository
{

    public WaitRoomRepository(IDatabase database, IOutput console)
    {
        this.database = database;
        this.console = console;

    }

    @Override
    public String getTableName()
    {
        return "headhunters_waitroom";
    }

    @Override
    public HashMap<Integer, List<String>> getSchemaUpdateQueries()
    {
        console.fine("WaitingRoomRepository - schema updates");
        HashMap<Integer, List<String>> exec = new HashMap<Integer, List<String>>();
        ArrayList<String> queries = new ArrayList<String>();
        String query = "CREATE TABLE IF NOT EXISTS `headhunters_waitroom` (\n" +
                "  `AREANAME` varchar(64) NOT NULL\n" +
                ")";
        queries.add(query);
        exec.put(1, queries);
        return exec;
    }

    public String getWaitRoom()
    {
        String query = "SELECT * FROM headhunters_waitroom";
        IRow row = database.QueryRow(query);
        if(row == null || row == Set.Empty) return null;
        else return row.String("AREANAME");
    }

    private void delWaitRoom()
    {
        String query = "TRUNCATE TABLE headhunters_waitroom";
        database.Execute(query);
    }

    public void setWaitRoom(String name)
    {
        String query = String.format("INSERT INTO headhunters_waitroom (`AREANAME`) VALUES ('%s');", name);
        delWaitRoom();
        database.Update(query);
    }


    private final IDatabase database;
    private final IOutput console;

}
