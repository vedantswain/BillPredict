package in.ac.iiitd.zenatix.billpredict.Database;

/**
 * Created by vedantdasswain on 10/03/15.
 */
public class DbEntryObject {
    private float entryReading;
    private String entryDate;

    public DbEntryObject(float reading,String date){
        this.entryDate=date;
        this.entryReading=reading;
    }

    public String getDate(){
        return entryDate;
    }
    public Float getReading(){
        return entryReading;
    }

    public void putDate(String entryDate){
        this.entryDate=entryDate;
    }
    public void putReading(Float entryReading){
        this.entryReading=entryReading;
    }
}
