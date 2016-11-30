package szz;

/**
 * Created by usi on 11/29/16.
 */

import com.gitblit.models.PathModel;
import com.gitblit.models.PathModel.PathChangeModel;
import org.eclipse.jgit.blame.BlameGenerator;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import java.io.ByteArrayOutputStream;

import java.io.IOException;


public class Blamer
{
    private PathChangeModel path;
    private Repository repository;

    public Blamer(PathModel.PathChangeModel path, Repository repository) {
        this.path=path;
        this.repository=repository;
    }

    public PathChangeModel getPath() {
        return  path;
    }

    public Repository getRepository() {
        return repository;
    }

    public void blameGeneration() throws IOException {
        BlameGenerator blameGen = new BlameGenerator(repository.getGitRepository(),"/Users/usi/GitTemp/temp2063004325970775410"+path.path);
        BlameResult blameRes = blameGen.computeBlameResult();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RawText RText = new RawText(out.toByteArray());
        RText= blameRes.getResultContents();
        RText.getLineDelimiter();

        for (int i=0; i<RText.size(); i++) {
            System.out.println(RText.getString(i));
        }

        blameGen.close();
    }




}
