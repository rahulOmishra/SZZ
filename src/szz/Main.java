package szz;

/**
 * Created by usi on 11/15/16.
 */
import com.gitblit.models.PathModel.PathChangeModel;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.jgit.blame.BlameResult;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.revwalk.*;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

public class Main {

    private static String REMOTE_URL = null;
    private static String commitHash=null;
    public static boolean flag= false;
    public static Map<Commit, List<Commit>> blameMap = new HashMap<>();

    public static void findBugInducingCommits(Commit commit, int fileCounter, Repository repository,Map<Commit, List<Commit>> blameMap ) throws Exception {
        List<PathChangeModel> plist = commit.getFilesInCommit(true);
        List<Commit> bCommits = new ArrayList<>();
        for (PathChangeModel path : plist) {
            System.out.println(path.path);
            System.out.println("deletions:   " + path.deletions);
            System.out.println("insertions:   " + path.insertions);

            Blamer blamer = new Blamer(path, repository);
            //Set<RevCommit> listCommiter = blamer.blameGeneration(commit);
            BlameResult bResult= blamer.blameGeneration(commit);

            new Diff(repository,commit).blameOnDiff(bResult, fileCounter,bCommits);
            fileCounter++;

        }
        blameMap.put(commit,bCommits);
    }
    public static void main(String[] args) throws java.lang.Exception {

        REMOTE_URL = args[0];
        String[] urlParts= REMOTE_URL.split("/");
        String repoName=urlParts[urlParts.length-1];
        String[] repoParts= repoName.split("\\.");
        List<String> bugNumber = new LinkedList<>();

        Repository repository = Repository.getRemoteRepository(REMOTE_URL,urlParts[urlParts.length - 2]+repoParts[0].toUpperCase());
        if(!args[2].contains("no")) {

            List<Issue> issueList = new Issues(urlParts[urlParts.length - 2], repoParts[0]).fetchIssue();

            for(Issue issue: issueList){

                bugNumber.add(String.valueOf(issue.getNumber()));
                System.out.println(issue.getNumber());
            }

        }

        if(args[1].contains("no")){
            List<Commit> commitList = repository.getCommits();
            int commitCounter = 0;
            System.out.println(commitList.size());

            for (Commit commit : commitList) {

                if(args[2].contains("no")) {
                    if (commit.isLikelyBugFixingCommit()) {
                        commitCounter++;
                        System.out.println("(#" + commitCounter + ") + bug fixing commit \t" + commit);
                        int fileCounter = 0;
                        findBugInducingCommits(commit, fileCounter, repository, blameMap);
                    }
                }else{


                    Boolean match=false;
                    for(String bugNum: bugNumber){
                        if (commit.getGitCommit().getFullMessage().contains(bugNum)){
                            match=true;
                        }
                    }
                    if ((commit.isLikelyBugFixingCommit())&& match) {
                        commitCounter++;
                        System.out.println("(#" + commitCounter + ") + bug fixing commit \t" + commit);
                        int fileCounter = 0;
                        findBugInducingCommits(commit, fileCounter, repository, blameMap);
                    }

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

            System.out.println(" bug fixing commit \t" + commit);
            int fileCounter=0;
            findBugInducingCommits(commit,fileCounter,repository, blameMap);

        }
        new CsvOut().writeToCSV(repoParts[0],blameMap);
    }
}







