package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, PomData> pomMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("=== POM TOOL ===");

        mainLoop:
        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "exit":
                    System.out.println("Bye!");
                    break mainLoop;
                case "1":
                    printParentChildRelations();
                    analyzeAllParents();
                    break;
                case "2":
                    loadFromCSV();
                    break;
                default:
                    loadSinglePom(input);
                    break;
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nOptions:");
        System.out.println("Enter path to pom.xml to load a POM");
        System.out.println("1 - Print report for all parents");
        System.out.println("2 - Load multiple POMs from CSV string");
        System.out.println("exit - Quit");
        System.out.print("Your choice: ");
    }

    private static void loadSinglePom(String pathStr) {
        Path pomPath = Path.of(pathStr);
        if (!Files.exists(pomPath)) {
            System.out.println("File not found: " + pathStr);
            return;
        }

        try {
            PomData pomData = PomParser.parse(pomPath);
            String key = pomData.getCoordinates();
            pomMap.put(key, pomData);
            System.out.println("Loaded: " + pomData);
        } catch (Exception e) {
            System.out.println("Error reading POM: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadFromCSV() {
        System.out.print("Enter CSV string of pom.xml paths (comma separated): ");
        String csv = scanner.nextLine();
        String[] paths = csv.split(",");

        for (String pathStr : paths) {
            loadSinglePom(pathStr.trim());
        }
    }

    private static void printParentChildRelations() {
        System.out.println("\n=== PARENT-CHILD RELATIONS ===");
        for (PomData pom : pomMap.values()) {
            String parent = pom.getParentCoordinates();
            if (parent != null) {
                System.out.printf("%s → %s%n", pom.getCoordinates(), parent);
            } else {
                System.out.printf("%s → no parent%n", pom.getCoordinates());
            }
        }
        System.out.println();
    }

    private static void analyzeAllParents() {
        // Find all unique parent coordinates
        Set<String> allParents = new HashSet<>();
        for (PomData pom : pomMap.values()) {
            String parentCoord = pom.getParentCoordinates();
            if (parentCoord != null) {
                allParents.add(parentCoord);
            }
        }

        if (allParents.isEmpty()) {
            System.out.println("No parent POMs detected.");
            return;
        }

        System.out.println("Detected parent POMs:");
        for (String parent : allParents) {
            System.out.println(" - " + parent);
        }
        System.out.println();

        // For each parent analyze child modules
        for (String parent : allParents) {
            System.out.println("=== Analyzing parent: " + parent + " ===");

            PomData parentPom = pomMap.get(parent);
            if (parentPom == null) {
                System.out.println("Parent POM details not loaded for: " + parent);
                System.out.println("Skipping analysis for this parent.\n");
                continue;
            }

            analyzeDependenciesForParent(parentPom);

            System.out.println();
        }
    }

    private static void analyzeDependenciesForParent(PomData parentPom) {
        String parentCoord = parentPom.getCoordinates();

        Set<Dependency> parentDependencies = new HashSet<>(parentPom.getDependencies());

        // Find child modules with this parent
        List<PomData> childModules = new ArrayList<>();
        for (PomData pom : pomMap.values()) {
            if (parentCoord.equals(pom.getParentCoordinates())) {
                childModules.add(pom);
            }
        }

        if (childModules.isEmpty()) {
            System.out.println("No child modules found for parent " + parentCoord);
            return;
        }

        int totalChildren = childModules.size();

        // Build dependency usage map: dependency → set of child module coords
        Map<Dependency, Set<String>> depUsage = new HashMap<>();
        for (PomData child : childModules) {
            for (Dependency dep : child.getDependencies()) {
                depUsage.computeIfAbsent(dep, k -> new HashSet<>()).add(child.getCoordinates());
            }
        }

        System.out.println("Child modules: " + totalChildren);

        System.out.println("--- Dependencies used in child modules ---");
        for (Map.Entry<Dependency, Set<String>> entry : depUsage.entrySet()) {
            Dependency dep = entry.getKey();
            Set<String> modules = entry.getValue();
            double ratio = (double) modules.size() / totalChildren;
            String ratioStr = String.format("%.0f%%", ratio * 100);

            if (modules.size() == totalChildren) {
                System.out.printf("Dependency %s is used in ALL (%d/%d) child modules.%n", dep, modules.size(), totalChildren);
                System.out.printf("  → Consider moving this dependency to parent %s%n", parentCoord);
            } else if (modules.size() > 1) {
                System.out.printf("Dependency %s is used in MOST (%d/%d, %s) child modules.%n", dep, modules.size(), totalChildren, ratioStr);
                System.out.printf("  → Consider moving this dependency to parent %s%n", parentCoord);
            }
        }
        System.out.println();

        System.out.println("--- Dependencies declared in parent and child (redundant in child) ---");
        boolean foundRedundant = false;
        for (PomData child : childModules) {
            for (Dependency dep : child.getDependencies()) {
                if (parentDependencies.contains(dep)) {
                    foundRedundant = true;
                    System.out.printf("Dependency %s is declared in both parent %s and child %s.%n",
                            dep, parentCoord, child.getCoordinates());
                    System.out.printf("  → Can be removed from child %s%n", child.getCoordinates());
                }
            }
        }
        if (!foundRedundant) {
            System.out.println("No redundant dependencies found.");
        }
    }
}
