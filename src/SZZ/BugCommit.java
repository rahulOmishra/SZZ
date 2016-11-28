package SZZ; /**
 * Created by usi on 11/15/16.
 */



import java.io.File;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import java.io.IOException;
import org.eclipse.jgit.transport.FetchResult;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import java.io.ByteArrayOutputStream;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.diff.RawText;
import com.gitblit.models.PathModel.PathChangeModel;
import com.gitblit.models.RefModel;
import org.eclipse.jgit.lib.Ref;
import java.util.Map;
import java.util.Collections;
import java.util.Date;
import java.util.Map.Entry;
import org.apache.commons.lang3.*;
import java.lang.*;
import com.gitblit.utils.DiffStatFormatter;
import org.eclipse.jgit.revwalk.RevObject;





public class BugCommit {

    private static final String REMOTE_URL = "https://github.com/eclipse/che.git";


    public static boolean hasCommits(Repository repository) throws java.lang.NullPointerException, org.eclipse.jgit.errors.MissingObjectException{
        if (repository != null && repository.getDirectory().exists()) {
            return (new File(repository.getDirectory(), "objects").list().length > 2)
                    || (new File(repository.getDirectory(), "objects/pack").list().length > 0);
        }
        return false;
    }



    public static List<RefModel> getLocalBranches(Repository repository, boolean fullName,
                                                  int maxCount)throws java.io.IOException {
        return getRefs(repository, Constants.R_HEADS, fullName, maxCount,0);
    }



    private static List<RefModel> getRefs(Repository repository, String refs, boolean fullName,
                                          int maxCount, int offset) throws java.io.IOException {
        List<RefModel> list = new ArrayList<>();
        if (maxCount == 0) {
            return list;
        }
        if (!hasCommits(repository)) {
            return list;
        }

        Map<String, Ref> map = repository.getRefDatabase().getRefs(refs);
        RevWalk rw = new RevWalk(repository);
        for (Entry<String, Ref> entry : map.entrySet()) {
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

    public static ObjectId getDefaultBranch(Repository repository) throws Exception {
        ObjectId object = repository.resolve(Constants.HEAD);
        if (object == null) {
            // no HEAD
            // perhaps non-standard repository, try local branches
            List<RefModel> branchModels = getLocalBranches(repository, true, -1);
            if (branchModels.size() > 0) {
                // use most recently updated branch
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
    public static List<PathChangeModel> getFilesInCommit(Repository repository, RevCommit commit, boolean calculateDiffStat) throws java.lang.Exception  {
        List<PathChangeModel> list = new ArrayList<>();

        RevWalk rw = new RevWalk(repository);
        try {
            if (commit == null) {
                ObjectId object = getDefaultBranch(repository);
                commit = rw.parseCommit(object);
            }
                RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
                DiffStatFormatter df = new DiffStatFormatter(commit.getName(), repository);
                df.setRepository(repository);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                for (DiffEntry diff : diffs) {
                    PathChangeModel pcm = PathChangeModel.from(diff, commit.getName(), repository);

                    if (calculateDiffStat) {
                        df.format(diff);
                        PathChangeModel pathStat = df.getDiffStat().getPath(pcm.path);
                        if (pathStat != null) {
                            pcm.insertions = pathStat.insertions;
                            pcm.deletions = pathStat.deletions;

                        }
                    }
                    list.add(pcm);
                }

        }  catch (RevisionSyntaxException | org.eclipse.jgit.errors.MissingObjectException | GitAPIException  e) {
            e.printStackTrace();
        }


            rw.dispose();
        return list;
    }












    public static void main(String[] args) throws java.lang.Exception {


        File input_file = new File("/Users/usi/Desktop/commits.txt");

        File output_file = new File("/Users/usi/Desktop/bugCommit.txt");

            Repository repository = new Get_repository().getReposit(REMOTE_URL);



            try (Git git = new Git(repository)) {

                FetchResult result1 = git.fetch().setCheckFetchedObjects(true).call();

                //  System.out.println("Messages: " + result1.getMessages());
            }

            BufferedWriter bf_input = new BufferedWriter(new FileWriter(input_file));

            try (Git git = new Git(repository)) {

                Iterable<RevCommit> All_commit = git.log().all().call();
                List<Commit_ready> commit_list = new ArrayList<>();
                int count = 0;
                for (RevCommit commit : All_commit) {


                    commit_list.add(count,new Commit_ready(commit.getName(),commit.getCommitterIdent(),commit.getFullMessage()));


                    count++;
                }
                System.out.println("no of commits::"+commit_list.size());




//                String[] content1 = FileUtils.readFileToString(input_file, Charset.forName("utf-8")).split("\n");

               for (int i = 0; i < commit_list.size(); i++) {
                    if (commit_list.get(i).getMessage().contains("fixes") || commit_list.get(i).getMessage().contains("fixed") || commit_list.get(i).getMessage().contains("closed") || commit_list.get(i).getMessage().contains("closed")) {


                        System.out.println("commit_no: " +i+ "  commit_hash:   "+ commit_list.get(i).getHash_id() + "  Committer:   "+ commit_list.get(i).getCommitter().getName() + "  Commit_message:  " + commit_list.get(i).getMessage());


                        List<PathChangeModel> plist = new ArrayList<>();

                        RevWalk walk1 = new RevWalk(repository);
                        ObjectId id1 = repository.resolve(commit_list.get(i).getHash_id());
                        RevCommit commitAgain1 = walk1.parseCommit(id1);
                        plist = getFilesInCommit(repository, commitAgain1, true);


                        for (int j = 0; j < plist.size(); j++) {

                            System.out.println(plist.get(j).objectId);


                            System.out.println("deletions:   " + plist.get(j).deletions +  "    file_no:  " + j);
                            System.out.println("insertions:   " + plist.get(j).insertions + "   file_no:  " + j);


                        }

             walk1.dispose();
                    }


                }











                ObjectId oldHead = repository.resolve("HEAD^^^^{tree}");
                ObjectId head = repository.resolve("HEAD^{tree}");

                // System.out.println("Printing diff between tree: " + oldHead + " and " + head);


                try (ObjectReader reader = repository.newObjectReader()) {
                    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                    oldTreeIter.reset(reader, oldHead);
                    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                    newTreeIter.reset(reader, head);

                    RevWalk walk = new RevWalk(repository);
                    ObjectId id = repository.resolve("151b5a2c9d786e086bff228193e0610a5ee4ec1b");
                    RevCommit commitAgain = walk.parseCommit(id);
                   // System.out.println("Found Commit again: " + commitAgain.getFullMessage());

                    walk.dispose();

                    RevTree tree = commitAgain.getTree();
                    //System.out.println("Having tree: " + tree);

                    try (TreeWalk treeWalk = new TreeWalk(repository)) {
                        treeWalk.addTree(tree);
                        treeWalk.setRecursive(true);


//                        ObjectId objectId = treeWalk.getObjectId(0);
//                        ObjectLoader loader = repository.open(objectId);
                        ObjectLoader loader = repository.open(tree.getId());
                       // loader.copyTo(System.out);
                    }

                    walk.dispose();
//////////////////////////////////////////////////diff-change type/////////////////////////////////////////////////
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int lines = 0;
                    DiffFormatter df = new DiffFormatter(out);
                    df.setRepository(repository);
                    df.setDiffComparator(RawTextComparator.DEFAULT);
                    df.setDetectRenames(true);
                    List<DiffEntry> diffs1 = df.scan(commitAgain.getTree(), commitAgain.getParent(0).getTree());
//                    for (DiffEntry diff : diffs1) {
//                        lines++;
//                        System.out.println("changeType=" + diff.getChangeType().name()
//                                + "newPath=" + diff.getNewPath()
//                                + "old path " + diff.getOldPath()
//                                + "Hash code " + diff.hashCode()
//                                + "String  " + diff.toString()
//                                + "change " + diff.getChangeType().toString()
//                        );


                        try (Git git1 = new Git(repository)) {
                            List<DiffEntry> diffs2 = git1.diff()
                                    .setNewTree(newTreeIter)
                                    .setOldTree(oldTreeIter)
                                    .call();
                            for (DiffEntry entry : diffs2) {
                               // System.out.println("Entry: " + entry);
                            }
                        }

//////////////////////////////////////////////blame////////////////////////////////////////////////////////////////////
                    BlameCommand blamer = new BlameCommand(repository);
                    ObjectId commitID = repository.resolve("HEAD");
                    blamer.setStartCommit(commitID);
                    blamer.setFilePath("README.md");
                    BlameResult blame = blamer.call();


//                for (int i = 0; i < lines; i++) {
//                    RevCommit commit = blame.getSourceCommit(i);
//                    System.out.println("Line: " + i + ": " + commit);
//                }
//
//                System.out.println("Displayed commits responsible for " + lines + " lines of README.md");

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    ObjectId old;
                    ObjectReader reader1 = repository.newObjectReader();
                    CanonicalTreeParser oldTreeIter1 = new CanonicalTreeParser();
                    CanonicalTreeParser newTreeIter1 = new CanonicalTreeParser();
                    List<DiffEntry> diffs = null;

                    try {
                        old = repository.resolve("151b5a2c9d786e086bff228193e0610a5ee4ec1b" + "^{tree}");
                        head = repository.resolve("HEAD^{tree}");
                        oldTreeIter1.reset(reader1, old);
                        newTreeIter1.reset(reader1, head);
                        diffs = git.diff()
                                .setNewTree(newTreeIter)
                                .setOldTree(oldTreeIter)
                                .call();

                    } catch (RevisionSyntaxException | IOException | GitAPIException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                    DiffFormatter df1 = new DiffFormatter(out1);
                    // df.setRepository(git.getRepos());
                    df.setRepository(repository);
                    ArrayList<String> diffText = new ArrayList<>();
                    for (DiffEntry diff : diffs) {
                        try {
                            df1.format(diff);
                            RawText r = new RawText(out.toByteArray());
                            r.getLineDelimiter();

//                            String diffText1 = out.toString("UTF-8");
//                            System.out.println(diffText);
//                            out.reset();
                            diffText.add(out.toString("UTF-8"));
                            out.reset();
                        } catch (IOException e) {
                           e.printStackTrace();
                        }

                    }
                    for (int i = 0; i < diffs.size(); i++) {
                        //System.out.println(diffText.get(i));


                       // System.out.println(i);

                    }





//                        TreeWalk treeWalk  = TreeWalk.forPath( repository, fileName, commit.getTree() );
//                        InputStream inputStream = repository.open( treeWalk.getObjectId( 0 ), Constants.OBJ_BLOB ).openStream();
///
//                        treeWalk.close(); // use release() in JGit < 4.0







                    }




                }
    }
        }






