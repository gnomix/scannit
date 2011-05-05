package nl.javadude.scannit.reader;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import javassist.bytecode.ClassFile;
import nl.javadude.scannit.Configuration;
import nl.javadude.scannit.metadata.JavassistHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Read an URI into a File list, using TrueZip to scan through package.
 */
public class URIReader {
    /**
     * Convert a URI into a recursive list of all the File entries in it and any of its directories.
     *
     * @param uri The URI to read
     * @return a List of TFile of all the entries in the URI.
     */
    public List<TFile> listFiles(URI uri) {
        TFile tFile = null;
        if (uri.getPath() != null) {
            // if the URI has a path, this means it is a real file, use the detection of the path, and not look at the scheme
            tFile = new TFile(uri.getPath());
        } else {
            // Detect based on the scheme by passing in the uri.
            tFile = new TFile(uri);
        }
        return listDirectory(tFile);
    }

    private List<TFile> listDirectory(TFile tFile) {
        logger.debug("Listing directory/archive of file: {}", tFile);
        List<TFile> files = newArrayList();
        for (TFile file : tFile.listFiles()) {
            if (file.isArchive() || file.isDirectory()) {
                files.addAll(listDirectory(file));
            } else if (file.isEntry() || file.isFile()) {
                logger.debug("Found file/entry {}", file);
                files.add(file);
            }
        }

        return files;
    }

    private static final Logger logger = LoggerFactory.getLogger(URIReader.class);
}
