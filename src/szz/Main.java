package szz;

/**
 * Created by usi on 11/15/16.
 */
import com.gitblit.models.PathModel.PathChangeModel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String REMOTE_URL = "https://github.com/eclipse/che.git";

    public static void main(String[] args) throws java.lang.Exception {

        Repository repository = Repository.getRemoteRepository(REMOTE_URL);
        List<Commit> commitList = repository.getCommits();

        int commitCounter = 0;

        for (Commit commit : commitList) {
            if (commit.isLikelyBugFixingCommit()) {
                commitCounter++;

                System.out.println("(#" + commitCounter + ") + bug fixing commit \t" + commit);

                List<PathChangeModel> plist = commit.getFilesInCommit(true);

                for (PathChangeModel path: plist) {
                    System.out.println(path.path);
//                        Blamer blame = new Blamer("/Users/usi/GitTemp/temp2106637655004264668/" + plist.get(10).name, Repository);
//                        blame.blameGenration();
                    System.out.println("deletions:   " + path.deletions);
                    System.out.println("insertions:   " + path.insertions);
                }

            }
        }
    }

}







