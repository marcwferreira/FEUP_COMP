

### This was done for the subject of ESOF in FEUP. Grade = 90%.

# Summary

This programming project aims to develop a compiler for a subset of the Java programming language, called “jmm”.
To make a compiler there are a few phases of compilation. First, the input file is parsed and an AST is generated. After the AST is parsed, a symbol table is generated. Finally, the OLLIR code is generated and the JASMIN code is generated from the Ollir input.

# Semantic Analysis

### Symbol Table 	  

- Has information about imports and the declared class   

- Has information about extends, fields and methods   

- Has information about the parameters and local variables of each method 

 

### Type Verification    

- Check that the variable names used in the code have a corresponding declaration, either as a local variable, method parameter, or class field

- The operands of an operation must have types compatible with the operation

- Array cannot be used in arithmetic operations

- Array access is over an array	  

- The array access index is an expression of type integer	  

- The assignee type must match with the assignee 

- Expressions in conditions must return a boolean  

 

### Function Verification 	  

- When calling methods of the class declared in code, check that the types of arguments in the call are compatible with the types in the method's declaration

- If the method doesn't exist, verify if the class extends another class and report an error if it doesn't exist. Assume the method exists in one of the superclasses and is being called correctly

- When calling methods that belong to classes other than the class declared in the code, make sure the classes are being imported

# Code Generation

### Abstract Syntax Tree (AST)

The source code is read and transformed into an abstract syntax tree (AST). The AST has representations for every possible entity present in the source code.

### Ollir Generation

The OLLIR code is generated from .jmm files.
We created the class OllirGenerator, in which there is a function for converting each type of expression and statement. Auxiliar variables are created when necessary.
We also use a Code class to create temporary variables to implement sequences of three consecutive address instructions.

### Jasmin Generation 

The OLLIR code created earlier is used to generate the Jasmin code.

The Jasmin basic structure is being generated including the constructor, fields, and methods.
Also the limit of the stack and locals is calculated based on the instructions used in each method.

# This project was done by:

Inês Guimarães

Mariana Monteiro

Marcos Ferreira

Tea Madzarac


#### Also, with special appreciation for Mariana Monteiro.

