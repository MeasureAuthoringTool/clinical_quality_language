package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.cqlModels.LibraryHolder;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jack Meyer
 *
 * The Measure Authoring Tool CQL to ELM Wrapper.
 *
 * Last updated 7/6/2017, commit # 6ae6ac9fe1390131434a6a7c2cdd23f0dfd2cb34
 */
public class CQLtoELM {

    /**
     * The parent cql library string
     */
    private String parentCQLLibraryString;

    /**
     * The cql library mapping. It should follow the format <String, String>, where the key is in the format
     * LibraryName-x.x.xxx and the value should be the cql library.
     */
    private Map<String, String> cqlLibraryMapping;

    /**
     * The parent library cql file
     */
    private File parentCQLLibraryFile;

    /**
     * The parent elm string from cql-to-elm translation
     */
    private String parentElmString;

    /**
     * This list of elm strings from cql-to-elm translation
     */
    private List<String> elmStrings = new ArrayList<>();

    /**
     * The cql library holder mapping. It should follow the format. <String, String>, where the key is in the format
     * LibraryName-x.x.xxx and the value should be the cql library.
     */
    private Map<String, LibraryHolder> libraryHolderMap = new HashMap<>();

    /**
     * The parent cql library
     */
    private Library parentLibrary;

    /**
     * The messagse from cql-to-elm translation
     */
    private List<CqlTranslatorException> messages = new ArrayList<>();

    /**
     * The warnings from cql-to-elm translation
     */
    private List<CqlTranslatorException> warnings = new ArrayList<>();

    /**
     * The errors from cql-to-elm translation
     */
    private List<CqlTranslatorException> errors = new ArrayList<>();



    /**
     * CQL to ELM constructor from strings.
     * @param parentCQLLibraryString the parent cql library string
     * @param cqlLibraryMapping the cql library mapping. It should follow the format <String, String> where the key is
     *                          in the format LibraryName-x.x.xxx and the value should be the cql library string.
     */
    public CQLtoELM(String parentCQLLibraryString, Map<String, String> cqlLibraryMapping) {
        this.parentCQLLibraryString = parentCQLLibraryString;
        this.cqlLibraryMapping = cqlLibraryMapping;
        this.parentCQLLibraryFile = null;
    }

    /**
     * CQL to ELM constructor from files
     * @param parentCQLLibraryFile the parent cql library file
     */
    public CQLtoELM(File parentCQLLibraryFile) {
        this.parentCQLLibraryFile = parentCQLLibraryFile;
        this.parentCQLLibraryString = null;
        this.cqlLibraryMapping = null;
    }

    /**
     * The basic do translation method... so simple, so clean.
     * @param validationOnly validation only flag, when this flag is set to true, no exports will be generated
     */
    public void doTranslation(boolean validationOnly) {

        // MAT-8665
            // list path traversal should be 'on'
            // method style invocation should be 'off'
            // list promotion should be 'on'
            // list demotion should be 'off'

            // locators should be 'on'
            // result types should be 'off'
            // detailed errors should be 'off'

        // MAT-8702
            // messages should not come out in the ELM, therefore we will have our error level as 'Error'

        doTranslation(false, true, true, false,
                false, false, true, false,
                 true, CqlTranslatorException.ErrorSeverity.Error, validationOnly, "XML");
    }

    /**
     * The do translation method with all of the flags
     * @param enableDateRangeOptimization flag for enabling data range optimizations, when true, data range optimization
     *                                    will happen
     * @param enableAnnotations flag for enabling annotations, when true, annotations will appear in ELM output
     * @param enableLocators flag for enabling locators, when true, locators will appear in ELM output
     * @param enableResultTypes flag for enabling result types, when true, result types will appear in ELM output
     * @param enableDetailedErrors flag for enabling detailed errors, when true, translator will give detailed errors
     * @param disableListTraversal flag for disabling list traversal, when true, list traversal will not happen
     * @param disableDemotion flag for disabling demotion, when true, list demotion will not happen
     * @param disablePromotion flag for disabling promotion, when true, list promotion will not happen
     * @param disableMethodInvocation flag for disabling method invocation, when true, method invocation will not happen
     * @param errorSeverity flag for the error severity level
     * @param validationOnly flag for running in validation only mode, when true, no exports will be generated
     * @param format format string
     */
    public void doTranslation(boolean enableDateRangeOptimization, boolean enableAnnotations, boolean enableLocators,
                              boolean enableResultTypes, boolean enableDetailedErrors, boolean disableListTraversal,
                              boolean disableDemotion, boolean disablePromotion, boolean disableMethodInvocation,
                              CqlTranslatorException.ErrorSeverity errorSeverity, boolean validationOnly, String format) {

        // add in all of the flags
        List<CqlTranslator.Options> options = new ArrayList<>();
        if(enableDateRangeOptimization) {
            options.add(CqlTranslator.Options.EnableDateRangeOptimization);
        }

        if(enableAnnotations) {
            options.add(CqlTranslator.Options.EnableAnnotations);
        }

        if(enableLocators) {
            options.add(CqlTranslator.Options.EnableLocators);
        }

        if(enableResultTypes) {
            options.add(CqlTranslator.Options.EnableResultTypes);
        }

        if(enableDetailedErrors) {
            options.add(CqlTranslator.Options.EnableDetailedErrors);
        }

        if(disableDemotion) {
            options.add(CqlTranslator.Options.DisableDemotion);
        }

        if(disablePromotion) {
            options.add(CqlTranslator.Options.DisablePromotion);
        }

        if(disableMethodInvocation) {
            options.add(CqlTranslator.Options.DisableMethodInvocation);
        }

        // parse from string
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        if(parentCQLLibraryString != null && parentCQLLibraryFile == null) {
            libraryManager.getLibrarySourceLoader().registerProvider(
                    new StringLibrarySourceProvider(this.cqlLibraryMapping));
            writeToELM(options.toArray(new CqlTranslator.Options[options.size()]), errorSeverity, format, modelManager, libraryManager);
        }

        // parse from file
        else {
            libraryManager.getLibrarySourceLoader().registerProvider(
                    new DefaultLibrarySourceProvider(this.parentCQLLibraryFile.getParentFile().toPath()));
            writeToELM(options.toArray(new CqlTranslator.Options[options.size()]), errorSeverity, format, modelManager, libraryManager);
        }

    }

    /**
     * Converts the cql to elm, if validation only is false, it will create elm strings.
     * @param options the parser options
     */
    private void writeToELM(CqlTranslator.Options[] options, CqlTranslatorException.ErrorSeverity errorSeverity, String format, ModelManager modelManager,
                            LibraryManager libraryManager) {

        CqlTranslator translator = null;

        // parse from string
        if(parentCQLLibraryString != null && parentCQLLibraryFile == null) {
            translator = CqlTranslator.fromText(this.parentCQLLibraryString, modelManager, libraryManager, errorSeverity, options);
        }

        // parse from file
        else {
            try {
                translator = CqlTranslator.fromFile(this.parentCQLLibraryFile, modelManager, libraryManager, errorSeverity, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // the parent library is the one that is returned from the parent translator
        this.parentLibrary = translator.getTranslatedLibrary().getLibrary();
        libraryManager.getTranslators().put(this.parentLibrary.getIdentifier().getId() + "-" + this.parentLibrary.getIdentifier().getVersion(), translator);

        // set messages, warnings, errors
        this.messages = translator.getMessages();
        this.warnings = translator.getWarnings();
        this.errors = translator.getErrors();

        // create the library holder objects
        for(CqlTranslator cqlTranslator : libraryManager.getTranslators().values()) {
            Library currentLibrary = cqlTranslator.getTranslatedLibrary().getLibrary();
            String currentLibraryName = currentLibrary.getIdentifier().getId() + "-" + currentLibrary.getIdentifier().getVersion();
            Cql2ElmVisitor currentVisitor = cqlTranslator.getFinalVisitor();

            LibraryHolder holder = new LibraryHolder(currentLibrary, currentLibraryName, currentVisitor.getCqlCodeModelObjects(),
                    currentVisitor.getCqlCodeSystemModelObjects(), currentVisitor.getCqlExpressionModelObjects(),
                    currentVisitor.getCqlFunctionModelObjects(), currentVisitor.getCqlIncludeModelObjects(),
                    currentVisitor.getCqlParameterModelObjects(), currentVisitor.getCqlValueSetModelObjects());
            this.libraryHolderMap.put(currentLibraryName, holder);
        }

        // output the elm strings
        if(format.equalsIgnoreCase("XML")) {
            this.parentElmString = translator.toXml();
            for(CqlTranslator currentTranslator : libraryManager.getTranslators().values()) {
                this.elmStrings.add(currentTranslator.toXml());
            }
        }

        if(format.equalsIgnoreCase("JSON")) {
            this.parentElmString = translator.toJson();
            for(CqlTranslator currentTranslator : libraryManager.getTranslators().values()) {
                this.elmStrings.add(currentTranslator.toJson());
            }
        }

        if(format.equalsIgnoreCase("COFFEE")) {
            this.parentElmString = "module.exports = " + translator.toJson();
            for(CqlTranslator currentTranslator : libraryManager.getTranslators().values()) {
                this.elmStrings.add("module.exports = " + currentTranslator.toJson());
            }
        }
    }

    /**
     * Gets the parent library
     * @return returns the parent library
     */
    public Library getLibrary() {
        return parentLibrary;
    }

    /**
     * Gets the list of library holders from the library holder mapping
     * @return the list of library holders
     */
    public List<LibraryHolder> getLibraryList() {
        return new ArrayList<>(this.libraryHolderMap.values());
    }

    /**
     * Gets the library holder map. The map is in the form of <LibraryName-x.x.xxx, LibraryHolder>
     * @return the library holder map
     */
    public Map<String, LibraryHolder> getLibraryHolderMap() {
        return this.libraryHolderMap;
    }

    /**
     * Gets the messages from cql-to-elm translation
     * @return the messages
     */
    public List<CqlTranslatorException> getMessages() {
        return messages;
    }

    /**
     * Gets the warnings from cql-to-elm translation
     * @return the warnings
     */
    public List<CqlTranslatorException> getWarnings() {
        return warnings;
    }

    /**
     * Gets the errors from cql-to-elm translation
     * @return the errors
     */
    public List<CqlTranslatorException> getErrors() {
        return errors;
    }

    /**
     * Gets the parent elm string frmo cql-to-elm translation
     * @return the parent elm string
     */
    public String getElmString() {
        return this.parentElmString;
    }

    /**
     * Gets the list of elm strings from translation
     * @return the list of elm strings
     */
    public List<String> getElmStrings() {
        return elmStrings;
    }

    /**
     * Gets the expression definition object associated with a given name
     * @param name the name to find by
     * @return the expression definition
     */
    public ExpressionDef getExpression(String name) {
        List<ExpressionDef> expressions = this.parentLibrary.getStatements().getDef();

        for(ExpressionDef expression : expressions) {
            if(expression.getName().equalsIgnoreCase(name)) {
                return expression;
            }
        }

        return null;
    }

    /**
     * Gets the expression associated with a given name's return type in a string
     * @param name the name to find by
     * @return the return type as a string
     */
    public String getExpressionReturnType(String name) {
        ExpressionDef expression = getExpression(name);
        return expression.getResultType().toString();
    }

    /**
     * Gets the list of expressions, which includes functions and definitions
     * @return the list of expressions
     */
    public List<ExpressionDef> getExpressions() {
        return this.parentLibrary.getStatements().getDef();
    }

    /**
     * Gets a list of the expression names
     * @return the list of expression names
     */
    public List<String> getExpressionNames() {
        List<String> expressionNames = new ArrayList<>();

        for(ExpressionDef expression : this.parentLibrary.getStatements().getDef()) {
            expressionNames.add(expression.getName());
        }

        return expressionNames;
    }

    public static void main(String[] args) {
        File file = new File("path/to/some/file");
        String cqlString = cqlFileToString(file);
        // you could also create a string like String cqlString = <cql library string here>


        // if the string you are translating has included libraries, put them in the has map in the form of
        // <libraryname-version, included library string>
        HashMap<String, String> includedLibraries = new HashMap<>();
        // includedLibraries.put(libraryname-version, includedCqlString)

        CQLtoELM cqLtoELM = new CQLtoELM(cqlString, new HashMap<>());
        cqLtoELM.doTranslation(false);

        // you could also parse from a file
        // cqlLtoELM = new CQLtoELM(file).
    }

    /**
     * Converts a cql file to a cql string
     * @param file the file to convert
     * @return the cql string
     */
    private static String cqlFileToString(File file) {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(file.toPath());
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
