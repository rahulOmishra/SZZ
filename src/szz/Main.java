package szz;

/**
 * Created by usi on 11/15/16.
 */
import com.gitblit.models.PathModel.PathChangeModel;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import java.util.Set;

public class Main {

    private static final String REMOTE_URL = "https://github.com/apache/commons-lang.git";

    public static void main(String[] args) throws java.lang.Exception {

        Repository repository = Repository.getRemoteRepository(REMOTE_URL);
        List<Commit> commitList = repository.getCommits();

        Set<String> commitId= new HashSet<>();
        for(int i=0; i< commitList.size(); i++){
            commitId.add(commitList.get(i).getId());
        }


        int commitCounter = 0;
         System.out.println(commitId.size());

        for (Commit commit : commitList) {
            if (commit.isLikelyBugFixingCommit()) {
                commitCounter++;

               System.out.println("(#" + commitCounter + ") + bug fixing commit \t" + commit);

                List<PathChangeModel> plist = commit.getFilesInCommit(true);

                for (PathChangeModel path: plist) {
                    System.out.println(path.path);
                    System.out.println("deletions:   " + path.deletions);
                    System.out.println("insertions:   " + path.insertions);

                  Blamer blamer = new Blamer(path, repository);
                  blamer.blameGeneration();

                }

            }
        }
    }

}







