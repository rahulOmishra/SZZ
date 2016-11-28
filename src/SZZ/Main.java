package SZZ;

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





public class Main
{

    private static final String REMOTE_URL = "https://github.com/eclipse/che.git";

    public static void main(String[] args) throws java.lang.Exception
    {
                 org.eclipse.jgit.lib.Repository Repository = new Repositories().getReposit(REMOTE_URL);
                 try (Git git = new Git(Repository))
                 {
                 Iterable<RevCommit> All_commit = git.log().all().call();
                 List<Commit> commit_list = new ArrayList<>();
                 int count = 0;
                 for (RevCommit commit : All_commit)
                 {
                 commit_list.add(count,new Commit(commit.getName(),commit.getCommitterIdent(),commit.getFullMessage()));
                 count++;
                 }
                 System.out.println("no of commits::"+commit_list.size());


                for (int i = 0; i < commit_list.size(); i++)
                {
                    if (commit_list.get(i).getMessage().contains("fixes") || commit_list.get(i).getMessage().contains("fixed") || commit_list.get(i).getMessage().contains("closed") || commit_list.get(i).getMessage().contains("closed"))
                    {
                    System.out.println("commit_no: " +i+ "  commit_hash:   "+ commit_list.get(i).getHash_id() + "  Committer:   "+ commit_list.get(i).getCommitter().getName() + "  Commit_message:  " + commit_list.get(i).getMessage());
                    List<PathChangeModel> plist = new ArrayList<>();
                    RevWalk walk1 = new RevWalk(Repository);
                    ObjectId id1 = Repository.resolve(commit_list.get(i).getHash_id());
                    RevCommit commitAgain1 = walk1.parseCommit(id1);
                    plist = commit_list.get(i).getFilesInCommit(Repository, commitAgain1, true);
                       for (int j = 0; j < plist.size(); j++)
                       {
                       System.out.println(plist.get(j).objectId);
                       System.out.println("deletions:   " + plist.get(j).deletions +  "    file_no:  " + j);
                       System.out.println("insertions:   " + plist.get(j).insertions + "   file_no:  " + j);
                       }
                     walk1.dispose();
                     }


                }
                }
    }
}







