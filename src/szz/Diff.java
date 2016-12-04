package szz;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RawText;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.diff.Edit;
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

    public void computeDiff(BlameResult bResult, String path) throws IOException, GitAPIException {


        int linesAdded = 0;
        int linesDeleted = 0;
        int filesChanged = 0;

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

        filesChanged = diffs.size();
        ArrayList<String> diffText = new ArrayList<>();
        for (DiffEntry diff : diffs) {
            if ((diff.getChangeType().name() == "MODIFY" ||
                    diff.getChangeType().name() == "DELETE") ||
                    (diff.toString()== path)){
                EditList Elist=df.toFileHeader(diff).toEditList();

                for(int i=0; i<Elist.size();i++) {
                    for (int j= Elist.get(i).getBeginA()+1; j < Elist.get(i).getEndA(); j++) {
                        RevCommit commit = bResult.getSourceCommit(j);
                        System.out.println("Blamed commit:  " +commit+ "   Author:  "+ bResult.getSourceAuthor(j)+"   chnage type:  "+ Elist.get(i));
                    }

                 }


            }



        }
    }




}



