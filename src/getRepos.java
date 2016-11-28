import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import java.io.File;
import java.io.IOException;

/**
 * Created by usi on 11/28/16.
 */




public class getRepos {


    private String REMOTE_URL;



   public Repository getRepos(String remote_url) throws java.io.IOException, org.eclipse.jgit.api.errors.GitAPIException {


       File localPath = File.createTempFile("TestGitRepository", "");

       if (!localPath.delete()) {

           throw new IOException("Could not delete temporary file " + localPath);

       }
       REMOTE_URL=remote_url;
       System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);


       try (Git result = Git.cloneRepository()
               .setURI(REMOTE_URL)
               .setDirectory(localPath)
               .call()) {


           Repository repository;
           repository = result.getRepository();

           return repository;

       }

   }
}