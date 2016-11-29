package szz;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by usi on 11/29/16.
 */
public class Repository {

    private org.eclipse.jgit.lib.Repository gitRepository;
    private List<Commit> commits = null;

    public Repository(org.eclipse.jgit.lib.Repository gitRepository) {
        this.gitRepository = gitRepository;
    }

    public org.eclipse.jgit.lib.Repository getGitRepository() {
        return this.gitRepository;
    }

    public static Repository getRemoteRepository(String remoteURL) throws IOException, GitAPIException {

        File repoPath = new File("/Users/usi/GitTemp");
        File localPath = File.createTempFile("temp", "",repoPath);

        if (!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }

        System.out.println("Cloning from " + remoteURL + " to " + localPath);

        try (Git result = Git.cloneRepository().setURI(remoteURL).setDirectory(localPath).call()) {
            org.eclipse.jgit.lib.Repository repository;
            repository = result.getRepository();
            return new Repository(repository);
        }
    }

    public List<Commit> getCommits() throws IOException, GitAPIException {
        // Lazy attribute.
        if (this.commits == null) {
            try (Git git = new Git(this.gitRepository)) {
                Iterable<RevCommit> allCommits = git.log().all().call();
                this.commits = new ArrayList<>();
                allCommits.forEach(gitCommit -> commits.add(new Commit(this, gitCommit)));
            }
        }

        return this.commits;
    }
}
