package szz;

/**
 * Created by usi on 11/29/16.
 */

import org.eclipse.jgit.blame.BlameGenerator;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.Repository;
import java.io.ByteArrayOutputStream;

import java.io.IOException;


public class Blamer
{
    private String path;
    private Repository repository;

    public Blamer(String path, Repository repository) {
        this.path=path;
        this.repository=repository;
    }

    public String getPath() {
        return  path;
    }

    public void setPath(String path) {
        this.path=path;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository=repository;
    }

    public void blameGenration() throws IOException {
        BlameGenerator blameGen = new BlameGenerator(repository,path);
        BlameResult blameRes= blameGen.computeBlameResult();
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
