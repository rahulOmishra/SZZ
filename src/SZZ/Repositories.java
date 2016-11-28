package SZZ;



/**
 * Created by usi on 11/28/16.
 */

import org.eclipse.jgit.api.Git;
import java.io.File;
import java.io.IOException;



public class Repositories
{

    public org.eclipse.jgit.lib.Repository getReposit(String remote_url) throws java.io.IOException, org.eclipse.jgit.api.errors.GitAPIException
    {

    File localPath = File.createTempFile("TestGitRepository", "");
    if (!localPath.delete())
    {
    throw new IOException("Could not delete temporary file " + localPath);

    }
    String REMOTE_URL=remote_url;
    System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
    try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath).call())
    {
    org.eclipse.jgit.lib.Repository repository;
    repository = result.getRepository();
    return repository;

    }

    }

}


