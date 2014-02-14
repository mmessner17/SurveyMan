package system;

import com.amazonaws.mturk.requester.HIT;

import org.apache.log4j.Logger;
import qc.QC;
import survey.Survey;
import survey.SurveyResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import survey.SurveyException;
import system.interfaces.SurveyPoster;

/**
 * Record is the class used to hold instance information about a currently running survey.
 */
public class Record {

    final private static Logger LOGGER = Logger.getLogger(Record.class);
    final private static Gensym gensym = new Gensym("rec");

    final public String outputFileName;
    final public Survey survey;
    public Library library;
    final public QC qc;
    final public String rid = gensym.next();
    //public QualificationType qualificationType;
    public List<SurveyResponse> responses;
    public List<SurveyResponse> botResponses;
    private Deque<HIT> hits; // these should be hitids
    private String htmlFileName = "";
    public String hitTypeId = "";

    public Record(final Survey survey, Library someLib)
            throws IOException, SurveyException, IllegalAccessException, InstantiationException {
        (new File(Library.OUTDIR)).mkdir();
        (new File("logs")).mkdir();
        File outfile = new File(String.format("%s%s%s_%s_%s.csv"
                , Library.OUTDIR
                , Library.fileSep
                , survey.sourceName
                , survey.sid
                , Library.TIME));
        outfile.createNewFile();
        File htmlFileName = new File(String.format("%s%slogs%s%s_%s_%s.html"
                , (new File("")).getAbsolutePath()
                , Library.fileSep
                , Library.fileSep
                , survey.sourceName
                , survey.sid
                , Library.TIME));
        htmlFileName.createNewFile();
        this.outputFileName = outfile.getCanonicalPath();
        this.htmlFileName = htmlFileName.getCanonicalPath();
        this.survey = survey;
        this.library = someLib; //new MturkLibrary();
        this.qc = new QC(survey);
        this.responses = new Vector<SurveyResponse>();
        this.botResponses = new Vector<SurveyResponse>();
        this.hits = new ArrayDeque<HIT>();
        LOGGER.info(String.format("New record with id (%s) created for survey %s (%s)."
                , rid
                , survey.sourceName
                , survey.sid
        ));
    }

    public String getHtmlFileName() {
        return this.htmlFileName;
    }

    public void addNewHIT(HIT hit) {
        hits.push(hit);
    }

    public HIT getLastHIT(){
        return hits.peekFirst();
    }

    public HIT[] getAllHITs() {
        return this.hits.toArray(new HIT[hits.size()]);
    }

    public List<String> getAllHITIds() {
        List<String> retval = new ArrayList<String>();
        for (HIT hit : this.hits){
            retval.add(hit.getHITId());
        }
        return retval;
    }
}
