package szz;

/**
 * Created by usi on 11/15/16.
 */
import com.gitblit.models.PathModel.PathChangeModel;
import org.eclipse.jgit.blame.BlameResult;
import java.util.List;

public class Main {

    private static String REMOTE_URL = null;

    public static void main(String[] args) throws java.lang.Exception {

        REMOTE_URL = args[0];
        Repository repository = Repository.getRemoteRepository(REMOTE_URL);
        List<Commit> commitList = repository.getCommits();


        int commitCounter = 0;
        System.out.println(commitList.size());

        for (Commit commit : commitList) {
            if (commit.isLikelyBugFixingCommit()) {
                commitCounter++;
                System.out.println("(#" + commitCounter + ") + bug fixing commit \t" + commit);

                List<PathChangeModel> plist = commit.getFilesInCommit(true);
                int fileCounter=0;
                for (PathChangeModel path : plist) {
                    System.out.println(path.path);
                    System.out.println("deletions:   " + path.deletions);
                    System.out.println("insertions:   " + path.insertions);

                    Blamer blamer = new Blamer(path, repository);
                    //Set<RevCommit> listCommiter = blamer.blameGeneration(commit);
                    BlameResult bResult= blamer.blameGeneration(commit);

                    new Diff(repository,commit).blameOnDiff(bResult,path.path, fileCounter);
                    fileCounter++;

                }
                fileCounter=0;
            }
        }
    }
}







