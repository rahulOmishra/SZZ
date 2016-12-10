package szz;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat;


/**
 * Created by usi on 12/7/16.
 */
public class CsvOut {

    private  String lineSeparator = "\n";

    private  String fileHeader = "BugFix-CommitId,BugInducing-CommitId";


    public void writeToCSV(String fileName, Map<Commit, List<Commit>> blameMap) throws IOException {

        FileWriter writer = null;
        CSVPrinter csvPrinter = null;

        File dir=new File("/Users/usi/AllCSV");
        if(!dir.exists()){
            dir.mkdir();
        }
        File tagFile=new File(dir,fileName+".csv");
        if(!tagFile.exists()){
            tagFile.createNewFile();
        }
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(lineSeparator).withHeader(fileHeader);

        writer = new FileWriter(tagFile);
        csvPrinter = new CSVPrinter(writer, csvFileFormat);
        for( Map.Entry<Commit, List<Commit>> entry : blameMap.entrySet()) {
            Commit key = entry.getKey();
            for (Commit value : entry.getValue()) {
                List<java.io.Serializable> szzOutput= new ArrayList<>();


                szzOutput.add(key.getId());
                //szzOutput.add(key.getCommitter().getName());
                szzOutput.add(value.getId());
                //szzOutput.add(value.getGitCommit().getAuthorIdent().getName());
                csvPrinter.printRecord(szzOutput);

            }

        }

        writer.close();
        csvPrinter.close();
    }

}
