package szz;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.DiffEntry;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.diff.EditList;
/**
 * Created by usi on 12/2/16.
 */
public class Diff {

    private Repository repository;
    private Commit commit;
    private String path;

    public Diff(Repository repository, Commit commit) {

        this.repository = repository;
        this.commit = commit;


    }

    public void blameOnDiff(BlameResult bResult, String path, int fileCounter) throws IOException, GitAPIException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        df.setRepository(repository.getGitRepository());
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);
        ObjectReader reader = repository.getGitRepository().newObjectReader();
        List<DiffEntry> diffs;
        diffs = new Git(repository.getGitRepository()).diff()
                .setNewTree(new CanonicalTreeParser().resetRoot(reader,commit.getGitCommit().getTree()))
                .setOldTree(new CanonicalTreeParser().resetRoot(reader, commit.getGitCommit().getParent(0).getTree()))
                .call();

        int filesChanged = diffs.size();
        EditList Elist=df.toFileHeader(diffs.get(fileCounter)).toEditList();

            for(int i=0; i<Elist.size();i++) {
                if ((Elist.get(i).getType().name() == "DELETE") ||
                        (Elist.get(i).getType().name() == "REPLACE")) {

                    for (int j = Elist.get(i).getBeginA()+1; j <= Elist.get(i).getEndA(); j++) {
                        RevCommit commit = bResult.getSourceCommit(j);
                        System.out.println("Blamed commit:  " + commit + "   Author:  " + bResult.getSourceAuthor(j));
                    }

                }
            }

    }

}



