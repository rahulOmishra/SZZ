package szz;

/**
 * Created by usi on 11/15/16.
 */
import com.gitblit.models.PathModel.PathChangeModel;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import java.util.Set;

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

//                if(commitCounter==1)
//                    break;
                commitCounter++;
                System.out.println("(#" + commitCounter + ") + bug fixing commit \t" + commit);

                List<PathChangeModel> plist = commit.getFilesInCommit(true);

                for (PathChangeModel path : plist) {
                    System.out.println(path.path);
                    System.out.println("deletions:   " + path.deletions);
                    System.out.println("insertions:   " + path.insertions);

                    Blamer blamer = new Blamer(path, repository);
                    //Set<RevCommit> listCommiter = blamer.blameGeneration(commit);
                    BlameResult bResult= blamer.blameGeneration(commit);

                        new Diff(repository,commit).computeDiff(bResult,path.path);

                }
            }
        }
    }
}







