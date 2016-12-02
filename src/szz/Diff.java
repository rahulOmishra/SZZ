package szz;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;

import java.io.IOException;
import java.util.List;


/**
 * Created by usi on 12/2/16.
 */
public class Diff {

    private Repository repository;
    private Commit commit;


    public Diff(Repository repository, Commit commit){

        this.repository= repository;
        this.commit= commit;





    }

    public void computeDiff(RevCommit parent) throws IOException{


        int linesAdded = 0;
        int linesDeleted = 0;
        int filesChanged = 0;
        try {


            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository.getGitRepository());
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs;

            diffs = df.scan(parent.getTree(), commit.getGitCommit().getTree());
            filesChanged = diffs.size();
            for (DiffEntry diff : diffs) {
                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getEndA() - edit.getBeginA();
                    System.out.println(linesDeleted);
                    linesAdded += edit.getEndB() - edit.getBeginB();
                    System.out.println(linesAdded);
                }
            }
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }


    }



}
