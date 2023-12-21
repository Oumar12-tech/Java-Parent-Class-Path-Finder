# Java-Parent-Class-Path-Finder


Overview:
This project involves the development of a semantic analyzer for Java classes, implemented in two Java files. The analyzer takes an input file containing pairs of classes and determines the relationship between them. The possible relationships include "U" (upwards), "D" (downwards), "N" (no path), and "E" (error).

Key Components:

ASTVisitorEx (Abstract Syntax Tree Visitor):

The first Java file, ASTVisitorEx, defines a custom ASTVisitor that traverses the AST (Abstract Syntax Tree) of Java source code using the Eclipse JDT (Java Development Tools) library.
It specifically looks for TypeDeclarations (class declarations) and builds a HashMap (parentMap) that stores the parent-child relationships between classes.
RunAction:

The second Java file, RunAction, serves as the main entry point for the semantic analyzer.
It utilizes the ASTVisitorEx to traverse the AST of Java source files within Eclipse projects, collecting information about class relationships.
The semantic analysis involves determining the direction of the relationship between two classes (upwards, downwards, or no path) and handling potential errors.
Semantic Analysis Logic:

The RunAction class performs semantic analysis on pairs of classes read from an input file ("input.txt").
For each class pair, the analyzer checks for the existence of a relationship and outputs the corresponding result ("U," "D," "N," or "E") based on the direction of the relationship or any potential errors.
Error Handling:

The analyzer accounts for scenarios where classes may have no relationship ("N") or where errors occur during the analysis ("E").
Execution:

The analyzer runs within the Eclipse IDE, utilizing the Eclipse JDT library for AST parsing and analysis.
It processes Java projects in the Eclipse workspace, extracts class relationships, and performs semantic analysis on specified class pairs.
Output:

The analyzer outputs the results of the semantic analysis, indicating the relationship direction or error status for each class pair, following the specified format ("U," "D," "N," or "E").
Input File:

The input file ("input.txt") contains pairs of class names, representing the classes for which the semantic analysis will be performed.
Conclusion:

This semantic analyzer provides valuable insights into the hierarchical relationships between Java classes within Eclipse projects. It aids developers in understanding the direction of relationships and potential issues in class hierarchies.
