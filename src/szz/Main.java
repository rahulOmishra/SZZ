package szz;

/**
 * Created by usi on 11/15/16.
 */
import com.gitblit.models.PathModel.PathChangeModel;
import org.eclipse.jgit.blame.BlameResult;
import java.util.List;
import org.eclipse.jgit.revwalk.*;

public class Main {

    private static String REMOTE_URL = null;
    private static String commitHash=null;
    public static boolean flag= false;


    public static void findBugInducingCommits(Commit commit, int fileCounter, Repository repository) throws Exception {
        List<PathChangeModel> plist = commit.getFilesInCommit(true);

        for (PathChangeModel path : plist) {
            System.out.println(path.path);
            System.out.println("deletions:   " + path.deletions);
            System.out.println("insertions:   " + path.insertions);

            Blamer blamer = new Blamer(path, repository);
            //Set<RevCommit> listCommiter = blamer.blameGeneration(commit);
            BlameResult bResult= blamer.blameGeneration(commit);

            new Diff(repository,commit).blameOnDiff(bResult, fileCounter);
            fileCounter++;

        }

    }
    public static void main(String[] args) throws java.lang.Exception {

        REMOTE_URL = args[0];
        Repository repository = Repository.getRemoteRepository(REMOTE_URL);

        if(args.length==1){
            List<Commit> commitList = repository.getCommits();
            int commitCounter = 0;
            System.out.println(commitList.size());

            for (Commit commit : commitList) {
                if (commit.isLikelyBugFixingCommit()) {
                    commitCounter++;
                    System.out.println("(#" + commitCounter + ") + bug fixing commit \t" + commit);
                    int fileCounter=0;
                    findBugInducingCommits(commit,fileCounter,repository);
                    fileCounter=0;
                }
            }

        } else{
            flag=true;
            commitHash= args[1];
            RevWalk RW= new RevWalk(repository.getGitRepository());
            Commit commit= new Commit(repository,RW
                           .parseCommit(repository.getGitRepository()
                           .resolve(commitHash)));
            //RW.dispose();
            if (commit.isLikelyBugFixingCommit()) {
                System.out.println(" bug fixing commit \t" + commit);
                int fileCounter=0;
                findBugInducingCommits(commit,fileCounter,repository);

            }

        }

    }
}







