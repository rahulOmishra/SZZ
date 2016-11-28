package SZZ;

/**
 * Created by usi on 11/28/16.
 */

import com.gitblit.models.PathModel;
import com.gitblit.models.RefModel;
import com.gitblit.utils.DiffStatFormatter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.util.*;

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


    public PersonIdent getCommitter() {
        return committer;
    }


    public String getMessage()

    {
        return message;
    }

    public  List<PathModel.PathChangeModel> getFilesInCommit(org.eclipse.jgit.lib.Repository Repository, RevCommit commit, boolean calculateDiffStat) throws java.lang.Exception {
        List<PathModel.PathChangeModel> list = new ArrayList<>();
        RevWalk rw = new RevWalk(Repository);
        try {
            if (commit == null) {
                ObjectId object = getDefaultBranch(Repository);
                commit = rw.parseCommit(object);
            }
            RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
            DiffStatFormatter df = new DiffStatFormatter(commit.getName(), Repository);
            df.setRepository(Repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
            for (DiffEntry diff : diffs) {
                PathModel.PathChangeModel pcm = PathModel.PathChangeModel.from(diff, commit.getName(), Repository);

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


    public static ObjectId getDefaultBranch(org.eclipse.jgit.lib.Repository Repository) throws Exception

    {
        ObjectId object = Repository.resolve(Constants.HEAD);
        if (object == null) {
            List<RefModel> branchModels = getLocalBranches(Repository, true, -1);
            if (branchModels.size() > 0) {
                RefModel branch = null;
                Date lastDate = new Date(0);
                for (RefModel branchModel : branchModels) {
                    if (branchModel.getDate().after(lastDate)) {
                        branch = branchModel;
                        lastDate = branch.getDate();
                    }
                }
                object = branch.getReferencedObjectId();
            }
        }
        return object;
    }

    public static List<RefModel> getLocalBranches(org.eclipse.jgit.lib.Repository Repository, boolean fullName,
                                                  int maxCount) throws java.io.IOException {
        return getRefs(Repository, Constants.R_HEADS, fullName, maxCount, 0);
    }


    private static List<RefModel> getRefs(org.eclipse.jgit.lib.Repository Repository, String refs, boolean fullName,
                                          int maxCount, int offset) throws java.io.IOException {
        List<RefModel> list = new ArrayList<>();
        if (maxCount == 0) {
            return list;
        }
        if (!hasCommits(Repository)) {
            return list;
        }

        Map<String, Ref> map = Repository.getRefDatabase().getRefs(refs);
        RevWalk rw = new RevWalk(Repository);
        for (Map.Entry<String, Ref> entry : map.entrySet()) {
            Ref ref = entry.getValue();
            RevObject object = rw.parseAny(ref.getObjectId());
            String name = entry.getKey();
            if (fullName && !StringUtils.isEmpty(refs)) {
                name = refs + name;
            }
            list.add(new RefModel(name, ref, object));
        }
        rw.dispose();
        Collections.sort(list);
        Collections.reverse(list);
        if (maxCount > 0 && list.size() > maxCount) {
            if (offset < 0) {
                offset = 0;
            }
            int endIndex = offset + maxCount;
            if (endIndex > list.size()) {
                endIndex = list.size();
            }
            list = new ArrayList<RefModel>(list.subList(offset, endIndex));
        }

        return list;
    }


    public static boolean hasCommits(org.eclipse.jgit.lib.Repository Repository) throws java.lang.NullPointerException, org.eclipse.jgit.errors.MissingObjectException {
        if (Repository != null && Repository.getDirectory().exists()) {
            return (new File(Repository.getDirectory(), "objects").list().length > 2)
                    || (new File(Repository.getDirectory(), "objects/pack").list().length > 0);
        }
        return false;
    }
}