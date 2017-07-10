package org.cqframework.cql.cql2elm.cqlModels;

import java.util.HashSet;
import java.util.Set;

public class UsedReferencesContainer{

    private Set<CQLIncludeModelObject> includeList;

    private Set<CQLExpressionModelObject> expressionList;

    private Set<CQLParameterModelObject> parameterList;

    private Set<CQLValueSetModelObject> valuesetList;

    private Set<CQLCodeSystemModelObject> codesystemList;

    private Set<CQLCodeModelObject> codeList;

    private Set<CQLFunctionModelObject> functionList;

    public UsedReferencesContainer(Set<CQLIncludeModelObject> includeList,
                                   Set<CQLExpressionModelObject> expressionList,
                                   Set<CQLParameterModelObject> parameterList,
                                   Set<CQLValueSetModelObject> valuesetList,
                                   Set<CQLCodeSystemModelObject> codesystemList,
                                   Set<CQLCodeModelObject> codeList,
                                   Set<CQLFunctionModelObject> functionList) {
        this.includeList = includeList;
        this.expressionList = expressionList;
        this.parameterList = parameterList;
        this.valuesetList = valuesetList;
        this.codesystemList = codesystemList;
        this.codeList = codeList;
        this.functionList = functionList;
    }

    public UsedReferencesContainer() {
        this.includeList = new HashSet<>();
        this.expressionList = new HashSet<>();
        this.parameterList = new HashSet<>();
        this.valuesetList = new HashSet<>();
        this.codesystemList = new HashSet<>();
        this.codeList = new HashSet<>();
        this.functionList = new HashSet<>();
    }

    public Set<CQLIncludeModelObject> getIncludeList() {
        return includeList;
    }

    public void setIncludeList(Set<CQLIncludeModelObject> includeList) {
        this.includeList = includeList;
    }

    public Set<CQLExpressionModelObject> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(Set<CQLExpressionModelObject> expressionList) {
        this.expressionList = expressionList;
    }

    public Set<CQLParameterModelObject> getParameterList() {
        return parameterList;
    }

    public void setParameterList(Set<CQLParameterModelObject> parameterList) {
        this.parameterList = parameterList;
    }

    public Set<CQLValueSetModelObject> getValuesetList() {
        return valuesetList;
    }

    public void setValuesetList(Set<CQLValueSetModelObject> valuesetList) {
        this.valuesetList = valuesetList;
    }

    public Set<CQLCodeSystemModelObject> getCodesystemList() {
        return codesystemList;
    }

    public void setCodesystemList(Set<CQLCodeSystemModelObject> codesystemList) {
        this.codesystemList = codesystemList;
    }

    public Set<CQLCodeModelObject> getCodeList() {
        return codeList;
    }

    public void setCodeList(Set<CQLCodeModelObject> codeList) {
        this.codeList = codeList;
    }

    public Set<CQLFunctionModelObject> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(Set<CQLFunctionModelObject> functionList) {
        this.functionList = functionList;
    }

    public void addInclude(CQLIncludeModelObject include) {
        this.includeList.add(include);
    }

    public void addExpression(CQLExpressionModelObject expression) {
        this.expressionList.add(expression);
    }

    public void addParameter(CQLParameterModelObject parameter) {
        this.parameterList.add(parameter);
    }

    public void addValueset(CQLValueSetModelObject valueset) {
        this.valuesetList.add(valueset);
    }

    public void addCodesystem(CQLCodeSystemModelObject codesystem) {
        this.codesystemList.add(codesystem);
    }

    public void addCode(CQLCodeModelObject code) {
        this.codeList.add(code);
    }

    public void addFunction(CQLFunctionModelObject function) {
        this.functionList.add(function);
    }

    @Override
    public String toString() {
        return "UsedReferencesContainer{" +
                "\nincludeList=" + includeList +
                ", \nexpressionList=" + expressionList +
                ", \nfunctionList = " + functionList +
                ", \nparameterList=" + parameterList +
                ", \nvaluesetList=" + valuesetList +
                ", \ncodesystemList=" + codesystemList +
                ", \ncodeList=" + codeList +
                '}';
    }

    public void print() {
        System.out.println("Includes: ");
        for(CQLIncludeModelObject include : this.includeList) {
            System.out.println("\t" + include.getName());
        }

        System.out.println("Expressions: ");
        for(CQLExpressionModelObject expression : this.expressionList) {
            System.out.println("\t" + expression.getName());
        }

        System.out.println("Paramters: ");
        for(CQLParameterModelObject parameter : this.parameterList) {
            System.out.println("\t" + parameter.getName());
        }

        System.out.println("Valuesets: ");
        for(CQLValueSetModelObject valueset : this.valuesetList) {
            System.out.println("\t" + valueset.getName());
        }

        System.out.println("Codesystems: ");
        for(CQLCodeSystemModelObject codesystem : this.codesystemList) {
            System.out.println("\t" + codesystem.getName());
        }

        System.out.println("Codes: ");
        for(CQLCodeModelObject code : this.codeList) {
            System.out.println("\t" + code.getName());
        }

        System.out.println("Functions: ");
        for(CQLFunctionModelObject function : this.functionList) {
            System.out.println("\t" + function.getName());
        }
    }
}