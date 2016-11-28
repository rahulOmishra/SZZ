package SZZ;

/**
 * Created by usi on 11/28/16.
 */

import org.eclipse.jgit.lib.PersonIdent;

public class Commit {

    private String hash_id;

    private PersonIdent committer;

    private String message;


    public Commit(String hash_id, PersonIdent committer, String message) {


        this.hash_id = hash_id;
        this.committer = committer;
        this.message = message;


    }

    public void setHash_id(String hash_id) {

        this.hash_id = hash_id;
    }


    public void setCommitter(PersonIdent commiter)

    {
        this.committer = commiter;


    }

    public void setMessage(String message)

    {
        this.message = message;


    }

    public String getHash_id()

    {

        return hash_id;

    }


    public PersonIdent getCommitter()
    {

        return committer;


    }


    public String getMessage()

    {

        return message;
    }
}
