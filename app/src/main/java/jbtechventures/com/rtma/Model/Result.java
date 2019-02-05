package jbtechventures.com.rtma.Model;

import java.util.ArrayList;

public class Result {
    public int Id;
    public int ElectionId;
    public String Unit;
    public int RegVotes;
    public int AccredVotes;
    public int CastVoted;
    public int InvalidVoted;
    public ArrayList<Vote> Votes;
    public String CreateDate;
    public String UpdateDate;
    public int Synced;
    public int Completed;
    public byte[] ProofImage;
    public String ProofImagePath;
    public String SyncErrorText;
    public int UserId;
}
