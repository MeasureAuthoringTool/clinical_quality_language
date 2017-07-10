package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Manages a set of CQL libraries. As new library references are encountered
 * during translation, the corresponding source is obtained via
 * librarySourceLoader, translated and cached for later use.
 */
public class LibraryManager {
    private ModelManager modelManager;
    private final Map<String, TranslatedLibrary> libraries;
    private final Stack<String> translationStack;
    private final DefaultLibrarySourceLoader librarySourceLoader;

    // For use in MAT code... capture elm output, translator mappings, and library mappings here.
    private Map<String, CqlTranslator> translators;
    private Map<String, TranslatedLibrary> libraryMap; // I know a mapping is already done up above, but this captures
    // information on libraryname-x.x.xxx, which is consistent with
    // with all of our implementations

    public LibraryManager(ModelManager modelManager) {
        if (modelManager == null) {
            throw new IllegalArgumentException("modelManager is null");
        }
        this.modelManager = modelManager;
        libraries = new HashMap<>();
        translationStack = new Stack<>();
        this.librarySourceLoader = new DefaultLibrarySourceLoader();

        // more MAT code...
        this.translators = new HashMap<>();
        this.libraryMap = new HashMap<>();
    }

    public LibrarySourceLoader getLibrarySourceLoader() {
        return librarySourceLoader;
    }

    public TranslatedLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, List<CqlTranslatorException> errors) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        TranslatedLibrary library = libraries.get(libraryIdentifier.getId());

        if (library != null
                && libraryIdentifier.getVersion() != null
                && !libraryIdentifier.getVersion().equals(library.getIdentifier().getVersion())) {
            throw new CqlTranslatorIncludeException(String.format("Could not resolve reference to library %s, version %s because version %s is already loaded.",
                    libraryIdentifier.getId(), libraryIdentifier.getVersion(), library.getIdentifier().getVersion()), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        } else {
            library = translateLibrary(libraryIdentifier, errors);
            libraries.put(libraryIdentifier.getId(), library);
        }

        return library;
    }

    private TranslatedLibrary translateLibrary(VersionedIdentifier libraryIdentifier, List<CqlTranslatorException> errors) {
        InputStream librarySource = null;
        try {
            librarySource = librarySourceLoader.getLibrarySource(libraryIdentifier);
        }
        catch (Exception e) {
            throw new CqlTranslatorIncludeException(e.getMessage(), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }

        if (librarySource == null) {
            throw new CqlTranslatorIncludeException(String.format("Could not load source for library %s, version %s.",
                    libraryIdentifier.getId(), libraryIdentifier.getVersion()), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        }

        try {
            CqlTranslator translator = CqlTranslator.fromStream(librarySource, modelManager, this);
            if (errors != null) {
                errors.addAll(translator.getErrors());
            }

            TranslatedLibrary result = translator.getTranslatedLibrary();
            if (libraryIdentifier.getVersion() != null && !libraryIdentifier.getVersion().equals(result.getIdentifier().getVersion())) {
                throw new CqlTranslatorIncludeException(String.format("Library %s was included as version %s, but version %s of the library was found.",
                        libraryIdentifier.getId(), libraryIdentifier.getVersion(), result.getIdentifier().getVersion()),
                        libraryIdentifier.getId(), libraryIdentifier.getVersion());
            }

            // for use in MAT code... We should be able to capture the translators here, creating a mapping with
            // between LibraryName-x.x.xxx and translator.
            this.translators.put(libraryIdentifier.getId() + "-" + libraryIdentifier.getVersion(), translator);
            this.libraryMap.put(libraryIdentifier.getId() + "-" + libraryIdentifier.getVersion(), result);

            return result;
        } catch (IOException e) {
            throw new CqlTranslatorIncludeException(String.format("Errors occurred translating library %s, version %s.",
                    libraryIdentifier.getId(), libraryIdentifier.getVersion()), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }
    }

    public void beginTranslation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        if (translationStack.contains(libraryName)) {
            throw new IllegalArgumentException(String.format("Circular library reference %s.", libraryName));
        }

        translationStack.push(libraryName);
    }

    public void endTranslation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        String currentLibraryName = translationStack.pop();
        if (!libraryName.equals(currentLibraryName)) {
            throw new IllegalArgumentException(String.format("Translation stack imbalance for library %s.", libraryName));
        }
    }

    // more mat code below....
    /**
     * Returns a mapping of the libraryname-x.x.xxx to the translated library
     * @return the library mapping
     */
    public Map<String, TranslatedLibrary> getLibraries() {
        return libraryMap;
    }

    /**
     * Returns a mapping of the libraryname-x-x-xxx to the translated library
     * @return the translator mapping
     */
    public Map<String, CqlTranslator> getTranslators() {
        return translators;
    }
}
