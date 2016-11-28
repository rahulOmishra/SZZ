/**
 * Created by usi on 11/28/16.
 */

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class Commits {

private String Commit_hash_id;

private String Committer;

private String Message;





public Commits (String commit_hash_id, String committer,String message)
{


    Commit_hash_id=commit_hash_id;
    Committer=committer;
    Message=message;


}

}
