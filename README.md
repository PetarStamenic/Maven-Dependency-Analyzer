# Maven Parent-Child Dependency Analyzer

This CLI tool analyzes a set of Maven `pom.xml` files, detects parent-child relationships, and suggests dependency optimizations such as moving common dependencies up to the parent POM or removing redundant child dependencies.

---

## Features

- Load single or multiple `pom.xml` files (via CSV input)  
- Automatically detects all parent POMs and their child modules  
- Compares dependencies ignoring versions (`groupId:artifactId` only)  
- Suggests dependencies used in **all or most child modules** to move to the parent  
- Detects dependencies declared in both parent and child (redundant in child)  
- Prints detailed reports per parent POM  

---

## Prerequisites

- Java 23  
- Maven (for building the project)  

---

## Build

Clone the repository and build with Maven:

```bash
mvn clean package
```

## Usage
```txt
Options:
Enter path to pom.xml to load a POM
1 - Print report for all parents
2 - Load multiple POMs from CSV string
exit - Quit
Your choice:
```
