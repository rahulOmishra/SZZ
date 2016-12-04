package szz;

/**
 * Created by usi on 11/28/16.
 */

import com.gitblit.utils.DiffStatFormatter;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import com.gitblit.models.PathModel;
import org.eclipse.jgit.revwalk.RevWalk;
import com.gitblit.utils.PatchFormatter;
import com.gitblit.*;
import org.eclipse.jgit.*;

import java.util.*;

public class Commit {

    private String id;
    private PersonIdent committer;
    private String message;
    private Repository repository;
    private RevCommit gitCommit;

    public Commit(Repository repository, RevCommit gitCommit) {
        this.repository = repository;
        this.gitCommit = gitCommit;
        this.id = gitCommit.getName();
        this.message = gitCommit.getFullMessage();
        this.committer = gitCommit.getCommitterIdent();

    }

    public String getId()

    {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PersonIdent getCommitter() {
        return committer;
    }

    public void setCommitter(PersonIdent commiter)

    {
        this.committer = commiter;
    }

    public String getMessage()

    {
        return message;
    }
    public RevCommit getGitCommit()

    {
        return gitCommit;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLikelyBugFixingCommit() {
        return (this.message.contains("fixes")  ||
                this.message.contains("fixed")  ||
                this.message.contains("closes") ||
                this.message.contains("fix")    ||
                this.message.contains("closed"));
    }

    public List<PathModel.PathChangeModel> getFilesInCommit(boolean calculateDiffStat) throws java.lang.Exception {
        List<PathModel.PathChangeModel> list = new ArrayList<>();
        RevWalk rw = new RevWalk(this.repository.getGitRepository());
        try {
            if (gitCommit == null) {
                ObjectId object = this.repository.getDefaultBranch();
                gitCommit = rw.parseCommit(object);
            }
            RevCommit parent = rw.parseCommit(gitCommit.getParent(0).getId());
            DiffStatFormatter df = new DiffStatFormatter(gitCommit.getName(), this.repository.getGitRepository());
            df.setRepository(this.repository.getGitRepository());
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs = df.scan(parent.getTree(), gitCommit.getTree());
            for (DiffEntry diff: diffs) {
                PathModel.PathChangeModel pcm = PathModel.PathChangeModel.from(diff, gitCommit.getName(), this.repository.getGitRepository());

                if (calculateDiffStat) {
                    df.format(diff);
                    PathModel.PathChangeModel pathStat = df.getDiffStat().getPath(pcm.path);
                    if (pathStat != null) {
                        pcm.insertions = pathStat.insertions;
                        pcm.deletions = pathStat.deletions;
                    }
                }
                list.add(pcm);
            }

        } catch (RevisionSyntaxException | org.eclipse.jgit.errors.MissingObjectException | GitAPIException e) {
            e.printStackTrace();
        }

        rw.dispose();
        return list;
    }

    @Override public String toString() {
        return "id:\t" + this.id + ", committer:\t" + this.committer.getName() + ", commit message:\t" + this.message;
    }
}